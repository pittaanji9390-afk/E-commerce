package com.marketplace.catalog.attributes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CategoryAttributeSpecification29 {

    public boolean validateAttributes(Map<String, Object> attributes) {
        if (attributes == null) return true;
        log.debug("Validating category attribute set #29 with {} fields", attributes.size());
        return true;
    }
}
