const { write } = require('./generator_helper');

console.log('Generating Loyalty, Advertising, Fraud & CMS Domains...');

// ----------------------------------------------------
// 1. LOYALTY & GIFT CARDS
// ----------------------------------------------------
write('backend/src/main/java/com/marketplace/loyalty/domain/LoyaltyTier.java', `
package com.marketplace.loyalty.domain;

public enum LoyaltyTier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    DIAMOND
}
`);

write('backend/src/main/java/com/marketplace/loyalty/domain/LoyaltyAccount.java', `
package com.marketplace.loyalty.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loyalty_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyAccount extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Customer customer;

    @Column(name = "current_points_balance", nullable = false)
    @Builder.Default
    private int currentPointsBalance = 0;

    @Column(name = "lifetime_points_earned", nullable = false)
    @Builder.Default
    private int lifetimePointsEarned = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", length = 30, nullable = false)
    @Builder.Default
    private LoyaltyTier tier = LoyaltyTier.BRONZE;

    @Column(name = "referral_code", nullable = false, unique = true, length = 30)
    private String referralCode;
}
`);

write('backend/src/main/java/com/marketplace/loyalty/domain/GiftCard.java', `
package com.marketplace.loyalty.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "gift_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCard extends AuditableEntity {

    @Column(name = "card_code", nullable = false, unique = true, length = 30)
    private String cardCode;

    @Column(name = "pin_hash", nullable = false)
    private String pinHash;

    @Column(name = "initial_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal initialBalance;

    @Column(name = "current_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchased_by_customer_id")
    private Customer purchasedBy;

    @Column(name = "recipient_email", length = 150)
    private String recipientEmail;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "expires_at")
    private Instant expiresAt;
}
`);

write('backend/src/main/java/com/marketplace/loyalty/repository/LoyaltyAccountRepository.java', `
package com.marketplace.loyalty.repository;

import com.marketplace.loyalty.domain.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, UUID> {
    Optional<LoyaltyAccount> findByReferralCode(String referralCode);
}
`);

write('backend/src/main/java/com/marketplace/loyalty/repository/GiftCardRepository.java', `
package com.marketplace.loyalty.repository;

import com.marketplace.loyalty.domain.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, UUID> {
    Optional<GiftCard> findByCardCodeAndActiveTrue(String cardCode);
}
`);

write('backend/src/main/java/com/marketplace/loyalty/dto/LoyaltyAccountDto.java', `
package com.marketplace.loyalty.dto;

import com.marketplace.loyalty.domain.LoyaltyTier;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyAccountDto {
    private UUID customerId;
    private int currentPointsBalance;
    private int lifetimePointsEarned;
    private LoyaltyTier tier;
    private String referralCode;
    private double pointCashValueUsd;
}
`);

write('backend/src/main/java/com/marketplace/loyalty/service/LoyaltyService.java', `
package com.marketplace.loyalty.service;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.loyalty.domain.LoyaltyAccount;
import com.marketplace.loyalty.domain.LoyaltyTier;
import com.marketplace.loyalty.dto.LoyaltyAccountDto;
import com.marketplace.loyalty.repository.LoyaltyAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final CustomerService customerService;

    @Transactional
    public LoyaltyAccountDto getOrCreateAccount(UUID customerId) {
        return accountRepository.findById(customerId)
                .map(this::toDto)
                .orElseGet(() -> {
                    Customer customer = customerService.getOrCreateCustomer(customerId);
                    String refCode = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    LoyaltyAccount account = LoyaltyAccount.builder()
                            .customer(customer)
                            .currentPointsBalance(100) // Welcome bonus
                            .lifetimePointsEarned(100)
                            .tier(LoyaltyTier.BRONZE)
                            .referralCode(refCode)
                            .build();
                    return toDto(accountRepository.save(account));
                });
    }

    @Transactional
    public void awardPoints(UUID customerId, int points, String reason) {
        LoyaltyAccount account = accountRepository.findById(customerId).orElse(null);
        if (account != null) {
            account.setCurrentPointsBalance(account.getCurrentPointsBalance() + points);
            account.setLifetimePointsEarned(account.getLifetimePointsEarned() + points);
            updateTier(account);
            accountRepository.save(account);
            log.info("Awarded {} loyalty points to customer {}. Reason: {}", points, customerId, reason);
        }
    }

    private void updateTier(LoyaltyAccount a) {
        int pts = a.getLifetimePointsEarned();
        if (pts >= 10000) a.setTier(LoyaltyTier.DIAMOND);
        else if (pts >= 5000) a.setTier(LoyaltyTier.PLATINUM);
        else if (pts >= 2000) a.setTier(LoyaltyTier.GOLD);
        else if (pts >= 500) a.setTier(LoyaltyTier.SILVER);
        else a.setTier(LoyaltyTier.BRONZE);
    }

    private LoyaltyAccountDto toDto(LoyaltyAccount a) {
        return LoyaltyAccountDto.builder()
                .customerId(a.getCustomer().getId())
                .currentPointsBalance(a.getCurrentPointsBalance())
                .lifetimePointsEarned(a.getLifetimePointsEarned())
                .tier(a.getTier())
                .referralCode(a.getReferralCode())
                .pointCashValueUsd(a.getCurrentPointsBalance() * 0.01) // 100 pts = $1.00
                .build();
    }
}
`);

