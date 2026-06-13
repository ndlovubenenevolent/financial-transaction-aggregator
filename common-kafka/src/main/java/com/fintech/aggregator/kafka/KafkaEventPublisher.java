package com.fintech.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String topic;
    private final EventMetadataExtractor<T> metadataExtractor;
    private final String sourceLabel;

    public void publish(T event) {
        EventMetadata metadata = metadataExtractor.extract(event);
        kafkaTemplate.send(topic, metadata.messageKey(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} transaction customerId={} eventId={}",
                                sourceLabel, metadata.customerId(), metadata.eventId(), ex);
                    } else {
                        log.info("Published {} transaction customerId={} eventId={} topic={}",
                                sourceLabel, metadata.customerId(), metadata.eventId(), topic);
                    }
                });
    }
}
