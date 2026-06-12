package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.repository.TransactionRepository;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionPersistenceServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionPersistenceService persistenceService;

    @Test
    void shouldSaveWhenNotDuplicate() {
        Transaction transaction = sampleTransaction();
        when(transactionRepository.existsBySourceAndExternalTransactionId(
                TransactionSource.BANK, "BANK-1")).thenReturn(false);

        persistenceService.saveIfNotExists(transaction);

        verify(transactionRepository).save(transaction);
    }

    @Test
    void shouldSkipDuplicate() {
        Transaction transaction = sampleTransaction();
        when(transactionRepository.existsBySourceAndExternalTransactionId(
                TransactionSource.BANK, "BANK-1")).thenReturn(true);

        persistenceService.saveIfNotExists(transaction);

        verify(transactionRepository, never()).save(transaction);
    }

    private Transaction sampleTransaction() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId("BANK-1")
                .customerId("CUST-001")
                .source(TransactionSource.BANK)
                .category(TransactionCategory.INCOME)
                .description("Salary Payment")
                .amount(new BigDecimal("1000.00"))
                .transactionDate(Instant.now())
                .build();
    }
}
