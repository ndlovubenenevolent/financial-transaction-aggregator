package com.fintech.aggregator.aggregator.categorization.rules;

import com.fintech.aggregator.aggregator.categorization.KeywordCategorizationRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IncomeRule extends KeywordCategorizationRule {

    public IncomeRule() {
        super(TransactionCategory.INCOME, 10, List.of("salary", "dividend payment"));
    }
}
