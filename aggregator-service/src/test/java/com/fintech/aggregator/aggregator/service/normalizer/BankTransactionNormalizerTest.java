package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BankTransactionNormalizerTest {

    private final BankTransactionNormalizer normalizer = new BankTransactionNormalizer();

    @Test
    void shouldNormalizeBankEvent() {
        BankTransactionEvent event = BankTransactionEvent.builder()
                .transactionRef("BANK-123")
                .accountHolderId("CUST-001")
                .narrative("Salary Payment")
                .value(new BigDecimal("45000.00"))
                .postedAt(Instant.parse("2025-06-01T10:00:00Z"))
                .build();

        Transaction result = normalizer.normalize(event);

        assertThat(result.getExternalTransactionId()).isEqualTo("BANK-123");
        assertThat(result.getCustomerId()).isEqualTo("CUST-001");
        assertThat(result.getSource()).isEqualTo(TransactionSource.BANK);
        assertThat(result.getDescription()).isEqualTo("Salary Payment");
        assertThat(result.getCategory()).isNull();
        assertThat(result.getAmount()).isEqualByComparingTo("45000.00");
    }
}
