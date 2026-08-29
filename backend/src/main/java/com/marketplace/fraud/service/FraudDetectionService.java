package com.marketplace.fraud.service;

import com.marketplace.fraud.domain.RiskEvaluation;
import com.marketplace.fraud.domain.RiskLevel;
import com.marketplace.fraud.repository.RiskEvaluationRepository;
import com.marketplace.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final RiskEvaluationRepository riskRepository;

    @Transactional
    public RiskEvaluation evaluateOrderRisk(Order order, String ipAddress, String fingerprint) {
        int score = 10; // baseline safe

        if (order.getTotalAmount().compareTo(BigDecimal.valueOf(2000.00)) > 0) {
            score += 25;
        }

        RiskLevel level;
        if (score > 75) level = RiskLevel.CRITICAL;
        else if (score > 50) level = RiskLevel.HIGH;
        else if (score > 25) level = RiskLevel.MEDIUM;
        else level = RiskLevel.LOW;

        RiskEvaluation evaluation = RiskEvaluation.builder()
                .order(order)
                .riskScore(score)
                .riskLevel(level)
                .ipAddress(ipAddress)
                .deviceFingerprint(fingerprint)
                .flagsJson("[\"AUTO_EVALUATED\"]")
                .build();

        log.info("Risk assessment complete for order {}: [score={}, level={}]", order.getOrderNumber(), score, level);
        return riskRepository.save(evaluation);
    }
}
