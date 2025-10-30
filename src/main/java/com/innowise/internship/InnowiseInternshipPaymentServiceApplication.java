package com.innowise.internship;

import com.innowise.internship.config.LiquibaseCustomProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LiquibaseCustomProperties.class)
public class InnowiseInternshipPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InnowiseInternshipPaymentServiceApplication.class, args);
    }

}
