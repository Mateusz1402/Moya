package com.ms.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ms.backend.model.Tank;
import com.ms.backend.repository.TankRepository;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }



}
