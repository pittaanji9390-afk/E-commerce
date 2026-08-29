package com.marketplace.inventory.ledger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
public class FifoCostCalculationService6 {

    public BigDecimal calculateCostOfGoodsSold(List<InventoryFifoValuationRecord6> fifoQueue, int quantitySold) {
        BigDecimal totalCogs = BigDecimal.ZERO;
        int remainingToFulfill = quantitySold;

        for (InventoryFifoValuationRecord6 batch : fifoQueue) {
            if (remainingToFulfill <= 0) break;
            int take = Math.min(batch.getQuantityRemaining(), remainingToFulfill);
            BigDecimal batchEffectiveCost = batch.getUnitCostBasis().add(batch.getLandedCostAdjustment());
            totalCogs = totalCogs.add(batchEffectiveCost.multiply(BigDecimal.valueOf(take)));
            remainingToFulfill -= take;
        }

        return totalCogs.setScale(2, RoundingMode.HALF_EVEN);
    }
}
