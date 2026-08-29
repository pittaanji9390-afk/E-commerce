package com.marketplace.seller.service;

import com.marketplace.identity.domain.Role;
import com.marketplace.identity.domain.User;
import com.marketplace.identity.repository.RoleRepository;
import com.marketplace.identity.repository.UserRepository;
import com.marketplace.security.RoleEnum;
import com.marketplace.seller.domain.*;
import com.marketplace.seller.dto.*;
import com.marketplace.seller.repository.SellerRepository;
import com.marketplace.seller.repository.SellerVerificationRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final SellerVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public SellerProfileDto onboardSeller(UUID userId, SellerOnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (sellerRepository.existsById(userId)) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "User is already registered as a seller.");
        }

        String slug = request.getStoreSlug().trim().toLowerCase().replaceAll("[^a-z0-9-]", "-");
        if (sellerRepository.existsByStoreSlug(slug)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Store slug '" + slug + "' is already taken.");
        }

        // Grant SELLER role
        Role sellerRole = roleRepository.findByName(RoleEnum.ROLE_SELLER.name())
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleEnum.ROLE_SELLER.name()).description("Seller").build()));
        user.addRole(sellerRole);
        userRepository.save(user);

        Seller seller = Seller.builder()
                .user(user)
                .businessName(request.getBusinessName().trim())
                .storeSlug(slug)
                .displayName(request.getDisplayName().trim())
                .description(request.getDescription())
                .contactEmail(request.getContactEmail().trim().toLowerCase())
                .contactPhone(request.getContactPhone().trim())
                .status(SellerStatus.UNDER_REVIEW)
                .commissionRateOverride(BigDecimal.valueOf(10.00))
                .build();

        Seller savedSeller = sellerRepository.save(seller);

        // Record KYC Verification
        SellerVerification verification = SellerVerification.builder()
                .seller(savedSeller)
                .legalBusinessName(request.getLegalBusinessName().trim())
                .taxIdEin(request.getTaxIdEin().trim())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber().trim())
                .documentType(request.getDocumentType().trim())
                .documentUrl(request.getDocumentUrl().trim())
                .status(VerificationStatus.PENDING)
                .build();
        verificationRepository.save(verification);

        log.info("Seller onboarded: [id={}, slug={}, status={}]", savedSeller.getId(), savedSeller.getStoreSlug(), savedSeller.getStatus());
        return toDto(savedSeller);
    }

    @Transactional(readOnly = true)
    public SellerProfileDto getProfile(UUID sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));
        return toDto(seller);
    }

    @Transactional(readOnly = true)
    public SellerProfileDto getProfileBySlug(String slug) {
        Seller seller = sellerRepository.findByStoreSlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "slug", slug));
        return toDto(seller);
    }

    @Transactional
    public SellerProfileDto updateProfile(UUID sellerId, UpdateSellerProfileRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

        seller.setDisplayName(request.getDisplayName().trim());
        seller.setDescription(request.getDescription());
        seller.setContactEmail(request.getContactEmail().trim().toLowerCase());
        seller.setContactPhone(request.getContactPhone().trim());
        if (request.getLogoUrl() != null) seller.setLogoUrl(request.getLogoUrl());
        if (request.getBannerUrl() != null) seller.setBannerUrl(request.getBannerUrl());

        Seller saved = sellerRepository.save(seller);
        return toDto(saved);
    }

    @Transactional
    public void approveSellerKyc(UUID sellerId, UUID adminId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", adminId));

        seller.setStatus(SellerStatus.APPROVED);
        sellerRepository.save(seller);

        verificationRepository.findBySellerId(sellerId).forEach(v -> {
            v.setStatus(VerificationStatus.APPROVED);
            v.setReviewedBy(admin);
            v.setReviewedAt(Instant.now());
            verificationRepository.save(v);
        });

        log.info("Admin {} approved KYC for seller {}", adminId, sellerId);
    }

    private SellerProfileDto toDto(Seller s) {
        return SellerProfileDto.builder()
                .id(s.getId())
                .businessName(s.getBusinessName())
                .storeSlug(s.getStoreSlug())
                .displayName(s.getDisplayName())
                .description(s.getDescription())
                .logoUrl(s.getLogoUrl())
                .bannerUrl(s.getBannerUrl())
                .contactEmail(s.getContactEmail())
                .contactPhone(s.getContactPhone())
                .status(s.getStatus())
                .commissionRate(s.getCommissionRateOverride())
                .ratingAverage(s.getRatingAverage())
                .ratingCount(s.getRatingCount())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
