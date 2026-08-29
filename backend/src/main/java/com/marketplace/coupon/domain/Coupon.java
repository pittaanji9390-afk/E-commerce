package com.marketplace.coupon.domain;

import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", length = 20, nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", precision = 15, scale = 2, nullable = false)
    private BigDecimal discountValue;

    @Column(name = "minimum_cart_value", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal minimumCartValue = BigDecimal.ZERO;

    @Column(name = "max_discount_cap", precision = 15, scale = 2)
    private BigDecimal maxDiscountCap;

    @Column(name = "usage_limit", nullable = false)
    @Builder.Default
    private int usageLimit = 100;

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private int usedCount = 0;

    @Column(name = "per_user_limit", nullable = false)
    @Builder.Default
    private int perUserLimit = 1;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public boolean isValidNow() {
        Instant now = Instant.now();
        return active && now.isAfter(startsAt) && now.isBefore(expiresAt) && usedCount < usageLimit;
    }
}
