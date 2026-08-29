const fs = require('fs');
const path = require('path');
const { write } = require('./generator_helper');

console.log('Generating Full Enterprise Multi-Vendor Architecture Suite...');

// Generate extensive enterprise specs, validators, services, repositories, DTOs, and controllers

// 1. Tax Compliance Engine & Multi-Jurisdiction Nexus
write('backend/src/main/java/com/marketplace/pricing/domain/TaxJurisdiction.java', `
package com.marketplace.pricing.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tax_jurisdictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxJurisdiction extends BaseEntity {

    @Column(name = "country_code", length = 3, nullable = false)
    private String countryCode;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "postal_code_prefix", length = 20)
    private String postalCodePrefix;

    @Column(name = "standard_rate", precision = 5, scale = 4, nullable = false)
    private BigDecimal standardRate;

    @Column(name = "reduced_rate", precision = 5, scale = 4)
    private BigDecimal reducedRate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
`);

write('backend/src/main/java/com/marketplace/pricing/repository/TaxJurisdictionRepository.java', `
package com.marketplace.pricing.repository;

import com.marketplace.pricing.domain.TaxJurisdiction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaxJurisdictionRepository extends JpaRepository<TaxJurisdiction, UUID> {
    List<TaxJurisdiction> findByCountryCodeAndStateProvinceAndActiveTrue(String countryCode, String stateProvince);
}
`);

// 2. Shipping Carrier Integrations & Dynamic Rate Estimator
write('backend/src/main/java/com/marketplace/shipping/domain/ShippingZone.java', `
package com.marketplace.shipping.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingZone extends BaseEntity {

    @Column(name = "zone_name", length = 100, nullable = false)
    private String zoneName;

    @Column(name = "country_code", length = 3, nullable = false)
    private String countryCode;

    @Column(name = "base_rate", precision = 15, scale = 2, nullable = false)
    private BigDecimal baseRate;

    @Column(name = "per_kg_rate", precision = 15, scale = 2, nullable = false)
    private BigDecimal perKgRate;

    @Column(name = "estimated_days_min", nullable = false)
    private int estimatedDaysMin;

    @Column(name = "estimated_days_max", nullable = false)
    private int estimatedDaysMax;
}
`);

write('backend/src/main/java/com/marketplace/shipping/repository/ShippingZoneRepository.java', `
package com.marketplace.shipping.repository;

import com.marketplace.shipping.domain.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShippingZoneRepository extends JpaRepository<ShippingZone, UUID> {
    List<ShippingZone> findByCountryCode(String countryCode);
}
`);

write('backend/src/main/java/com/marketplace/shipping/service/ShippingRateCalculatorService.java', `
package com.marketplace.shipping.service;

import com.marketplace.shipping.domain.ShippingZone;
import com.marketplace.shipping.repository.ShippingZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingRateCalculatorService {

    private final ShippingZoneRepository zoneRepository;

    public BigDecimal calculateShippingRate(String countryCode, BigDecimal weightKg) {
        List<ShippingZone> zones = zoneRepository.findByCountryCode(countryCode);
        if (zones.isEmpty()) {
            return BigDecimal.valueOf(15.00); // Standard international flat fallback
        }

        ShippingZone zone = zones.get(0);
        BigDecimal weightCharge = weightKg.multiply(zone.getPerKgRate());
        return zone.getBaseRate().add(weightCharge).setScale(2, RoundingMode.HALF_EVEN);
    }
}
`);

// 3. Seller Store Customization & Policy Management
write('backend/src/main/java/com/marketplace/seller/domain/StorePolicy.java', `
package com.marketplace.seller.domain;

import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_store_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorePolicy extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(name = "return_policy_days", nullable = false)
    @Builder.Default
    private int returnPolicyDays = 30;

    @Column(name = "shipping_policy_text", columnDefinition = "TEXT")
    private String shippingPolicyText;

    @Column(name = "refund_policy_text", columnDefinition = "TEXT")
    private String refundPolicyText;

    @Column(name = "warranty_policy_text", columnDefinition = "TEXT")
    private String warrantyPolicyText;
}
`);

write('backend/src/main/java/com/marketplace/seller/repository/StorePolicyRepository.java', `
package com.marketplace.seller.repository;

import com.marketplace.seller.domain.StorePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StorePolicyRepository extends JpaRepository<StorePolicy, UUID> {
}
`);

console.log('Core Expansion files created.');
`);
