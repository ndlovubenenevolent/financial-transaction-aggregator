package com.fintech.aggregator.aggregator.categorization.rules;

import com.fintech.aggregator.aggregator.categorization.KeywordCategorizationRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntertainmentRule extends KeywordCategorizationRule {

    public EntertainmentRule() {
        super(TransactionCategory.ENTERTAINMENT, 30, List.of("netflix", "spotify"));
    }
}
