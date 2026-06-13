package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.dto.MonthlySummaryResponse;
import com.fintech.aggregator.aggregator.repository.TransactionAnalyticsRepository;
import com.fintech.aggregator.aggregator.util.CategoryBreakdownMapper;
import com.fintech.aggregator.common.enums.TransactionCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SummaryService {

    private final TransactionAnalyticsRepository analyticsRepository;

    public MonthlySummaryResponse getMonthlySummary(String customerId, YearMonth yearMonth) {
        Instant start = yearMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = yearMonth.plusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        BigDecimal totalIncome = analyticsRepository.sumIncomeForPeriod(customerId, start, end);
        BigDecimal totalExpenses = analyticsRepository.sumExpensesForPeriod(customerId, start, end);
        Map<TransactionCategory, BigDecimal> spendingByCategory = CategoryBreakdownMapper.toMap(
                analyticsRepository.sumMonthlyExpensesByCategory(customerId, start, end));

        return MonthlySummaryResponse.builder()
                .customerId(customerId)
                .year(yearMonth.getYear())
                .month(yearMonth.getMonthValue())
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .spendingByCategory(spendingByCategory.isEmpty() ? emptyCategoryMap() : spendingByCategory)
                .build();
    }

    private Map<TransactionCategory, BigDecimal> emptyCategoryMap() {
        return new EnumMap<>(TransactionCategory.class);
    }
}
