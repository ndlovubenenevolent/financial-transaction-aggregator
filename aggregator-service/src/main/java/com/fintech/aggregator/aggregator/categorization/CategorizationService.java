package com.fintech.aggregator.aggregator.categorization;

import com.fintech.aggregator.common.enums.TransactionCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorizationService {

    private final List<CategorizationRule> rules;

    public TransactionCategory categorize(String description) {
        if (description == null || description.isBlank()) {
            return TransactionCategory.OTHER;
        }
        String normalized = description.toLowerCase();
        return rules.stream()
                .sorted(Comparator.comparingInt(CategorizationRule::order))
                .filter(rule -> rule.matches(normalized))
                .map(CategorizationRule::category)
                .findFirst()
                .orElse(TransactionCategory.OTHER);
    }
}
