package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ihm.controller", "domain.service", "persistence", "org.example"}) // Scan for components in our existing packages
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
