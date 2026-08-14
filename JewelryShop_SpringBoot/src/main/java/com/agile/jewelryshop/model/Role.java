package com.agile.jewelryshop.model;

public enum Role {
    CUSTOMER("Khách hàng"),
    STAFF("Nhân viên"),
    ADMIN("Quản trị viên");

    private final String label;

    Role(String label) { this.label = label; }
    public String getLabel() { return label; }
}
