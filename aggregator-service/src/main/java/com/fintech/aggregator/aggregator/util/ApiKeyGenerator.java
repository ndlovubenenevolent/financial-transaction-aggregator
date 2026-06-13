package com.fintech.aggregator.aggregator.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

/**
 * CLI helper for generating API keys and BCrypt hashes compatible with {@code ApiKeyService}.
 * Run via {@code ./scripts/generate-api-key.sh <client-name>}.
 */
public final class ApiKeyGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private ApiKeyGenerator() {
    }

    public static void main(String[] args) {
        String clientName = args.length > 0 ? args[0] : "new-client";
        String plainKey = args.length > 1 ? args[1] : generateKey();
        String hash = new BCryptPasswordEncoder().encode(plainKey);

        System.out.println("Client name:   " + clientName);
        System.out.println("Plain API key: " + plainKey);
        System.out.println();
        System.out.println("-- SQL (plain key is not stored; save it now):");
        System.out.printf(
                "INSERT INTO api_keys (client_name, api_key_hash, active, created_at)%n"
                        + "VALUES ('%s', '%s', TRUE, NOW());%n",
                clientName.replace("'", "''"),
                hash);
    }

    private static String generateKey() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        StringBuilder key = new StringBuilder(64);
        for (byte b : bytes) {
            key.append(String.format("%02x", b));
        }
        return key.toString();
    }
}
