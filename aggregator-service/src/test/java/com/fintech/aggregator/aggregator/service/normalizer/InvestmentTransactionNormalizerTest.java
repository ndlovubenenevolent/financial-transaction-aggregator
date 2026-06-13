package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InvestmentTransactionNormalizerTest {

    private final InvestmentTransactionNormalizer normalizer = new InvestmentTransactionNormalizer();

    @Test
    void shouldNormalizeInvestmentEvent() {
        InvestmentTransactionEvent event = InvestmentTransactionEvent.builder()
                .tradeId("INV-123")
                .investorId("CUST-003")
                .instrumentDescription("Share Purchase")
                .netAmount(new BigDecimal("-2500.00"))
                .settlementDate(Instant.parse("2025-06-01T14:00:00Z"))
                .build();

        Transaction result = normalizer.normalize(event);

        assertThat(result.getSource()).isEqualTo(TransactionSource.INVESTMENT);
        assertThat(result.getDescription()).isEqualTo("Share Purchase");
        assertThat(result.getCategory()).isNull();
    }
}
