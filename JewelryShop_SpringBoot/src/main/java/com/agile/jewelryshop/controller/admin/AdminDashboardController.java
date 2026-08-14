package com.agile.jewelryshop.controller.admin;

import com.agile.jewelryshop.model.OrderStatus;
import com.agile.jewelryshop.model.Role;
import com.agile.jewelryshop.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public AdminDashboardController(UserRepository userRepository, ProductRepository productRepository,
                                    CategoryRepository categoryRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("customerCount", userRepository.countByRole(Role.CUSTOMER));
        model.addAttribute("staffCount", userRepository.countByRole(Role.STAFF));
        model.addAttribute("adminCount", userRepository.countByRole(Role.ADMIN));
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("pendingCount", orderRepository.countByStatus(OrderStatus.PENDING));
        model.addAttribute("revenue", orderRepository.totalCompletedRevenue());
        model.addAttribute("recentOrders", orderRepository.findAllByOrderByCreatedAtDesc().stream().limit(8).toList());
        return "admin/dashboard";
    }
}
