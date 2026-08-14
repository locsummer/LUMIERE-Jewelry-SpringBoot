package com.agile.jewelryshop.config;

import com.agile.jewelryshop.model.User;
import com.agile.jewelryshop.repository.CategoryRepository;
import com.agile.jewelryshop.repository.UserRepository;
import com.agile.jewelryshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {
    private final CartService cartService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public GlobalModelAdvice(CartService cartService, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.cartService = cartService;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) { return cartService.count(session); }

    @ModelAttribute("navCategories")
    public Object navCategories() { return categoryRepository.findByActiveTrueOrderByNameAsc(); }

    @ModelAttribute("currentUser")
    public User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
    }
}
