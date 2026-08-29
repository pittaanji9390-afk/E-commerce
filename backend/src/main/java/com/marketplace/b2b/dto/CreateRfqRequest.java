package com.marketplace.b2b.dto;

import com.marketplace.b2b.domain.CreditTermType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRfqRequest {

    @NotNull(message = "Seller ID is required")
    private UUID sellerId;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String taxExemptionNumber;

    @Builder.Default
    private CreditTermType creditTerms = CreditTermType.PREPAID;

    private BigDecimal targetPrice;

    private String buyerMessage;

    @NotEmpty(message = "RFQ items cannot be empty")
    private List<RfqItemRequest> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RfqItemRequest {
        @NotNull
        private UUID variantId;
        @NotNull
        private Integer requestedQuantity;
        private BigDecimal targetUnitPrice;
    }
}
