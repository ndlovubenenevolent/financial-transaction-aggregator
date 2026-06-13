package com.fintech.aggregator.aggregator.security;

import com.fintech.aggregator.aggregator.config.RateLimitProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ClientRateLimiter {

    private final RateLimitProperties properties;
    private final Cache<String, WindowState> windows = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofMinutes(10))
            .build();

    public boolean tryAcquire(String clientName) {
        long currentMinute = Instant.now().getEpochSecond() / 60;
        int limit = properties.getRequestsPerMinute();

        final boolean[] allowed = {false};
        windows.asMap().compute(clientName, (key, window) -> {
            if (window == null || window.minute() != currentMinute) {
                allowed[0] = true;
                return new WindowState(currentMinute, 1);
            }
            if (window.count() >= limit) {
                allowed[0] = false;
                return window;
            }
            allowed[0] = true;
            return new WindowState(currentMinute, window.count() + 1);
        });
        return allowed[0];
    }

    private record WindowState(long minute, int count) {
    }
}
