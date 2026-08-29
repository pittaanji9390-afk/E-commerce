package com.marketplace.b2b.service;

import com.marketplace.b2b.domain.PurchaseOrderBatch10;
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
public class PurchaseOrderBatchService10 {

    @Transactional
    public PurchaseOrderBatch10 createBatch(Customer buyer, Seller seller, String entity, BigDecimal total, int netDays) {
        String poNum = "PO-BATCH-10-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        PurchaseOrderBatch10 po = PurchaseOrderBatch10.builder()
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
