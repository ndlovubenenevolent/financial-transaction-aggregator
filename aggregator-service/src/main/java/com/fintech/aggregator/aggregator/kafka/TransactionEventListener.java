package com.fintech.aggregator.aggregator.kafka;

import com.fintech.aggregator.aggregator.service.TransactionPersistenceService;
import com.fintech.aggregator.aggregator.service.normalizer.BankTransactionNormalizer;
import com.fintech.aggregator.aggregator.service.normalizer.CardTransactionNormalizer;
import com.fintech.aggregator.aggregator.service.normalizer.InvestmentTransactionNormalizer;
import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final BankTransactionNormalizer bankTransactionNormalizer;
    private final CardTransactionNormalizer cardTransactionNormalizer;
    private final InvestmentTransactionNormalizer investmentTransactionNormalizer;
    private final TransactionPersistenceService persistenceService;

    @KafkaListener(topics = KafkaTopics.BANK_TRANSACTIONS, groupId = "aggregator-group",
            containerFactory = "bankKafkaListenerContainerFactory")
    public void onBankTransaction(BankTransactionEvent event) {
        processEvent(event.getAccountHolderId(), event.getTransactionRef(), () ->
                persistenceService.saveIfNotExists(bankTransactionNormalizer.normalize(event)));
    }

    @KafkaListener(topics = KafkaTopics.CARD_TRANSACTIONS, groupId = "aggregator-group",
            containerFactory = "cardKafkaListenerContainerFactory")
    public void onCardTransaction(CardTransactionEvent event) {
        processEvent(event.getCardholderId(), event.getAuthId(), () ->
                persistenceService.saveIfNotExists(cardTransactionNormalizer.normalize(event)));
    }

    @KafkaListener(topics = KafkaTopics.INVESTMENT_TRANSACTIONS, groupId = "aggregator-group",
            containerFactory = "investmentKafkaListenerContainerFactory")
    public void onInvestmentTransaction(InvestmentTransactionEvent event) {
        processEvent(event.getInvestorId(), event.getTradeId(), () ->
                persistenceService.saveIfNotExists(investmentTransactionNormalizer.normalize(event)));
    }

    private void processEvent(String customerId, String eventId, Runnable action) {
        try {
            MDC.put("customerId", customerId);
            MDC.put("eventId", eventId);
            action.run();
        } finally {
            MDC.clear();
        }
    }
}
