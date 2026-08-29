package com.marketplace.catalog.rules;

import com.marketplace.product.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CatalogSafetyValidationService14 {

    public CatalogComplianceRule14 validateProduct(Product product) {
        boolean clean = product.getTitle() != null && !product.getTitle().toLowerCase().contains("prohibited");
        return CatalogComplianceRule14.builder()
                .product(product)
                .ruleType("SAFETY_COMPLIANCE_STANDARDS_14")
                .passedSafetyChecks(clean)
                .flaggedTermsCount(clean ? 0 : 1)
                .build();
    }
}
