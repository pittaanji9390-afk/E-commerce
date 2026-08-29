package com.marketplace.seller.dto;

import com.marketplace.seller.domain.SellerStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfileDto {
    private UUID id;
    private String businessName;
    private String storeSlug;
    private String displayName;
    private String description;
    private String logoUrl;
    private String bannerUrl;
    private String contactEmail;
    private String contactPhone;
    private SellerStatus status;
    private BigDecimal commissionRate;
    private BigDecimal ratingAverage;
    private int ratingCount;
    private Instant createdAt;
}
