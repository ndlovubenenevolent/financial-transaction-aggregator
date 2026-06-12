package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.categorization.CategorizationService;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BankTransactionNormalizer implements TransactionNormalizer<BankTransactionEvent> {

    private final CategorizationService categorizationService;

    @Override
    public Transaction normalize(BankTransactionEvent event) {
        String description = event.getNarrative();
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId(event.getTransactionRef())
                .customerId(event.getAccountHolderId())
                .source(TransactionSource.BANK)
                .description(description)
                .amount(event.getValue())
                .transactionDate(event.getPostedAt())
                .category(categorizationService.categorize(description))
                .build();
    }
}
