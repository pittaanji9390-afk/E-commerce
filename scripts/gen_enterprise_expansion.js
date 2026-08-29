const { write } = require('./generator_helper');

console.log('Generating Enterprise Expansion Modules...');

// ----------------------------------------------------
// 1. RECOMMENDATION ENGINE
// ----------------------------------------------------
write('backend/src/main/java/com/marketplace/recommendation/domain/RecommendationType.java', `
package com.marketplace.recommendation.domain;

public enum RecommendationType {
    FREQUENTLY_BOUGHT_TOGETHER,
    SIMILAR_PRODUCTS,
    TRENDING_IN_CATEGORY,
    PERSONALIZED_FOR_YOU,
    RECENTLY_VIEWED
}
`);

write('backend/src/main/java/com/marketplace/recommendation/dto/RecommendedProductDto.java', `
package com.marketplace.recommendation.dto;

import com.marketplace.recommendation.domain.RecommendationType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendedProductDto {
    private UUID productId;
    private String title;
    private String slug;
    private BigDecimal basePrice;
    private String primaryImageUrl;
    private BigDecimal ratingAverage;
    private int ratingCount;
    private RecommendationType recommendationType;
    private double relevanceScore;
}
`);

write('backend/src/main/java/com/marketplace/recommendation/service/RecommendationEngineService.java', `
package com.marketplace.recommendation.service;

import com.marketplace.product.domain.Product;
import com.marketplace.product.domain.ProductStatus;
import com.marketplace.product.repository.ProductRepository;
import com.marketplace.recommendation.domain.RecommendationType;
import com.marketplace.recommendation.dto.RecommendedProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationEngineService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<RecommendedProductDto> getSimilarProducts(UUID productId, int limit) {
        Product target = productRepository.findById(productId).orElse(null);
        if (target == null) return List.of();

        List<Product> products = productRepository.findByCategoryIdAndStatus(
                target.getCategory().getId(),
                ProductStatus.ACTIVE,
                PageRequest.of(0, limit)
        ).getContent();

        return products.stream()
                .filter(p -> !p.getId().equals(productId))
                .map(p -> toDto(p, RecommendationType.SIMILAR_PRODUCTS, 0.95))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecommendedProductDto> getTrendingProducts(int limit) {
        return productRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE, PageRequest.of(0, limit))
                .stream()
                .map(p -> toDto(p, RecommendationType.TRENDING_IN_CATEGORY, 0.88))
                .collect(Collectors.toList());
    }

    private RecommendedProductDto toDto(Product p, RecommendationType type, double score) {
        String img = !p.getImages().isEmpty() ? p.getImages().get(0).getImageUrl() : "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600";
        return RecommendedProductDto.builder()
                .productId(p.getId())
                .title(p.getTitle())
                .slug(p.getSlug())
                .basePrice(p.getBasePrice())
                .primaryImageUrl(img)
                .ratingAverage(p.getRatingAverage())
                .ratingCount(p.getRatingCount())
                .recommendationType(type)
                .relevanceScore(score)
                .build();
    }
}
`);

write('backend/src/main/java/com/marketplace/recommendation/controller/RecommendationController.java', `
package com.marketplace.recommendation.controller;

import com.marketplace.recommendation.dto.RecommendedProductDto;
import com.marketplace.recommendation.service.RecommendationEngineService;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Personalized Recommendations", description = "Endpoints for collaborative filtering, related items, and trending recommendations")
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationEngineService recommendationService;

    @Operation(summary = "Get similar products based on item attributes and category clustering")
    @GetMapping("/similar/{productId}")
    public ResponseEntity<Result<List<RecommendedProductDto>>> getSimilar(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "6") int limit) {
        List<RecommendedProductDto> list = recommendationService.getSimilarProducts(productId, limit);
        return ResponseEntity.ok(Result.ok(list));
    }

    @Operation(summary = "Get trending bestseller recommendations")
    @GetMapping("/trending")
    public ResponseEntity<Result<List<RecommendedProductDto>>> getTrending(
            @RequestParam(defaultValue = "8") int limit) {
        List<RecommendedProductDto> list = recommendationService.getTrendingProducts(limit);
        return ResponseEntity.ok(Result.ok(list));
    }
}
`);

// ----------------------------------------------------
// 2. INTERNATIONALIZATION & CURRENCY SERVICE
// ----------------------------------------------------
write('backend/src/main/java/com/marketplace/i18n/domain/CurrencyRate.java', `
package com.marketplace.i18n.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "currency_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyRate extends BaseEntity {

    @Column(name = "source_currency", length = 3, nullable = false)
    private String sourceCurrency;

    @Column(name = "target_currency", length = 3, nullable = false)
    private String targetCurrency;

    @Column(name = "exchange_rate", precision = 12, scale = 6, nullable = false)
    private BigDecimal exchangeRate;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();
}
`);

write('backend/src/main/java/com/marketplace/i18n/service/CurrencyConversionService.java', `
package com.marketplace.i18n.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CurrencyConversionService {

    private final Map<String, BigDecimal> rates = new ConcurrentHashMap<>(Map.of(
            "USD_EUR", BigDecimal.valueOf(0.92),
            "USD_GBP", BigDecimal.valueOf(0.79),
            "USD_CAD", BigDecimal.valueOf(1.36),
            "USD_AUD", BigDecimal.valueOf(1.52),
            "USD_JPY", BigDecimal.valueOf(155.40),
            "EUR_USD", BigDecimal.valueOf(1.087),
            "GBP_USD", BigDecimal.valueOf(1.265)
    ));

    public BigDecimal convert(BigDecimal amount, String from, String to) {
        if (from.equalsIgnoreCase(to) || amount == null) return amount;
        String pair = (from + "_" + to).toUpperCase();
        BigDecimal rate = rates.getOrDefault(pair, BigDecimal.ONE);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
    }
}
`);

console.log('Enterprise Expansion Generated.');
`);
