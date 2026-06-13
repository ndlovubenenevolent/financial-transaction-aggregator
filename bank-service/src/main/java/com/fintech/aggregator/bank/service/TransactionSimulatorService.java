package com.fintech.aggregator.bank.service;

import com.fintech.aggregator.common.CustomerIdGenerator;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.kafka.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private static final List<BankTemplate> TEMPLATES = List.of(
            new BankTemplate("Salary Payment", new BigDecimal("45000.00")),
            new BankTemplate("ATM Withdrawal", new BigDecimal("-500.00")),
            new BankTemplate("EFT Transfer", new BigDecimal("-1200.00"))
    );

    private final KafkaEventPublisher<BankTransactionEvent> eventPublisher;

    @Value("${bank.simulator.customer-pool-size:100}")
    private int customerPoolSize = 100;

    @Scheduled(fixedDelayString = "${bank.simulator.interval-ms:60000}")
    public void generateTransaction() {
        BankTemplate template = TEMPLATES.get(ThreadLocalRandom.current().nextInt(TEMPLATES.size()));
        String customerId = CustomerIdGenerator.randomCustomerId(customerPoolSize);

        BankTransactionEvent event = BankTransactionEvent.builder()
                .transactionRef("BANK-" + UUID.randomUUID())
                .accountHolderId(customerId)
                .narrative(template.description())
                .value(template.amount())
                .postedAt(Instant.now())
                .build();

        log.debug("Simulating bank transaction customerId={} eventId={} narrative={}",
                customerId, event.getTransactionRef(), template.description());
        eventPublisher.publish(event);
    }

    private record BankTemplate(String description, BigDecimal amount) {
    }
}
