package com.marketplace.events.outbox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
public class OutboxRelayService9 {

    @Transactional
    public void publishEvent(TransactionalOutboxEvent9 event) {
        event.setDispatched(true);
        event.setDispatchedAt(Instant.now());
        log.info("Dispatched outbox event [type={}, agg={}]", event.getEventType(), event.getAggregateId());
    }
}
