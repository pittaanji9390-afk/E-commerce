package com.marketplace.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {

    private Long parentId;

    @NotBlank(message = "Category name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Category slug is required")
    @Size(max = 120)
    private String slug;

    private String description;

    private String iconUrl;

    private String imageUrl;

    private Integer displayOrder;

    private BigDecimal commissionRate;
}
