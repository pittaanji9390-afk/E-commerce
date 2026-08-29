package com.marketplace.product.service;

import com.marketplace.product.domain.Product;
import com.marketplace.product.domain.ProductStatus;
import com.marketplace.product.dto.ProductDto;
import com.marketplace.product.dto.ProductSearchFilter;
import com.marketplace.product.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(ProductSearchFilter filter, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Only ACTIVE products
            predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));

            // Query text search against title or description
            if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
                String pattern = "%" + filter.getQuery().trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate skuMatch = cb.like(cb.lower(root.get("sku")), pattern);
                predicates.add(cb.or(titleMatch, descMatch, skuMatch));
            }

            // Category filter
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            // Brand filter
            if (filter.getBrandId() != null) {
                predicates.add(cb.equal(root.get("brand").get("id"), filter.getBrandId()));
            }

            // Seller filter
            if (filter.getSellerId() != null) {
                predicates.add(cb.equal(root.get("seller").get("id"), filter.getSellerId()));
            }

            // Price range
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("basePrice"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("basePrice"), filter.getMaxPrice()));
            }

            // Min Rating
            if (filter.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ratingAverage"), filter.getMinRating()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Determine sort order
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (filter.getSortBy() != null) {
            switch (filter.getSortBy().toLowerCase()) {
                case "price_asc" -> sort = Sort.by(Sort.Direction.ASC, "basePrice");
                case "price_desc" -> sort = Sort.by(Sort.Direction.DESC, "basePrice");
                case "rating" -> sort = Sort.by(Sort.Direction.DESC, "ratingAverage");
                case "popular" -> sort = Sort.by(Sort.Direction.DESC, "totalSales");
                case "newest" -> sort = Sort.by(Sort.Direction.DESC, "createdAt");
            }
        }

        Pageable effectivePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return productRepository.findAll(spec, effectivePageable)
                .map(p -> productService.getProductById(p.getId()));
    }
}
