package com.agile.jewelryshop.model;

public enum SupportCategory {
    ORDER("Đơn hàng"),
    PAYMENT("Thanh toán"),
    REFUND("Hoàn tiền"),
    DELIVERY("Giao hàng"),
    FOOD("Chất lượng sản phẩm"),
    ACCOUNT("Tài khoản"),
    PROMOTION("Khuyến mãi"),
    OTHER("Vấn đề khác");

    private final String label;

    SupportCategory(String label) { this.label = label; }
    public String getLabel() { return label; }
}
