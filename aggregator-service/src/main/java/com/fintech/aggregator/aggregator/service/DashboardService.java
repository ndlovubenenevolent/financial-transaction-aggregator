package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.dto.DashboardResponse;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.mapper.TransactionMapper;
import com.fintech.aggregator.aggregator.repository.TransactionRepository;
import com.fintech.aggregator.aggregator.util.CategoryBreakdownMapper;
import com.fintech.aggregator.common.enums.TransactionCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public DashboardResponse getDashboard(String customerId) {
        BigDecimal totalBalance = transactionRepository.sumAmountByCustomerId(customerId);
        long transactionCount = transactionRepository.countByCustomerId(customerId);
        Map<TransactionCategory, BigDecimal> categoryBreakdown = CategoryBreakdownMapper.toMap(
                transactionRepository.sumExpensesByCategory(customerId));
        List<Transaction> recent = transactionRepository.findTop10ByCustomerIdOrderByTransactionDateDesc(customerId);

        return DashboardResponse.builder()
                .customerId(customerId)
                .totalBalance(totalBalance)
                .transactionCount(transactionCount)
                .categoryBreakdown(categoryBreakdown.isEmpty() ? new EnumMap<>(TransactionCategory.class) : categoryBreakdown)
                .recentTransactions(transactionMapper.toResponseList(recent))
                .build();
    }
}
