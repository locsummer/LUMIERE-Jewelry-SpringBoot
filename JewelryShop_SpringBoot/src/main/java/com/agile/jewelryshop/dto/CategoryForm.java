package com.agile.jewelryshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryForm {
    @NotBlank(message = "Vui lòng nhập tên danh mục")
    @Size(max = 100)
    private String name;
    @Size(max = 500)
    private String description;
    private boolean active = true;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
