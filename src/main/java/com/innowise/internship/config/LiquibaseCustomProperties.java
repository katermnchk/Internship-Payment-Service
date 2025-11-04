package com.innowise.internship.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.liquibase")
public record LiquibaseCustomProperties(
       boolean enabled,
       String changeLog,
       String url
) {

}