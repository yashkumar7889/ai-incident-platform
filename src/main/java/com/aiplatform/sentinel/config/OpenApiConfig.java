package com.aiplatform.sentinel.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI incidentPlatformOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("AI Incident Platform API")

                        .version("v1.0")

                        .description("""
                                Backend service for AI-powered Incident Management Platform.

                                Features:
                                - Incident CRUD
                                - AI-based Incident Classification (Upcoming)
                                - Semantic Search (Upcoming)
                                - RAG-powered Assistant (Upcoming)
                                """)

                        .contact(new Contact()
                                .name("Yash")
                                .email("yashkumarsinghpatwa7889@gmail.com")
                                .url("https://github.com/yashkumar7889/ai-incident-platform")))

                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development")
                ))

                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://github.com/your-github"));
    }
}