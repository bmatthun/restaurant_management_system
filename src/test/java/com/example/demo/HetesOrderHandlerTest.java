package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class HetesOrderHandlerTest {

    @Test
    @DisplayName("A main metódus elindítja a Spring Boot alkalmazást")
    void mainStartsSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            HetesOrderHandler.main(args);

            springApplication.verify(() -> SpringApplication.run(HetesOrderHandler.class, args));
        }
    }
}
