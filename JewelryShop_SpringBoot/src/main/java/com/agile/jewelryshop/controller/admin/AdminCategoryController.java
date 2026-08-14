package com.agile.jewelryshop.controller.admin;

import com.agile.jewelryshop.dto.CategoryForm;
import com.agile.jewelryshop.model.Category;
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
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public AdminCategoryController(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        model.addAttribute("editId", null);
        return "admin/categories/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute CategoryForm categoryForm, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (categoryRepository.existsByNameIgnoreCase(categoryForm.getName())) {
            result.rejectValue("name", "duplicate", "Tên danh mục đã tồn tại");
        }
        if (result.hasErrors()) {
            model.addAttribute("editId", null);
            return "admin/categories/form";
        }
        Category category = new Category();
        apply(category, categoryForm);
        categoryRepository.save(category);
        ra.addFlashAttribute("success", "Đã thêm danh mục");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = requireCategory(id);
        CategoryForm form = new CategoryForm();
        form.setName(category.getName());
        form.setDescription(category.getDescription());
        form.setActive(category.isActive());
        model.addAttribute("categoryForm", form);
        model.addAttribute("editId", id);
        return "admin/categories/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute CategoryForm categoryForm,
                       BindingResult result, Model model, RedirectAttributes ra) {
        Category category = requireCategory(id);
        if (categoryRepository.findAll().stream().anyMatch(c -> !c.getId().equals(id)
                && c.getName().equalsIgnoreCase(categoryForm.getName()))) {
            result.rejectValue("name", "duplicate", "Tên danh mục đã tồn tại");
        }
        if (result.hasErrors()) {
            model.addAttribute("editId", id);
            return "admin/categories/form";
        }
        apply(category, categoryForm);
        categoryRepository.save(category);
        ra.addFlashAttribute("success", "Đã cập nhật danh mục");
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Category category = requireCategory(id);
        if (productRepository.countByCategoryId(id) > 0) {
            category.setActive(false);
            categoryRepository.save(category);
            ra.addFlashAttribute("error", "Danh mục đang có sản phẩm nên chỉ được chuyển sang ẩn");
        } else {
            categoryRepository.delete(category);
            ra.addFlashAttribute("success", "Đã xóa danh mục");
        }
        return "redirect:/admin/categories";
    }

    private void apply(Category category, CategoryForm form) {
        category.setName(form.getName().trim());
        category.setSlug(uniqueSlug(form.getName(), category.getId()));
        category.setDescription(form.getDescription());
        category.setActive(form.isActive());
    }

    private String uniqueSlug(String name, Long currentId) {
        String base = SlugUtils.toSlug(name);
        String slug = base;
        int index = 2;
        while (true) {
            var existing = categoryRepository.findBySlug(slug);
            if (existing.isEmpty() || existing.get().getId().equals(currentId)) return slug;
            slug = base + "-" + index++;
        }
    }

    private Category requireCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
    }
}
