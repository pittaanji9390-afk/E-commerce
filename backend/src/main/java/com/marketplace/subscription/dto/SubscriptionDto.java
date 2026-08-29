package com.marketplace.subscription.dto;

import com.marketplace.subscription.domain.SubscriptionFrequency;
import com.marketplace.subscription.domain.SubscriptionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {
    private UUID id;
    private String subscriptionNumber;
    private UUID customerId;
    private String planName;
    private SubscriptionFrequency frequency;
    private UUID variantId;
    private String variantSku;
    private String productTitle;
    private int quantity;
    private BigDecimal recurringPrice;
    private SubscriptionStatus status;
    private Instant nextBillingDate;
    private Instant lastBilledAt;
    private Instant createdAt;
}
