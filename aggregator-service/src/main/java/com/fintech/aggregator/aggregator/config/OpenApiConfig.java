package com.fintech.aggregator.aggregator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aggregatorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Financial Transaction Aggregator API")
                        .description("REST APIs for aggregated customer financial transactions")
                        .version("1.0.0"));
    }
}
