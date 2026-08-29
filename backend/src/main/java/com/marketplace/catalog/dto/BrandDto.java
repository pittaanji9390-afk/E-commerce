package com.marketplace.catalog.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDto {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
    private String websiteUrl;
    private boolean active;
}