write('backend/src/main/java/com/marketplace/loyalty/controller/LoyaltyController.java', `
package com.marketplace.loyalty.controller;

import com.marketplace.loyalty.dto.LoyaltyAccountDto;
import com.marketplace.loyalty.service.LoyaltyService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customer Loyalty & Rewards", description = "Endpoints for point balances, tiers, and referral bonuses")
@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @Operation(summary = "Get current customer loyalty tier and point balance")
    @GetMapping("/my-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<LoyaltyAccountDto>> getMyAccount(@AuthenticationPrincipal UserPrincipal principal) {
        LoyaltyAccountDto dto = loyaltyService.getOrCreateAccount(principal.getId());
        return ResponseEntity.ok(Result.ok(dto));
    }
}
`);

// ----------------------------------------------------
// 2. SELLER ADVERTISING & SPONSORED PRODUCTS
// ----------------------------------------------------
write('backend/src/main/java/com/marketplace/advertising/domain/CampaignStatus.java', `
package com.marketplace.advertising.domain;

public enum CampaignStatus {
    ACTIVE,
    PAUSED,
    BUDGET_EXHAUSTED,
    ARCHIVED
}
`);

write('backend/src/main/java/com/marketplace/advertising/domain/AdCampaign.java', `
package com.marketplace.advertising.domain;

import com.marketplace.product.domain.Product;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ad_campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdCampaign extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promoted_product_id", nullable = false)
    private Product promotedProduct;

    @Column(name = "daily_budget", precision = 15, scale = 2, nullable = false)
    private BigDecimal dailyBudget;

    @Column(name = "cpc_bid", precision = 15, scale = 2, nullable = false)
    private BigDecimal cpcBid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.ACTIVE;

    @Column(name = "total_impressions", nullable = false)
    @Builder.Default
    private long totalImpressions = 0;

    @Column(name = "total_clicks", nullable = false)
    @Builder.Default
    private long totalClicks = 0;

    @Column(name = "total_spend", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalSpend = BigDecimal.ZERO;
}
`);

write('backend/src/main/java/com/marketplace/advertising/repository/AdCampaignRepository.java', `
package com.marketplace.advertising.repository;

import com.marketplace.advertising.domain.AdCampaign;
import com.marketplace.advertising.domain.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdCampaignRepository extends JpaRepository<AdCampaign, UUID> {
    Page<AdCampaign> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);
    List<AdCampaign> findByStatus(CampaignStatus status);
}
`);

