package com.marketplace.shipping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentRequest {

    @NotNull(message = "Seller order ID is required")
    private UUID sellerOrderId;

    @NotBlank(message = "Carrier is required (e.g. FEDEX, UPS, DHL, USPS)")
    private String carrier;

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    private String shippingLabelUrl;
}
