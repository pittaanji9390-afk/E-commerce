package com.marketplace.payment.domain;

public enum PaymentTransactionStatus {
    INITIALIZED,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    CANCELLED,
    REFUNDED
}
