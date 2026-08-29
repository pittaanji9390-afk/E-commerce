package com.marketplace.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAddressRequest {

    private String addressTitle;

    @NotBlank(message = "Recipient name is required")
    @Size(max = 150)
    private String recipientName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 30)
    private String phoneNumber;

    @NotBlank(message = "Street address is required")
    @Size(max = 255)
    private String streetLine1;

    private String streetLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State/Province is required")
    @Size(max = 100)
    private String stateProvince;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20)
    private String postalCode;

    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 2)
    @Builder.Default
    private String countryCode = "US";

    private boolean defaultShipping;

    private boolean defaultBilling;
}
