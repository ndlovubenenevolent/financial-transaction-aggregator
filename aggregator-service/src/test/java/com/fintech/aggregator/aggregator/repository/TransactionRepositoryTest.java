package com.fintech.aggregator.aggregator.repository;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:aggregator_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldPersistAndFindTransaction() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId("CUST-001")
                .source(TransactionSource.BANK)
                .category(TransactionCategory.INCOME)
                .description("Salary Payment")
                .amount(new BigDecimal("1000.00"))
                .transactionDate(Instant.now())
                .externalTransactionId("BANK-REPO-1")
                .build();

        transactionRepository.save(transaction);

        assertThat(transactionRepository.existsBySourceAndExternalTransactionId(
                TransactionSource.BANK, "BANK-REPO-1")).isTrue();
        assertThat(transactionRepository.findByCustomerId("CUST-001", org.springframework.data.domain.Pageable.unpaged()))
                .hasSize(1);
    }
}
