package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CardTransactionNormalizerTest {

    private final CardTransactionNormalizer normalizer = new CardTransactionNormalizer();

    @Test
    void shouldNormalizeCardEvent() {
        CardTransactionEvent event = CardTransactionEvent.builder()
                .authId("CARD-123")
                .cardholderId("CUST-002")
                .merchantName("Uber")
                .chargeAmount(new BigDecimal("-85.50"))
                .purchaseDate(Instant.parse("2025-06-01T12:00:00Z"))
                .build();

        Transaction result = normalizer.normalize(event);

        assertThat(result.getSource()).isEqualTo(TransactionSource.CARD);
        assertThat(result.getDescription()).isEqualTo("Uber");
        assertThat(result.getCategory()).isNull();
        assertThat(result.getAmount()).isEqualByComparingTo("-85.50");
    }
}
