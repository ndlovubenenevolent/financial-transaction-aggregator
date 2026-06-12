package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.repository.TransactionRepository;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionQueryService {

    private final TransactionRepository transactionRepository;

    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Page<Transaction> findByCustomerId(String customerId, Pageable pageable) {
        return transactionRepository.findByCustomerId(customerId, pageable);
    }

    public Page<Transaction> findByCategory(TransactionCategory category, Pageable pageable) {
        return transactionRepository.findByCategory(category, pageable);
    }

    public Page<Transaction> findBySource(TransactionSource source, Pageable pageable) {
        return transactionRepository.findBySource(source, pageable);
    }
}
