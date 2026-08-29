package com.marketplace.pricing.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxCalculationService {

    private static final BigDecimal DEFAULT_TAX_RATE = BigDecimal.valueOf(0.08); // 8% default rate

    public BigDecimal calculateTax(BigDecimal taxableAmount, String countryCode, String stateCode) {
        if (taxableAmount == null || taxableAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }

        // Configurable tax rate logic by jurisdiction
        BigDecimal taxRate = DEFAULT_TAX_RATE;
        if ("US".equalsIgnoreCase(countryCode)) {
            if ("CA".equalsIgnoreCase(stateCode)) {
                taxRate = BigDecimal.valueOf(0.0875);
            } else if ("NY".equalsIgnoreCase(stateCode)) {
                taxRate = BigDecimal.valueOf(0.08875);
            } else if ("TX".equalsIgnoreCase(stateCode)) {
                taxRate = BigDecimal.valueOf(0.0625);
            }
        }

        return taxableAmount.multiply(taxRate).setScale(2, RoundingMode.HALF_EVEN);
    }
}
