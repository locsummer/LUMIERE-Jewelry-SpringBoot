package com.agile.jewelryshop.controller.admin;

import com.agile.jewelryshop.dto.ProductForm;
import com.agile.jewelryshop.model.Product;
import com.agile.jewelryshop.repository.CategoryRepository;
import com.agile.jewelryshop.repository.ProductRepository;
import com.agile.jewelryshop.util.SlugUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("editId", null);
        return "admin/products/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute ProductForm productForm, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("editId", null);
            return "admin/products/form";
        }
        Product product = new Product();
        apply(product, productForm, null);
        productRepository.save(product);
        ra.addFlashAttribute("success", "Đã thêm sản phẩm mới");
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = requireProduct(id);
        ProductForm form = toForm(product);
        model.addAttribute("productForm", form);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("editId", id);
        return "admin/products/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute ProductForm productForm,
                       BindingResult result, Model model, RedirectAttributes ra) {
        Product product = requireProduct(id);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("editId", id);
            return "admin/products/form";
        }
        apply(product, productForm, product.getSlug());
        productRepository.save(product);
        ra.addFlashAttribute("success", "Đã cập nhật sản phẩm");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        Product product = requireProduct(id);
        product.setAvailable(!product.isAvailable());
        productRepository.save(product);
        ra.addFlashAttribute("success", "Đã đổi trạng thái sản phẩm");
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        productRepository.delete(requireProduct(id));
        ra.addFlashAttribute("success", "Đã xóa sản phẩm");
        return "redirect:/admin/products";
    }

    private Product requireProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
    }

    private void apply(Product product, ProductForm form, String currentSlug) {
        product.setName(form.getName().trim());
        String base = SlugUtils.toSlug(form.getName());
        if (currentSlug == null || !currentSlug.equals(base)) product.setSlug(uniqueSlug(base, product.getId()));
        product.setDescription(form.getDescription().trim());
        product.setPrice(form.getPrice());
        product.setDiscountPercent(form.getDiscountPercent());
        product.setImage(form.getImage().trim());
        product.setCategory(categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại")));
        product.setStock(form.getStock());
        product.setPreparationMinutes(form.getPreparationMinutes());
        product.setAvailable(form.isAvailable() && form.getStock() > 0);
        product.setFeatured(form.isFeatured());
    }

    private String uniqueSlug(String base, Long currentId) {
        String slug = base;
        int index = 2;
        while (true) {
            var existing = productRepository.findBySlug(slug);
            if (existing.isEmpty() || existing.get().getId().equals(currentId)) return slug;
            slug = base + "-" + index++;
        }
    }

    private ProductForm toForm(Product product) {
        ProductForm form = new ProductForm();
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setDiscountPercent(product.getDiscountPercent());
        form.setImage(product.getImage());
        form.setCategoryId(product.getCategory().getId());
        form.setStock(product.getStock());
        form.setPreparationMinutes(product.getPreparationMinutes());
        form.setAvailable(product.isAvailable());
        form.setFeatured(product.isFeatured());
        return form;
    }
}
