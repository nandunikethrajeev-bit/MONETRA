package com.monetra.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.monetra")
public class MonetraApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonetraApiApplication.class, args);
    }
}
