package com.fintech.aggregator.common;

public final class KafkaTopics {

    public static final String BANK_TRANSACTIONS = "bank-transactions";
    public static final String CARD_TRANSACTIONS = "card-transactions";
    public static final String INVESTMENT_TRANSACTIONS = "investment-transactions";

    public static final String BANK_TRANSACTIONS_DLT = "bank-transactions.DLT";
    public static final String CARD_TRANSACTIONS_DLT = "card-transactions.DLT";
    public static final String INVESTMENT_TRANSACTIONS_DLT = "investment-transactions.DLT";

    private KafkaTopics() {
    }
}
