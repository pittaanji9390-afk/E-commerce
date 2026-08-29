package com.marketplace.payout.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPayoutRequest {

    @NotNull(message = "Payout amount is required")
    @Positive(message = "Payout amount must be greater than zero")
    private BigDecimal amount;

    private UUID bankAccountId;
}
