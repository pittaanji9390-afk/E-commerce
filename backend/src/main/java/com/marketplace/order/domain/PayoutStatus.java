package com.marketplace.order.domain;

public enum PayoutStatus {
    PENDING,
    ESCROW_HELD,
    ELIGIBLE,
    PROCESSING,
    PAID,
    ON_HOLD,
    FORFEITED
}
