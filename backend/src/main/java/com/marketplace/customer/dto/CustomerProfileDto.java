package com.marketplace.customer.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String avatarUrl;
    private String currencyPreference;
    private String localePreference;
    private boolean marketingOptIn;
    private List<AddressDto> addresses;
}
