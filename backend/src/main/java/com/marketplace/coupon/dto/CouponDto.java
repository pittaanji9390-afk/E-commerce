package com.marketplace.coupon.dto;

import com.marketplace.coupon.domain.DiscountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {
    private UUID id;
    private UUID sellerId;
    private String sellerName;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minimumCartValue;
    private BigDecimal maxDiscountCap;
    private int usageLimit;
    private int usedCount;
    private int perUserLimit;
    private boolean active;
    private Instant startsAt;
    private Instant expiresAt;
}
