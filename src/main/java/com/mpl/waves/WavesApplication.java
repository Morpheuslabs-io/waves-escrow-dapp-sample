package com.mpl.waves;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WavesApplication  {
	private static Logger LOG = LoggerFactory.getLogger(WavesApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WavesApplication.class, args);
	}

}
