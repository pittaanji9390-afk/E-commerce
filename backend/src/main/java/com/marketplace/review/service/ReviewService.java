package com.marketplace.review.service;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.product.domain.Product;
import com.marketplace.product.repository.ProductRepository;
import com.marketplace.review.domain.Review;
import com.marketplace.review.domain.ReviewImage;
import com.marketplace.review.domain.ReviewStatus;
import com.marketplace.review.dto.CreateReviewRequest;
import com.marketplace.review.dto.ReviewDto;
import com.marketplace.review.repository.ReviewImageRepository;
import com.marketplace.review.repository.ReviewRepository;
import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.repository.SellerRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CustomerService customerService;

    @Transactional
    public ReviewDto createReview(UUID customerId, CreateReviewRequest request) {
        Customer customer = customerService.getOrCreateCustomer(customerId);

        if (reviewRepository.existsByCustomerIdAndProductId(customerId, request.getProductId())) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "You have already submitted a review for this product.");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        Seller seller = product.getSeller();

        Review review = Review.builder()
                .customer(customer)
                .product(product)
                .seller(seller)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment().trim())
                .verifiedPurchase(true)
                .status(ReviewStatus.APPROVED)
                .build();

        if (request.getImageUrls() != null) {
            for (String url : request.getImageUrls()) {
                ReviewImage img = ReviewImage.builder()
                        .imageUrl(url)
                        .build();
                review.addImage(img);
            }
        }

        Review saved = reviewRepository.save(review);

        // Recalculate rolling average on Product
        Double productAvg = reviewRepository.calculateProductAverageRating(product.getId());
        long productCount = reviewRepository.countApprovedProductReviews(product.getId());
        product.setRatingAverage(BigDecimal.valueOf(productAvg != null ? productAvg : 0.0).setScale(2, RoundingMode.HALF_EVEN));
        product.setRatingCount((int) productCount);
        productRepository.save(product);

        // Recalculate rolling average on Seller
        Double sellerAvg = reviewRepository.calculateSellerAverageRating(seller.getId());
        long sellerCount = reviewRepository.countApprovedSellerReviews(seller.getId());
        seller.setRatingAverage(BigDecimal.valueOf(sellerAvg != null ? sellerAvg : 0.0).setScale(2, RoundingMode.HALF_EVEN));
        seller.setRatingCount((int) sellerCount);
        sellerRepository.save(seller);

        log.info("Submitted review: [productId={}, customerId={}, rating={}]", product.getId(), customerId, request.getRating());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto> getProductReviews(UUID productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable).map(this::toDto);
    }

    @Transactional
    public void voteHelpful(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.setHelpfulVotes(review.getHelpfulVotes() + 1);
        reviewRepository.save(review);
    }

    private ReviewDto toDto(Review r) {
        List<String> images = reviewImageRepository.findByReviewId(r.getId()).stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        String customerName = r.getCustomer().getUser().getFirstName() + " " +
                r.getCustomer().getUser().getLastName().substring(0, 1) + ".";

        return ReviewDto.builder()
                .id(r.getId())
                .productId(r.getProduct().getId())
                .customerId(r.getCustomer().getId())
                .customerName(customerName)
                .rating(r.getRating())
                .title(r.getTitle())
                .comment(r.getComment())
                .verifiedPurchase(r.isVerifiedPurchase())
                .helpfulVotes(r.getHelpfulVotes())
                .imageUrls(images)
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
