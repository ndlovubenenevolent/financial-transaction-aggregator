package com.fintech.aggregator.aggregator.security;

import com.fintech.aggregator.aggregator.config.RateLimitProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RateLimitProperties rateLimitProperties;
    @Mock
    private ClientRateLimiter clientRateLimiter;
    @Mock
    private SecurityResponseWriter securityResponseWriter;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    @Test
    void shouldRejectWhenRateLimitExceeded() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/transactions");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimitProperties.isEnabled()).thenReturn(true);
        when(clientRateLimiter.tryAcquire("demo-client")).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthenticationToken("demo-client"));

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(securityResponseWriter).writeTooManyRequests(response);
        verify(filterChain, never()).doFilter(request, response);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWhenWithinLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/transactions");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimitProperties.isEnabled()).thenReturn(true);
        when(clientRateLimiter.tryAcquire("demo-client")).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthenticationToken("demo-client"));

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(securityResponseWriter, never()).writeTooManyRequests(response);
        SecurityContextHolder.clearContext();
    }
}
