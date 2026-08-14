package com.agile.jewelryshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileForm {
    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(max = 100)
    private String fullName;
    @Pattern(regexp = "^$|0[0-9]{9}$", message = "Số điện thoại phải gồm 10 số và bắt đầu bằng 0")
    private String phone;
    @Size(max = 500)
    private String address;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
