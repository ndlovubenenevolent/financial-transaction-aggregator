package com.fintech.aggregator.aggregator.security;

import com.fintech.aggregator.aggregator.logging.MdcKeys;
import com.fintech.aggregator.aggregator.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-Key";

    private final ApiKeyService apiKeyService;
    private final SecurityResponseWriter securityResponseWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!SecurityPathConstants.requiresApiKey(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        var clientName = apiKeyService.validateAndResolveClient(apiKey);

        if (clientName.isEmpty()) {
            log.warn("Rejected unauthenticated request method={} uri={}", request.getMethod(), request.getRequestURI());
            securityResponseWriter.writeUnauthorized(response);
            return;
        }

        try {
            MDC.put(MdcKeys.CLIENT_NAME, clientName.get());
            SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthenticationToken(clientName.get()));
            log.info("Authenticated client={} method={} uri={}",
                    clientName.get(), request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MdcKeys.CLIENT_NAME);
        }
    }
}
