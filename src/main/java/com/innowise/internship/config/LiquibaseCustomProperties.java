package com.innowise.internship.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.liquibase")
@Data
public class LiquibaseCustomProperties {
    private boolean enabled;
    private String changeLog;
    private String url;
}