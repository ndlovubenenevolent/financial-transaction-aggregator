package com.fintech.aggregator.card.service;

import com.fintech.aggregator.common.events.CardTransactionEvent;
import com.fintech.aggregator.kafka.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
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

    private final KafkaEventPublisher<CardTransactionEvent> eventPublisher;

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

        log.debug("Simulating card transaction customerId={} eventId={} merchant={}",
                customerId, event.getAuthId(), template.merchant());
        eventPublisher.publish(event);
    }

    private record CardTemplate(String merchant, BigDecimal amount) {
    }
}
