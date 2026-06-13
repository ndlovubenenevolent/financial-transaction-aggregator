package com.fintech.aggregator.aggregator.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityJsonHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final SecurityResponseWriter securityResponseWriter;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        log.warn("Authentication entry point triggered method={} uri={}", request.getMethod(), request.getRequestURI());
        securityResponseWriter.writeUnauthorized(response);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        log.warn("Access denied method={} uri={} reason={}",
                request.getMethod(), request.getRequestURI(), accessDeniedException.getMessage());
        securityResponseWriter.writeForbidden(response);
    }
}
