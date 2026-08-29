package com.marketplace.loyalty.dto;

import com.marketplace.loyalty.domain.LoyaltyTier;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyAccountDto {
    private UUID customerId;
    private int currentPointsBalance;
    private int lifetimePointsEarned;
    private LoyaltyTier tier;
    private String referralCode;
    private double pointCashValueUsd;
}
