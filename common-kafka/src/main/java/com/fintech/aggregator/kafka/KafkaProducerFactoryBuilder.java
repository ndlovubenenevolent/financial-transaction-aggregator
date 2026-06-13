package com.fintech.aggregator.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public final class KafkaProducerFactoryBuilder {

    private KafkaProducerFactoryBuilder() {
    }

    public static Map<String, Object> baseProducerConfig(String bootstrapServers) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return config;
    }

    public static <T> ProducerFactory<String, T> createProducerFactory(String bootstrapServers) {
        return new DefaultKafkaProducerFactory<>(baseProducerConfig(bootstrapServers));
    }

    public static <T> KafkaTemplate<String, T> createTemplate(String bootstrapServers) {
        return new KafkaTemplate<>(createProducerFactory(bootstrapServers));
    }
}
