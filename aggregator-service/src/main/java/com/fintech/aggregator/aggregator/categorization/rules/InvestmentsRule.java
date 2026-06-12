package com.fintech.aggregator.aggregator.categorization.rules;

import com.fintech.aggregator.aggregator.categorization.KeywordCategorizationRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvestmentsRule extends KeywordCategorizationRule {

    public InvestmentsRule() {
        super(TransactionCategory.INVESTMENTS, 50, List.of("easyequities", "etf purchase", "share purchase"));
    }
}
