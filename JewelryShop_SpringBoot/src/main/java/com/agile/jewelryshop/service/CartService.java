package com.agile.jewelryshop.service;

import com.agile.jewelryshop.dto.CartItemView;
import com.agile.jewelryshop.model.Product;
import com.agile.jewelryshop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {
    private static final String CART_KEY = "FOOD_CART";
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) { this.productRepository = productRepository; }

    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getCart(HttpSession session) {
        Object value = session.getAttribute(CART_KEY);
        if (value instanceof Map<?, ?>) return (Map<Long, Integer>) value;
        Map<Long, Integer> cart = new LinkedHashMap<>();
        session.setAttribute(CART_KEY, cart);
        return cart;
    }

    public void add(HttpSession session, Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        if (!product.isAvailable() || product.getStock() <= 0) throw new IllegalArgumentException("Sản phẩm hiện đã hết hàng");
        Map<Long, Integer> cart = getCart(session);
        int newQuantity = Math.min(product.getStock(), cart.getOrDefault(productId, 0) + Math.max(quantity, 1));
        cart.put(productId, newQuantity);
    }

    public void update(HttpSession session, Long productId, int quantity) {
        Map<Long, Integer> cart = getCart(session);
        if (quantity <= 0) cart.remove(productId);
        else {
            Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
            cart.put(productId, Math.min(quantity, Math.max(product.getStock(), 0)));
            if (product.getStock() <= 0) cart.remove(productId);
        }
    }

    public List<CartItemView> items(HttpSession session) {
        List<CartItemView> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : getCart(session).entrySet()) {
            productRepository.findById(entry.getKey()).ifPresent(product -> {
                int quantity = Math.min(entry.getValue(), product.getStock());
                if (quantity > 0 && product.isAvailable()) {
                    items.add(new CartItemView(product, quantity, product.getFinalPrice().multiply(BigDecimal.valueOf(quantity))));
                }
            });
        }
        return items;
    }

    public BigDecimal subtotal(HttpSession session) {
        return items(session).stream().map(CartItemView::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int count(HttpSession session) { return getCart(session).values().stream().mapToInt(Integer::intValue).sum(); }
    public void clear(HttpSession session) { getCart(session).clear(); }
}
