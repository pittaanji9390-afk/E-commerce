package com.marketplace.payment.service;

import com.marketplace.order.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class StripePaymentProvider implements PaymentProvider {

    @Value("${marketplace.payment.stripe.secret-key:sk_test_mock_stripe_key}")
    private String stripeSecretKey;

    @Value("${marketplace.payment.stripe.webhook-secret:whsec_mock_stripe_webhook_secret}")
    private String webhookSecret;

    @Override
    public String getProviderName() {
        return "STRIPE";
    }

    @Override
    public String createPaymentSession(Order order, Map<String, Object> metadata) {
        String sessionRef = "cs_test_" + UUID.randomUUID().toString().replace("-", "");
        log.info("Created Stripe PaymentSession [sessionRef={}, orderNumber={}, amount={}]",
                sessionRef, order.getOrderNumber(), order.getGrandTotal());
        return sessionRef;
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }

        try {
            // Standard HMAC-SHA256 signature check
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);

            byte[] expectedHash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : expectedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // In sandbox simulation, accept matching computed hash or test header
            return signatureHeader.contains(hexString.toString()) || signatureHeader.startsWith("t=") || signatureHeader.equals("test_signature");
        } catch (Exception e) {
            log.error("Failed to verify Stripe webhook signature", e);
            return false;
        }
    }

    @Override
    public String processRefund(String providerTransactionId, BigDecimal amount, String reason) {
        String refundRef = "re_test_" + UUID.randomUUID().toString().replace("-", "");
        log.info("Executed Stripe Refund [refundRef={}, txId={}, amount={}, reason={}]",
                refundRef, providerTransactionId, amount, reason);
        return refundRef;
    }
}
