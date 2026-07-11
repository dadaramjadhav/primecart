package com.primecart.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI primeCartOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("PrimeCart Product Service API")
                        .description("REST APIs for managing products, categories, and brands.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("PrimeCart Team")
                                .email("support@primecart.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("PrimeCart Documentation")
                        .url("https://github.com/your-username/primecart"));
    }
}