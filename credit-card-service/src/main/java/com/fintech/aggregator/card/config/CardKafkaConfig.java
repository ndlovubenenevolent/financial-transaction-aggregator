package com.fintech.aggregator.card.config;

import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import com.fintech.aggregator.kafka.EventMetadata;
import com.fintech.aggregator.kafka.KafkaEventPublisher;
import com.fintech.aggregator.kafka.KafkaProducerFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class CardKafkaConfig {

    @Bean
    public KafkaTemplate<String, CardTransactionEvent> cardKafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return KafkaProducerFactoryBuilder.createTemplate(bootstrapServers);
    }

    @Bean
    public KafkaEventPublisher<CardTransactionEvent> cardEventPublisher(
            KafkaTemplate<String, CardTransactionEvent> cardKafkaTemplate) {
        return new KafkaEventPublisher<>(
                cardKafkaTemplate,
                KafkaTopics.CARD_TRANSACTIONS,
                event -> EventMetadata.of(
                        event.getCardholderId(),
                        event.getAuthId(),
                        event.getAuthId()),
                "card");
    }
}
