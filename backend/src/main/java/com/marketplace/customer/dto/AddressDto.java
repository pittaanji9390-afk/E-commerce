package com.marketplace.customer.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private UUID id;
    private String addressTitle;
    private String recipientName;
    private String phoneNumber;
    private String streetLine1;
    private String streetLine2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String countryCode;
    private boolean defaultShipping;
    private boolean defaultBilling;
}
