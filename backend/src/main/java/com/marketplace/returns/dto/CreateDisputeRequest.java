package com.marketplace.returns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDisputeRequest {

    @NotNull(message = "Seller Order ID is required")
    private UUID sellerOrderId;

    private UUID returnId;

    @NotBlank(message = "Dispute reason is required")
    private String reason;

    @NotBlank(message = "Detailed description is required")
    private String description;

    private String evidenceUrls;
}
