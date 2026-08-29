package com.marketplace.forex.service;

import com.marketplace.forex.domain.ForexHedgingContract4;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForexRiskManagementService4 {

    public BigDecimal calculateGainLoss(ForexHedgingContract4 contract, BigDecimal spotRate) {
        BigDecimal diff = spotRate.subtract(contract.getLockedRate());
        return contract.getNotionalAmount().multiply(diff).setScale(2, RoundingMode.HALF_EVEN);
    }
}
