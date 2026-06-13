package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CardTransactionNormalizer implements TransactionNormalizer<CardTransactionEvent> {

    @Override
    public Transaction normalize(CardTransactionEvent event) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId(event.getAuthId())
                .customerId(event.getCardholderId())
                .source(TransactionSource.CARD)
                .description(event.getMerchantName())
                .amount(event.getChargeAmount())
                .transactionDate(event.getPurchaseDate())
                .build();
    }
}
