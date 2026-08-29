package com.marketplace.catalog.rules;

import com.marketplace.product.domain.Product;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "catalog_compliance_rules_34")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogComplianceRule34 extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "rule_type", nullable = false, length = 80)
    private String ruleType;

    @Column(name = "has_passed_safety_checks", nullable = false)
    @Builder.Default
    private boolean passedSafetyChecks = true;

    @Column(name = "flagged_terms_count", nullable = false)
    @Builder.Default
    private int flaggedTermsCount = 0;

    @Column(name = "checked_at", nullable = false)
    @Builder.Default
    private Instant checkedAt = Instant.now();
}
