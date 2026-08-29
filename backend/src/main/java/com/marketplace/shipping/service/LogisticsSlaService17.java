package com.marketplace.shipping.service;

import com.marketplace.shipping.domain.LogisticsSlaTracker17;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsSlaService17 {

    public void evaluateSla(LogisticsSlaTracker17 tracker, Instant deliveryTime) {
        tracker.setActualDeliveryDate(deliveryTime);
        if (deliveryTime.isAfter(tracker.getPromisedDeliveryDate())) {
            tracker.setSlaBreached(true);
            long hours = Duration.between(tracker.getPromisedDeliveryDate(), deliveryTime).toHours();
            tracker.setDelayHours((int) hours);
            log.warn("Carrier {} breached SLA by {} hours", tracker.getCarrierName(), hours);
        } else {
            tracker.setSlaBreached(false);
            tracker.setDelayHours(0);
        }
    }
}
