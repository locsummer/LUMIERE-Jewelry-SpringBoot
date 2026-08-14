package com.agile.jewelryshop.controller;

import com.agile.jewelryshop.dto.ProfileForm;
import com.agile.jewelryshop.model.Order;
import com.agile.jewelryshop.model.User;
import com.agile.jewelryshop.repository.OrderRepository;
import com.agile.jewelryshop.repository.UserRepository;
import com.agile.jewelryshop.service.CurrentUserService;
import com.agile.jewelryshop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CustomerController {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public CustomerController(CurrentUserService currentUserService, UserRepository userRepository,
                              OrderRepository orderRepository, OrderService orderService) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = currentUserService.require(authentication);
        ProfileForm form = new ProfileForm();
        form.setFullName(user.getFullName());
        form.setPhone(user.getPhone());
        form.setAddress(user.getAddress());
        model.addAttribute("profileForm", form);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute ProfileForm profileForm, BindingResult result,
                                Authentication authentication, Model model, RedirectAttributes ra) {
        User user = currentUserService.require(authentication);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "profile";
        }
        user.setFullName(profileForm.getFullName().trim());
        user.setPhone(profileForm.getPhone());
        user.setAddress(profileForm.getAddress());
        userRepository.save(user);
        ra.addFlashAttribute("success", "Cập nhật hồ sơ thành công");
        return "redirect:/profile";
    }

    @GetMapping("/orders")
    public String orders(Authentication authentication, Model model) {
        User user = currentUserService.require(authentication);
        model.addAttribute("orders", orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
        return "orders/list";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        User user = currentUserService.require(authentication);
        model.addAttribute("order", orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng")));
        return "orders/detail";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication authentication, RedirectAttributes ra) {
        User user = currentUserService.require(authentication);
        Order order = orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        try {
            orderService.cancelByCustomer(order);
            ra.addFlashAttribute("success", "Đã hủy đơn hàng");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/orders/{id}/refund-request")
    public String requestRefund(@PathVariable Long id, @RequestParam String reason,
                                Authentication authentication, RedirectAttributes ra) {
        User user = currentUserService.require(authentication);
        Order order = orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        try {
            orderService.requestRefund(order, reason);
            ra.addFlashAttribute("success", "Đã gửi yêu cầu hoàn tiền. Cửa hàng sẽ sớm phản hồi.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}
