package com.marketplace.shipping.carrier;

import com.marketplace.shipping.domain.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class CarrierIntegrationGateway13 {

    public String createShippingLabel(Shipment shipment, String serviceLevel) {
        String trackingNumber = "TRK-13-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        log.info("Dispatch label generated via carrier provider #13 [tracking={}]", trackingNumber);
        return "https://labels.marketplace.internal/pdf/" + trackingNumber + ".pdf";
    }

    public BigDecimal fetchLiveQuote(String originZip, String destZip, BigDecimal weightKg) {
        BigDecimal base = BigDecimal.valueOf(8.50 + (13 % 5));
        return base.add(weightKg.multiply(BigDecimal.valueOf(1.75)));
    }
}
