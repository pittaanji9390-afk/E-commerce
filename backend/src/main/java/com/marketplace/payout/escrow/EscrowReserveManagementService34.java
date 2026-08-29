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
public class EscrowReserveManagementService34 {

    @Transactional
    public EscrowDisputeReserve34 holdReserve(Seller seller, BigDecimal grossSales, BigDecimal rate) {
        BigDecimal hold = grossSales.multiply(rate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))
                .setScale(2, RoundingMode.HALF_EVEN);
        String ref = "RSV-34-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        EscrowDisputeReserve34 reserve = EscrowDisputeReserve34.builder()
                .reserveReference(ref)
                .seller(seller)
                .heldAmount(hold)
                .reservePercentage(rate)
                .releaseScheduledAt(Instant.now().plus(14, ChronoUnit.DAYS))
                .released(false)
                .reason("Rolling 14-day dispute risk reserve buffer #34")
                .build();
        log.info("Held escrow reserve [ref={}, seller={}, amount={}]", ref, seller.getId(), hold);
        return reserve;
    }
}
