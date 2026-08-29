package com.marketplace.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBrandRequest {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Brand slug is required")
    @Size(max = 120)
    private String slug;

    private String logoUrl;

    private String description;

    private String websiteUrl;
}
