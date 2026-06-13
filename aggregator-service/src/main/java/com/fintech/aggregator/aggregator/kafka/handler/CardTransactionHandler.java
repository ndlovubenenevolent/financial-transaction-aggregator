package com.fintech.aggregator.aggregator.kafka.handler;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.service.normalizer.CardTransactionNormalizer;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardTransactionHandler implements SourceTransactionHandler<CardTransactionEvent> {

    private final CardTransactionNormalizer normalizer;

    @Override
    public String customerId(CardTransactionEvent event) {
        return event.getCardholderId();
    }

    @Override
    public String eventId(CardTransactionEvent event) {
        return event.getAuthId();
    }

    @Override
    public Transaction normalize(CardTransactionEvent event) {
        return normalizer.normalize(event);
    }
}
