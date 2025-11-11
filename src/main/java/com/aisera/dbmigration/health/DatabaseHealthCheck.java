package com.aisera.dbmigration.health;

import com.aisera.dbmigration.config.DatabaseConfig;
import io.agroal.api.AgroalDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.sql.Connection;

@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    AgroalDataSource dataSource;

    @Inject
    DatabaseConfig dbConfig;

    @PostConstruct
    void init() {
        this.dataSource = dbConfig.getDataSource();
    }

    @Override
    public HealthCheckResponse call() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                return HealthCheckResponse.up("Database connection health check");
            } else {
                return HealthCheckResponse.down("Database connection health check");
            }
        } catch (Exception e) {
            return HealthCheckResponse.named("Database connection")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}