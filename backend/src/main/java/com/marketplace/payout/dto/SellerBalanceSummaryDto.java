package com.marketplace.payout.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerBalanceSummaryDto {
    private UUID sellerId;
    private BigDecimal availableBalance;
    private BigDecimal pendingEscrowBalance;
    private BigDecimal lifetimeEarnings;
    private BigDecimal totalWithdrawn;
    private String currency;
}
