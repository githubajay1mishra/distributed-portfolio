package com.systemscale.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IngestionApplication {
    public static void main(String[] args) {
        // Bootstrap the Spring container framework
        SpringApplication.run(IngestionApplication.class, args);
    }
}
