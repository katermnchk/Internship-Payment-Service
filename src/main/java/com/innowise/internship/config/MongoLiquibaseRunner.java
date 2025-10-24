package com.innowise.internship.config;

import jakarta.annotation.PostConstruct;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoLiquibaseRunner {

    private final LiquibaseCustomProperties properties;

    @PostConstruct
    public void runMigrations() {
        if (!properties.isEnabled()) {
            return;
        }

        String changeLogPath = properties.getChangeLog();
        String url = properties.getUrl();

        try (Database database = DatabaseFactory.getInstance().openDatabase(
                url, null, null, null, new ClassLoaderResourceAccessor()
        )) {
            Liquibase liquibase = new Liquibase(
                    changeLogPath,
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update();
        } catch (Exception e) {
            throw new RuntimeException("Failed to run Liquibase migrations", e);
        }
    }
}