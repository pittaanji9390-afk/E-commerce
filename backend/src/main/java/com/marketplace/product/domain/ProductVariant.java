package com.marketplace.product.domain;

import com.marketplace.inventory.domain.Inventory;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", length = 100, nullable = false, unique = true)
    private String sku;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "price_adjustment", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal priceAdjustment = BigDecimal.ZERO;

    @Column(name = "weight_adjustment_grams", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal weightAdjustmentGrams = BigDecimal.ZERO;

    @Column(name = "attributes_json", columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private String attributesJson = "{}";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory inventory;

    public BigDecimal getEffectivePrice() {
        return product.getBasePrice().add(priceAdjustment);
    }
}
