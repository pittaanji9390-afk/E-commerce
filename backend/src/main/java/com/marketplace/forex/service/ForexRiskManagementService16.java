package com.marketplace.forex.service;

import com.marketplace.forex.domain.ForexHedgingContract16;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForexRiskManagementService16 {

    public BigDecimal calculateGainLoss(ForexHedgingContract16 contract, BigDecimal spotRate) {
        BigDecimal diff = spotRate.subtract(contract.getLockedRate());
        return contract.getNotionalAmount().multiply(diff).setScale(2, RoundingMode.HALF_EVEN);
    }
}
