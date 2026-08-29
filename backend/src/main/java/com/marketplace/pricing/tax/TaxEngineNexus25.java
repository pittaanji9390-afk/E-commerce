package com.marketplace.pricing.tax;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class TaxEngineNexus25 {

    public BigDecimal calculateJurisdictionTax(BigDecimal taxableAmount, String countryCode, String stateCode) {
        if (taxableAmount == null || taxableAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = getRate(countryCode, stateCode);
        return taxableAmount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getRate(String country, String state) {
        if ("US".equalsIgnoreCase(country)) {
            return BigDecimal.valueOf(0.0725 + ((25 % 10) * 0.002));
        } else if ("EU".equalsIgnoreCase(country) || "GB".equalsIgnoreCase(country)) {
            return BigDecimal.valueOf(0.2000);
        }
        return BigDecimal.valueOf(0.0500);
    }
}
