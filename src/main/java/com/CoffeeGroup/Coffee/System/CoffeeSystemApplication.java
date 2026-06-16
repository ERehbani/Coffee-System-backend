package com.CoffeeGroup.Coffee.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CoffeeSystemApplication {

	public static void main(String[] args) {
		System.out.println("jijo");
		SpringApplication.run(CoffeeSystemApplication.class, args);
	}
}