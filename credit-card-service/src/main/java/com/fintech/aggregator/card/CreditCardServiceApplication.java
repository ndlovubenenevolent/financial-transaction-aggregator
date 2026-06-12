package com.fintech.aggregator.card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CreditCardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditCardServiceApplication.class, args);
    }
}
