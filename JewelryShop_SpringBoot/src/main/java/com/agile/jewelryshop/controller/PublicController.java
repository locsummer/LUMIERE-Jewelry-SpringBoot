package com.agile.jewelryshop.controller;

import com.agile.jewelryshop.repository.CategoryRepository;
import com.agile.jewelryshop.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PublicController {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public PublicController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredProducts", productRepository.findTop8ByAvailableTrueAndFeaturedTrueOrderByIdDesc());
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        return "home";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String q, Model model) {
        String normalizedCategory = category == null || category.isBlank() ? null : category;
        String normalizedKeyword = q == null || q.isBlank() ? null : q.trim();
        model.addAttribute("products", productRepository.searchAvailable(normalizedCategory, normalizedKeyword));
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        model.addAttribute("selectedCategory", normalizedCategory);
        model.addAttribute("keyword", normalizedKeyword);
        return "products";
    }

    @GetMapping("/product/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        var product = productRepository.findBySlugAndAvailableTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", productRepository.searchAvailable(product.getCategory().getSlug(), null)
                .stream().filter(f -> !f.getId().equals(product.getId())).limit(4).toList());
        return "product-detail";
    }

    @GetMapping("/about")
    public String about() { return "about"; }
}
