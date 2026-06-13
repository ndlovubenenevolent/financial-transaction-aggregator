package com.fintech.aggregator.aggregator.kafka.handler;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.service.normalizer.BankTransactionNormalizer;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankTransactionHandler implements SourceTransactionHandler<BankTransactionEvent> {

    private final BankTransactionNormalizer normalizer;

    @Override
    public String customerId(BankTransactionEvent event) {
        return event.getAccountHolderId();
    }

    @Override
    public String eventId(BankTransactionEvent event) {
        return event.getTransactionRef();
    }

    @Override
    public Transaction normalize(BankTransactionEvent event) {
        return normalizer.normalize(event);
    }
}
