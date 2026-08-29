package com.marketplace.shipping.dto;

import com.marketplace.shipping.domain.ShipmentStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDto {
    private UUID id;
    private UUID sellerOrderId;
    private String carrier;
    private String trackingNumber;
    private String shippingLabelUrl;
    private ShipmentStatus status;
    private Instant shippedAt;
    private Instant estimatedDelivery;
    private Instant deliveredAt;
    private List<ShipmentEventDto> events;
}
