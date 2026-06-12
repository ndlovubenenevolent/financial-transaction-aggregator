package com.fintech.aggregator.card.service;

import com.fintech.aggregator.card.kafka.KafkaTransactionProducer;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TransactionSimulatorService {

    private static final List<String> CUSTOMERS = List.of(
            "CUST-001", "CUST-002", "CUST-003", "CUST-004", "CUST-005"
    );

    private static final List<CardTemplate> TEMPLATES = List.of(
            new CardTemplate("Uber", new BigDecimal("-85.50")),
            new CardTemplate("Netflix", new BigDecimal("-159.00")),
            new CardTemplate("Checkers", new BigDecimal("-650.25")),
            new CardTemplate("Woolworths", new BigDecimal("-420.75"))
    );

    private final KafkaTransactionProducer producer;

    @Scheduled(fixedDelayString = "${card.simulator.interval-ms:60000}")
    public void generateTransaction() {
        CardTemplate template = TEMPLATES.get(ThreadLocalRandom.current().nextInt(TEMPLATES.size()));
        String customerId = CUSTOMERS.get(ThreadLocalRandom.current().nextInt(CUSTOMERS.size()));

        CardTransactionEvent event = CardTransactionEvent.builder()
                .authId("CARD-" + UUID.randomUUID())
                .cardholderId(customerId)
                .merchantName(template.merchant())
                .chargeAmount(template.amount())
                .purchaseDate(Instant.now())
                .build();

        producer.publish(event);
    }

    private record CardTemplate(String merchant, BigDecimal amount) {
    }
}
