package com.fintech.aggregator.investment.kafka;

import com.fintech.aggregator.common.KafkaTopics;
import com.fintech.aggregator.common.events.InvestmentTransactionEvent;
import com.fintech.aggregator.kafka.KafkaEventPublisher;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import com.fintech.aggregator.investment.service.TransactionSimulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {KafkaTopics.INVESTMENT_TRANSACTIONS})
@DirtiesContext
class KafkaEventPublisherTest {

    @Autowired
    private KafkaEventPublisher<InvestmentTransactionEvent> eventPublisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private TransactionSimulatorService transactionSimulatorService;

    @Test
    void shouldPublishInvestmentTransaction() {
        InvestmentTransactionEvent event = InvestmentTransactionEvent.builder()
                .tradeId("INV-TEST-1")
                .investorId("CUST-001")
                .instrumentDescription("Share Purchase")
                .netAmount(new BigDecimal("-2500.00"))
                .settlementDate(Instant.now())
                .build();

        eventPublisher.publish(event);

        Map<String, Object> consumerProps = new HashMap<>(KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker));
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.fintech.aggregator.common.events");
        consumerProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, InvestmentTransactionEvent.class.getName());

        try (Consumer<String, InvestmentTransactionEvent> consumer = new DefaultKafkaConsumerFactory<>(
                consumerProps, new StringDeserializer(), new JsonDeserializer<>(InvestmentTransactionEvent.class, false)).createConsumer()) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, KafkaTopics.INVESTMENT_TRANSACTIONS);
            ConsumerRecords<String, InvestmentTransactionEvent> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records.count()).isGreaterThanOrEqualTo(1);
            assertThat(records.iterator().next().value().getTradeId()).isEqualTo("INV-TEST-1");
        }
    }
}
