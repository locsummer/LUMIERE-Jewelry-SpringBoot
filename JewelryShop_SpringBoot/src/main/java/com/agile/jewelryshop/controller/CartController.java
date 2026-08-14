package com.agile.jewelryshop.controller;

import com.agile.jewelryshop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) { this.cartService = cartService; }

    @GetMapping
    public String view(HttpSession session, Model model) {
        model.addAttribute("items", cartService.items(session));
        model.addAttribute("subtotal", cartService.subtotal(session));
        return "cart";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
                      HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            cartService.add(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null && referer.startsWith(request.getScheme() + "://" + request.getServerName())
                ? referer.substring(referer.indexOf('/', referer.indexOf("//") + 2)) : "/products");
    }

    @PostMapping("/update")
    public String update(@RequestParam Long productId, @RequestParam int quantity, HttpSession session) {
        cartService.update(session, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String remove(@PathVariable Long productId, HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.update(session, productId, 0);
        redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(HttpSession session) {
        cartService.clear(session);
        return "redirect:/cart";
    }
}
