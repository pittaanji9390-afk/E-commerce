package com.marketplace.coupon.dto;

import com.marketplace.coupon.domain.DiscountType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCouponRequest {

    private UUID sellerId;

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be greater than zero")
    private BigDecimal discountValue;

    @Builder.Default
    private BigDecimal minimumCartValue = BigDecimal.ZERO;

    private BigDecimal maxDiscountCap;

    @Builder.Default
    private Integer usageLimit = 100;

    @Builder.Default
    private Integer perUserLimit = 1;

    @NotNull(message = "Start date is required")
    private Instant startsAt;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private Instant expiresAt;
}
