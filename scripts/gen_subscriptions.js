const { write } = require('./generator_helper');

console.log('Generating Subscriptions Domain...');

// Entities
write('backend/src/main/java/com/marketplace/subscription/domain/SubscriptionFrequency.java', `
package com.marketplace.subscription.domain;

public enum SubscriptionFrequency {
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    QUARTERLY,
    SEMI_ANNUAL,
    ANNUAL
}
`);

write('backend/src/main/java/com/marketplace/subscription/domain/SubscriptionStatus.java', `
package com.marketplace.subscription.domain;

public enum SubscriptionStatus {
    ACTIVE,
    PAUSED,
    PAST_DUE,
    CANCELLED,
    EXPIRED
}
`);

write('backend/src/main/java/com/marketplace/subscription/domain/SubscriptionPlan.java', `
package com.marketplace.subscription.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", length = 30, nullable = false)
    private SubscriptionFrequency frequency;

    @Column(name = "discount_percentage", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.valueOf(10.00);

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
`);

write('backend/src/main/java/com/marketplace/subscription/domain/CustomerSubscription.java', `
package com.marketplace.subscription.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.domain.CustomerAddress;
import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "customer_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSubscription extends AuditableEntity {

    @Column(name = "subscription_number", nullable = false, unique = true, length = 50)
    private String subscriptionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private CustomerAddress shippingAddress;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private int quantity = 1;

    @Column(name = "recurring_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal recurringPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "next_billing_date", nullable = false)
    private Instant nextBillingDate;

    @Column(name = "last_billed_at")
    private Instant lastBilledAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;
}
`);

write('backend/src/main/java/com/marketplace/subscription/domain/SubscriptionInvoice.java', `
package com.marketplace.subscription.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "subscription_invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionInvoice extends BaseEntity {

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private CustomerSubscription subscription;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "is_paid", nullable = false)
    @Builder.Default
    private boolean paid = false;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "dunning_attempt_count", nullable = false)
    @Builder.Default
    private int dunningAttemptCount = 0;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
`);

// Repositories
write('backend/src/main/java/com/marketplace/subscription/repository/SubscriptionPlanRepository.java', `
package com.marketplace.subscription.repository;

import com.marketplace.subscription.domain.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    List<SubscriptionPlan> findByVariantIdAndActiveTrue(UUID variantId);
}
`);

write('backend/src/main/java/com/marketplace/subscription/repository/CustomerSubscriptionRepository.java', `
package com.marketplace.subscription.repository;

import com.marketplace.subscription.domain.CustomerSubscription;
import com.marketplace.subscription.domain.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerSubscriptionRepository extends JpaRepository<CustomerSubscription, UUID> {
    Optional<CustomerSubscription> findBySubscriptionNumber(String subscriptionNumber);
    Page<CustomerSubscription> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
    List<CustomerSubscription> findByStatusAndNextBillingDateLessThanEqual(SubscriptionStatus status, Instant date);
}
`);

write('backend/src/main/java/com/marketplace/subscription/repository/SubscriptionInvoiceRepository.java', `
package com.marketplace.subscription.repository;

import com.marketplace.subscription.domain.SubscriptionInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionInvoiceRepository extends JpaRepository<SubscriptionInvoice, UUID> {
    List<SubscriptionInvoice> findBySubscriptionIdOrderByCreatedAtDesc(UUID subscriptionId);
}
`);

// DTOs
write('backend/src/main/java/com/marketplace/subscription/dto/SubscriptionDto.java', `
package com.marketplace.subscription.dto;

import com.marketplace.subscription.domain.SubscriptionFrequency;
import com.marketplace.subscription.domain.SubscriptionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {
    private UUID id;
    private String subscriptionNumber;
    private UUID customerId;
    private String planName;
    private SubscriptionFrequency frequency;
    private UUID variantId;
    private String variantSku;
    private String productTitle;
    private int quantity;
    private BigDecimal recurringPrice;
    private SubscriptionStatus status;
    private Instant nextBillingDate;
    private Instant lastBilledAt;
    private Instant createdAt;
}
`);

write('backend/src/main/java/com/marketplace/subscription/dto/CreateSubscriptionRequest.java', `
package com.marketplace.subscription.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotNull(message = "Subscription plan ID is required")
    private UUID planId;

    @NotNull(message = "Shipping address ID is required")
    private UUID shippingAddressId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Builder.Default
    private int quantity = 1;
}
`);

// Service
write('backend/src/main/java/com/marketplace/subscription/service/SubscriptionService.java', `
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
`);

