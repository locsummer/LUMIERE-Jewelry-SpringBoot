package com.agile.jewelryshop.controller.admin;

import com.agile.jewelryshop.dto.AccountForm;
import com.agile.jewelryshop.model.Role;
import com.agile.jewelryshop.model.User;
import com.agile.jewelryshop.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Role role,
                       @RequestParam(required = false) String q,
                       Model model) {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        if (role != null) users = users.stream().filter(user -> user.getRole() == role).toList();
        if (q != null && !q.isBlank()) {
            String keyword = q.trim().toLowerCase(Locale.ROOT);
            users = users.stream().filter(user -> user.getFullName().toLowerCase(Locale.ROOT).contains(keyword)
                    || user.getEmail().toLowerCase(Locale.ROOT).contains(keyword)
                    || (user.getPhone() != null && user.getPhone().contains(keyword))).toList();
        }
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());
        model.addAttribute("selectedRole", role);
        model.addAttribute("keyword", q);
        model.addAttribute("customerCount", userRepository.countByRole(Role.CUSTOMER));
        model.addAttribute("staffCount", userRepository.countByRole(Role.STAFF));
        model.addAttribute("adminCount", userRepository.countByRole(Role.ADMIN));
        return "admin/users/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("accountForm", new AccountForm());
        model.addAttribute("roles", Role.values());
        model.addAttribute("editId", null);
        return "admin/users/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute AccountForm accountForm, BindingResult result,
                         Model model, RedirectAttributes ra) {
        validatePassword(accountForm.getPassword(), true, result);
        String email = normalizeEmail(accountForm.getEmail());
        if (!email.isBlank() && userRepository.existsByEmailIgnoreCase(email)) {
            result.rejectValue("email", "duplicate", "Email này đã được sử dụng");
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("editId", null);
            return "admin/users/form";
        }
        User user = new User();
        apply(user, accountForm, true);
        userRepository.save(user);
        ra.addFlashAttribute("success", "Đã tạo tài khoản " + accountForm.getRole().getLabel());
        return "redirect:/admin/users?role=" + accountForm.getRole();
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        User user = requireUser(id);
        AccountForm form = toForm(user);
        model.addAttribute("accountForm", form);
        model.addAttribute("roles", Role.values());
        model.addAttribute("editId", id);
        return "admin/users/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute AccountForm accountForm,
                       BindingResult result, Model model, RedirectAttributes ra) {
        User user = requireUser(id);
        validatePassword(accountForm.getPassword(), false, result);
        String email = normalizeEmail(accountForm.getEmail());
        userRepository.findByEmailIgnoreCase(email).filter(found -> !found.getId().equals(id))
                .ifPresent(found -> result.rejectValue("email", "duplicate", "Email này đã được sử dụng"));
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("editId", id);
            return "admin/users/form";
        }
        apply(user, accountForm, false);
        userRepository.save(user);
        ra.addFlashAttribute("success", "Đã cập nhật tài khoản và chức vụ");
        return "redirect:/admin/users?role=" + accountForm.getRole();
    }

    @PostMapping("/{id}/role")
    public String role(@PathVariable Long id, @RequestParam Role role, RedirectAttributes ra) {
        User user = requireUser(id);
        user.setRole(role);
        userRepository.save(user);
        ra.addFlashAttribute("success", "Đã phân quyền tài khoản");
        return "redirect:/admin/users?role=" + role;
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        User user = requireUser(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        ra.addFlashAttribute("success", "Đã đổi trạng thái tài khoản");
        return "redirect:/admin/users";
    }

    private User requireUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
    }

    private void apply(User user, AccountForm form, boolean creating) {
        user.setFullName(form.getFullName().trim());
        user.setEmail(normalizeEmail(form.getEmail()));
        user.setPhone(form.getPhone() == null ? "" : form.getPhone().trim());
        user.setAddress(form.getAddress() == null ? "" : form.getAddress().trim());
        user.setRole(form.getRole());
        user.setEnabled(form.isEnabled());
        if (creating || (form.getPassword() != null && !form.getPassword().isBlank())) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
    }

    private AccountForm toForm(User user) {
        AccountForm form = new AccountForm();
        form.setFullName(user.getFullName());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        form.setAddress(user.getAddress());
        form.setRole(user.getRole());
        form.setEnabled(user.isEnabled());
        return form;
    }

    private void validatePassword(String password, boolean required, BindingResult result) {
        if (required && (password == null || password.isBlank())) {
            result.rejectValue("password", "required", "Vui lòng nhập mật khẩu");
        } else if (password != null && !password.isBlank() && password.length() < 6) {
            result.rejectValue("password", "length", "Mật khẩu phải có ít nhất 6 ký tự");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
