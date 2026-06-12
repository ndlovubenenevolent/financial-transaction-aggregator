package com.fintech.aggregator.aggregator.dto;

import com.fintech.aggregator.common.enums.TransactionCategory;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Map;

@Value
@Builder
public class MonthlySummaryResponse {
    String customerId;
    int year;
    int month;
    BigDecimal totalIncome;
    BigDecimal totalExpenses;
    Map<TransactionCategory, BigDecimal> spendingByCategory;
}
