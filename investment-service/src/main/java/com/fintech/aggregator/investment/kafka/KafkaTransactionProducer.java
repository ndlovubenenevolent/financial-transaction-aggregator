package com.fintech.aggregator.investment.kafka;

import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, InvestmentTransactionEvent> kafkaTemplate;

    public void publish(InvestmentTransactionEvent event) {
        try {
            MDC.put("customerId", event.getInvestorId());
            MDC.put("eventId", event.getTradeId());
            kafkaTemplate.send(KafkaTopics.INVESTMENT_TRANSACTIONS, event.getTradeId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish investment transaction {}", event.getTradeId(), ex);
                        } else {
                            log.info("Published investment transaction to topic {}", KafkaTopics.INVESTMENT_TRANSACTIONS);
                        }
                    });
        } finally {
            MDC.clear();
        }
    }
}
