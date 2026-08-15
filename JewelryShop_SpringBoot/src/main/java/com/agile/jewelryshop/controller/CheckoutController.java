package com.agile.jewelryshop.controller;

import com.agile.jewelryshop.dto.CheckoutForm;
import com.agile.jewelryshop.model.*;
import com.agile.jewelryshop.repository.OrderRepository;
import com.agile.jewelryshop.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;

@Controller
public class CheckoutController {
    private final CartService cartService;
    private final CurrentUserService currentUserService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Value("${app.payment.bank-name}") private String bankName;
    @Value("${app.payment.bank-id:MB}") private String bankId;
    @Value("${app.payment.bank-account}") private String bankAccount;
    @Value("${app.payment.account-name}") private String accountName;

    public CheckoutController(CartService cartService, CurrentUserService currentUserService,
                              OrderService orderService, OrderRepository orderRepository) {
        this.cartService = cartService;
        this.currentUserService = currentUserService;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/checkout")
    public String checkoutForm(Authentication authentication, HttpSession session, Model model) {
        if (cartService.items(session).isEmpty()) return "redirect:/cart";
        User user = currentUserService.require(authentication);
        CheckoutForm form = new CheckoutForm();
        form.setCustomerName(user.getFullName());
        form.setCustomerEmail(user.getEmail());
        form.setCustomerPhone(user.getPhone());
        form.setDeliveryAddress(user.getAddress());
        model.addAttribute("checkoutForm", form);
        populateCheckout(model, session);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String checkout(@Valid @ModelAttribute CheckoutForm checkoutForm, BindingResult result,
                           Authentication authentication, HttpSession session, Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateCheckout(model, session);
            return "checkout";
        }
        try {
            Order order = orderService.checkout(currentUserService.require(authentication), checkoutForm, session);
            if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) return "redirect:/payment/bank/order/" + order.getId();
            if (order.getPaymentMethod() == PaymentMethod.VNPAY) return "redirect:/payment/vnpay/" + order.getOrderCode();
            return "redirect:/orders/success/" + order.getOrderCode();
        } catch (IllegalArgumentException ex) {
            result.reject("checkout", ex.getMessage());
            populateCheckout(model, session);
            return "checkout";
        }
    }

    @GetMapping("/payment/bank/order/{id}")
    public String bank(@PathVariable Long id, Authentication authentication, Model model,
                       RedirectAttributes ra) {
        Order order = requireOwnedOrder(id, authentication);
        if (!orderService.canPay(order, PaymentMethod.BANK_TRANSFER)) {
            ra.addFlashAttribute("error", "Đơn hàng đã hủy hoặc không còn chờ thanh toán");
            return "redirect:/orders/" + order.getId();
        }
        model.addAttribute("order", order);
        model.addAttribute("bankName", bankName);
        model.addAttribute("bankAccount", bankAccount);
        model.addAttribute("accountName", accountName);
        model.addAttribute("vietQrUrl", buildVietQrUrl(order));
        return "payment/bank";
    }

    @PostMapping("/payment/bank/order/{id}/confirm")
    public String confirmBank(@PathVariable Long id, Authentication authentication, RedirectAttributes ra) {
        Order order = requireOwnedOrder(id, authentication);
        if (!orderService.canPay(order, PaymentMethod.BANK_TRANSFER)) {
            ra.addFlashAttribute("error", "Đơn hàng đã hủy hoặc không còn chờ thanh toán");
            return "redirect:/orders/" + order.getId();
        }
        ra.addFlashAttribute("success", "Đã gửi thông báo chuyển khoản. Vui lòng chờ cửa hàng kiểm tra và xác nhận thanh toán.");
        return "redirect:/orders/" + order.getId();
    }

    @GetMapping("/payment/vnpay/{code}")
    public String vnpay(@PathVariable String code, Authentication authentication, Model model,
                        RedirectAttributes ra) {
        Order order = requireOwnedOrder(code, authentication);
        if (!orderService.canPay(order, PaymentMethod.VNPAY)) {
            ra.addFlashAttribute("error", "Đơn hàng đã hủy hoặc không còn chờ thanh toán");
            return "redirect:/orders/" + order.getId();
        }
        model.addAttribute("order", order);
        return "payment/vnpay-demo";
    }

    @PostMapping("/payment/vnpay/{code}/pay")
    public String payVnpay(@PathVariable String code, Authentication authentication, RedirectAttributes ra) {
        Order order = requireOwnedOrder(code, authentication);
        try {
            orderService.markPaid(order, PaymentMethod.VNPAY);
            ra.addFlashAttribute("success", "Thanh toán VNPay demo thành công");
            return "redirect:/orders/success/" + code;
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/orders/" + order.getId();
        }
    }

    @GetMapping("/orders/success/{code}")
    public String success(@PathVariable String code, Authentication authentication, Model model,
                          RedirectAttributes ra) {
        Order order = requireOwnedOrder(code, authentication);
        if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER
                && order.getPaymentStatus() != PaymentStatus.PAID) {
            ra.addFlashAttribute("error", "Xin hãy thanh toán đơn hàng của bạn.");
            return "redirect:/payment/bank/order/" + order.getId();
        }
        model.addAttribute("order", order);
        return "orders/success";
    }

    private Order requireOwnedOrder(String code, Authentication authentication) {
        User user = currentUserService.require(authentication);
        Order order = orderRepository.findByOrderCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (!order.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("Bạn không có quyền xem đơn này");
        return order;
    }

    private Order requireOwnedOrder(Long id, Authentication authentication) {
        User user = currentUserService.require(authentication);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (!order.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("Bạn không có quyền xem đơn này");
        return order;
    }

    private String buildVietQrUrl(Order order) {
        return UriComponentsBuilder
                .fromUriString("https://img.vietqr.io/image/{bankId}-{accountNo}-compact.png")
                .queryParam("amount", order.getTotalAmount().stripTrailingZeros().toPlainString())
                .queryParam("addInfo", "LUMIERE " + order.getOrderCode())
                .queryParam("accountName", accountName)
                .buildAndExpand(bankId, bankAccount)
                .encode()
                .toUriString();
    }

    private void populateCheckout(Model model, HttpSession session) {
        BigDecimal subtotal = cartService.subtotal(session);
        BigDecimal shipping = subtotal.compareTo(OrderService.FREE_SHIPPING_FROM) >= 0 ? BigDecimal.ZERO : OrderService.SHIPPING_FEE;
        model.addAttribute("items", cartService.items(session));
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", subtotal.add(shipping));
        model.addAttribute("paymentMethods", PaymentMethod.values());
    }
}
