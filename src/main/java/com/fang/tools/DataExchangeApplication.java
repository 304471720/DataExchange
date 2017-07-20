package com.fang.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class DataExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataExchangeApplication.class, args);
	}

}
