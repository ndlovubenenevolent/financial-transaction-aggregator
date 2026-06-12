package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.categorization.CategorizationService;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardTransactionNormalizerTest {

    @Mock
    private CategorizationService categorizationService;

    @InjectMocks
    private CardTransactionNormalizer normalizer;

    @Test
    void shouldNormalizeCardEvent() {
        CardTransactionEvent event = CardTransactionEvent.builder()
                .authId("CARD-123")
                .cardholderId("CUST-002")
                .merchantName("Uber")
                .chargeAmount(new BigDecimal("-85.50"))
                .purchaseDate(Instant.parse("2025-06-01T12:00:00Z"))
                .build();
        when(categorizationService.categorize("Uber")).thenReturn(TransactionCategory.TRANSPORT);

        Transaction result = normalizer.normalize(event);

        assertThat(result.getSource()).isEqualTo(TransactionSource.CARD);
        assertThat(result.getCategory()).isEqualTo(TransactionCategory.TRANSPORT);
        assertThat(result.getAmount()).isEqualByComparingTo("-85.50");
    }
}
