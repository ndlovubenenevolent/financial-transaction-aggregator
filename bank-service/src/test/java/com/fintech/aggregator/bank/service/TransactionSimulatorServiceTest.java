package com.fintech.aggregator.bank.service;

import com.fintech.aggregator.common.events.BankTransactionEvent;
import com.fintech.aggregator.kafka.KafkaEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionSimulatorServiceTest {

    @Mock
    private KafkaEventPublisher<BankTransactionEvent> eventPublisher;

    @InjectMocks
    private TransactionSimulatorService simulatorService;

    @Test
    void shouldGenerateAndPublishTransaction() {
        simulatorService.generateTransaction();

        ArgumentCaptor<BankTransactionEvent> captor = ArgumentCaptor.forClass(BankTransactionEvent.class);
        verify(eventPublisher).publish(captor.capture());
        BankTransactionEvent event = captor.getValue();
        assertThat(event.getTransactionRef()).startsWith("BANK-");
        assertThat(event.getAccountHolderId()).matches("CUST-\\d{3}");
        assertThat(event.getNarrative()).isNotBlank();
        assertThat(event.getValue()).isNotNull();
    }
}
