package com.iolivares.codeChallenge.common.configuration;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
	private static final String API_VERSION = "v1";
	private static final String GROUP_NAME = "com.iolivares";
	private static final String API_TITLE = "TRANSACTION Api Rest";
	private static final String API_DESCRIPTION = "This API gives support to the Transactions Manager";
	private static final String REQUEST_HANDLERS_SELECTORS_BASE_PACKAGE = "com.iolivares.codeChallenge.bank.controller";
	
	public static final String API_BASE_PATH = "/api";
	
	@Bean
	@Primary
	public Docket apiV1() {
		return new Docket(DocumentationType.SWAGGER_2) //
								.groupName(GROUP_NAME + " " + API_VERSION) //
								.useDefaultResponseMessages(false) //
								.apiInfo(apiInfo(API_VERSION)) //
								.useDefaultResponseMessages(false).select() //
								.paths(regex(".*/" + API_VERSION + "/.*")) //
								.build();
	}
	
	private ApiInfo apiInfo(String version) {
		return new ApiInfoBuilder() //
								.version(version) //
								.title(API_TITLE) //
								.description(API_DESCRIPTION) //
								.build();
	}

}
