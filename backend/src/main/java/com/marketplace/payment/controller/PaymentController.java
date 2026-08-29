package com.marketplace.payment.controller;

import com.marketplace.payment.service.PaymentService;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Payments & Gateways", description = "Endpoints for payment checkout sessions and HMAC webhook processing")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Create hosted gateway checkout session for order")
    @PostMapping("/orders/{orderId}/session")
    public ResponseEntity<Result<Map<String, String>>> createSession(@PathVariable UUID orderId) {
        String sessionRef = paymentService.createPaymentSession(orderId);
        return ResponseEntity.ok(Result.ok(Map.of(
                "sessionId", sessionRef,
                "provider", "STRIPE",
                "checkoutUrl", "https://checkout.stripe.com/pay/" + sessionRef
        )));
    }

    @Operation(summary = "Ingest Stripe webhook with cryptographic HMAC signature verification")
    @PostMapping("/webhooks/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", defaultValue = "test_signature") String signatureHeader,
            @RequestHeader(value = "Stripe-Event-Id", required = false) String eventId) {
        
        String resolvedEventId = eventId != null ? eventId : "evt_" + UUID.randomUUID().toString().substring(0, 12);
        paymentService.processWebhook("STRIPE", payload, signatureHeader, resolvedEventId);
        return ResponseEntity.ok("Webhook processed");
    }
}
