package com.marketplace.inventory.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDto {
    private UUID id;
    private UUID variantId;
    private String variantSku;
    private String variantTitle;
    private String productTitle;
    private int onHand;
    private int reserved;
    private int available;
    private int lowStockThreshold;
    private boolean lowStock;
    private Instant updatedAt;
}
