package com.agile.jewelryshop.model;

public enum PaymentStatus {
    PENDING("Chưa thanh toán"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy thanh toán"),
    REFUND_REQUESTED("Chờ hoàn tiền"),
    REFUNDED("Đã hoàn tiền"),
    REFUND_REJECTED("Từ chối hoàn tiền");

    private final String label;

    PaymentStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
