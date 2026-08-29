package com.marketplace.shipping.domain;

public enum ShipmentStatus {
    LABEL_CREATED,
    READY_TO_SHIP,
    SHIPPED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    DELIVERY_FAILED,
    RETURN_TO_SELLER
}
