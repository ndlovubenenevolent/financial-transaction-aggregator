package com.fintech.aggregator.investment.service;

import com.fintech.aggregator.common.CustomerIdGenerator;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
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

    private static final List<InvestmentTemplate> TEMPLATES = List.of(
            new InvestmentTemplate("Share Purchase", new BigDecimal("-2500.00")),
            new InvestmentTemplate("ETF Purchase", new BigDecimal("-1500.00")),
            new InvestmentTemplate("Dividend Payment", new BigDecimal("350.00"))
    );

    private final KafkaEventPublisher<InvestmentTransactionEvent> eventPublisher;

    @Value("${investment.simulator.customer-pool-size:100}")
    private int customerPoolSize = 100;

    @Scheduled(fixedDelayString = "${investment.simulator.interval-ms:60000}")
    public void generateTransaction() {
        InvestmentTemplate template = TEMPLATES.get(ThreadLocalRandom.current().nextInt(TEMPLATES.size()));
        String customerId = CustomerIdGenerator.randomCustomerId(customerPoolSize);

        InvestmentTransactionEvent event = InvestmentTransactionEvent.builder()
                .tradeId("INV-" + UUID.randomUUID())
                .investorId(customerId)
                .instrumentDescription(template.description())
                .netAmount(template.amount())
                .settlementDate(Instant.now())
                .build();

        log.debug("Simulating investment transaction customerId={} eventId={} instrument={}",
                customerId, event.getTradeId(), template.description());
        eventPublisher.publish(event);
    }

    private record InvestmentTemplate(String description, BigDecimal amount) {
    }
}
