package com.marketplace.wms.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickListItemDto {
    private UUID id;
    private String binCode;
    private String zoneAisle;
    private UUID variantId;
    private String variantSku;
    private String productTitle;
    private int quantityToPick;
    private int quantityPicked;
    private boolean verified;
}
