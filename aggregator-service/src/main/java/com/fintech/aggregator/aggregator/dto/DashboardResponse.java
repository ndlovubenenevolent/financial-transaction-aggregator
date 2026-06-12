package com.fintech.aggregator.aggregator.dto;

import com.fintech.aggregator.common.enums.TransactionCategory;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class DashboardResponse {
    String customerId;
    BigDecimal totalBalance;
    long transactionCount;
    Map<TransactionCategory, BigDecimal> categoryBreakdown;
    List<TransactionResponse> recentTransactions;
}
