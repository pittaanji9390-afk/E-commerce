package com.marketplace.inventory.dto;

import com.marketplace.inventory.domain.InventoryTransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjustInventoryRequest {

    @NotNull(message = "Variant ID is required")
    private UUID variantId;

    @NotNull(message = "New on-hand count is required")
    private Integer newOnHand;

    @NotNull(message = "Transaction type is required")
    private InventoryTransactionType type;

    private String reason;
}
