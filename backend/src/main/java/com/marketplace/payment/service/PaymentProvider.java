package com.marketplace.payment.service;

import com.marketplace.order.domain.Order;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentProvider {

    String getProviderName();

    String createPaymentSession(Order order, Map<String, Object> metadata);

    boolean verifyWebhookSignature(String payload, String signatureHeader);

    String processRefund(String providerTransactionId, BigDecimal amount, String reason);
}
