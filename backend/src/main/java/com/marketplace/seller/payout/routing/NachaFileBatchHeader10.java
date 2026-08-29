package com.marketplace.seller.payout.routing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
public class NachaFileBatchHeader10 {

    public String generateNachaBatch(UUID batchId, BigDecimal totalAmount, int recordCount) {
        String nachaHeader = "101 121000358 " + LocalDate.now() + " 0001 MARKETPLACE INC";
        log.info("Generated ACH NACHA Batch #10 for amount ${} across {} items", totalAmount, recordCount);
        return nachaHeader;
    }
}
