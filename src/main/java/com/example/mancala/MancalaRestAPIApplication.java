package com.example.mancala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class MancalaRestAPIApplication {
    public static void main(String[] args) {
        SpringApplication.run(MancalaRestAPIApplication.class, args);
    }
}
