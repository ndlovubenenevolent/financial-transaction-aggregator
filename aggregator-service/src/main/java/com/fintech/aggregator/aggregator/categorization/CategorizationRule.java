package com.fintech.aggregator.aggregator.categorization;

import com.fintech.aggregator.common.enums.TransactionCategory;

public interface CategorizationRule {

    boolean matches(String description);

    TransactionCategory category();

    int order();
}
