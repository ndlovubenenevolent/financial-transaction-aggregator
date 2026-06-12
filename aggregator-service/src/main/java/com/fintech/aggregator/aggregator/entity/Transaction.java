package com.fintech.aggregator.aggregator.entity;

import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionCategory category;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @Column(name = "external_transaction_id", nullable = false, length = 100)
    private String externalTransactionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