write('backend/src/main/java/com/marketplace/advertising/dto/AdCampaignDto.java', `
package com.marketplace.advertising.dto;

import com.marketplace.advertising.domain.CampaignStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdCampaignDto {
    private UUID id;
    private String name;
    private UUID sellerId;
    private UUID promotedProductId;
    private String promotedProductTitle;
    private BigDecimal dailyBudget;
    private BigDecimal cpcBid;
    private CampaignStatus status;
    private long totalImpressions;
    private long totalClicks;
    private BigDecimal totalSpend;
    private double clickThroughRate;
    private Instant createdAt;
}
`);

write('backend/src/main/java/com/marketplace/advertising/service/AdvertisingService.java', `
package com.marketplace.advertising.service;

import com.marketplace.advertising.domain.AdCampaign;
import com.marketplace.advertising.domain.CampaignStatus;
import com.marketplace.advertising.dto.AdCampaignDto;
import com.marketplace.advertising.repository.AdCampaignRepository;
import com.marketplace.product.domain.Product;
import com.marketplace.product.repository.ProductRepository;
import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.repository.SellerRepository;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvertisingService {

    private final AdCampaignRepository campaignRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public AdCampaignDto createCampaign(UUID sellerId, String name, UUID productId, BigDecimal dailyBudget, BigDecimal cpcBid) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        AdCampaign campaign = AdCampaign.builder()
                .seller(seller)
                .name(name)
                .promotedProduct(product)
                .dailyBudget(dailyBudget)
                .cpcBid(cpcBid)
                .status(CampaignStatus.ACTIVE)
                .build();

        AdCampaign saved = campaignRepository.save(campaign);
        log.info("Sponsored Ad Campaign launched [id={}, seller={}, product={}]", saved.getId(), seller.getDisplayName(), product.getTitle());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<AdCampaignDto> getSellerCampaigns(UUID sellerId, Pageable pageable) {
        return campaignRepository.findBySellerIdOrderByCreatedAtDesc(sellerId, pageable).map(this::toDto);
    }

    private AdCampaignDto toDto(AdCampaign a) {
        double ctr = a.getTotalImpressions() > 0
                ? ((double) a.getTotalClicks() / a.getTotalImpressions()) * 100.0
                : 0.0;

        return AdCampaignDto.builder()
                .id(a.getId())
                .name(a.getName())
                .sellerId(a.getSeller().getId())
                .promotedProductId(a.getPromotedProduct().getId())
                .promotedProductTitle(a.getPromotedProduct().getTitle())
                .dailyBudget(a.getDailyBudget())
                .cpcBid(a.getCpcBid())
                .status(a.getStatus())
                .totalImpressions(a.getTotalImpressions())
                .totalClicks(a.getTotalClicks())
                .totalSpend(a.getTotalSpend())
                .clickThroughRate(Math.round(ctr * 100.0) / 100.0)
                .createdAt(a.getCreatedAt())
                .build();
    }
}
`);

write('backend/src/main/java/com/marketplace/advertising/controller/AdvertisingController.java', `
package com.marketplace.advertising.controller;

import com.marketplace.advertising.dto.AdCampaignDto;
import com.marketplace.advertising.service.AdvertisingService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.PagedResult;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Seller Advertising & CPC Sponsored Ads", description = "Endpoints for sponsored product bidding, impression auctions, and campaign analytics")
@RestController
@RequestMapping("/api/v1/advertising/campaigns")
@RequiredArgsConstructor
public class AdvertisingController {

    private final AdvertisingService adService;

    @Operation(summary = "Create a new sponsored product ad campaign (Seller)")
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<AdCampaignDto>> createCampaign(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String name,
            @RequestParam UUID productId,
            @RequestParam BigDecimal dailyBudget,
            @RequestParam BigDecimal cpcBid) {
        AdCampaignDto campaign = adService.createCampaign(principal.getId(), name, productId, dailyBudget, cpcBid);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(campaign, "Ad Campaign launched."));
    }

    @Operation(summary = "Get seller ad campaigns with CTR and spend analytics")
    @GetMapping("/my-campaigns")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<PagedResult<AdCampaignDto>> getMyCampaigns(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdCampaignDto> page = adService.getSellerCampaigns(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }
}
`);

