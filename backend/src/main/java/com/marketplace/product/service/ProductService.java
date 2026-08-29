package com.marketplace.product.service;

import com.marketplace.catalog.domain.Brand;
import com.marketplace.catalog.domain.Category;
import com.marketplace.catalog.repository.BrandRepository;
import com.marketplace.catalog.repository.CategoryRepository;
import com.marketplace.inventory.domain.Inventory;
import com.marketplace.inventory.domain.InventoryTransaction;
import com.marketplace.inventory.domain.InventoryTransactionType;
import com.marketplace.inventory.repository.InventoryRepository;
import com.marketplace.inventory.repository.InventoryTransactionRepository;
import com.marketplace.product.domain.Product;
import com.marketplace.product.domain.ProductImage;
import com.marketplace.product.domain.ProductStatus;
import com.marketplace.product.domain.ProductVariant;
import com.marketplace.product.dto.*;
import com.marketplace.product.repository.ProductImageRepository;
import com.marketplace.product.repository.ProductRepository;
import com.marketplace.product.repository.ProductVariantRepository;
import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.domain.SellerStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTxRepository;

    @Transactional
    public ProductDto createProduct(UUID sellerId, CreateProductRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new BusinessRuleException(ErrorCode.SELLER_NOT_APPROVED, "Seller must be approved to create products. Current status: " + seller.getStatus());
        }

        String slug = request.getSlug().trim().toLowerCase().replaceAll("[^a-z0-9-]", "-");
        if (productRepository.existsBySlug(slug)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Product slug '" + slug + "' is already taken.");
        }

        if (productRepository.existsBySku(request.getSku().trim())) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Product master SKU '" + request.getSku() + "' already exists.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));
        }

        Product product = Product.builder()
                .seller(seller)
                .category(category)
                .brand(brand)
                .title(request.getTitle().trim())
                .slug(slug)
                .sku(request.getSku().trim())
                .shortDescription(request.getShortDescription())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .compareAtPrice(request.getCompareAtPrice())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .weightGrams(request.getWeightGrams() != null ? request.getWeightGrams() : BigDecimal.ZERO)
                .dimensionsCm(request.getDimensionsCm())
                .status(ProductStatus.ACTIVE) // Auto-active for approved sellers
                .build();

        Product savedProduct = productRepository.save(product);

        // Process variants if provided, or create default variant
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (CreateVariantRequest vReq : request.getVariants()) {
                createVariantInternal(savedProduct, vReq);
            }
        } else {
            CreateVariantRequest defaultVar = CreateVariantRequest.builder()
                    .sku(savedProduct.getSku())
                    .title("Default")
                    .priceAdjustment(BigDecimal.ZERO)
                    .initialStock(10)
                    .build();
            createVariantInternal(savedProduct, defaultVar);
        }

        log.info("Created product: [id={}, title={}, seller={}]", savedProduct.getId(), savedProduct.getTitle(), seller.getDisplayName());
        return getProductById(savedProduct.getId());
    }

    @Transactional
    public ProductVariantDto createVariant(UUID sellerId, UUID productId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new BusinessRuleException(ErrorCode.FORBIDDEN, "You do not own this product.");
        }

        ProductVariant variant = createVariantInternal(product, request);
        return toVariantDto(variant);
    }

    private ProductVariant createVariantInternal(Product product, CreateVariantRequest request) {
        if (variantRepository.existsBySku(request.getSku().trim())) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Variant SKU '" + request.getSku() + "' already exists.");
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.getSku().trim())
                .barcode(request.getBarcode())
                .title(request.getTitle().trim())
                .priceAdjustment(request.getPriceAdjustment() != null ? request.getPriceAdjustment() : BigDecimal.ZERO)
                .weightAdjustmentGrams(request.getWeightAdjustmentGrams() != null ? request.getWeightAdjustmentGrams() : BigDecimal.ZERO)
                .attributesJson(request.getAttributesJson() != null ? request.getAttributesJson() : "{}")
                .active(true)
                .build();

        ProductVariant savedVariant = variantRepository.save(variant);

        // Create associated Inventory ledger record
        int initialStock = request.getInitialStock() != null ? request.getInitialStock() : 0;
        Inventory inventory = Inventory.builder()
                .variant(savedVariant)
                .onHand(initialStock)
                .reserved(0)
                .lowStockThreshold(5)
                .build();
        Inventory savedInventory = inventoryRepository.save(inventory);

        if (initialStock > 0) {
            InventoryTransaction tx = InventoryTransaction.builder()
                    .inventory(savedInventory)
                    .transactionType(InventoryTransactionType.RESTOCK)
                    .quantity(initialStock)
                    .previousOnHand(0)
                    .newOnHand(initialStock)
                    .previousReserved(0)
                    .newReserved(0)
                    .reason("Initial variant stock registration")
                    .build();
            inventoryTxRepository.save(tx);
        }

        return savedVariant;
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return toDto(product);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return toDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getActiveProducts(Pageable pageable) {
        return productRepository.findByStatus(ProductStatus.ACTIVE, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getSellerProducts(UUID sellerId, Pageable pageable) {
        return productRepository.findBySellerId(sellerId, pageable).map(this::toDto);
    }

    @Transactional
    public ProductDto updateProductStatus(UUID productId, UpdateProductStatusRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        product.setStatus(request.getStatus());
        if (request.getModerationNotes() != null) {
            product.setModerationNotes(request.getModerationNotes());
        }

        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    private ProductDto toDto(Product p) {
        List<ProductVariant> variants = variantRepository.findByProductId(p.getId());
        List<ProductImage> images = imageRepository.findByProductIdOrderByDisplayOrderAsc(p.getId());

        return ProductDto.builder()
                .id(p.getId())
                .sellerId(p.getSeller().getId())
                .sellerName(p.getSeller().getDisplayName())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .brandId(p.getBrand() != null ? p.getBrand().getId() : null)
                .brandName(p.getBrand() != null ? p.getBrand().getName() : null)
                .title(p.getTitle())
                .slug(p.getSlug())
                .sku(p.getSku())
                .shortDescription(p.getShortDescription())
                .description(p.getDescription())
                .basePrice(p.getBasePrice())
                .compareAtPrice(p.getCompareAtPrice())
                .currency(p.getCurrency())
                .taxCategory(p.getTaxCategory())
                .status(p.getStatus())
                .ratingAverage(p.getRatingAverage())
                .ratingCount(p.getRatingCount())
                .totalSales(p.getTotalSales())
                .variants(variants.stream().map(this::toVariantDto).collect(Collectors.toList()))
                .images(images.stream().map(this::toImageDto).collect(Collectors.toList()))
                .createdAt(p.getCreatedAt())
                .build();
    }

    private ProductVariantDto toVariantDto(ProductVariant v) {
        int available = 0;
        if (v.getInventory() != null) {
            available = v.getInventory().getAvailable();
        } else {
            available = inventoryRepository.findByVariantId(v.getId())
                    .map(Inventory::getAvailable).orElse(0);
        }

        return ProductVariantDto.builder()
                .id(v.getId())
                .productId(v.getProduct().getId())
                .sku(v.getSku())
                .barcode(v.getBarcode())
                .title(v.getTitle())
                .priceAdjustment(v.getPriceAdjustment())
                .effectivePrice(v.getEffectivePrice())
                .weightAdjustmentGrams(v.getWeightAdjustmentGrams())
                .attributesJson(v.getAttributesJson())
                .availableStock(available)
                .active(v.isActive())
                .build();
    }

    private ProductImageDto toImageDto(ProductImage img) {
        return ProductImageDto.builder()
                .id(img.getId())
                .imageUrl(img.getImageUrl())
                .thumbnailUrl(img.getThumbnailUrl())
                .altText(img.getAltText())
                .displayOrder(img.getDisplayOrder())
                .primary(img.isPrimary())
                .build();
    }
}
