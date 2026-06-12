package com.fintech.aggregator.aggregator.controller;

import com.fintech.aggregator.aggregator.dto.DashboardResponse;
import com.fintech.aggregator.aggregator.dto.MonthlySummaryResponse;
import com.fintech.aggregator.aggregator.dto.TransactionResponse;
import com.fintech.aggregator.aggregator.mapper.TransactionMapper;
import com.fintech.aggregator.aggregator.service.DashboardService;
import com.fintech.aggregator.aggregator.service.SummaryService;
import com.fintech.aggregator.aggregator.service.TransactionQueryService;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transactions", description = "Financial transaction aggregation APIs")
public class TransactionController {

    private final TransactionQueryService transactionQueryService;
    private final SummaryService summaryService;
    private final DashboardService dashboardService;
    private final TransactionMapper transactionMapper;

    @GetMapping("/transactions")
    @Operation(summary = "Get all transactions with pagination and sorting")
    public Page<TransactionResponse> getAllTransactions(
            @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return transactionQueryService.findAll(pageable).map(transactionMapper::toResponse);
    }

    @GetMapping("/customers/{customerId}/transactions")
    @Operation(summary = "Get transactions for a specific customer")
    public Page<TransactionResponse> getCustomerTransactions(
            @PathVariable @NotBlank String customerId,
            @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return transactionQueryService.findByCustomerId(customerId, pageable).map(transactionMapper::toResponse);
    }

    @GetMapping("/transactions/category/{category}")
    @Operation(summary = "Filter transactions by category")
    public Page<TransactionResponse> getByCategory(
            @PathVariable TransactionCategory category,
            @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return transactionQueryService.findByCategory(category, pageable).map(transactionMapper::toResponse);
    }

    @GetMapping("/transactions/source/{source}")
    @Operation(summary = "Filter transactions by source")
    public Page<TransactionResponse> getBySource(
            @PathVariable TransactionSource source,
            @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return transactionQueryService.findBySource(source, pageable).map(transactionMapper::toResponse);
    }

    @GetMapping("/customers/{customerId}/monthly-summary")
    @Operation(summary = "Get monthly spending summary for a customer")
    public MonthlySummaryResponse getMonthlySummary(
            @PathVariable @NotBlank String customerId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month) {
        YearMonth yearMonth = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.now();
        return summaryService.getMonthlySummary(customerId, yearMonth);
    }

    @GetMapping("/customers/{customerId}/dashboard")
    @Operation(summary = "Get customer dashboard with balance, breakdown, and recent transactions")
    public DashboardResponse getDashboard(@PathVariable @NotBlank String customerId) {
        return dashboardService.getDashboard(customerId);
    }
}
