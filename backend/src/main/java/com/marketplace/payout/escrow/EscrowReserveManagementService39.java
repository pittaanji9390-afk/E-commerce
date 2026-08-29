package com.marketplace.payout.escrow;

import com.marketplace.seller.domain.Seller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EscrowReserveManagementService39 {

    @Transactional
    public EscrowDisputeReserve39 holdReserve(Seller seller, BigDecimal grossSales, BigDecimal rate) {
        BigDecimal hold = grossSales.multiply(rate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))
                .setScale(2, RoundingMode.HALF_EVEN);
        String ref = "RSV-39-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        EscrowDisputeReserve39 reserve = EscrowDisputeReserve39.builder()
                .reserveReference(ref)
                .seller(seller)
                .heldAmount(hold)
                .reservePercentage(rate)
                .releaseScheduledAt(Instant.now().plus(14, ChronoUnit.DAYS))
                .released(false)
                .reason("Rolling 14-day dispute risk reserve buffer #39")
                .build();
        log.info("Held escrow reserve [ref={}, seller={}, amount={}]", ref, seller.getId(), hold);
        return reserve;
    }
}
