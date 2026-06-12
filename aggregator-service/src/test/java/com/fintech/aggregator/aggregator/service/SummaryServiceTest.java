package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.dto.MonthlySummaryResponse;
import com.fintech.aggregator.aggregator.repository.TransactionRepository;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SummaryService summaryService;

    @Test
    void shouldBuildMonthlySummary() {
        YearMonth yearMonth = YearMonth.of(2025, 6);
        when(transactionRepository.sumIncomeForPeriod(eq("CUST-001"), any(Instant.class), any(Instant.class)))
                .thenReturn(new BigDecimal("45000.00"));
        when(transactionRepository.sumExpensesForPeriod(eq("CUST-001"), any(Instant.class), any(Instant.class)))
                .thenReturn(new BigDecimal("1500.00"));
        when(transactionRepository.sumMonthlyExpensesByCategory(eq("CUST-001"), any(Instant.class), any(Instant.class)))
                .thenReturn(List.<Object[]>of(new Object[]{TransactionCategory.GROCERIES, new BigDecimal("650.00")}));

        MonthlySummaryResponse response = summaryService.getMonthlySummary("CUST-001", yearMonth);

        assertThat(response.getTotalIncome()).isEqualByComparingTo("45000.00");
        assertThat(response.getTotalExpenses()).isEqualByComparingTo("1500.00");
        assertThat(response.getSpendingByCategory()).containsEntry(TransactionCategory.GROCERIES, new BigDecimal("650.00"));
    }

    @Test
    void shouldReturnEmptySpendingMapWhenNoExpenses() {
        YearMonth yearMonth = YearMonth.of(2025, 6);
        when(transactionRepository.sumIncomeForPeriod(eq("CUST-001"), any(Instant.class), any(Instant.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumExpensesForPeriod(eq("CUST-001"), any(Instant.class), any(Instant.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumMonthlyExpensesByCategory(eq("CUST-001"), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of());

        MonthlySummaryResponse response = summaryService.getMonthlySummary("CUST-001", yearMonth);

        assertThat(response.getSpendingByCategory()).isEmpty();
    }
}
