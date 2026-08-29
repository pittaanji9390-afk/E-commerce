package com.marketplace.catalog.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private Long parentId;
    private String name;
    private String slug;
    private String description;
    private String iconUrl;
    private String imageUrl;
    private String path;
    private int level;
    private int displayOrder;
    private BigDecimal commissionRate;
    private boolean active;
}
