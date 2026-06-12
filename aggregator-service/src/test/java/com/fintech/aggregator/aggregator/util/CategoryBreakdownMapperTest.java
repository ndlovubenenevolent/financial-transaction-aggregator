package com.fintech.aggregator.aggregator.util;

import com.fintech.aggregator.common.enums.TransactionCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryBreakdownMapperTest {

    @Test
    void shouldMapRowsToCategoryBreakdown() {
        List<Object[]> rows = List.<Object[]>of(
                new Object[]{TransactionCategory.GROCERIES, new BigDecimal("100.00")},
                new Object[]{TransactionCategory.TRANSPORT, new BigDecimal("50.00")}
        );

        Map<TransactionCategory, BigDecimal> result = CategoryBreakdownMapper.toMap(rows);

        assertThat(result).hasSize(2);
        assertThat(result.get(TransactionCategory.GROCERIES)).isEqualByComparingTo("100.00");
        assertThat(result.get(TransactionCategory.TRANSPORT)).isEqualByComparingTo("50.00");
    }
}
