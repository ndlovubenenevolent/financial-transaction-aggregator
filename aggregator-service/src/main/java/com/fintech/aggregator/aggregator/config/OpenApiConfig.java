package com.fintech.aggregator.aggregator.config;

import com.fintech.aggregator.aggregator.security.ApiKeyAuthFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aggregatorOpenApi() {
        String apiKeyScheme = "ApiKeyAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Financial Transaction Aggregator API")
                        .description("REST APIs for aggregated customer financial transactions")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes(apiKeyScheme, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name(ApiKeyAuthFilter.API_KEY_HEADER)))
                .addSecurityItem(new SecurityRequirement().addList(apiKeyScheme));
    }
}
