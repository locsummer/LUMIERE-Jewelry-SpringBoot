package com.agile.jewelryshop.controller.admin;

import com.agile.jewelryshop.model.Order;
import com.agile.jewelryshop.model.OrderStatus;
import com.agile.jewelryshop.model.PaymentStatus;
import com.agile.jewelryshop.repository.OrderRepository;
import com.agile.jewelryshop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public AdminOrderController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status,
                       @RequestParam(required = false) PaymentStatus paymentStatus,
                       Model model) {
        var orders = orderRepository.findAllByOrderByCreatedAtDesc();
        if (status != null) orders = orders.stream().filter(o -> o.getStatus() == status).toList();
        if (paymentStatus != null) {
            orders = orders.stream().filter(o -> o.getPaymentStatus() == paymentStatus).toList();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("allPaymentStatuses", PaymentStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPaymentStatus", paymentStatus);
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", requireOrder(id));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("paymentStatuses", List.of(PaymentStatus.PENDING, PaymentStatus.PAID));
        return "admin/orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes ra) {
        try {
            orderService.updateStatus(requireOrder(id), status);
            ra.addFlashAttribute("success", "Đã cập nhật trạng thái đơn hàng");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/{id}/payment")
    public String updatePayment(@PathVariable Long id, @RequestParam PaymentStatus paymentStatus, RedirectAttributes ra) {
        try {
            orderService.updatePaymentStatus(requireOrder(id), paymentStatus);
            ra.addFlashAttribute("success", "Đã cập nhật thanh toán");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/{id}/refund")
    public String processRefund(@PathVariable Long id, @RequestParam String action,
                                @RequestParam(required = false) String note, RedirectAttributes ra) {
        try {
            boolean approve = "approve".equals(action);
            if (!approve && !"reject".equals(action)) {
                throw new IllegalArgumentException("Thao tác hoàn tiền không hợp lệ");
            }
            orderService.processRefund(requireOrder(id), approve, note);
            ra.addFlashAttribute("success", approve
                    ? "Đã xác nhận hoàn tiền cho khách hàng"
                    : "Đã từ chối yêu cầu hoàn tiền");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    private Order requireOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
    }
}
