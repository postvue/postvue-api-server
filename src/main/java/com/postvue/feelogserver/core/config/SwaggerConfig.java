package com.postvue.feelogserver.core.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openApi() {
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER).name("Authorization");
		SecurityRequirement schemaRequirement = new SecurityRequirement().addList("bearerAuth");

		return new OpenAPI()
			.components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
			.security(Collections.singletonList(schemaRequirement))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("Feelog API")
			.description("Feelog API")
			.version("0.0.1");
	}
}
