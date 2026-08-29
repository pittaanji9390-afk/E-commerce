package com.marketplace.catalog.rules;

import com.marketplace.product.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CatalogSafetyValidationService38 {

    public CatalogComplianceRule38 validateProduct(Product product) {
        boolean clean = product.getTitle() != null && !product.getTitle().toLowerCase().contains("prohibited");
        return CatalogComplianceRule38.builder()
                .product(product)
                .ruleType("SAFETY_COMPLIANCE_STANDARDS_38")
                .passedSafetyChecks(clean)
                .flaggedTermsCount(clean ? 0 : 1)
                .build();
    }
}
