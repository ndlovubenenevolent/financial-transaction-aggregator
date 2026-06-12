package com.fintech.aggregator.aggregator.config;

import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, BankTransactionEvent> bankConsumerFactory() {
        return buildConsumerFactory(BankTransactionEvent.class);
    }

    @Bean
    public ConsumerFactory<String, CardTransactionEvent> cardConsumerFactory() {
        return buildConsumerFactory(CardTransactionEvent.class);
    }

    @Bean
    public ConsumerFactory<String, InvestmentTransactionEvent> investmentConsumerFactory() {
        return buildConsumerFactory(InvestmentTransactionEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BankTransactionEvent> bankKafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler) {
        return buildListenerFactory(bankConsumerFactory(), errorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CardTransactionEvent> cardKafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler) {
        return buildListenerFactory(cardConsumerFactory(), errorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InvestmentTransactionEvent> investmentKafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler) {
        return buildListenerFactory(investmentConsumerFactory(), errorHandler);
    }

    @Bean
    public ProducerFactory<String, Object> dltProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> dltKafkaTemplate() {
        return new KafkaTemplate<>(dltProducerFactory());
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> dltKafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dltKafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> type) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "aggregator-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.fintech.aggregator.common.events");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, type.getName());
        return new DefaultKafkaConsumerFactory<>(config);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildListenerFactory(
            ConsumerFactory<String, T> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
