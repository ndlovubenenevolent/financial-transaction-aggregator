package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionPersistenceService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public void saveIfNotExists(Transaction transaction) {
        if (transactionRepository.existsBySourceAndExternalTransactionId(
                transaction.getSource(), transaction.getExternalTransactionId())) {
            log.info("Skipping duplicate transaction source={} externalId={}",
                    transaction.getSource(), transaction.getExternalTransactionId());
            return;
        }
        transactionRepository.save(transaction);
        log.info("Persisted transaction source={} externalId={} customerId={}",
                transaction.getSource(), transaction.getExternalTransactionId(), transaction.getCustomerId());
    }
}
