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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyManagementServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ActiveApiKeyCacheEvictor cacheEvictor;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ApiKeyManagementService managementService;

    @BeforeEach
    void setUp() {
        managementService = new ApiKeyManagementService(apiKeyRepository, passwordEncoder, cacheEvictor);
    }

    @Test
    void shouldCreateApiKeyAndEvictCache() {
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
            ApiKey apiKey = invocation.getArgument(0);
            apiKey.setId(1L);
            return apiKey;
        });

        ApiKey created = managementService.createApiKey("new-client", "secret-key");

        assertThat(created.getClientName()).isEqualTo("new-client");
        assertThat(passwordEncoder.matches("secret-key", created.getApiKeyHash())).isTrue();
        verify(cacheEvictor).evictAll();
    }

    @Test
    void shouldDeactivateApiKeyAndEvictCache() {
        ApiKey apiKey = ApiKey.builder()
                .id(1L)
                .clientName("old-client")
                .apiKeyHash("hash")
                .active(true)
                .build();
        when(apiKeyRepository.findById(1L)).thenReturn(Optional.of(apiKey));
        when(apiKeyRepository.save(apiKey)).thenReturn(apiKey);

        managementService.deactivateApiKey(1L);

        assertThat(apiKey.isActive()).isFalse();
        verify(cacheEvictor).evictAll();
    }

    @Test
    void shouldEvictCacheOnRefresh() {
        managementService.refreshCache();
        verify(cacheEvictor).evictAll();
    }
}
