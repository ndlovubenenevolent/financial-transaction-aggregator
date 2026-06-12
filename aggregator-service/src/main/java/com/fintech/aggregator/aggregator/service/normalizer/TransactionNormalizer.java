package com.fintech.aggregator.aggregator.service.normalizer;

import com.fintech.aggregator.aggregator.entity.Transaction;

public interface TransactionNormalizer<T> {

    Transaction normalize(T event);
}
