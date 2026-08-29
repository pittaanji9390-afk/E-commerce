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
