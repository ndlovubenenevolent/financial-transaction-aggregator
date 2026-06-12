package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.categorization.CategorizationService;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvestmentTransactionNormalizer implements TransactionNormalizer<InvestmentTransactionEvent> {

    private final CategorizationService categorizationService;

    @Override
    public Transaction normalize(InvestmentTransactionEvent event) {
        String description = event.getInstrumentDescription();
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId(event.getTradeId())
                .customerId(event.getInvestorId())
                .source(TransactionSource.INVESTMENT)
                .description(description)
                .amount(event.getNetAmount())
                .transactionDate(event.getSettlementDate())
                .category(categorizationService.categorize(description))
                .build();
    }
}
