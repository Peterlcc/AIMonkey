package com.peter;

import com.peter.service.AlgorithmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AiMonkeyApplication implements CommandLineRunner {

	@Autowired
	private AlgorithmService algorithmService;

	public static void main(String[] args) {
		SpringApplication.run(AiMonkeyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		algorithmService.run();
	}
}
