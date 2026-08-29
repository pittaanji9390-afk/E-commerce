package com.marketplace.b2b.service;

import com.marketplace.b2b.domain.PurchaseOrderBatch13;
import com.marketplace.customer.domain.Customer;
import com.marketplace.seller.domain.Seller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderBatchService13 {

    @Transactional
    public PurchaseOrderBatch13 createBatch(Customer buyer, Seller seller, String entity, BigDecimal total, int netDays) {
        String poNum = "PO-BATCH-13-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        PurchaseOrderBatch13 po = PurchaseOrderBatch13.builder()
                .poNumber(poNum)
                .buyer(buyer)
                .seller(seller)
                .corporateEntityName(entity)
                .subtotalAmount(total)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(total)
                .paymentDueDate(LocalDate.now().plusDays(netDays))
                .settled(false)
                .build();
        log.info("Batch PO created: {}", poNum);
        return po;
    }
}
