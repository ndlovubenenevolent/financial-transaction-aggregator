package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.categorization.CategorizationService;
import com.fintech.aggregator.aggregator.entity.Transaction;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryEnricherTest {

    @Mock
    private CategorizationService categorizationService;

    @InjectMocks
    private CategoryEnricher categoryEnricher;

    @Test
    void shouldApplyCategoryFromDescription() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId("CUST-001")
                .source(TransactionSource.BANK)
                .description("Salary Payment")
                .amount(BigDecimal.TEN)
                .transactionDate(Instant.now())
                .externalTransactionId("BANK-1")
                .build();
        when(categorizationService.categorize("Salary Payment")).thenReturn(TransactionCategory.INCOME);

        Transaction enriched = categoryEnricher.enrich(transaction);

        assertThat(enriched.getCategory()).isEqualTo(TransactionCategory.INCOME);
    }
}
