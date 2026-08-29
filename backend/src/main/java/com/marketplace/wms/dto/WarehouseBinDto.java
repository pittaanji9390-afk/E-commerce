package com.marketplace.wms.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseBinDto {
    private UUID id;
    private UUID warehouseId;
    private String warehouseCode;
    private String binCode;
    private String zoneCode;
    private String aisle;
    private String shelf;
    private UUID variantId;
    private String variantSku;
    private String productTitle;
    private int quantityOnHand;
    private int maxCapacity;
}
