package com.fintech.aggregator.aggregator.controller;

import com.fintech.aggregator.aggregator.config.CacheConfig;
import com.fintech.aggregator.aggregator.config.RateLimitConfig;
import com.fintech.aggregator.aggregator.config.SecurityConfig;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.mapper.TransactionMapper;
import com.fintech.aggregator.aggregator.security.ApiKeyAuthFilter;
import com.fintech.aggregator.aggregator.security.ClientRateLimiter;
import com.fintech.aggregator.aggregator.security.RateLimitFilter;
import com.fintech.aggregator.aggregator.security.SecurityJsonHandlers;
import com.fintech.aggregator.aggregator.security.SecurityResponseWriter;
import com.fintech.aggregator.aggregator.service.ApiKeyService;
import com.fintech.aggregator.aggregator.service.TransactionQueryService;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import({SecurityConfig.class, CacheConfig.class, RateLimitConfig.class, ApiKeyAuthFilter.class,
        RateLimitFilter.class, SecurityJsonHandlers.class, SecurityResponseWriter.class})
class TransactionControllerTest {

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
    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUpApiKeyValidation() {
        when(apiKeyService.validateAndResolveClient(API_KEY)).thenReturn(Optional.of("demo-client"));
        when(apiKeyService.validateAndResolveClient(null)).thenReturn(Optional.empty());
        when(apiKeyService.validateAndResolveClient("")).thenReturn(Optional.empty());
        when(clientRateLimiter.tryAcquire("demo-client")).thenReturn(true);
    }

    @Test
    void shouldReturnUnauthorizedWithoutApiKey() throws Exception {
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Invalid API Key"));
    }

    @Test
    void shouldReturnTransactionsWithApiKey() throws Exception {
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

        mockMvc.perform(get("/api/v1/transactions").header("X-API-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerId").value("CUST-001"));
    }

    @Test
    void shouldReturnTooManyRequestsWhenRateLimited() throws Exception {
        when(clientRateLimiter.tryAcquire("demo-client")).thenReturn(false);

        mockMvc.perform(get("/api/v1/transactions").header("X-API-Key", API_KEY))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").value("TOO_MANY_REQUESTS"))
                .andExpect(jsonPath("$.message").value("Rate limit exceeded"));
    }

    @Test
    void shouldReturnUnauthorizedForInvalidApiKey() throws Exception {
        when(apiKeyService.validateAndResolveClient("bad-key")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/transactions").header("X-API-Key", "bad-key"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturnByCategoryWithApiKey() throws Exception {
        when(transactionQueryService.findByCategory(eq(TransactionCategory.GROCERIES), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/transactions/category/GROCERIES").header("X-API-Key", API_KEY))
                .andExpect(status().isOk());
    }
}
