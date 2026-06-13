package com.fintech.aggregator.aggregator.repository;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    boolean existsBySourceAndExternalTransactionId(TransactionSource source, String externalTransactionId);

    Page<Transaction> findByCustomerId(String customerId, Pageable pageable);

    Page<Transaction> findByCategory(TransactionCategory category, Pageable pageable);

    Page<Transaction> findBySource(TransactionSource source, Pageable pageable);
}
