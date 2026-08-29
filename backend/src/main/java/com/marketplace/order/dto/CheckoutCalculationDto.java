package com.marketplace.order.dto;

import com.marketplace.cart.dto.VendorCartGroupDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutCalculationDto {
    private List<VendorCartGroupDto> vendorGroups;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private String couponApplied;
}
