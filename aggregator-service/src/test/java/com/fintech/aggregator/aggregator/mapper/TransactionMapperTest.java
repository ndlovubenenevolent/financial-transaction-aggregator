package com.fintech.aggregator.aggregator.mapper;

import com.fintech.aggregator.aggregator.dto.TransactionResponse;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void shouldMapEntityToResponse() {
        Transaction entity = Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId("BANK-1")
                .customerId("CUST-001")
                .source(TransactionSource.BANK)
                .description("Salary")
                .amount(new BigDecimal("1000"))
                .transactionDate(Instant.parse("2025-06-01T00:00:00Z"))
                .category(TransactionCategory.INCOME)
                .createdAt(Instant.parse("2025-06-01T01:00:00Z"))
                .build();

        TransactionResponse response = mapper.toResponse(entity);

        assertThat(response.getCustomerId()).isEqualTo("CUST-001");
        assertThat(response.getSource()).isEqualTo(TransactionSource.BANK);
        assertThat(mapper.toResponseList(List.of(entity))).hasSize(1);
    }
}
