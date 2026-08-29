package com.marketplace.catalog.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "category_attributes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"category_id", "code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAttribute extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "attribute_type", length = 30, nullable = false)
    private AttributeType attributeType;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private boolean required = false;

    @Column(name = "is_filterable", nullable = false)
    @Builder.Default
    private boolean filterable = true;

    @Column(name = "options_json", columnDefinition = "JSONB")
    private String optionsJson;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
