package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.categorization.CategorizationService;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
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
class InvestmentTransactionNormalizerTest {

    @Mock
    private CategorizationService categorizationService;

    @InjectMocks
    private InvestmentTransactionNormalizer normalizer;

    @Test
    void shouldNormalizeInvestmentEvent() {
        InvestmentTransactionEvent event = InvestmentTransactionEvent.builder()
                .tradeId("INV-123")
                .investorId("CUST-003")
                .instrumentDescription("Share Purchase")
                .netAmount(new BigDecimal("-2500.00"))
                .settlementDate(Instant.parse("2025-06-01T14:00:00Z"))
                .build();
        when(categorizationService.categorize("Share Purchase")).thenReturn(TransactionCategory.INVESTMENTS);

        Transaction result = normalizer.normalize(event);

        assertThat(result.getSource()).isEqualTo(TransactionSource.INVESTMENT);
        assertThat(result.getCategory()).isEqualTo(TransactionCategory.INVESTMENTS);
    }
}
