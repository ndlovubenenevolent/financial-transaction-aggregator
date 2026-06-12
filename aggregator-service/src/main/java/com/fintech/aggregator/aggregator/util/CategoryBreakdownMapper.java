package com.fintech.aggregator.aggregator.util;

import com.fintech.aggregator.common.enums.TransactionCategory;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CategoryBreakdownMapper {

    private CategoryBreakdownMapper() {
    }

    public static Map<TransactionCategory, BigDecimal> toMap(List<Object[]> rows) {
        Map<TransactionCategory, BigDecimal> result = new EnumMap<>(TransactionCategory.class);
        for (Object[] row : rows) {
            result.put((TransactionCategory) row[0], (BigDecimal) row[1]);
        }
        return result;
    }
}
