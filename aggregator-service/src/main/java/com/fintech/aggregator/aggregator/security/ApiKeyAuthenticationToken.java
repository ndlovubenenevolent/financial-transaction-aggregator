package com.fintech.aggregator.aggregator.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String clientName;

    public ApiKeyAuthenticationToken(String clientName) {
        super(Collections.emptyList());
        this.clientName = clientName;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return clientName;
    }

    @Override
    public String getName() {
        return clientName;
    }
}
