package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.categorization.CategorizationService;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardTransactionNormalizer implements TransactionNormalizer<CardTransactionEvent> {

    private final CategorizationService categorizationService;

    @Override
    public Transaction normalize(CardTransactionEvent event) {
        String description = event.getMerchantName();
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId(event.getAuthId())
                .customerId(event.getCardholderId())
                .source(TransactionSource.CARD)
                .description(description)
                .amount(event.getChargeAmount())
                .transactionDate(event.getPurchaseDate())
                .category(categorizationService.categorize(description))
                .build();
    }
}
