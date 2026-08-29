package com.marketplace.product.controller;

import com.marketplace.product.dto.*;
import com.marketplace.product.service.ProductService;
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

@Tag(name = "Products & Catalog", description = "Endpoints for product discovery, creation, variants, and moderation")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Browse all active marketplace products (Paginated)")
    @GetMapping
    public ResponseEntity<PagedResult<ProductDto>> getActiveProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductDto> page = productService.getActiveProducts(pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Get product details by URL slug")
    @GetMapping("/{slug}")
    public ResponseEntity<Result<ProductDto>> getProductBySlug(@PathVariable String slug) {
        ProductDto product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(Result.ok(product));
    }

    @Operation(summary = "Create a new product with variants (Seller)")
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<ProductDto>> createProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateProductRequest request) {
        ProductDto product = productService.createProduct(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(product, "Product created successfully."));
    }

    @Operation(summary = "Add a new variant to an existing product (Seller)")
    @PostMapping("/{productId}/variants")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<ProductVariantDto>> addVariant(
            @PathVariable UUID productId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateVariantRequest request) {
        ProductVariantDto variant = productService.createVariant(principal.getId(), productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(variant, "Variant created successfully."));
    }

    @Operation(summary = "Get all products listed by authenticated seller")
    @GetMapping("/seller/my-products")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<PagedResult<ProductDto>> getMyProducts(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductDto> page = productService.getSellerProducts(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Moderate product status (Admin)")
    @PatchMapping("/{productId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CATALOG_ADMIN')")
    public ResponseEntity<Result<ProductDto>> updateStatus(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductStatusRequest request) {
        ProductDto updated = productService.updateProductStatus(productId, request);
        return ResponseEntity.ok(Result.ok(updated, "Product status updated."));
    }
}
