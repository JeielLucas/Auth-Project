package com.auth.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		String envDirectory = System.getenv("ENV_DIRECTORY");
		if (envDirectory == null || envDirectory.isEmpty()) {
			envDirectory = "./";
		}

		Dotenv dotenv = Dotenv.configure()
				.directory(envDirectory)
				.load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(ApiApplication.class, args);
	}

}
