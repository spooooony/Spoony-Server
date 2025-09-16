package com.spoony.spoony_server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://www.spoony-dev.n-e.kr"),
                        new Server().url("https://www.spoony-prod.o-r.kr")
                ))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", securityScheme()))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .info(apiInfo());
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("BearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }

    private Info apiInfo() {
        return new Info()
                .title("Spoony API Docs")
                .description("API documentation for Spoony using Swagger UI.")
                .version("1.0.0");
    }
}
