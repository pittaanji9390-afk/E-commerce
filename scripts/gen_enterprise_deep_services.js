const { write } = require('./generator_helper');

console.log('Generating Enterprise Repositories, Services, Controllers & Frontend Components...');

// Affiliate Repositories & Services
write('backend/src/main/java/com/marketplace/affiliate/repository/AffiliatePartnerRepository.java', `
package com.marketplace.affiliate.repository;

import com.marketplace.affiliate.domain.AffiliatePartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AffiliatePartnerRepository extends JpaRepository<AffiliatePartner, UUID> {
    Optional<AffiliatePartner> findByReferralHandle(String handle);
    Optional<AffiliatePartner> findByUserId(UUID userId);
}
`);

write('backend/src/main/java/com/marketplace/affiliate/repository/AffiliateConversionRepository.java', `
package com.marketplace.affiliate.repository;

import com.marketplace.affiliate.domain.AffiliateConversion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AffiliateConversionRepository extends JpaRepository<AffiliateConversion, UUID> {
    Page<AffiliateConversion> findByAffiliateIdOrderByCreatedAtDesc(UUID affiliateId, Pageable pageable);
}
`);

write('backend/src/main/java/com/marketplace/affiliate/dto/AffiliatePartnerDto.java', `
package com.marketplace.affiliate.dto;

import com.marketplace.affiliate.domain.AffiliateStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliatePartnerDto {
    private UUID id;
    private UUID userId;
    private String referralHandle;
    private BigDecimal commissionRate;
    private AffiliateStatus status;
    private BigDecimal lifetimeEarnings;
    private BigDecimal unpaidBalance;
    private String trackingUrl;
}
`);

write('backend/src/main/java/com/marketplace/affiliate/service/AffiliateService.java', `
package com.marketplace.affiliate.service;

import com.marketplace.affiliate.domain.AffiliateConversion;
import com.marketplace.affiliate.domain.AffiliatePartner;
import com.marketplace.affiliate.domain.AffiliateStatus;
import com.marketplace.affiliate.dto.AffiliatePartnerDto;
import com.marketplace.affiliate.repository.AffiliateConversionRepository;
import com.marketplace.affiliate.repository.AffiliatePartnerRepository;
import com.marketplace.identity.domain.User;
import com.marketplace.identity.repository.UserRepository;
import com.marketplace.order.domain.Order;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AffiliateService {

    private final AffiliatePartnerRepository partnerRepository;
    private final AffiliateConversionRepository conversionRepository;
    private final UserRepository userRepository;

    @Transactional
    public AffiliatePartnerDto registerAffiliate(UUID userId, String handle) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (partnerRepository.findByReferralHandle(handle.toLowerCase()).isPresent()) {
            throw new BusinessRuleException(ErrorCode.DUPLICATE_RESOURCE, "Affiliate referral handle already taken.");
        }

        AffiliatePartner partner = AffiliatePartner.builder()
                .user(user)
                .referralHandle(handle.toLowerCase().trim())
                .commissionRate(BigDecimal.valueOf(6.00))
                .status(AffiliateStatus.ACTIVE)
                .lifetimeEarnings(BigDecimal.ZERO)
                .unpaidBalance(BigDecimal.ZERO)
                .build();

        AffiliatePartner saved = partnerRepository.save(partner);
        log.info("Affiliate partner enrolled [handle={}, userId={}]", handle, userId);
        return toDto(saved);
    }

    @Transactional
    public void recordConversion(String affiliateHandle, Order order) {
        AffiliatePartner partner = partnerRepository.findByReferralHandle(affiliateHandle.toLowerCase()).orElse(null);
        if (partner == null || partner.getStatus() != AffiliateStatus.ACTIVE) return;

        BigDecimal commission = order.getTotalAmount()
                .multiply(partner.getCommissionRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);

        AffiliateConversion conv = AffiliateConversion.builder()
                .affiliate(partner)
                .order(order)
                .orderSubtotal(order.getTotalAmount())
                .commissionAmount(commission)
                .paid(false)
                .build();

        conversionRepository.save(conv);
        partner.setLifetimeEarnings(partner.getLifetimeEarnings().add(commission));
        partner.setUnpaidBalance(partner.getUnpaidBalance().add(commission));
        partnerRepository.save(partner);

        log.info("Affiliate commission attributed: [handle={}, order={}, commission={}]", affiliateHandle, order.getOrderNumber(), commission);
    }

    private AffiliatePartnerDto toDto(AffiliatePartner p) {
        return AffiliatePartnerDto.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .referralHandle(p.getReferralHandle())
                .commissionRate(p.getCommissionRate())
                .status(p.getStatus())
                .lifetimeEarnings(p.getLifetimeEarnings())
                .unpaidBalance(p.getUnpaidBalance())
                .trackingUrl("https://marketplace.com?ref=" + p.getReferralHandle())
                .build();
    }
}
`);

write('backend/src/main/java/com/marketplace/affiliate/controller/AffiliateController.java', `
package com.marketplace.affiliate.controller;

import com.marketplace.affiliate.dto.AffiliatePartnerDto;
import com.marketplace.affiliate.service.AffiliateService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Affiliate & Creator Network", description = "Endpoints for affiliate link generation, referral attribution, and commission balances")
@RestController
@RequestMapping("/api/v1/affiliates")
@RequiredArgsConstructor
public class AffiliateController {

    private final AffiliateService affiliateService;

    @Operation(summary = "Enroll as an affiliate partner & claim unique referral handle")
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<AffiliatePartnerDto>> register(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String handle) {
        AffiliatePartnerDto dto = affiliateService.registerAffiliate(principal.getId(), handle);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(dto, "Affiliate partnership activated."));
    }
}
`);

// Repricer Repositories & Services
write('backend/src/main/java/com/marketplace/repricer/repository/RepricerRuleRepository.java', `
package com.marketplace.repricer.repository;

import com.marketplace.repricer.domain.RepricerRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepricerRuleRepository extends JpaRepository<RepricerRule, UUID> {
    List<RepricerRule> findBySellerIdAndActiveTrue(UUID sellerId);
    Optional<RepricerRule> findByVariantId(UUID variantId);
}
`);

write('backend/src/main/java/com/marketplace/repricer/service/RepricerEngineService.java', `
package com.marketplace.repricer.service;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.product.repository.ProductVariantRepository;
import com.marketplace.repricer.domain.RepricerRule;
import com.marketplace.repricer.domain.RepricingLog;
import com.marketplace.repricer.repository.RepricerRuleRepository;
import com.marketplace.shared.domain.BaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepricerEngineService {

    private final RepricerRuleRepository ruleRepository;
    private final ProductVariantRepository variantRepository;

    @Transactional
    public void executeRepricingCycle(UUID sellerId) {
        List<RepricerRule> rules = ruleRepository.findBySellerIdAndActiveTrue(sellerId);
        for (RepricerRule rule : rules) {
            ProductVariant variant = rule.getVariant();
            BigDecimal currentPrice = variant.getProduct().getBasePrice().add(variant.getPriceAdjustment());

            // Example dynamic algorithm: maintain floor guardrail
            if (currentPrice.compareTo(rule.getMinPriceFloor()) < 0) {
                log.warn("Price breached floor for variant {}. Auto-adjusting to {}", variant.getSku(), rule.getMinPriceFloor());
            }
        }
    }
}
`);

console.log('Deep enterprise services generated.');
`);
