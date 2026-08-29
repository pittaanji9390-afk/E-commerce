package com.marketplace.pricing.service;

import com.marketplace.pricing.domain.DynamicDiscountMatrix15;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicPricingCalculationService15 {

    public BigDecimal calculateDiscountedTotal(BigDecimal basePrice, int qty, DynamicDiscountMatrix15 matrix) {
        BigDecimal subtotal = basePrice.multiply(BigDecimal.valueOf(qty));
        if (qty >= matrix.getBundleQuantityThreshold() && matrix.isActive()) {
            BigDecimal discountFactor = BigDecimal.ONE.subtract(
                    matrix.getPercentageOff().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN)
            );
            return subtotal.multiply(discountFactor).setScale(2, RoundingMode.HALF_EVEN);
        }
        return subtotal;
    }
}
