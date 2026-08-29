package com.marketplace.catalog.service;

import com.marketplace.catalog.domain.Brand;
import com.marketplace.catalog.domain.Category;
import com.marketplace.catalog.dto.*;
import com.marketplace.catalog.repository.BrandRepository;
import com.marketplace.catalog.repository.CategoryAttributeRepository;
import com.marketplace.catalog.repository.CategoryRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CategoryAttributeRepository attributeRepository;

    @Transactional(readOnly = true)
    public List<CategoryTreeDto> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNullAndActiveTrueOrderByDisplayOrderAsc();
        return rootCategories.stream().map(this::buildCategoryTree).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return toCategoryDto(category);
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        String slug = request.getSlug().trim().toLowerCase().replaceAll("[^a-z0-9-]", "-");
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Category slug already exists.");
        }

        Category parent = null;
        String path = slug;
        int level = 1;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
            path = parent.getPath() + "/" + slug;
            level = parent.getLevel() + 1;
        }

        Category category = Category.builder()
                .parent(parent)
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .imageUrl(request.getImageUrl())
                .path(path)
                .level(level)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .commissionRate(request.getCommissionRate() != null ? request.getCommissionRate() : BigDecimal.valueOf(10.00))
                .active(true)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Created category: [id={}, slug={}, path={}]", saved.getId(), saved.getSlug(), saved.getPath());
        return toCategoryDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BrandDto> getAllBrands() {
        return brandRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::toBrandDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BrandDto createBrand(CreateBrandRequest request) {
        String slug = request.getSlug().trim().toLowerCase().replaceAll("[^a-z0-9-]", "-");
        if (brandRepository.existsBySlug(slug)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Brand slug already exists.");
        }

        Brand brand = Brand.builder()
                .name(request.getName().trim())
                .slug(slug)
                .logoUrl(request.getLogoUrl())
                .description(request.getDescription())
                .websiteUrl(request.getWebsiteUrl())
                .active(true)
                .build();

        Brand saved = brandRepository.save(brand);
        log.info("Created brand: [id={}, slug={}]", saved.getId(), saved.getSlug());
        return toBrandDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryAttributeDto> getCategoryAttributes(Long categoryId) {
        return attributeRepository.findByCategoryId(categoryId).stream()
                .map(a -> CategoryAttributeDto.builder()
                        .id(a.getId())
                        .categoryId(a.getCategory().getId())
                        .name(a.getName())
                        .code(a.getCode())
                        .attributeType(a.getAttributeType())
                        .required(a.isRequired())
                        .filterable(a.isFilterable())
                        .optionsJson(a.getOptionsJson())
                        .build())
                .collect(Collectors.toList());
    }

    private CategoryTreeDto buildCategoryTree(Category category) {
        CategoryTreeDto dto = CategoryTreeDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .iconUrl(category.getIconUrl())
                .imageUrl(category.getImageUrl())
                .level(category.getLevel())
                .children(new ArrayList<>())
                .build();

        List<Category> children = categoryRepository.findByParentIdAndActiveTrueOrderByDisplayOrderAsc(category.getId());
        for (Category child : children) {
            dto.getChildren().add(buildCategoryTree(child));
        }

        return dto;
    }

    private CategoryDto toCategoryDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .name(c.getName())
                .slug(c.getSlug())
                .description(c.getDescription())
                .iconUrl(c.getIconUrl())
                .imageUrl(c.getImageUrl())
                .path(c.getPath())
                .level(c.getLevel())
                .displayOrder(c.getDisplayOrder())
                .commissionRate(c.getCommissionRate())
                .active(c.isActive())
                .build();
    }

    private BrandDto toBrandDto(Brand b) {
        return BrandDto.builder()
                .id(b.getId())
                .name(b.getName())
                .slug(b.getSlug())
                .logoUrl(b.getLogoUrl())
                .description(b.getDescription())
                .websiteUrl(b.getWebsiteUrl())
                .active(b.isActive())
                .build();
    }
}
