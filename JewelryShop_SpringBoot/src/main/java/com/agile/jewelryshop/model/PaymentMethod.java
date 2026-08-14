package com.agile.jewelryshop.model;

public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    VNPAY("VNPay (chế độ demo)");

    private final String label;

    PaymentMethod(String label) { this.label = label; }
    public String getLabel() { return label; }
}
