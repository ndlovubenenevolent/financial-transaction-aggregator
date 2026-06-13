package com.fintech.aggregator.aggregator.kafka;

import com.fintech.aggregator.aggregator.kafka.handler.BankTransactionHandler;
import com.fintech.aggregator.aggregator.kafka.handler.CardTransactionHandler;
import com.fintech.aggregator.aggregator.kafka.handler.InvestmentTransactionHandler;
import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final TransactionIngestionService ingestionService;
    private final BankTransactionHandler bankTransactionHandler;
    private final CardTransactionHandler cardTransactionHandler;
    private final InvestmentTransactionHandler investmentTransactionHandler;

    @KafkaListener(topics = KafkaTopics.BANK_TRANSACTIONS, groupId = "aggregator-group",
            containerFactory = "bankKafkaListenerContainerFactory")
    public void onBankTransaction(BankTransactionEvent event) {
        ingestionService.ingest(event, bankTransactionHandler);
    }

    @KafkaListener(topics = KafkaTopics.CARD_TRANSACTIONS, groupId = "aggregator-group",
            containerFactory = "cardKafkaListenerContainerFactory")
    public void onCardTransaction(CardTransactionEvent event) {
        ingestionService.ingest(event, cardTransactionHandler);
    }

    @KafkaListener(topics = KafkaTopics.INVESTMENT_TRANSACTIONS, groupId = "aggregator-group",
            containerFactory = "investmentKafkaListenerContainerFactory")
    public void onInvestmentTransaction(InvestmentTransactionEvent event) {
        ingestionService.ingest(event, investmentTransactionHandler);
    }
}
