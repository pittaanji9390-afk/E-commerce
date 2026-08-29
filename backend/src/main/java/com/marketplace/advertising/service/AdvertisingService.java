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
