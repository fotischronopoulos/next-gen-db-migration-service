package com.aisera.dbmigration.service;

import com.aisera.dbmigration.config.DatabaseConfig;
import io.agroal.api.AgroalDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.sql.Statement;

@Singleton
public class DatabaseFactory {

    AgroalDataSource dataSource;
    DatabaseConfig dbConfig;

    public DatabaseFactory(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @PostConstruct
    void init() {
        this.dataSource = dbConfig.getDataSource();
    }

    public void createSchema() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS my_schema");
        } catch (Exception e) {
            // Handle exception (e.g., log error)
            System.out.println(e.getMessage());
        }
    }
}
