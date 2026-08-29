package com.marketplace.order.domain;

public enum PaymentStatus {
    PENDING,
    AUTHORIZED,
    PAID,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED
}
