package com.marketplace.wishlist.controller;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.product.domain.Product;
import com.marketplace.product.dto.ProductDto;
import com.marketplace.product.repository.ProductRepository;
import com.marketplace.product.service.ProductService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.exception.ResourceNotFoundException;
import com.marketplace.shared.response.Result;
import com.marketplace.wishlist.domain.Wishlist;
import com.marketplace.wishlist.repository.WishlistRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Wishlist", description = "Endpoints for customer saved items")
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    @Operation(summary = "Get customer wishlist")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public ResponseEntity<Result<List<ProductDto>>> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        List<Wishlist> items = wishlistRepository.findByCustomerId(principal.getId());
        List<ProductDto> products = items.stream()
                .map(w -> productService.getProductById(w.getProduct().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(Result.ok(products));
    }

    @Operation(summary = "Add a product to wishlist")
    @PostMapping("/products/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Result<Void>> addToWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID productId) {
        Customer customer = customerService.getOrCreateCustomer(principal.getId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!wishlistRepository.existsByCustomerIdAndProductId(customer.getId(), productId)) {
            Wishlist item = Wishlist.builder()
                    .customer(customer)
                    .product(product)
                    .build();
            wishlistRepository.save(item);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(null, "Product added to wishlist."));
    }

    @Operation(summary = "Remove a product from wishlist")
    @DeleteMapping("/products/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Result<Void>> removeFromWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID productId) {
        wishlistRepository.findByCustomerIdAndProductId(principal.getId(), productId)
                .ifPresent(wishlistRepository::delete);
        return ResponseEntity.ok(Result.ok(null, "Product removed from wishlist."));
    }
}
