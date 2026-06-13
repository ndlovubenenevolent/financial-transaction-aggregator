package com.fintech.aggregator.common;

import java.util.concurrent.ThreadLocalRandom;

public final class CustomerIdGenerator {

    private CustomerIdGenerator() {
    }

    public static String randomCustomerId(int poolSize) {
        if (poolSize < 1) {
            throw new IllegalArgumentException("poolSize must be at least 1");
        }
        int id = ThreadLocalRandom.current().nextInt(1, poolSize + 1);
        return "CUST-" + String.format("%03d", id);
    }
}
