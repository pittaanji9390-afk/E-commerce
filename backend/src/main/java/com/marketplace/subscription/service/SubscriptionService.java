package com.marketplace.subscription.service;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.domain.CustomerAddress;
import com.marketplace.customer.repository.CustomerAddressRepository;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import com.marketplace.subscription.domain.*;
import com.marketplace.subscription.dto.*;
import com.marketplace.subscription.repository.CustomerSubscriptionRepository;
import com.marketplace.subscription.repository.SubscriptionInvoiceRepository;
import com.marketplace.subscription.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final CustomerSubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionInvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final CustomerAddressRepository addressRepository;

    @Transactional
    public SubscriptionDto subscribe(UUID customerId, CreateSubscriptionRequest request) {
        Customer customer = customerService.getOrCreateCustomer(customerId);

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("SubscriptionPlan", "id", request.getPlanId()));

        CustomerAddress address = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("CustomerAddress", "id", request.getShippingAddressId()));

        BigDecimal basePrice = plan.getVariant().getProduct().getBasePrice()
                .add(plan.getVariant().getPriceAdjustment());

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                plan.getDiscountPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN)
        );

        BigDecimal discountedUnitPrice = basePrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal recurringTotal = discountedUnitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        String subNum = "SUB-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        CustomerSubscription subscription = CustomerSubscription.builder()
                .subscriptionNumber(subNum)
                .customer(customer)
                .plan(plan)
                .variant(plan.getVariant())
                .shippingAddress(address)
                .quantity(request.getQuantity())
                .recurringPrice(recurringTotal)
                .status(SubscriptionStatus.ACTIVE)
                .nextBillingDate(calculateNextBillingDate(plan.getFrequency()))
                .build();

        CustomerSubscription saved = subscriptionRepository.save(subscription);
        log.info("Customer subscribed [number={}, customer={}, plan={}]", subNum, customerId, plan.getName());
        return toDto(saved);
    }

    @Transactional
    public SubscriptionDto pauseSubscription(UUID customerId, UUID subscriptionId) {
        CustomerSubscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("CustomerSubscription", "id", subscriptionId));

        if (!sub.getCustomer().getId().equals(customerId)) {
            throw new BusinessRuleException(ErrorCode.FORBIDDEN, "Unauthorized subscription access.");
        }

        sub.setStatus(SubscriptionStatus.PAUSED);
        return toDto(subscriptionRepository.save(sub));
    }

    @Transactional
    public SubscriptionDto resumeSubscription(UUID customerId, UUID subscriptionId) {
        CustomerSubscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("CustomerSubscription", "id", subscriptionId));

        if (!sub.getCustomer().getId().equals(customerId)) {
            throw new BusinessRuleException(ErrorCode.FORBIDDEN, "Unauthorized subscription access.");
        }

        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setNextBillingDate(calculateNextBillingDate(sub.getPlan().getFrequency()));
        return toDto(subscriptionRepository.save(sub));
    }

    @Transactional(readOnly = true)
    public Page<SubscriptionDto> getCustomerSubscriptions(UUID customerId, Pageable pageable) {
        return subscriptionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable).map(this::toDto);
    }

    private Instant calculateNextBillingDate(SubscriptionFrequency freq) {
        Instant now = Instant.now();
        return switch (freq) {
            case WEEKLY -> now.plus(7, ChronoUnit.DAYS);
            case BIWEEKLY -> now.plus(14, ChronoUnit.DAYS);
            case MONTHLY -> now.plus(30, ChronoUnit.DAYS);
            case QUARTERLY -> now.plus(90, ChronoUnit.DAYS);
            case SEMI_ANNUAL -> now.plus(180, ChronoUnit.DAYS);
            case ANNUAL -> now.plus(365, ChronoUnit.DAYS);
        };
    }

    private SubscriptionDto toDto(CustomerSubscription s) {
        return SubscriptionDto.builder()
                .id(s.getId())
                .subscriptionNumber(s.getSubscriptionNumber())
                .customerId(s.getCustomer().getId())
                .planName(s.getPlan().getName())
                .frequency(s.getPlan().getFrequency())
                .variantId(s.getVariant().getId())
                .variantSku(s.getVariant().getSku())
                .productTitle(s.getVariant().getProduct().getTitle())
                .quantity(s.getQuantity())
                .recurringPrice(s.getRecurringPrice())
                .status(s.getStatus())
                .nextBillingDate(s.getNextBillingDate())
                .lastBilledAt(s.getLastBilledAt())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
