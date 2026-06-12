package com.fintech.aggregator.aggregator.dto;

import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class TransactionResponse {
    UUID id;
    String externalTransactionId;
    String customerId;
    TransactionSource source;
    String description;
    BigDecimal amount;
    Instant transactionDate;
    TransactionCategory category;
    Instant createdAt;
}
