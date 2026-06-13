package com.fintech.aggregator.aggregator.security;

import com.fintech.aggregator.aggregator.config.RateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties rateLimitProperties;
    private final ClientRateLimiter clientRateLimiter;
    private final SecurityResponseWriter securityResponseWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!rateLimitProperties.isEnabled() || !SecurityPathConstants.requiresApiKey(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientName = authentication.getName();
        if (!clientRateLimiter.tryAcquire(clientName)) {
            log.warn("Rate limit exceeded client={} method={} uri={}",
                    clientName, request.getMethod(), request.getRequestURI());
            securityResponseWriter.writeTooManyRequests(response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
