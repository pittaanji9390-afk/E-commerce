package com.marketplace.shared.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public interface DomainEvent extends Serializable {

    default UUID getEventId() {
        return UUID.randomUUID();
    }

    default Instant getOccurredOn() {
        return Instant.now();
    }

    String getEventType();
}
