package com.fintech.aggregator.bank.kafka;

import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, BankTransactionEvent> kafkaTemplate;

    public void publish(BankTransactionEvent event) {
        try {
            MDC.put("customerId", event.getAccountHolderId());
            MDC.put("eventId", event.getTransactionRef());
            kafkaTemplate.send(KafkaTopics.BANK_TRANSACTIONS, event.getTransactionRef(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish bank transaction {}", event.getTransactionRef(), ex);
                        } else {
                            log.info("Published bank transaction to topic {}", KafkaTopics.BANK_TRANSACTIONS);
                        }
                    });
        } finally {
            MDC.clear();
        }
    }
}
