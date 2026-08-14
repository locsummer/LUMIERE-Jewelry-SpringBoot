package com.agile.jewelryshop.model;

public enum SupportStatus {
    OPEN("Mới tiếp nhận"),
    IN_PROGRESS("Đang xử lý"),
    WAITING_CUSTOMER("Chờ khách phản hồi"),
    RESOLVED("Đã giải quyết"),
    CLOSED("Đã đóng");

    private final String label;

    SupportStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
