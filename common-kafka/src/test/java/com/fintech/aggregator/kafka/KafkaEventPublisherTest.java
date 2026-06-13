package com.fintech.aggregator.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldPublishWithMetadataKey() {
        KafkaEventPublisher<String> publisher = new KafkaEventPublisher<>(
                kafkaTemplate,
                "test-topic",
                event -> EventMetadata.of("CUST-001", "EVT-1", "EVT-1"),
                "test");
        when(kafkaTemplate.send(eq("test-topic"), eq("EVT-1"), eq("payload")))
                .thenReturn(CompletableFuture.completedFuture(new SendResult<>(null, null)));

        publisher.publish("payload");

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("test-topic"), keyCaptor.capture(), eq("payload"));
        assertThat(keyCaptor.getValue()).isEqualTo("EVT-1");
    }
}
