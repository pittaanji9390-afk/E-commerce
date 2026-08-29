package com.marketplace.b2b.dto;

import com.marketplace.b2b.domain.CreditTermType;
import com.marketplace.b2b.domain.QuoteStatus;
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
public class RfqDto {
    private UUID id;
    private String rfqNumber;
    private UUID buyerId;
    private String buyerEmail;
    private UUID sellerId;
    private String sellerName;
    private String companyName;
    private String taxExemptionNumber;
    private CreditTermType creditTerms;
    private QuoteStatus status;
    private BigDecimal targetPrice;
    private BigDecimal quotedTotal;
    private String buyerMessage;
    private String sellerNotes;
    private Instant validUntil;
    private List<RfqItemDto> items;
    private Instant createdAt;
}
