package com.fintech.aggregator.investment.service;

import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import com.fintech.aggregator.investment.kafka.KafkaTransactionProducer;
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

    private static final List<InvestmentTemplate> TEMPLATES = List.of(
            new InvestmentTemplate("Share Purchase", new BigDecimal("-2500.00")),
            new InvestmentTemplate("ETF Purchase", new BigDecimal("-1500.00")),
            new InvestmentTemplate("Dividend Payment", new BigDecimal("350.00"))
    );

    private final KafkaTransactionProducer producer;

    @Scheduled(fixedDelayString = "${investment.simulator.interval-ms:60000}")
    public void generateTransaction() {
        InvestmentTemplate template = TEMPLATES.get(ThreadLocalRandom.current().nextInt(TEMPLATES.size()));
        String customerId = CUSTOMERS.get(ThreadLocalRandom.current().nextInt(CUSTOMERS.size()));

        InvestmentTransactionEvent event = InvestmentTransactionEvent.builder()
                .tradeId("INV-" + UUID.randomUUID())
                .investorId(customerId)
                .instrumentDescription(template.description())
                .netAmount(template.amount())
                .settlementDate(Instant.now())
                .build();

        producer.publish(event);
    }

    private record InvestmentTemplate(String description, BigDecimal amount) {
    }
}
