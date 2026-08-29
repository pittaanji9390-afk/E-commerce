package com.marketplace.pricing.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "dynamic_discount_matrices_7")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DynamicDiscountMatrix7 extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "bundle_quantity_threshold", nullable = false)
    private int bundleQuantityThreshold;

    @Column(name = "percentage_off", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentageOff;

    @Column(name = "cash_discount_amount", precision = 15, scale = 2)
    private BigDecimal cashDiscountAmount;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
