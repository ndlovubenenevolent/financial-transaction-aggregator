package com.fintech.aggregator.aggregator.categorization.rules;

import com.fintech.aggregator.aggregator.categorization.KeywordCategorizationRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransportRule extends KeywordCategorizationRule {

    public TransportRule() {
        super(TransactionCategory.TRANSPORT, 20, List.of("uber", "bolt"));
    }
}
