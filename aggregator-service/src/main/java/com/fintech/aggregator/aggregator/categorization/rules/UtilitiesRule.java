package com.fintech.aggregator.aggregator.categorization.rules;

import com.fintech.aggregator.aggregator.categorization.KeywordCategorizationRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UtilitiesRule extends KeywordCategorizationRule {

    public UtilitiesRule() {
        super(TransactionCategory.UTILITIES, 45, List.of("eskom", "municipality", "water bill", "electricity"));
    }
}
