package com.fintech.aggregator.kafka;

@FunctionalInterface
public interface EventMetadataExtractor<T> {

    EventMetadata extract(T event);
}
