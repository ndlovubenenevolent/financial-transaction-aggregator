package com.fintech.aggregator.aggregator.categorization;

import com.fintech.aggregator.aggregator.categorization.rules.EntertainmentRule;
import com.fintech.aggregator.aggregator.categorization.rules.GroceriesRule;
import com.fintech.aggregator.aggregator.categorization.rules.IncomeRule;
import com.fintech.aggregator.aggregator.categorization.rules.InvestmentsRule;
import com.fintech.aggregator.aggregator.categorization.rules.TransportRule;
import com.fintech.aggregator.aggregator.categorization.rules.TransfersRule;
import com.fintech.aggregator.common.enums.TransactionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategorizationServiceTest {

    private CategorizationService categorizationService;

    @BeforeEach
    void setUp() {
        categorizationService = new CategorizationService(List.of(
                new IncomeRule(),
                new TransportRule(),
                new EntertainmentRule(),
                new GroceriesRule(),
                new InvestmentsRule(),
                new TransfersRule()
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "Salary Payment, INCOME",
            "Monthly Salary, INCOME",
            "Uber ride, TRANSPORT",
            "Bolt trip, TRANSPORT",
            "Netflix subscription, ENTERTAINMENT",
            "Checkers groceries, GROCERIES",
            "Woolworths food, GROCERIES",
            "Share Purchase, INVESTMENTS",
            "ETF Purchase, INVESTMENTS",
            "EFT Transfer outbound, TRANSFERS",
            "Unknown merchant, OTHER"
    })
    void shouldCategorizeDescriptions(String description, TransactionCategory expected) {
        assertThat(categorizationService.categorize(description)).isEqualTo(expected);
    }

    @Test
    void shouldReturnOtherForBlankDescription() {
        assertThat(categorizationService.categorize("  ")).isEqualTo(TransactionCategory.OTHER);
        assertThat(categorizationService.categorize(null)).isEqualTo(TransactionCategory.OTHER);
    }
}
