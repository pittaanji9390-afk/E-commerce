package com.marketplace.returns.dto;

import com.marketplace.returns.domain.ReturnStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReturnRequest {

    @NotNull(message = "Decision status is required")
    private ReturnStatus status;

    private String responseNotes;

    private BigDecimal refundAmount;
}
