package com.fintech.aggregator.aggregator.security;

public final class SecurityPathConstants {

    public static final String API_PATH_PATTERN = "/api/**";

    private SecurityPathConstants() {
    }

    public static boolean requiresApiKey(String requestUri) {
        return requestUri.startsWith("/api/");
    }
}
