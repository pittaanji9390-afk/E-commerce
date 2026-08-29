package com.marketplace.cms.domain;

import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cms_banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner extends AuditableEntity {

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "cta_link", nullable = false, length = 255)
    private String ctaLink;

    @Column(name = "cta_text", nullable = false, length = 50)
    private String ctaText;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
