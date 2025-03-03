package com.postvue.feelogserver.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	@Value("${serverUrl.api}")
	private String serverApiUrl;

	@Value("${serverUrl.admin}")
	private String serverAdminUrl;


	@Bean
	public OpenAPI openApi() {
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER).name("Authorization");
		SecurityRequirement schemaRequirement = new SecurityRequirement().addList("bearerAuth");

		// https://에 접근 가능하게 설정
		List<Server> serverList = new ArrayList<>();
		Server serverApiDomain = new Server();
		serverApiDomain.setUrl(serverApiUrl);
		serverList.add(serverApiDomain);

		Server serverAdminDomain = new Server();
		serverAdminDomain.setUrl(serverAdminUrl);
		serverList.add(serverAdminDomain);


		return new OpenAPI()
			.components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
			.security(Collections.singletonList(schemaRequirement))
			.info(apiInfo())
			.servers(serverList);
	}

	private Info apiInfo() {
		return new Info()
			.title("Feelog API")
			.description("지도 위에 남기는 나의 기록")
			.version("0.0.1");
	}
}
