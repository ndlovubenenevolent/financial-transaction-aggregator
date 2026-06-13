package com.fintech.aggregator.aggregator.repository;

import com.fintech.aggregator.aggregator.entity.ApiKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:api_key_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class ApiKeyRepositoryTest {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Test
    void shouldFindActiveApiKeysAndUpdateLastUsedAt() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        apiKeyRepository.save(ApiKey.builder()
                .clientName("active-client")
                .apiKeyHash(encoder.encode("active-key"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build());
        apiKeyRepository.save(ApiKey.builder()
                .clientName("inactive-client")
                .apiKeyHash(encoder.encode("inactive-key"))
                .active(false)
                .createdAt(LocalDateTime.now())
                .build());

        List<ApiKey> activeKeys = apiKeyRepository.findAllByActiveTrue();

        assertThat(activeKeys).hasSize(1);
        assertThat(activeKeys.get(0).getClientName()).isEqualTo("active-client");

        apiKeyRepository.updateLastUsedAt(activeKeys.get(0).getId(), LocalDateTime.of(2026, 6, 13, 10, 0));

        ApiKey updated = apiKeyRepository.findById(activeKeys.get(0).getId()).orElseThrow();
        assertThat(updated.getLastUsedAt()).isEqualTo(LocalDateTime.of(2026, 6, 13, 10, 0));
    }
}
