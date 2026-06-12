package com.fintech.aggregator.aggregator.repository;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    boolean existsBySourceAndExternalTransactionId(TransactionSource source, String externalTransactionId);

    Page<Transaction> findByCustomerId(String customerId, Pageable pageable);

    Page<Transaction> findByCategory(TransactionCategory category, Pageable pageable);

    Page<Transaction> findBySource(TransactionSource source, Pageable pageable);

    List<Transaction> findTop10ByCustomerIdOrderByTransactionDateDesc(String customerId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.customerId = :customerId")
    BigDecimal sumAmountByCustomerId(@Param("customerId") String customerId);

    long countByCustomerId(String customerId);

    @Query("""
            SELECT t.category, COALESCE(SUM(ABS(t.amount)), 0)
            FROM Transaction t
            WHERE t.customerId = :customerId AND t.amount < 0
            GROUP BY t.category
            """)
    List<Object[]> sumExpensesByCategory(@Param("customerId") String customerId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.customerId = :customerId
              AND t.amount > 0
              AND t.transactionDate >= :start
              AND t.transactionDate < :end
            """)
    BigDecimal sumIncomeForPeriod(
            @Param("customerId") String customerId,
            @Param("start") Instant start,
            @Param("end") Instant end);

    @Query("""
            SELECT COALESCE(SUM(ABS(t.amount)), 0)
            FROM Transaction t
            WHERE t.customerId = :customerId
              AND t.amount < 0
              AND t.transactionDate >= :start
              AND t.transactionDate < :end
            """)
    BigDecimal sumExpensesForPeriod(
            @Param("customerId") String customerId,
            @Param("start") Instant start,
            @Param("end") Instant end);

    @Query("""
            SELECT t.category, COALESCE(SUM(ABS(t.amount)), 0)
            FROM Transaction t
            WHERE t.customerId = :customerId
              AND t.amount < 0
              AND t.transactionDate >= :start
              AND t.transactionDate < :end
            GROUP BY t.category
            """)
    List<Object[]> sumMonthlyExpensesByCategory(
            @Param("customerId") String customerId,
            @Param("start") Instant start,
            @Param("end") Instant end);
}
