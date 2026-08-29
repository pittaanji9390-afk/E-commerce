package com.marketplace.order.domain;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    PROCESSING,
    PARTIALLY_SHIPPED,
    SHIPPED,
    PARTIALLY_DELIVERED,
    DELIVERED,
    CANCELLED,
    COMPLETED,
    REFUNDED
}
