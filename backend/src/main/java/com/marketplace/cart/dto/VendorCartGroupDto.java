package com.marketplace.cart.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorCartGroupDto {
    private UUID sellerId;
    private String sellerName;
    private String sellerSlug;
    private List<CartItemDto> items;
    private BigDecimal groupSubtotal;
}
