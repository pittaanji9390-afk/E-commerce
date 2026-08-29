package com.marketplace.coupon.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDiscountResult {
    private UUID couponId;
    private String code;
    private BigDecimal discountAmount;
    private BigDecimal finalSubtotal;
}
