package com.innowise.internship.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.liquibase")
@Data
public class LiquibaseCustomProperties {
    private boolean enabled;
    private String changeLog;
    private String url;
}