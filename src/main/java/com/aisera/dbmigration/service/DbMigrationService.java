package com.aisera.dbmigration.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class DbMigrationService {

    DatabaseFactory databaseFactory;

    public DbMigrationService(DatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void migrate() {
        databaseFactory.createSchema();
    }
}
