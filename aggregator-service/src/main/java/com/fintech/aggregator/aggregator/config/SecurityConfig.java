package com.fintech.aggregator.aggregator.config;

import com.fintech.aggregator.aggregator.security.ApiKeyAuthFilter;
import com.fintech.aggregator.aggregator.security.RateLimitFilter;
import com.fintech.aggregator.aggregator.security.SecurityJsonHandlers;
import com.fintech.aggregator.aggregator.security.SecurityPathConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiKeyAuthFilter apiKeyAuthFilter;
    private final RateLimitFilter rateLimitFilter;
    private final SecurityJsonHandlers securityJsonHandlers;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/api-docs/**").permitAll()
                        .requestMatchers(SecurityPathConstants.API_PATH_PATTERN).authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityJsonHandlers)
                        .accessDeniedHandler(securityJsonHandlers))
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, ApiKeyAuthFilter.class)
                .build();
    }
}
