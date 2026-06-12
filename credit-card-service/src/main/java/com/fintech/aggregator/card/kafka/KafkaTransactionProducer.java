package com.fintech.aggregator.card.kafka;

import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, CardTransactionEvent> kafkaTemplate;

    public void publish(CardTransactionEvent event) {
        try {
            MDC.put("customerId", event.getCardholderId());
            MDC.put("eventId", event.getAuthId());
            kafkaTemplate.send(KafkaTopics.CARD_TRANSACTIONS, event.getAuthId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish card transaction {}", event.getAuthId(), ex);
                        } else {
                            log.info("Published card transaction to topic {}", KafkaTopics.CARD_TRANSACTIONS);
                        }
                    });
        } finally {
            MDC.clear();
        }
    }
}
