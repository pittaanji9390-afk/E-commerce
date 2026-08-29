package com.marketplace.coupon.service;

import com.marketplace.coupon.domain.Coupon;
import com.marketplace.coupon.domain.CouponRedemption;
import com.marketplace.coupon.domain.DiscountType;
import com.marketplace.coupon.dto.ApplyCouponRequest;
import com.marketplace.coupon.dto.CouponDiscountResult;
import com.marketplace.coupon.dto.CouponDto;
import com.marketplace.coupon.dto.CreateCouponRequest;
import com.marketplace.coupon.repository.CouponRedemptionRepository;
import com.marketplace.coupon.repository.CouponRepository;
import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.repository.CustomerRepository;
import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.repository.SellerRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final SellerRepository sellerRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public CouponDto createCoupon(CreateCouponRequest request) {
        String cleanCode = request.getCode().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(cleanCode)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Coupon code '" + cleanCode + "' already exists.");
        }

        Seller seller = null;
        if (request.getSellerId() != null) {
            seller = sellerRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", request.getSellerId()));
        }

        Coupon coupon = Coupon.builder()
                .seller(seller)
                .code(cleanCode)
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minimumCartValue(request.getMinimumCartValue() != null ? request.getMinimumCartValue() : BigDecimal.ZERO)
                .maxDiscountCap(request.getMaxDiscountCap())
                .usageLimit(request.getUsageLimit() != null ? request.getUsageLimit() : 100)
                .perUserLimit(request.getPerUserLimit() != null ? request.getPerUserLimit() : 1)
                .startsAt(request.getStartsAt())
                .expiresAt(request.getExpiresAt())
                .active(true)
                .build();

        Coupon saved = couponRepository.save(coupon);
        log.info("Created coupon: [code={}, type={}, val={}]", saved.getCode(), saved.getDiscountType(), saved.getDiscountValue());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public CouponDiscountResult validateAndCalculateDiscount(UUID customerId, ApplyCouponRequest request) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(request.getCode().trim())
                .orElseThrow(() -> new BusinessRuleException(ErrorCode.COUPON_INVALID_OR_EXPIRED, "Coupon code is invalid."));

        if (!coupon.isValidNow()) {
            throw new BusinessRuleException(ErrorCode.COUPON_INVALID_OR_EXPIRED, "Coupon is expired or usage limit exceeded.");
        }

        if (request.getCartSubtotal().compareTo(coupon.getMinimumCartValue()) < 0) {
            throw new BusinessRuleException(ErrorCode.COUPON_INVALID_OR_EXPIRED,
                    "Minimum order value of $" + coupon.getMinimumCartValue() + " required to use this coupon.");
        }

        if (customerId != null) {
            long usedCount = redemptionRepository.countByCouponIdAndCustomerId(coupon.getId(), customerId);
            if (usedCount >= coupon.getPerUserLimit()) {
                throw new BusinessRuleException(ErrorCode.COUPON_INVALID_OR_EXPIRED, "You have already reached the maximum usage limit for this coupon.");
            }
        }

        BigDecimal discountAmount;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = request.getCartSubtotal().multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
            if (coupon.getMaxDiscountCap() != null && discountAmount.compareTo(coupon.getMaxDiscountCap()) > 0) {
                discountAmount = coupon.getMaxDiscountCap();
            }
        } else {
            discountAmount = coupon.getDiscountValue().min(request.getCartSubtotal());
        }

        BigDecimal finalSubtotal = request.getCartSubtotal().subtract(discountAmount).max(BigDecimal.ZERO);

        return CouponDiscountResult.builder()
                .couponId(coupon.getId())
                .code(coupon.getCode())
                .discountAmount(discountAmount)
                .finalSubtotal(finalSubtotal)
                .build();
    }

    /**
     * Atomically redeems a coupon to prevent race conditions during concurrent checkouts.
     */
    @Transactional
    public void recordRedemption(UUID couponId, UUID customerId, UUID orderId, BigDecimal discountApplied) {
        int updated = couponRepository.incrementUsageAtomic(couponId);
        if (updated == 0) {
            throw new BusinessRuleException(ErrorCode.COUPON_INVALID_OR_EXPIRED, "Coupon usage limit was reached during checkout processing.");
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", couponId));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        CouponRedemption redemption = CouponRedemption.builder()
                .coupon(coupon)
                .customer(customer)
                .orderId(orderId)
                .discountApplied(discountApplied)
                .redeemedAt(Instant.now())
                .build();
        redemptionRepository.save(redemption);

        log.info("Redeemed coupon: [code={}, orderId={}, discount={}]", coupon.getCode(), orderId, discountApplied);
    }

    private CouponDto toDto(Coupon c) {
        return CouponDto.builder()
                .id(c.getId())
                .sellerId(c.getSeller() != null ? c.getSeller().getId() : null)
                .sellerName(c.getSeller() != null ? c.getSeller().getDisplayName() : "Platform Global")
                .code(c.getCode())
                .discountType(c.getDiscountType())
                .discountValue(c.getDiscountValue())
                .minimumCartValue(c.getMinimumCartValue())
                .maxDiscountCap(c.getMaxDiscountCap())
                .usageLimit(c.getUsageLimit())
                .usedCount(c.getUsedCount())
                .perUserLimit(c.getPerUserLimit())
                .active(c.isActive())
                .startsAt(c.getStartsAt())
                .expiresAt(c.getExpiresAt())
                .build();
    }
}
