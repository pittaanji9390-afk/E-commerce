package com.marketplace.product.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDto {
    private UUID id;
    private String imageUrl;
    private String thumbnailUrl;
    private String altText;
    private int displayOrder;
    private boolean primary;
}
