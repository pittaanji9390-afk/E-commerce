package com.marketplace.returns.dto;

import com.marketplace.returns.domain.ReturnReason;
import com.marketplace.returns.domain.ReturnStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnDto {
    private UUID id;
    private String returnNumber;
    private UUID sellerOrderId;
    private String sellerOrderNumber;
    private UUID sellerId;
    private String sellerName;
    private UUID customerId;
    private ReturnReason reason;
    private String customerNotes;
    private String evidenceUrls;
    private ReturnStatus status;
    private String sellerResponseNotes;
    private BigDecimal refundAmount;
    private List<ReturnItemDto> items;
    private Instant createdAt;
    private Instant updatedAt;
}
