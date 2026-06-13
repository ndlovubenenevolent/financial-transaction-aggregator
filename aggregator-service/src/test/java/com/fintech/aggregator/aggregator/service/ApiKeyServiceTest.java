package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.entity.ApiKey;
import com.fintech.aggregator.aggregator.repository.ApiKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ActiveApiKeyLoader activeApiKeyLoader;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ApiKeyService apiKeyService;

    @BeforeEach
    void setUp() {
        ApiKeyValidator validator = new ApiKeyValidator(activeApiKeyLoader, passwordEncoder);
        ApiKeyUsageTracker usageTracker = new ApiKeyUsageTracker(apiKeyRepository);
        apiKeyService = new ApiKeyService(validator, usageTracker);
    }

    @Test
    void shouldValidateMatchingApiKey() {
        String rawKey = "demo-api-key";
        ApiKey apiKey = ApiKey.builder()
                .id(1L)
                .clientName("demo-client")
                .apiKeyHash(passwordEncoder.encode(rawKey))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        when(activeApiKeyLoader.loadActiveApiKeys()).thenReturn(List.of(apiKey));

        Optional<String> client = apiKeyService.validateAndResolveClient(rawKey);

        assertThat(client).contains("demo-client");
        verify(apiKeyRepository).updateLastUsedAt(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void shouldRejectInvalidApiKey() {
        ApiKey apiKey = ApiKey.builder()
                .id(1L)
                .clientName("demo-client")
                .apiKeyHash(passwordEncoder.encode("other-key"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        when(activeApiKeyLoader.loadActiveApiKeys()).thenReturn(List.of(apiKey));

        Optional<String> client = apiKeyService.validateAndResolveClient("wrong-key");

        assertThat(client).isEmpty();
    }

    @Test
    void shouldRejectBlankApiKey() {
        assertThat(apiKeyService.validateAndResolveClient(null)).isEmpty();
        assertThat(apiKeyService.validateAndResolveClient("  ")).isEmpty();
    }
}
