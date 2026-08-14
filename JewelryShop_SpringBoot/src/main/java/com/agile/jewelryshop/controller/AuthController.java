package com.agile.jewelryshop.controller;

import com.agile.jewelryshop.dto.RegisterForm;
import com.agile.jewelryshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/login")
    public String login() { return "auth/login"; }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm registerForm, BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "auth/register";
        try {
            userService.register(registerForm);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công. Bạn có thể đăng nhập ngay!");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            result.reject("register", ex.getMessage());
            return "auth/register";
        }
    }
}