// ----------------------------------------------------
// 3. FRAUD & RISK SCORING
// ----------------------------------------------------
write('backend/src/main/java/com/marketplace/fraud/domain/RiskLevel.java', `
package com.marketplace.fraud.domain;

public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
`);

write('backend/src/main/java/com/marketplace/fraud/domain/RiskEvaluation.java', `
package com.marketplace.fraud.domain;

import com.marketplace.order.domain.Order;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "risk_evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskEvaluation extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "risk_score", nullable = false)
    private int riskScore; // 0 - 100

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 30, nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "device_fingerprint", length = 150)
    private String deviceFingerprint;

    @Column(name = "flags_json", columnDefinition = "TEXT")
    private String flagsJson;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
`);

write('backend/src/main/java/com/marketplace/fraud/repository/RiskEvaluationRepository.java', `
package com.marketplace.fraud.repository;

import com.marketplace.fraud.domain.RiskEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiskEvaluationRepository extends JpaRepository<RiskEvaluation, UUID> {
    Optional<RiskEvaluation> findByOrderId(UUID orderId);
}
`);

write('backend/src/main/java/com/marketplace/fraud/service/FraudDetectionService.java', `
package com.marketplace.fraud.service;

import com.marketplace.fraud.domain.RiskEvaluation;
import com.marketplace.fraud.domain.RiskLevel;
import com.marketplace.fraud.repository.RiskEvaluationRepository;
import com.marketplace.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final RiskEvaluationRepository riskRepository;

    @Transactional
    public RiskEvaluation evaluateOrderRisk(Order order, String ipAddress, String fingerprint) {
        int score = 10; // baseline safe

        if (order.getTotalAmount().compareTo(BigDecimal.valueOf(2000.00)) > 0) {
            score += 25;
        }

        RiskLevel level;
        if (score > 75) level = RiskLevel.CRITICAL;
        else if (score > 50) level = RiskLevel.HIGH;
        else if (score > 25) level = RiskLevel.MEDIUM;
        else level = RiskLevel.LOW;

        RiskEvaluation evaluation = RiskEvaluation.builder()
                .order(order)
                .riskScore(score)
                .riskLevel(level)
                .ipAddress(ipAddress)
                .deviceFingerprint(fingerprint)
                .flagsJson("[\"AUTO_EVALUATED\"]")
                .build();

        log.info("Risk assessment complete for order {}: [score={}, level={}]", order.getOrderNumber(), score, level);
        return riskRepository.save(evaluation);
    }
}
`);

// ----------------------------------------------------
// 4. CMS & MARKETING ENGINE
// ----------------------------------------------------
write('backend/src/main/java/com/marketplace/cms/domain/Banner.java', `
package com.marketplace.cms.domain;

import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cms_banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner extends AuditableEntity {

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "cta_link", nullable = false, length = 255)
    private String ctaLink;

    @Column(name = "cta_text", nullable = false, length = 50)
    private String ctaText;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
`);

write('backend/src/main/java/com/marketplace/cms/repository/BannerRepository.java', `
package com.marketplace.cms.repository;

import com.marketplace.cms.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BannerRepository extends JpaRepository<Banner, UUID> {
    List<Banner> findByActiveTrueOrderByDisplayOrderAsc();
}
`);

write('backend/src/main/java/com/marketplace/cms/controller/CmsController.java', `
package com.marketplace.cms.controller;

import com.marketplace.cms.domain.Banner;
import com.marketplace.cms.repository.BannerRepository;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "CMS & Marketing Banners", description = "Endpoints for storefront hero banners and content promotions")
@RestController
@RequestMapping("/api/v1/cms")
@RequiredArgsConstructor
public class CmsController {

    private final BannerRepository bannerRepository;

    @Operation(summary = "Get active storefront hero banners")
    @GetMapping("/banners")
    public ResponseEntity<Result<List<Banner>>> getActiveBanners() {
        List<Banner> banners = bannerRepository.findByActiveTrueOrderByDisplayOrderAsc();
        return ResponseEntity.ok(Result.ok(banners));
    }
}
`);

console.log('Loyalty, Advertising, Fraud & CMS Domains Generated.');
`);
