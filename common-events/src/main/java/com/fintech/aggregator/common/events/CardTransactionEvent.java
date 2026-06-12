package com.fintech.aggregator.common.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardTransactionEvent {
    private String authId;
    private String cardholderId;
    private String merchantName;
    private BigDecimal chargeAmount;
    private Instant purchaseDate;
}
