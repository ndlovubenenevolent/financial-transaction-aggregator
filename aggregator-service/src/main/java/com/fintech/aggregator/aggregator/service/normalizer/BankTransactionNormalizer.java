package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BankTransactionNormalizer implements TransactionNormalizer<BankTransactionEvent> {

    @Override
    public Transaction normalize(BankTransactionEvent event) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId(event.getTransactionRef())
                .customerId(event.getAccountHolderId())
                .source(TransactionSource.BANK)
                .description(event.getNarrative())
                .amount(event.getValue())
                .transactionDate(event.getPostedAt())
                .build();
    }
}
