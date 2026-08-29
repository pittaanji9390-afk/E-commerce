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
public class UpdateSellerProfileRequest {

    @NotBlank(message = "Store display name is required")
    @Size(max = 150)
    private String displayName;

    private String description;

    private String logoUrl;

    private String bannerUrl;

    @NotBlank(message = "Contact email is required")
    @Email
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    @Size(max = 30)
    private String contactPhone;
}
