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
