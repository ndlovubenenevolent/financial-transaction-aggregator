package com.fintech.aggregator.aggregator.controller;

import com.fintech.aggregator.aggregator.dto.TransactionResponse;
import com.fintech.aggregator.aggregator.mapper.TransactionMapper;
import com.fintech.aggregator.aggregator.service.TransactionQueryService;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction listing and filtering APIs")
public class TransactionController {

    private final TransactionQueryService transactionQueryService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    @Operation(summary = "Get all transactions with pagination and sorting")
    public Page<TransactionResponse> getAllTransactions(
            @ParameterObject @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return transactionQueryService.findAll(pageable).map(transactionMapper::toResponse);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filter transactions by category")
    public Page<TransactionResponse> getByCategory(
            @PathVariable TransactionCategory category,
            @ParameterObject @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return transactionQueryService.findByCategory(category, pageable).map(transactionMapper::toResponse);
    }

    @GetMapping("/source/{source}")
    @Operation(summary = "Filter transactions by source")
    public Page<TransactionResponse> getBySource(
            @PathVariable TransactionSource source,
            @ParameterObject @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return transactionQueryService.findBySource(source, pageable).map(transactionMapper::toResponse);
    }
}
