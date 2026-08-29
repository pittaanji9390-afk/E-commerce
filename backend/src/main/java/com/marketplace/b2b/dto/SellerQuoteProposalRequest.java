package com.marketplace.b2b.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerQuoteProposalRequest {

    @NotEmpty(message = "Item prices must be provided")
    private Map<UUID, BigDecimal> offeredUnitPrices;

    @NotNull(message = "Total quotation is required")
    private BigDecimal quotedTotal;

    private String sellerNotes;

    @NotNull(message = "Validity period is required")
    private Instant validUntil;
}
