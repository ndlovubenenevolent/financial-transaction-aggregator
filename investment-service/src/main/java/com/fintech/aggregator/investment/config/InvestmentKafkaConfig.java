package com.fintech.aggregator.investment.config;

import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import com.fintech.aggregator.kafka.EventMetadata;
import com.fintech.aggregator.kafka.KafkaEventPublisher;
import com.fintech.aggregator.kafka.KafkaProducerFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class InvestmentKafkaConfig {

    @Bean
    public KafkaTemplate<String, InvestmentTransactionEvent> investmentKafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return KafkaProducerFactoryBuilder.createTemplate(bootstrapServers);
    }

    @Bean
    public KafkaEventPublisher<InvestmentTransactionEvent> investmentEventPublisher(
            KafkaTemplate<String, InvestmentTransactionEvent> investmentKafkaTemplate) {
        return new KafkaEventPublisher<>(
                investmentKafkaTemplate,
                KafkaTopics.INVESTMENT_TRANSACTIONS,
                event -> EventMetadata.of(
                        event.getInvestorId(),
                        event.getTradeId(),
                        event.getTradeId()),
                "investment");
    }
}
