package com.agile.jewelryshop.service;

import com.agile.jewelryshop.dto.CartItemView;
import com.agile.jewelryshop.dto.CheckoutForm;
import com.agile.jewelryshop.model.*;
import com.agile.jewelryshop.repository.ProductRepository;
import com.agile.jewelryshop.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    public static final BigDecimal SHIPPING_FEE = new BigDecimal("30000");
    public static final BigDecimal FREE_SHIPPING_FROM = new BigDecimal("2000000");

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final MailService mailService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        CartService cartService, MailService mailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.mailService = mailService;
    }

    @Transactional
    public Order checkout(User user, CheckoutForm form, HttpSession session) {
        List<CartItemView> cartItems = cartService.items(session);
        if (cartItems.isEmpty()) throw new IllegalArgumentException("Giỏ hàng đang trống");

        Order order = new Order();
        order.setOrderCode(generateCode());
        order.setUser(user);
        order.setCustomerName(form.getCustomerName().trim());
        order.setCustomerEmail(form.getCustomerEmail().trim());
        order.setCustomerPhone(form.getCustomerPhone().trim());
        order.setDeliveryAddress(form.getDeliveryAddress().trim());
        order.setNote(form.getNote());
        order.setPaymentMethod(form.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItemView cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.product().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Có sản phẩm không còn tồn tại"));
            if (!product.isAvailable() || product.getStock() < cartItem.quantity()) {
                throw new IllegalArgumentException("Sản phẩm " + product.getName() + " không đủ số lượng");
            }
            product.setStock(product.getStock() - cartItem.quantity());
            if (product.getStock() == 0) product.setAvailable(false);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getImage());
            item.setPrice(product.getFinalPrice());
            item.setQuantity(cartItem.quantity());
            item.setSubtotal(product.getFinalPrice().multiply(BigDecimal.valueOf(cartItem.quantity())));
            subtotal = subtotal.add(item.getSubtotal());
            order.addItem(item);
        }

        BigDecimal shipping = subtotal.compareTo(FREE_SHIPPING_FROM) >= 0 ? BigDecimal.ZERO : SHIPPING_FEE;
        order.setSubtotal(subtotal);
        order.setShippingFee(shipping);
        order.setTotalAmount(subtotal.add(shipping));
        Order saved = orderRepository.save(order);
        cartService.clear(session);
        mailService.sendOrderConfirmation(saved);
        return saved;
    }

    @Transactional
    public void cancelByCustomer(Order order) {
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalArgumentException("Đơn hàng đã được xử lý nên không thể hủy");
        }
        order.setStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() == PaymentStatus.PENDING) {
            order.setPaymentStatus(PaymentStatus.CANCELLED);
        }
        restoreStock(order);
        orderRepository.save(order);
    }

    @Transactional
    public void updateStatus(Order order, OrderStatus newStatus) {
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Đơn đã kết thúc, không thể đổi trạng thái");
        }
        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
            if (order.getPaymentStatus() == PaymentStatus.PENDING) {
                order.setPaymentStatus(PaymentStatus.CANCELLED);
            }
        }
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.COMPLETED && order.getPaymentMethod() == PaymentMethod.COD) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }
        orderRepository.save(order);
    }

    @Transactional
    public void markPaid(Order order, PaymentMethod expectedMethod) {
        ensurePayable(order, expectedMethod);
        order.setPaymentStatus(PaymentStatus.PAID);
        orderRepository.save(order);
    }

    public boolean canPay(Order order, PaymentMethod expectedMethod) {
        return order.getStatus() != OrderStatus.CANCELLED
                && order.getPaymentStatus() == PaymentStatus.PENDING
                && order.getPaymentMethod() == expectedMethod;
    }

    public void ensurePayable(Order order, PaymentMethod expectedMethod) {
        if (order.getStatus() == OrderStatus.CANCELLED
                || order.getPaymentStatus() == PaymentStatus.CANCELLED) {
            throw new IllegalArgumentException("Đơn hàng đã hủy nên không thể thanh toán");
        }
        if (order.getPaymentMethod() != expectedMethod) {
            throw new IllegalArgumentException("Phương thức thanh toán không đúng với đơn hàng");
        }
        if (order.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Đơn hàng không còn ở trạng thái chờ thanh toán");
        }
    }

    @Transactional
    public void requestRefund(Order order, String reason) {
        if (order.getStatus() != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Chỉ có thể yêu cầu hoàn tiền cho đơn đã hủy");
        }
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalArgumentException("Đơn hàng chưa thanh toán hoặc đã được xử lý hoàn tiền");
        }
        String cleanReason = reason == null ? "" : reason.trim();
        if (cleanReason.length() < 10) {
            throw new IllegalArgumentException("Lý do hoàn tiền phải có ít nhất 10 ký tự");
        }
        if (cleanReason.length() > 500) {
            throw new IllegalArgumentException("Lý do hoàn tiền tối đa 500 ký tự");
        }
        order.setRefundReason(cleanReason);
        order.setRefundNote(null);
        order.setRefundRequestedAt(LocalDateTime.now());
        order.setRefundProcessedAt(null);
        order.setPaymentStatus(PaymentStatus.REFUND_REQUESTED);
        orderRepository.save(order);
    }

    @Transactional
    public void processRefund(Order order, boolean approve, String note) {
        if (order.getPaymentStatus() != PaymentStatus.REFUND_REQUESTED) {
            throw new IllegalArgumentException("Đơn hàng không có yêu cầu hoàn tiền đang chờ xử lý");
        }
        String cleanNote = note == null ? "" : note.trim();
        if (!approve && cleanNote.length() < 5) {
            throw new IllegalArgumentException("Vui lòng nhập lý do từ chối ít nhất 5 ký tự");
        }
        if (cleanNote.length() > 500) {
            throw new IllegalArgumentException("Ghi chú hoàn tiền tối đa 500 ký tự");
        }
        order.setRefundNote(cleanNote);
        order.setRefundProcessedAt(LocalDateTime.now());
        order.setPaymentStatus(approve ? PaymentStatus.REFUNDED : PaymentStatus.REFUND_REJECTED);
        orderRepository.save(order);
    }

    @Transactional
    public void updatePaymentStatus(Order order, PaymentStatus paymentStatus) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Đơn đã hủy, hãy xử lý qua quy trình hoàn tiền");
        }
        if (paymentStatus != PaymentStatus.PENDING && paymentStatus != PaymentStatus.PAID) {
            throw new IllegalArgumentException("Trạng thái này chỉ được cập nhật qua quy trình hoàn tiền");
        }
        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStock(product.getStock() + item.getQuantity());
                product.setAvailable(true);
            });
        }
    }

    private String generateCode() {
        return "FD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(10, 99);
    }
}
