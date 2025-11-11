package com.aisera.dbmigration.service;

import com.aisera.dbmigration.config.DatabaseConfig;
import io.quarkus.liquibase.LiquibaseFactory;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.collections4.MapUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class DbMigrationService {

    DatabaseFactory databaseFactory;

    private static final String DB_CHANGE_LOG_DIR = "db/changelog/";

    @Inject
    DatabaseConfig databaseConfig;


    public DbMigrationService(DatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void migrate() {
        databaseFactory.createSchema();
        runLiquibase();
    }

    public void runLiquibase() {
        Map<String, String> queryParams = initDbAuthParams();
        String url = retrieveDatabaseUrl(databaseConfig.constructJdbcUrl() + "?currentSchema=tenant_store", queryParams);
        // TODO ask EFi regarding when creating schema or database is needed from credentials perspective

        try (Connection connection = DriverManager.getConnection(url)) {
            Database database = liquibase.database.DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            String changeLogFile = DB_CHANGE_LOG_DIR + "tenant_store/db.changelog-tenant-store-master.yml";
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.validate();
            liquibase.update();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Map<String, String> initDbAuthParams() {
        Map<String, String> dbAuthParams = new HashMap<>();
        dbAuthParams.put("user", "test");
        dbAuthParams.put("password", "test");

        return dbAuthParams;
    }

    private String retrieveDatabaseUrl(String url, Map<String, String> queryParams) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }

        if (false) {
            queryParams.put("useSsl", "true");
        }

        if (MapUtils.isNotEmpty(queryParams)) {
            boolean isFirstParam = true;
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.append(isFirstParam ? "?" : "&");
                urlBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());

                isFirstParam = false;
            }
        }
        return urlBuilder.toString();
    }
}
