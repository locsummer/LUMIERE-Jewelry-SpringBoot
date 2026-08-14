package com.agile.jewelryshop.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductForm {
    @NotBlank(message = "Vui lòng nhập tên sản phẩm")
    @Size(max = 150)
    private String name;
    @NotBlank(message = "Vui lòng nhập mô tả")
    @Size(max = 1500)
    private String description;
    @NotNull(message = "Vui lòng nhập giá")
    @DecimalMin(value = "1000", message = "Giá phải từ 1.000đ")
    private BigDecimal price;
    @Min(value = 0, message = "Phần trăm giảm không được âm")
    @Max(value = 90, message = "Phần trăm giảm tối đa 90%")
    private int discountPercent;
    @NotBlank(message = "Vui lòng nhập đường dẫn ảnh")
    private String image;
    @NotNull(message = "Vui lòng chọn danh mục")
    private Long categoryId;
    @Min(value = 0, message = "Tồn kho không được âm")
    private int stock = 100;
    @Min(value = 1, message = "Thời gian phải lớn hơn 0")
    @Max(value = 180, message = "Thời gian tối đa 180 phút")
    private int preparationMinutes = 20;
    private boolean available = true;
    private boolean featured;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getPreparationMinutes() { return preparationMinutes; }
    public void setPreparationMinutes(int preparationMinutes) { this.preparationMinutes = preparationMinutes; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
}