// Controller
write('backend/src/main/java/com/marketplace/subscription/controller/SubscriptionController.java', `
package com.marketplace.subscription.controller;

import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.PagedResult;
import com.marketplace.shared.response.Result;
import com.marketplace.subscription.dto.CreateSubscriptionRequest;
import com.marketplace.subscription.dto.SubscriptionDto;
import com.marketplace.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Subscriptions & Recurring Billing", description = "Endpoints for subscribe & save auto-delivery and interval plans")
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Subscribe & Save to a product variant")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<SubscriptionDto>> subscribe(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionDto sub = subscriptionService.subscribe(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(sub, "Subscription activated with recurring discount."));
    }

    @Operation(summary = "Get current buyer recurring subscriptions")
    @GetMapping("/my-subscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResult<SubscriptionDto>> getMySubscriptions(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SubscriptionDto> page = subscriptionService.getCustomerSubscriptions(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Pause an active auto-delivery subscription")
    @PatchMapping("/{subscriptionId}/pause")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<SubscriptionDto>> pause(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID subscriptionId) {
        SubscriptionDto sub = subscriptionService.pauseSubscription(principal.getId(), subscriptionId);
        return ResponseEntity.ok(Result.ok(sub, "Subscription paused."));
    }

    @Operation(summary = "Resume a paused subscription")
    @PatchMapping("/{subscriptionId}/resume")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<SubscriptionDto>> resume(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID subscriptionId) {
        SubscriptionDto sub = subscriptionService.resumeSubscription(principal.getId(), subscriptionId);
        return ResponseEntity.ok(Result.ok(sub, "Subscription resumed."));
    }
}
`);

// Frontend Subscriptions Page
write('frontend/src/features/account/CustomerSubscriptionsPage.tsx', `
import React, { useState } from 'react';
import { Calendar, Repeat, Pause, Play, Trash2, CheckCircle2 } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { PriceDisplay } from '@/components/ui/PriceDisplay';

interface MockSubscription {
  id: string;
  subscriptionNumber: string;
  productTitle: string;
  variant: string;
  frequency: string;
  recurringPrice: number;
  nextBilling: string;
  status: 'ACTIVE' | 'PAUSED';
  image: string;
}

const mockSubs: MockSubscription[] = [
  {
    id: 'sub-1',
    subscriptionNumber: 'SUB-1724910294-842',
    productTitle: 'Organic Colombian Dark Roast Whole Bean Coffee (2lb)',
    variant: '2lb Whole Bean',
    frequency: 'Monthly (Save 15%)',
    recurringPrice: 28.50,
    nextBilling: 'September 24, 2026',
    status: 'ACTIVE',
    image: 'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=200',
  },
];

export const CustomerSubscriptionsPage: React.FC = () => {
  const [subs, setSubs] = useState<MockSubscription[]>(mockSubs);

  const toggleStatus = (id: string) => {
    setSubs((prev) =>
      prev.map((s) =>
        s.id === id
          ? { ...s, status: s.status === 'ACTIVE' ? ('PAUSED' as const) : ('ACTIVE' as const) }
          : s
      )
    );
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Repeat className="w-6 h-6 text-primary-600" /> Subscribe & Save Management
          </h1>
          <p className="text-gray-500 text-sm mt-1">Manage automated interval reorders, skip upcoming shipments, and pause schedules.</p>
        </div>
      </div>

      <div className="space-y-6">
        {subs.map((sub) => (
          <div key={sub.id} className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm flex flex-col md:flex-row items-center justify-between gap-6">
            <div className="flex items-center gap-4">
              <img src={sub.image} alt={sub.productTitle} className="w-20 h-20 object-cover rounded-xl border border-gray-200" />
              <div>
                <span className="font-mono text-xs text-gray-400 block">{sub.subscriptionNumber}</span>
                <h4 className="font-bold text-gray-900 text-base mt-0.5">{sub.productTitle}</h4>
                <p className="text-xs text-gray-500 mt-1">{sub.variant} • <span className="text-primary-600 font-semibold">{sub.frequency}</span></p>
                <div className="flex items-center gap-4 mt-2">
                  <span className="text-sm font-bold text-gray-900"><PriceDisplay amount={sub.recurringPrice} /></span>
                  <Badge variant={sub.status === 'ACTIVE' ? 'success' : 'warning'}>{sub.status}</Badge>
                </div>
              </div>
            </div>

            <div className="flex items-center gap-3 w-full md:w-auto justify-end border-t md:border-t-0 pt-4 md:pt-0">
              <Button size="sm" variant="outline" onClick={() => toggleStatus(sub.id)}>
                {sub.status === 'ACTIVE' ? (
                  <>
                    <Pause className="w-3.5 h-3.5 mr-1" /> Pause Auto-Delivery
                  </>
                ) : (
                  <>
                    <Play className="w-3.5 h-3.5 mr-1" /> Resume Schedule
                  </>
                )}
              </Button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
`);

console.log('Subscriptions Domain Generated.');
`);
