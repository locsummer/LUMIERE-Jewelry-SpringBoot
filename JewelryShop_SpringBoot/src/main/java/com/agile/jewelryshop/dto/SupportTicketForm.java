package com.agile.jewelryshop.dto;

import com.agile.jewelryshop.model.SupportCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SupportTicketForm {
    @NotNull(message = "Vui lòng chọn loại vấn đề")
    private SupportCategory category;

    @NotBlank(message = "Vui lòng nhập tiêu đề")
    @Size(min = 5, max = 180, message = "Tiêu đề từ 5 đến 180 ký tự")
    private String subject;

    @NotBlank(message = "Vui lòng mô tả vấn đề")
    @Size(min = 10, max = 3000, message = "Nội dung từ 10 đến 3.000 ký tự")
    private String message;

    private Long orderId;

    public SupportCategory getCategory() { return category; }
    public void setCategory(SupportCategory category) { this.category = category; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}
