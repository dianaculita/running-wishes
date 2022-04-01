package com.project.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class SwaggerConfiguration {

    private static final String SCHEME_NAME = "Bearer Authorization";
    private static final String SCHEME = "bearer";

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("Running Wishes Charity Platform")
                .packagesToScan("com.project")
                .pathsToMatch("/**")
                .build();
    }

    /**
     * Configuration for bearer authorization scheme
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().components(new Components().addSecuritySchemes(SCHEME_NAME, createSecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME);
    }

}
