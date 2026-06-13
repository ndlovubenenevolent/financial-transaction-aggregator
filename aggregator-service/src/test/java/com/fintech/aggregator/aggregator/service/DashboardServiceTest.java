package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.dto.DashboardResponse;
import com.fintech.aggregator.aggregator.dto.TransactionResponse;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.mapper.TransactionMapper;
import com.fintech.aggregator.aggregator.repository.TransactionAnalyticsRepository;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TransactionAnalyticsRepository analyticsRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldBuildDashboard() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId("CUST-001")
                .source(TransactionSource.CARD)
                .category(TransactionCategory.GROCERIES)
                .description("Checkers")
                .amount(new BigDecimal("-100.00"))
                .transactionDate(Instant.now())
                .externalTransactionId("CARD-1")
                .build();
        TransactionResponse response = TransactionResponse.builder()
                .id(transaction.getId())
                .customerId("CUST-001")
                .build();

        when(analyticsRepository.sumAmountByCustomerId("CUST-001")).thenReturn(new BigDecimal("5000.00"));
        when(analyticsRepository.countByCustomerId("CUST-001")).thenReturn(3L);
        when(analyticsRepository.sumExpensesByCategory("CUST-001"))
                .thenReturn(List.<Object[]>of(new Object[]{TransactionCategory.GROCERIES, new BigDecimal("100.00")}));
        when(analyticsRepository.findTop10ByCustomerIdOrderByTransactionDateDesc("CUST-001"))
                .thenReturn(List.of(transaction));
        when(transactionMapper.toResponseList(List.of(transaction))).thenReturn(List.of(response));

        DashboardResponse dashboard = dashboardService.getDashboard("CUST-001");

        assertThat(dashboard.getTotalBalance()).isEqualByComparingTo("5000.00");
        assertThat(dashboard.getTransactionCount()).isEqualTo(3L);
        assertThat(dashboard.getRecentTransactions()).hasSize(1);
    }

    @Test
    void shouldReturnEmptyCategoryBreakdownWhenNoExpenses() {
        when(analyticsRepository.sumAmountByCustomerId("CUST-001")).thenReturn(BigDecimal.ZERO);
        when(analyticsRepository.countByCustomerId("CUST-001")).thenReturn(0L);
        when(analyticsRepository.sumExpensesByCategory("CUST-001")).thenReturn(List.of());
        when(analyticsRepository.findTop10ByCustomerIdOrderByTransactionDateDesc("CUST-001")).thenReturn(List.of());
        when(transactionMapper.toResponseList(List.of())).thenReturn(List.of());

        DashboardResponse dashboard = dashboardService.getDashboard("CUST-001");

        assertThat(dashboard.getCategoryBreakdown()).isEmpty();
        assertThat(dashboard.getTransactionCount()).isZero();
    }
}
