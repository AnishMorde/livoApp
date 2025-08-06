package com.example.livoApp.livoApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LivoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivoAppApplication.class, args);
	}

}
