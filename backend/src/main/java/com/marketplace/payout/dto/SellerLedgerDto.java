package com.marketplace.payout.dto;

import com.marketplace.payout.domain.SellerLedgerType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerLedgerDto {
    private UUID id;
    private SellerLedgerType entryType;
    private BigDecimal amount;
    private String currency;
    private BigDecimal runningBalance;
    private String referenceType;
    private String referenceId;
    private String description;
    private Instant createdAt;
}
