package com.marketplace.catalog.dto;

import com.marketplace.catalog.domain.AttributeType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAttributeDto {
    private UUID id;
    private Long categoryId;
    private String name;
    private String code;
    private AttributeType attributeType;
    private boolean required;
    private boolean filterable;
    private String optionsJson;
}
