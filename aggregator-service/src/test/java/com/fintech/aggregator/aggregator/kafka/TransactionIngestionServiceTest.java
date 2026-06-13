package com.fintech.aggregator.aggregator.kafka;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.kafka.handler.BankTransactionHandler;
import com.fintech.aggregator.aggregator.service.CategoryEnricher;
import com.fintech.aggregator.aggregator.service.TransactionPersistenceService;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionIngestionServiceTest {

    @Mock
    private CategoryEnricher categoryEnricher;
    @Mock
    private TransactionPersistenceService persistenceService;
    @Mock
    private BankTransactionHandler bankTransactionHandler;

    @InjectMocks
    private TransactionIngestionService ingestionService;

    @Test
    void shouldEnrichAndPersistTransaction() {
        BankTransactionEvent event = BankTransactionEvent.builder()
                .transactionRef("BANK-1")
                .accountHolderId("CUST-001")
                .narrative("Salary")
                .value(new BigDecimal("1000"))
                .postedAt(Instant.now())
                .build();
        Transaction normalized = sampleTransaction();
        Transaction enriched = sampleTransaction();
        enriched.setCategory(TransactionCategory.INCOME);

        when(bankTransactionHandler.customerId(event)).thenReturn("CUST-001");
        when(bankTransactionHandler.eventId(event)).thenReturn("BANK-1");
        when(bankTransactionHandler.normalize(event)).thenReturn(normalized);
        when(categoryEnricher.enrich(normalized)).thenReturn(enriched);

        ingestionService.ingest(event, bankTransactionHandler);

        verify(persistenceService).saveIfNotExists(enriched);
    }

    @Test
    void shouldRethrowWhenPersistenceFails() {
        BankTransactionEvent event = BankTransactionEvent.builder()
                .transactionRef("BANK-1")
                .accountHolderId("CUST-001")
                .narrative("Salary")
                .value(new BigDecimal("1000"))
                .postedAt(Instant.now())
                .build();
        Transaction normalized = sampleTransaction();

        when(bankTransactionHandler.customerId(event)).thenReturn("CUST-001");
        when(bankTransactionHandler.eventId(event)).thenReturn("BANK-1");
        when(bankTransactionHandler.normalize(event)).thenReturn(normalized);
        when(categoryEnricher.enrich(normalized)).thenReturn(normalized);
        doThrow(new RuntimeException("DB down")).when(persistenceService).saveIfNotExists(normalized);

        assertThatThrownBy(() -> ingestionService.ingest(event, bankTransactionHandler))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB down");
    }

    private Transaction sampleTransaction() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId("BANK-1")
                .customerId("CUST-001")
                .source(TransactionSource.BANK)
                .description("Salary")
                .amount(BigDecimal.TEN)
                .transactionDate(Instant.now())
                .build();
    }
}
