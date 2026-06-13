package com.fintech.aggregator.kafka;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;

public final class KafkaListenerContainerFactoryBuilder {

    private KafkaListenerContainerFactoryBuilder() {
    }

    public static <T> ConcurrentKafkaListenerContainerFactory<String, T> create(
            ConsumerFactory<String, T> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
