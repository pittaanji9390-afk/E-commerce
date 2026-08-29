package com.marketplace.subscription.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", length = 30, nullable = false)
    private SubscriptionFrequency frequency;

    @Column(name = "discount_percentage", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.valueOf(10.00);

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
