package com.agile.jewelryshop.model;

public enum SupportPriority {
    NORMAL("Thông thường"),
    HIGH("Ưu tiên"),
    URGENT("Khẩn cấp");

    private final String label;

    SupportPriority(String label) { this.label = label; }
    public String getLabel() { return label; }
}
