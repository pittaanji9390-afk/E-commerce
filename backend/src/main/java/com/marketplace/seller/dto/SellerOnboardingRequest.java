package com.marketplace.seller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerOnboardingRequest {

    @NotBlank(message = "Business name is required")
    @Size(max = 255)
    private String businessName;

    @NotBlank(message = "Store display name is required")
    @Size(max = 150)
    private String displayName;

    @NotBlank(message = "Store slug is required")
    @Size(max = 255)
    private String storeSlug;

    private String description;

    @NotBlank(message = "Contact email is required")
    @Email
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    @Size(max = 30)
    private String contactPhone;

    // KYC Verification Details
    @NotBlank(message = "Legal business name is required")
    private String legalBusinessName;

    @NotBlank(message = "Tax ID / EIN is required")
    private String taxIdEin;

    @NotBlank(message = "Business registration number is required")
    private String businessRegistrationNumber;

    @NotBlank(message = "Document type is required")
    private String documentType;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;
}
