package com.marketplace.order.dto;

import com.marketplace.customer.dto.CreateAddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "Shipping address is required")
    @Valid
    private CreateAddressRequest shippingAddress;

    private CreateAddressRequest billingAddress;

    private String couponCode;

    private String paymentMethod; // e.g. STRIPE_CARD, RAZORPAY

    private String idempotencyKey;
}
