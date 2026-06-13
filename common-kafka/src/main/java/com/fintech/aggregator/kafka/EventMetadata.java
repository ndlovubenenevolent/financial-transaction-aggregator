package com.fintech.aggregator.kafka;

public record EventMetadata(String customerId, String eventId, String messageKey) {

    public static EventMetadata of(String customerId, String eventId, String messageKey) {
        return new EventMetadata(customerId, eventId, messageKey);
    }
}
