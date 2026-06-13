package com.fintech.aggregator.aggregator.kafka;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.kafka.handler.BankTransactionHandler;
import com.fintech.aggregator.aggregator.kafka.handler.CardTransactionHandler;
import com.fintech.aggregator.aggregator.kafka.handler.InvestmentTransactionHandler;
import com.fintech.aggregator.aggregator.service.CategoryEnricher;
import com.fintech.aggregator.aggregator.service.TransactionPersistenceService;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.common.events.CardTransactionEvent;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import org.junit.jupiter.api.BeforeEach;
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
    private TransactionIngestionService ingestionService;
    @Mock
    private BankTransactionHandler bankTransactionHandler;
    @Mock
    private CardTransactionHandler cardTransactionHandler;
    @Mock
    private InvestmentTransactionHandler investmentTransactionHandler;

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

        listener.onBankTransaction(event);

        verify(ingestionService).ingest(event, bankTransactionHandler);
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

        listener.onCardTransaction(event);

        verify(ingestionService).ingest(event, cardTransactionHandler);
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

        listener.onInvestmentTransaction(event);

        verify(ingestionService).ingest(event, investmentTransactionHandler);
    }
}
