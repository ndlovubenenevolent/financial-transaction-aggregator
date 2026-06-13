package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.categorization.CategorizationService;
import com.fintech.aggregator.aggregator.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Applies business categorization rules after source-specific field mapping.
 */
@Service
@RequiredArgsConstructor
public class CategoryEnricher {

    private final CategorizationService categorizationService;

    public Transaction enrich(Transaction transaction) {
        transaction.setCategory(categorizationService.categorize(transaction.getDescription()));
        return transaction;
    }
}
