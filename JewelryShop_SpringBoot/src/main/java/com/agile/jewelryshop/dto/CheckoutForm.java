package com.agile.jewelryshop.dto;

import com.agile.jewelryshop.model.PaymentMethod;
import jakarta.validation.constraints.*;

public class CheckoutForm {
    @NotBlank(message = "Vui lòng nhập tên người nhận")
    private String customerName;
    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không đúng định dạng")
    private String customerEmail;
    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải gồm 10 số và bắt đầu bằng 0")
    private String customerPhone;
    @NotBlank(message = "Vui lòng nhập địa chỉ giao hàng")
    @Size(max = 500)
    private String deliveryAddress;
    @Size(max = 1000)
    private String note;
    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
