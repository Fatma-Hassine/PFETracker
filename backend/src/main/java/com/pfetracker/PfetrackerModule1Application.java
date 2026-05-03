package com.pfetracker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;        // ← import
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration; // ← import
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
		scanBasePackages = "com.pfetracker",
		exclude = {
        R2dbcAutoConfiguration.class,                       
        R2dbcTransactionManagerAutoConfiguration.class      
    }
)

@EnableScheduling
public class PfetrackerModule1Application {

    public static void main(String[] args) {
        SpringApplication.run(PfetrackerModule1Application.class, args);
    }
}
