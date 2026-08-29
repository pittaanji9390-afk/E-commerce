package com.marketplace.b2b.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfqItemDto {
    private UUID id;
    private UUID variantId;
    private String variantSku;
    private String productTitle;
    private int requestedQuantity;
    private BigDecimal targetUnitPrice;
    private BigDecimal offeredUnitPrice;
}
