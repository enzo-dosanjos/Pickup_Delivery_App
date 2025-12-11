package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot entry point for the Pickup/Delivery backend.
 * Uses an explicit component scan to pick up controllers and services outside the default package.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"ihm.controller", "domain.service", "persistence", "org.example"}) // Scan for components in our existing packages
public class Application {

    /**
     * Starts the Spring application context.
     * @param args CLI arguments passed to Spring Boot.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
