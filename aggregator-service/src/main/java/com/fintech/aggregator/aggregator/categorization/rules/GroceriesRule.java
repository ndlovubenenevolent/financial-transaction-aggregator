package com.fintech.aggregator.aggregator.categorization.rules;

import com.fintech.aggregator.aggregator.categorization.KeywordCategorizationRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroceriesRule extends KeywordCategorizationRule {

    public GroceriesRule() {
        super(TransactionCategory.GROCERIES, 40, List.of("checkers", "pick n pay", "woolworths"));
    }
}
