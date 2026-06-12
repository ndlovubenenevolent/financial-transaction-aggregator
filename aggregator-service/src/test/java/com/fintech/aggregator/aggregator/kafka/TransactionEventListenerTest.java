package com.fintech.aggregator.aggregator.kafka;

import com.fintech.aggregator.aggregator.service.TransactionPersistenceService;
import com.fintech.aggregator.aggregator.service.normalizer.BankTransactionNormalizer;
import com.fintech.aggregator.aggregator.service.normalizer.CardTransactionNormalizer;
import com.fintech.aggregator.aggregator.service.normalizer.InvestmentTransactionNormalizer;
import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionEventListenerTest {

    @Mock
    private BankTransactionNormalizer bankTransactionNormalizer;
    @Mock
    private CardTransactionNormalizer cardTransactionNormalizer;
    @Mock
    private InvestmentTransactionNormalizer investmentTransactionNormalizer;
    @Mock
    private TransactionPersistenceService persistenceService;

    @InjectMocks
    private TransactionEventListener listener;

    @Test
    void shouldProcessBankEvent() {
        BankTransactionEvent event = BankTransactionEvent.builder()
                .transactionRef("BANK-1")
                .accountHolderId("CUST-001")
                .narrative("Salary")
                .value(new BigDecimal("1000"))
                .postedAt(Instant.now())
                .build();
        Transaction transaction = sampleTransaction(TransactionSource.BANK);
        when(bankTransactionNormalizer.normalize(event)).thenReturn(transaction);

        listener.onBankTransaction(event);

        verify(persistenceService).saveIfNotExists(transaction);
    }

    @Test
    void shouldProcessCardEvent() {
        CardTransactionEvent event = CardTransactionEvent.builder()
                .authId("CARD-1")
                .cardholderId("CUST-001")
                .merchantName("Uber")
                .chargeAmount(new BigDecimal("-50"))
                .purchaseDate(Instant.now())
                .build();
        Transaction transaction = sampleTransaction(TransactionSource.CARD);
        when(cardTransactionNormalizer.normalize(event)).thenReturn(transaction);

        listener.onCardTransaction(event);

        verify(persistenceService).saveIfNotExists(transaction);
    }

    @Test
    void shouldProcessInvestmentEvent() {
        InvestmentTransactionEvent event = InvestmentTransactionEvent.builder()
                .tradeId("INV-1")
                .investorId("CUST-001")
                .instrumentDescription("Share Purchase")
                .netAmount(new BigDecimal("-2000"))
                .settlementDate(Instant.now())
                .build();
        Transaction transaction = sampleTransaction(TransactionSource.INVESTMENT);
        when(investmentTransactionNormalizer.normalize(event)).thenReturn(transaction);

        listener.onInvestmentTransaction(event);

        verify(persistenceService).saveIfNotExists(transaction);
    }

    private Transaction sampleTransaction(TransactionSource source) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .externalTransactionId("EXT-1")
                .customerId("CUST-001")
                .source(source)
                .category(TransactionCategory.OTHER)
                .description("Test")
                .amount(BigDecimal.TEN)
                .transactionDate(Instant.now())
                .build();
    }
}
