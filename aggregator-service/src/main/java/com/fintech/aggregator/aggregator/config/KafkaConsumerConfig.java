package com.fintech.aggregator.aggregator.config;

import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import com.fintech.aggregator.kafka.KafkaConsumerErrorHandlerFactory;
import com.fintech.aggregator.kafka.KafkaConsumerFactoryBuilder;
import com.fintech.aggregator.kafka.KafkaDltProducerFactoryBuilder;
import com.fintech.aggregator.kafka.KafkaListenerContainerFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:aggregator-group}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, BankTransactionEvent> bankConsumerFactory() {
        return KafkaConsumerFactoryBuilder.createConsumerFactory(
                bootstrapServers, groupId, BankTransactionEvent.class);
    }

    @Bean
    public ConsumerFactory<String, CardTransactionEvent> cardConsumerFactory() {
        return KafkaConsumerFactoryBuilder.createConsumerFactory(
                bootstrapServers, groupId, CardTransactionEvent.class);
    }

    @Bean
    public ConsumerFactory<String, InvestmentTransactionEvent> investmentConsumerFactory() {
        return KafkaConsumerFactoryBuilder.createConsumerFactory(
                bootstrapServers, groupId, InvestmentTransactionEvent.class);
    }

    @Bean
    public KafkaTemplate<String, Object> dltKafkaTemplate() {
        return KafkaDltProducerFactoryBuilder.createDltTemplate(bootstrapServers);
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> dltKafkaTemplate) {
        return KafkaConsumerErrorHandlerFactory.create(dltKafkaTemplate);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BankTransactionEvent> bankKafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler) {
        return KafkaListenerContainerFactoryBuilder.create(bankConsumerFactory(), errorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CardTransactionEvent> cardKafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler) {
        return KafkaListenerContainerFactoryBuilder.create(cardConsumerFactory(), errorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InvestmentTransactionEvent> investmentKafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler) {
        return KafkaListenerContainerFactoryBuilder.create(investmentConsumerFactory(), errorHandler);
    }
}
