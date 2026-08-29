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
public class CartDto {
    private UUID id;
    private List<VendorCartGroupDto> vendorGroups;
    private BigDecimal subtotal;
    private int totalItemCount;
}
