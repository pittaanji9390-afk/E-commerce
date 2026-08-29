package com.marketplace.returns.dto;

import com.marketplace.returns.domain.ReturnReason;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReturnRequest {

    @NotNull(message = "Seller Order ID is required")
    private UUID sellerOrderId;

    @NotNull(message = "Return reason is required")
    private ReturnReason reason;

    private String customerNotes;

    private String evidenceUrls;

    @NotEmpty(message = "At least one item must be selected for return")
    private List<ReturnItemRequest> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReturnItemRequest {
        @NotNull
        private UUID orderItemId;
        @NotNull
        private Integer quantity;
    }
}
