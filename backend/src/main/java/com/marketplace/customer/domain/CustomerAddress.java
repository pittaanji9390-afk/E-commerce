package com.marketplace.customer.domain;

import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAddress extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "address_title", length = 50, nullable = false)
    @Builder.Default
    private String addressTitle = "Home";

    @Column(name = "recipient_name", length = 150, nullable = false)
    private String recipientName;

    @Column(name = "phone_number", length = 30, nullable = false)
    private String phoneNumber;

    @Column(name = "street_line1", length = 255, nullable = false)
    private String streetLine1;

    @Column(name = "street_line2", length = 255)
    private String streetLine2;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "state_province", length = 100, nullable = false)
    private String stateProvince;

    @Column(name = "postal_code", length = 20, nullable = false)
    private String postalCode;

    @Column(name = "country_code", length = 2, nullable = false)
    @Builder.Default
    private String countryCode = "US";

    @Column(name = "is_default_shipping", nullable = false)
    @Builder.Default
    private boolean defaultShipping = false;

    @Column(name = "is_default_billing", nullable = false)
    @Builder.Default
    private boolean defaultBilling = false;
}
