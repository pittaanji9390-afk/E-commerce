package com.marketplace.shipping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTrackingEventRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private String location;

    @NotBlank(message = "Description is required")
    private String description;
}
