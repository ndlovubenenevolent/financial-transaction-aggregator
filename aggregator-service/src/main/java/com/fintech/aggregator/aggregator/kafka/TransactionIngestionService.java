package com.fintech.aggregator.aggregator.kafka;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.kafka.handler.SourceTransactionHandler;
import com.fintech.aggregator.aggregator.logging.MdcKeys;
import com.fintech.aggregator.aggregator.service.CategoryEnricher;
import com.fintech.aggregator.aggregator.service.TransactionPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionIngestionService {

    private final CategoryEnricher categoryEnricher;
    private final TransactionPersistenceService persistenceService;

    public <T> void ingest(T event, SourceTransactionHandler<T> handler) {
        String customerId = handler.customerId(event);
        String eventId = handler.eventId(event);
        try {
            MDC.put(MdcKeys.CUSTOMER_ID, customerId);
            MDC.put(MdcKeys.EVENT_ID, eventId);
            log.debug("Ingesting transaction eventId={}", eventId);
            Transaction transaction = categoryEnricher.enrich(handler.normalize(event));
            persistenceService.saveIfNotExists(transaction);
        } catch (RuntimeException ex) {
            log.error("Failed to ingest transaction customerId={} eventId={}", customerId, eventId, ex);
            throw ex;
        } finally {
            MDC.remove(MdcKeys.CUSTOMER_ID);
            MDC.remove(MdcKeys.EVENT_ID);
        }
    }
}
