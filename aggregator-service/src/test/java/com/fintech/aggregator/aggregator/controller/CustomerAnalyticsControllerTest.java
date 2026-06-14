package com.fintech.aggregator.aggregator.controller;

import com.fintech.aggregator.aggregator.config.CacheConfig;
import com.fintech.aggregator.aggregator.config.RateLimitConfig;
import com.fintech.aggregator.aggregator.config.SecurityConfig;
import com.fintech.aggregator.aggregator.dto.DashboardResponse;
import com.fintech.aggregator.aggregator.dto.MonthlySummaryResponse;
import com.fintech.aggregator.aggregator.mapper.TransactionMapper;
import com.fintech.aggregator.aggregator.security.ApiKeyAuthFilter;
import com.fintech.aggregator.aggregator.security.ClientRateLimiter;
import com.fintech.aggregator.aggregator.security.RateLimitFilter;
import com.fintech.aggregator.aggregator.security.SecurityJsonHandlers;
import com.fintech.aggregator.aggregator.security.SecurityResponseWriter;
import com.fintech.aggregator.aggregator.service.ApiKeyService;
import com.fintech.aggregator.aggregator.service.DashboardService;
import com.fintech.aggregator.aggregator.service.SummaryService;
import com.fintech.aggregator.aggregator.service.TransactionQueryService;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerAnalyticsController.class)
@Import({SecurityConfig.class, CacheConfig.class, RateLimitConfig.class, ApiKeyAuthFilter.class,
        RateLimitFilter.class, SecurityJsonHandlers.class, SecurityResponseWriter.class})
class CustomerAnalyticsControllerTest {

    private static final String API_KEY = "demo-api-key";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiKeyService apiKeyService;

    @MockBean
    private ClientRateLimiter clientRateLimiter;

    @MockBean
    private TransactionQueryService transactionQueryService;

    @MockBean
    private SummaryService summaryService;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUpApiKeyValidation() {
        when(apiKeyService.validateAndResolveClient(API_KEY)).thenReturn(Optional.of("demo-client"));
        when(clientRateLimiter.tryAcquire("demo-client")).thenReturn(true);
    }

    @Test
    void shouldReturnDashboardWithApiKey() throws Exception {
        when(dashboardService.getDashboard("CUST-001")).thenReturn(
                DashboardResponse.builder()
                        .customerId("CUST-001")
                        .totalBalance(new BigDecimal("5000"))
                        .transactionCount(2)
                        .categoryBreakdown(new EnumMap<>(TransactionCategory.class))
                        .recentTransactions(List.of())
                        .build());

        mockMvc.perform(get("/api/v1/customers/CUST-001/dashboard").header("X-API-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST-001"));
    }

    @Test
    void shouldReturnCustomerTransactionsWithApiKey() throws Exception {
        when(transactionQueryService.findByCustomerId(eq("CUST-001"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/customers/CUST-001/transactions").header("X-API-Key", API_KEY))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnMonthlySummaryWithApiKey() throws Exception {
        when(summaryService.getMonthlySummary(eq("CUST-001"), any())).thenReturn(
                MonthlySummaryResponse.builder()
                        .customerId("CUST-001")
                        .year(2025)
                        .month(6)
                        .totalIncome(new BigDecimal("45000"))
                        .totalExpenses(new BigDecimal("1500"))
                        .spendingByCategory(new EnumMap<>(TransactionCategory.class))
                        .build());

        mockMvc.perform(get("/api/v1/customers/CUST-001/monthly-summary").header("X-API-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(45000));
    }
}
