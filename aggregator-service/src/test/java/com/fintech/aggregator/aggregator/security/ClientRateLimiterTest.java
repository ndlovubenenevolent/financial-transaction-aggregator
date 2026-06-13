package com.fintech.aggregator.aggregator.security;

import com.fintech.aggregator.aggregator.config.RateLimitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientRateLimiterTest {

    private ClientRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setEnabled(true);
        properties.setRequestsPerMinute(3);
        rateLimiter = new ClientRateLimiter(properties);
    }

    @Test
    void shouldAllowRequestsUpToLimit() {
        assertThat(rateLimiter.tryAcquire("demo-client")).isTrue();
        assertThat(rateLimiter.tryAcquire("demo-client")).isTrue();
        assertThat(rateLimiter.tryAcquire("demo-client")).isTrue();
    }

    @Test
    void shouldRejectRequestsAboveLimit() {
        for (int i = 0; i < 3; i++) {
            assertThat(rateLimiter.tryAcquire("demo-client")).isTrue();
        }
        assertThat(rateLimiter.tryAcquire("demo-client")).isFalse();
    }

    @Test
    void shouldTrackLimitsPerClient() {
        for (int i = 0; i < 3; i++) {
            assertThat(rateLimiter.tryAcquire("client-a")).isTrue();
        }
        assertThat(rateLimiter.tryAcquire("client-a")).isFalse();
        assertThat(rateLimiter.tryAcquire("client-b")).isTrue();
    }
}
