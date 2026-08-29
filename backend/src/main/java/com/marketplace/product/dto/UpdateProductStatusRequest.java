package com.marketplace.product.dto;

import com.marketplace.product.domain.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductStatusRequest {

    @NotNull(message = "Product status is required")
    private ProductStatus status;

    private String moderationNotes;
}
