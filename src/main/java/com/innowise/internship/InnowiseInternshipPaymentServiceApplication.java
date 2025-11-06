package com.innowise.internship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InnowiseInternshipPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InnowiseInternshipPaymentServiceApplication.class, args);
    }

}
