package com.example.hiringsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HiringSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(HiringSysApplication.class, args);
    }

}
