package com.fintech.aggregator.aggregator.categorization.rules;

import com.fintech.aggregator.aggregator.categorization.CategorizationRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategorizationRulesTest {

    private final List<CategorizationRule> rules = List.of(
            new IncomeRule(),
            new TransportRule(),
            new EntertainmentRule(),
            new GroceriesRule(),
            new UtilitiesRule(),
            new InvestmentsRule(),
            new TransfersRule()
    );

    @Test
    void eachRuleShouldMatchExpectedCategory() {
        assertRuleMatches("salary payment", TransactionCategory.INCOME);
        assertRuleMatches("uber trip", TransactionCategory.TRANSPORT);
        assertRuleMatches("netflix", TransactionCategory.ENTERTAINMENT);
        assertRuleMatches("woolworths", TransactionCategory.GROCERIES);
        assertRuleMatches("eskom payment", TransactionCategory.UTILITIES);
        assertRuleMatches("share purchase", TransactionCategory.INVESTMENTS);
        assertRuleMatches("eft transfer", TransactionCategory.TRANSFERS);
    }

    private void assertRuleMatches(String description, TransactionCategory expected) {
        boolean matched = rules.stream()
                .filter(rule -> rule.matches(description.toLowerCase()))
                .anyMatch(rule -> rule.category() == expected);
        assertThat(matched).isTrue();
    }
}
