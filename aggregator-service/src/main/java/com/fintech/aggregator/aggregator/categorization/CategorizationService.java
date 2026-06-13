package com.fintech.aggregator.aggregator.categorization;

import com.fintech.aggregator.common.enums.TransactionCategory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CategorizationService {

    private final List<CategorizationRule> rules;

    public CategorizationService(List<CategorizationRule> rules) {
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(CategorizationRule::order))
                .toList();
    }

    public TransactionCategory categorize(String description) {
        if (description == null || description.isBlank()) {
            return TransactionCategory.OTHER;
        }
        String normalized = description.toLowerCase();
        return rules.stream()
                .filter(rule -> rule.matches(normalized))
                .map(CategorizationRule::category)
                .findFirst()
                .orElse(TransactionCategory.OTHER);
    }
}
