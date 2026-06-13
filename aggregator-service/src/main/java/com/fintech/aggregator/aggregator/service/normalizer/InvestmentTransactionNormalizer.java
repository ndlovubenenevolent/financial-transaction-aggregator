package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InvestmentTransactionNormalizer implements TransactionNormalizer<InvestmentTransactionEvent> {

    @Override
    public Transaction normalize(InvestmentTransactionEvent event) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId(event.getTradeId())
                .customerId(event.getInvestorId())
                .source(TransactionSource.INVESTMENT)
                .description(event.getInstrumentDescription())
                .amount(event.getNetAmount())
                .transactionDate(event.getSettlementDate())
                .build();
    }
}
