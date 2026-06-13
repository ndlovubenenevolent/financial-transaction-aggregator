package com.fintech.aggregator.aggregator.repository;

import com.fintech.aggregator.aggregator.entity.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Read-side analytics queries (Interface Segregation).
 * Consumers that only need aggregations do not depend on CRUD/pagination methods.
 */
public interface TransactionAnalyticsRepository extends Repository<Transaction, UUID> {

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
