package com.fintech.aggregator.bank.config;

import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.kafka.EventMetadata;
import com.fintech.aggregator.kafka.KafkaEventPublisher;
import com.fintech.aggregator.kafka.KafkaProducerFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class BankKafkaConfig {

    @Bean
    public KafkaTemplate<String, BankTransactionEvent> bankKafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return KafkaProducerFactoryBuilder.createTemplate(bootstrapServers);
    }

    @Bean
    public KafkaEventPublisher<BankTransactionEvent> bankEventPublisher(
            KafkaTemplate<String, BankTransactionEvent> bankKafkaTemplate) {
        return new KafkaEventPublisher<>(
                bankKafkaTemplate,
                KafkaTopics.BANK_TRANSACTIONS,
                event -> EventMetadata.of(
                        event.getAccountHolderId(),
                        event.getTransactionRef(),
                        event.getTransactionRef()),
                "bank");
    }
}
