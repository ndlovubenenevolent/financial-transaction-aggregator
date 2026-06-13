package com.fintech.aggregator.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
public final class KafkaConsumerErrorHandlerFactory {

    private static final long RETRY_INTERVAL_MS = 1000L;
    private static final long MAX_RETRIES = 3L;

    private KafkaConsumerErrorHandlerFactory() {
    }

    public static DefaultErrorHandler create(KafkaTemplate<String, Object> dltKafkaTemplate) {
        DeadLetterPublishingRecoverer dltRecoverer = new DeadLetterPublishingRecoverer(
                dltKafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.error("Failed to process message after retries; publishing to DLT topic={} partition={} offset={} key={}",
                    record.topic(), record.partition(), record.offset(), record.key(), exception);
            dltRecoverer.accept(record, exception);
        }, new FixedBackOff(RETRY_INTERVAL_MS, MAX_RETRIES));
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                MessageConversionException.class,
                SerializationException.class);
        errorHandler.setRetryListeners((record, exception, deliveryAttempt) ->
                log.warn("Retrying failed message topic={} partition={} offset={} attempt={}",
                        record.topic(), record.partition(), record.offset(), deliveryAttempt));
        return errorHandler;
    }
}
