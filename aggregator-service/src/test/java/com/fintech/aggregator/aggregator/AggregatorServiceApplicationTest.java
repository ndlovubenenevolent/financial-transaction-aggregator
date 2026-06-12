package com.fintech.aggregator.aggregator;

import com.fintech.aggregator.common.KafkaTopics;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {
                KafkaTopics.BANK_TRANSACTIONS,
                KafkaTopics.CARD_TRANSACTIONS,
                KafkaTopics.INVESTMENT_TRANSACTIONS
        })
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:context_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@DirtiesContext
class AggregatorServiceApplicationTest {

    @Test
    void contextLoads() {
    }
}
