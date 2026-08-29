package com.marketplace.b2b.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rfq_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfqItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RequestForQuote rfq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "requested_quantity", nullable = false)
    private int requestedQuantity;

    @Column(name = "target_unit_price", precision = 15, scale = 2)
    private BigDecimal targetUnitPrice;

    @Column(name = "offered_unit_price", precision = 15, scale = 2)
    private BigDecimal offeredUnitPrice;
}
