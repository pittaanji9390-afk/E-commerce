package com.marketplace.returns.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnItemDto {
    private UUID id;
    private UUID orderItemId;
    private String productTitle;
    private String variantTitle;
    private String sku;
    private BigDecimal unitPrice;
    private int quantity;
}
