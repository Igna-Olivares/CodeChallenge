package com.iolivares.codeChallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EntityScan("com.iolivares.codeChallenge.bank.model.repository")
@EnableJpaRepositories("com.iolivares.codeChallenge.bank.repository")
@EnableSwagger2
@SpringBootApplication
public class CodeChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeChallengeApplication.class, args);
	}

}
