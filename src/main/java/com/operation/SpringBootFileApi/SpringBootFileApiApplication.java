package com.operation.SpringBootFileApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example")
@EnableSwagger2
public class SpringBootFileApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootFileApiApplication.class, args);
	}

}
