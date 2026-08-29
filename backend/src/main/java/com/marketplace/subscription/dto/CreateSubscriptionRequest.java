package com.marketplace.subscription.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotNull(message = "Subscription plan ID is required")
    private UUID planId;

    @NotNull(message = "Shipping address ID is required")
    private UUID shippingAddressId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Builder.Default
    private int quantity = 1;
}
