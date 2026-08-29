package com.marketplace.catalog.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeDto {
    private Long id;
    private String name;
    private String slug;
    private String iconUrl;
    private String imageUrl;
    private int level;
    @Builder.Default
    private List<CategoryTreeDto> children = new ArrayList<>();
}
