package com.pfetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PfetrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PfetrackerApplication.class, args);
    }
}