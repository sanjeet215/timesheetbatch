package com.asiczen.timecalculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimecalculationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimecalculationApplication.class, args);
	}

}
