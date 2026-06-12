package com.fintech.aggregator.aggregator.controller;

import com.fintech.aggregator.aggregator.dto.DashboardResponse;
import com.fintech.aggregator.aggregator.dto.MonthlySummaryResponse;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.mapper.TransactionMapper;
import com.fintech.aggregator.aggregator.service.DashboardService;
import com.fintech.aggregator.aggregator.service.SummaryService;
import com.fintech.aggregator.aggregator.service.TransactionQueryService;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionQueryService transactionQueryService;

    @MockBean
    private SummaryService summaryService;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private TransactionMapper transactionMapper;

    @Test
    void shouldReturnTransactions() throws Exception {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId("CUST-001")
                .source(TransactionSource.BANK)
                .category(TransactionCategory.INCOME)
                .description("Salary")
                .amount(new BigDecimal("1000"))
                .transactionDate(Instant.now())
                .externalTransactionId("BANK-1")
                .build();
        when(transactionQueryService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionMapper.toResponse(transaction)).thenReturn(
                com.fintech.aggregator.aggregator.dto.TransactionResponse.builder()
                        .id(transaction.getId())
                        .customerId("CUST-001")
                        .build());

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerId").value("CUST-001"));
    }

    @Test
    void shouldReturnDashboard() throws Exception {
        when(dashboardService.getDashboard("CUST-001")).thenReturn(
                DashboardResponse.builder()
                        .customerId("CUST-001")
                        .totalBalance(new BigDecimal("5000"))
                        .transactionCount(2)
                        .categoryBreakdown(new EnumMap<>(TransactionCategory.class))
                        .recentTransactions(List.of())
                        .build());

        mockMvc.perform(get("/api/v1/customers/CUST-001/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST-001"))
                .andExpect(jsonPath("$.transactionCount").value(2));
    }

    @Test
    void shouldReturnByCategory() throws Exception {
        when(transactionQueryService.findByCategory(eq(TransactionCategory.GROCERIES), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/transactions/category/GROCERIES"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBySource() throws Exception {
        when(transactionQueryService.findBySource(eq(TransactionSource.BANK), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/transactions/source/BANK"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnCustomerTransactions() throws Exception {
        when(transactionQueryService.findByCustomerId(eq("CUST-001"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/customers/CUST-001/transactions"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnMonthlySummary() throws Exception {
        when(summaryService.getMonthlySummary(eq("CUST-001"), any())).thenReturn(
                MonthlySummaryResponse.builder()
                        .customerId("CUST-001")
                        .year(2025)
                        .month(6)
                        .totalIncome(new BigDecimal("45000"))
                        .totalExpenses(new BigDecimal("1500"))
                        .spendingByCategory(new EnumMap<>(TransactionCategory.class))
                        .build());

        mockMvc.perform(get("/api/v1/customers/CUST-001/monthly-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(45000));
    }
}
