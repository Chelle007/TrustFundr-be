package com.example.trustfundr_be.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.dev-url}")
    private String devUrl;

    @Value("${openapi.staging-url}")
    private String stagingUrl;

    @Value("${openapi.prod-url}")
    private String prodUrl;

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI myOpenAPI() {

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server stagingServer = new Server();
        stagingServer.setUrl(stagingUrl);
        stagingServer.setDescription("Server URL in Staging environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("mc.chellechan@gmail.com");
        contact.setName("Trustfundr");
        contact.setUrl("https://www.trustfundr.com");

        License mitLicense = new License().name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("TrustFundr")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage trustfundr.")
                .termsOfService("https://www.trustfundr.com/terms")
                .license(mitLicense);

        SecurityScheme securityScheme = new SecurityScheme()
                .name(BEARER_AUTH_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI().info(info)
                .servers(List.of(devServer, stagingServer, prodServer))
                .components(new Components().addSecuritySchemes(BEARER_AUTH_SCHEME, securityScheme))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME));
    }
}