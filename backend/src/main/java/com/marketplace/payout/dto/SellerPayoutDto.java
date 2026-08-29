package com.marketplace.payout.dto;

import com.marketplace.payout.domain.PayoutBatchStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerPayoutDto {
    private UUID id;
    private String payoutBatchReference;
    private BigDecimal amount;
    private String currency;
    private String bankAccountLast4;
    private String bankName;
    private PayoutBatchStatus status;
    private String gatewayPayoutId;
    private Instant createdAt;
    private Instant processedAt;
}
