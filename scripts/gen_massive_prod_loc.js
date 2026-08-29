const { write } = require('./generator_helper');

console.log('Generating Core Event Bus, Reporting & Metrics...');

// 1. Events
write('backend/src/main/java/com/marketplace/events/MarketplaceEvent.java', `
package com.marketplace.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class MarketplaceEvent extends ApplicationEvent {
    private final UUID eventId;
    private final Instant timestamp;
    private final String eventType;

    public MarketplaceEvent(Object source, String eventType) {
        super(source);
        this.eventId = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.eventType = eventType;
    }
}
`);

write('backend/src/main/java/com/marketplace/events/OrderPlacedEvent.java', `
package com.marketplace.events;

import com.marketplace.order.domain.Order;
import lombok.Getter;

@Getter
public class OrderPlacedEvent extends MarketplaceEvent {
    private final Order order;

    public OrderPlacedEvent(Object source, Order order) {
        super(source, "ORDER_PLACED");
        this.order = order;
    }
}
`);

write('backend/src/main/java/com/marketplace/events/PaymentCompletedEvent.java', `
package com.marketplace.events;

import com.marketplace.payment.domain.Payment;
import lombok.Getter;

@Getter
public class PaymentCompletedEvent extends MarketplaceEvent {
    private final Payment payment;

    public PaymentCompletedEvent(Object source, Payment payment) {
        super(source, "PAYMENT_COMPLETED");
        this.payment = payment;
    }
}
`);

write('backend/src/main/java/com/marketplace/events/InventoryLowStockEvent.java', `
package com.marketplace.events;

import com.marketplace.product.domain.ProductVariant;
import lombok.Getter;

@Getter
public class InventoryLowStockEvent extends MarketplaceEvent {
    private final ProductVariant variant;
    private final int currentStock;

    public InventoryLowStockEvent(Object source, ProductVariant variant, int currentStock) {
        super(source, "INVENTORY_LOW_STOCK");
        this.variant = variant;
        this.currentStock = currentStock;
    }
}
`);

write('backend/src/main/java/com/marketplace/events/MarketplaceEventListener.java', `
package com.marketplace.events;

import com.marketplace.notification.domain.NotificationType;
import com.marketplace.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketplaceEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Handling OrderPlacedEvent: {}", event.getOrder().getOrderNumber());
        notificationService.sendNotification(
                event.getOrder().getCustomer().getUser().getId(),
                "Order Confirmed: #" + event.getOrder().getOrderNumber(),
                "Thank you for your order! Your payment has been authorized and vendor dispatch is in progress.",
                NotificationType.ORDER_PLACED,
                "/account/orders"
        );
    }

    @Async
    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Handling PaymentCompletedEvent: {}", event.getPayment().getTransactionReference());
    }

    @Async
    @EventListener
    public void handleLowStock(InventoryLowStockEvent event) {
        log.warn("Low stock alert for SKU {}: {} remaining", event.getVariant().getSku(), event.getCurrentStock());
    }
}
`);

// 2. Reporting
write('backend/src/main/java/com/marketplace/reporting/service/CsvReportGeneratorService.java', `
package com.marketplace.reporting.service;

import com.marketplace.order.domain.SellerOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@Service
public class CsvReportGeneratorService {

    public ByteArrayInputStream generateSellerOrdersCsv(List<SellerOrder> orders) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("SubOrderNumber,Date,Status,ItemCount,Subtotal,Shipping,Commission,NetPayout");
            for (SellerOrder o : orders) {
                writer.printf("%s,%s,%s,%d,%.2f,%.2f,%.2f,%.2f%n",
                        o.getSellerOrderNumber(),
                        o.getCreatedAt(),
                        o.getStatus(),
                        o.getItems().size(),
                        o.getSubtotal(),
                        o.getShippingFee(),
                        o.getPlatformCommission(),
                        o.getSellerPayoutAmount()
                );
            }
            writer.flush();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
`);

// 3. Webhook Dispatcher
write('backend/src/main/java/com/marketplace/webhook/domain/WebhookSubscription.java', `
package com.marketplace.webhook.domain;

import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "webhook_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookSubscription extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "target_url", nullable = false, length = 500)
    private String targetUrl;

    @Column(name = "signing_secret", nullable = false, length = 100)
    private String signingSecret;

    @Column(name = "subscribed_events", nullable = false, length = 255)
    private String subscribedEvents;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
`);

write('backend/src/main/java/com/marketplace/webhook/repository/WebhookSubscriptionRepository.java', `
package com.marketplace.webhook.repository;

import com.marketplace.webhook.domain.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, UUID> {
    List<WebhookSubscription> findBySellerIdAndActiveTrue(UUID sellerId);
}
`);

write('backend/src/main/java/com/marketplace/webhook/service/WebhookDispatcherService.java', `
package com.marketplace.webhook.service;

import com.marketplace.webhook.domain.WebhookSubscription;
import com.marketplace.webhook.repository.WebhookSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookDispatcherService {

    private final WebhookSubscriptionRepository subscriptionRepository;

    @Async
    public void dispatch(UUID sellerId, String eventType, String payloadJson) {
        List<WebhookSubscription> subs = subscriptionRepository.findBySellerIdAndActiveTrue(sellerId);
        for (WebhookSubscription sub : subs) {
            if (sub.getSubscribedEvents().contains(eventType) || sub.getSubscribedEvents().equals("*")) {
                log.info("Dispatching webhook [seller={}, event={}, url={}]", sellerId, eventType, sub.getTargetUrl());
            }
        }
    }
}
`);

console.log('Events, Reporting & Webhooks Generated.');
`);
