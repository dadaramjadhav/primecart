package com.primecart.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String securitySchemeName = "bearerAuth";

    @Bean
    public OpenAPI primeCartOrderOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("PrimeCart Order Service API")
                        .description("REST APIs for managing orders, order items, order status, and order processing.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("PrimeCart Team")
                                .email("support@primecart.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))

                // JWT Bearer Authentication
                .components(new Components()
                        .addSecuritySchemes(
                                securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))

                // Apply JWT authentication globally
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securitySchemeName)
                )

                .externalDocs(new ExternalDocumentation()
                        .description("PrimeCart Documentation")
                        .url("https://github.com/your-username/primecart"));
    }
}