package com.marketplace.catalog.controller;

import com.marketplace.catalog.dto.*;
import com.marketplace.catalog.service.CatalogService;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Catalog & Categories", description = "Endpoints for hierarchical categories, brands, and dynamic attributes")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @Operation(summary = "Get full hierarchical category tree")
    @GetMapping("/categories")
    public ResponseEntity<Result<List<CategoryTreeDto>>> getCategoryTree() {
        List<CategoryTreeDto> tree = catalogService.getCategoryTree();
        return ResponseEntity.ok(Result.ok(tree));
    }

    @Operation(summary = "Get category by slug")
    @GetMapping("/categories/{slug}")
    public ResponseEntity<Result<CategoryDto>> getCategoryBySlug(@PathVariable String slug) {
        CategoryDto category = catalogService.getCategoryBySlug(slug);
        return ResponseEntity.ok(Result.ok(category));
    }

    @Operation(summary = "Create a category (Admin / Catalog Manager)")
    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CATALOG_ADMIN')")
    public ResponseEntity<Result<CategoryDto>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto created = catalogService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(created, "Category created."));
    }

    @Operation(summary = "Get all active brands")
    @GetMapping("/brands")
    public ResponseEntity<Result<List<BrandDto>>> getBrands() {
        List<BrandDto> brands = catalogService.getAllBrands();
        return ResponseEntity.ok(Result.ok(brands));
    }

    @Operation(summary = "Create a new brand")
    @PostMapping("/brands")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CATALOG_ADMIN')")
    public ResponseEntity<Result<BrandDto>> createBrand(@Valid @RequestBody CreateBrandRequest request) {
        BrandDto brand = catalogService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(brand, "Brand created."));
    }

    @Operation(summary = "Get dynamic attributes for a category")
    @GetMapping("/categories/{categoryId}/attributes")
    public ResponseEntity<Result<List<CategoryAttributeDto>>> getCategoryAttributes(@PathVariable Long categoryId) {
        List<CategoryAttributeDto> attributes = catalogService.getCategoryAttributes(categoryId);
        return ResponseEntity.ok(Result.ok(attributes));
    }
}
