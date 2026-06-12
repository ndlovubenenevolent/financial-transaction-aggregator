package com.fintech.aggregator.aggregator.categorization;

import com.fintech.aggregator.common.enums.TransactionCategory;

import java.util.List;

public abstract class KeywordCategorizationRule implements CategorizationRule {

    private final TransactionCategory category;
    private final int order;
    private final List<String> keywords;

    protected KeywordCategorizationRule(TransactionCategory category, int order, List<String> keywords) {
        this.category = category;
        this.order = order;
        this.keywords = keywords.stream().map(String::toLowerCase).toList();
    }

    @Override
    public boolean matches(String description) {
        return keywords.stream().anyMatch(description::contains);
    }

    @Override
    public TransactionCategory category() {
        return category;
    }

    @Override
    public int order() {
        return order;
    }
}
