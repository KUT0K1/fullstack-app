package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @RestController
    @CrossOrigin(origins = "http://localhost:5173")
    static class HelloController {
        @GetMapping("/api/hello")
        public String hello() {
            return "Hallo vom Spring Boot Backend 👋";
        }
    }
}