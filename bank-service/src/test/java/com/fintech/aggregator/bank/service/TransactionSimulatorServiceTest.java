package com.fintech.aggregator.bank.service;

import com.fintech.aggregator.bank.kafka.KafkaTransactionProducer;
import com.fintech.aggregator.common.events.BankTransactionEvent;
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
    private KafkaTransactionProducer producer;

    @InjectMocks
    private TransactionSimulatorService simulatorService;

    @Test
    void shouldGenerateAndPublishTransaction() {
        simulatorService.generateTransaction();

        ArgumentCaptor<BankTransactionEvent> captor = ArgumentCaptor.forClass(BankTransactionEvent.class);
        verify(producer).publish(captor.capture());
        BankTransactionEvent event = captor.getValue();
        assertThat(event.getTransactionRef()).startsWith("BANK-");
        assertThat(event.getAccountHolderId()).startsWith("CUST-");
        assertThat(event.getNarrative()).isNotBlank();
        assertThat(event.getValue()).isNotNull();
    }
}
