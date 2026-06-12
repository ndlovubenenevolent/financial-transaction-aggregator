package com.fintech.aggregator.aggregator.categorization.rules;

import com.fintech.aggregator.aggregator.categorization.KeywordCategorizationRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransfersRule extends KeywordCategorizationRule {

    public TransfersRule() {
        super(TransactionCategory.TRANSFERS, 60, List.of("eft transfer", "transfer"));
    }
}
