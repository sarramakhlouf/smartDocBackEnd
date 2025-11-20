package com.miniprojet.smartdoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SmartDocBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartDocBackendApplication.class, args);
	}

}
