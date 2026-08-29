package com.marketplace.review.controller;

import com.marketplace.review.dto.CreateReviewRequest;
import com.marketplace.review.dto.ReviewDto;
import com.marketplace.review.service.ReviewService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.PagedResult;
import com.marketplace.shared.response.Result;
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

@Tag(name = "Reviews & Ratings", description = "Endpoints for verified customer product ratings and reviews")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Get verified product reviews (Paginated)")
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<PagedResult<ReviewDto>> getProductReviews(
            @PathVariable UUID productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewDto> page = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Submit a verified review for a product")
    @PostMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<ReviewDto>> createReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewDto review = reviewService.createReview(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(review, "Review submitted successfully."));
    }

    @Operation(summary = "Vote a review as helpful")
    @PostMapping("/reviews/{reviewId}/helpful")
    public ResponseEntity<Result<Void>> voteHelpful(@PathVariable UUID reviewId) {
        reviewService.voteHelpful(reviewId);
        return ResponseEntity.ok(Result.ok(null, "Feedback recorded."));
    }
}
