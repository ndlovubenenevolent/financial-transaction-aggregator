package com.fintech.aggregator.aggregator.kafka.handler;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.service.normalizer.InvestmentTransactionNormalizer;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvestmentTransactionHandler implements SourceTransactionHandler<InvestmentTransactionEvent> {

    private final InvestmentTransactionNormalizer normalizer;

    @Override
    public String customerId(InvestmentTransactionEvent event) {
        return event.getInvestorId();
    }

    @Override
    public String eventId(InvestmentTransactionEvent event) {
        return event.getTradeId();
    }

    @Override
    public Transaction normalize(InvestmentTransactionEvent event) {
        return normalizer.normalize(event);
    }
}
