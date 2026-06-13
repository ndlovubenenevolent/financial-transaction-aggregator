package com.fintech.aggregator.aggregator.kafka.handler;

import com.fintech.aggregator.aggregator.entity.Transaction;

/**
 * Port for ingesting a typed source event (Dependency Inversion).
 * New sources add a new implementation without changing the ingestion pipeline.
 */
public interface SourceTransactionHandler<T> {

    String customerId(T event);

    String eventId(T event);

    Transaction normalize(T event);
}
