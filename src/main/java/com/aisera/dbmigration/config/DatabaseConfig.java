package com.aisera.dbmigration.config;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Singleton
public class DatabaseConfig {

    private AgroalDataSource dataSource;

    @ConfigProperty(name = "aisera_postgres_host", defaultValue = "localhost")
    String dbHost;

    @ConfigProperty(name = "aisera_postgres_port", defaultValue = "5432")
    String dbPort;

    @ConfigProperty(name = "aisera_postgres_db_schema", defaultValue = "test_db")
    String db;

    @ConfigProperty(name = "aisera_postgres_db_user", defaultValue = "test")
    String dbUser;

    @ConfigProperty(name = "aisera_postgres_db_password", defaultValue = "test")
    String dbPassword;

    @PostConstruct
    void init() throws Exception {
        AgroalDataSourceConfigurationSupplier config = new AgroalDataSourceConfigurationSupplier()
                .connectionPoolConfiguration(cp -> cp
                        .connectionFactoryConfiguration(cf -> cf
                                .jdbcUrl(constructJdbcUrl())
                                .credential(new NamePrincipal(dbUser))
                                .credential(new SimplePassword(dbPassword))

                        )
                        .initialSize(10)
                        .maxSize(20)
                        .minSize(10)
                        .idleValidationTimeout(Duration.of(5, ChronoUnit.MINUTES))
                        .maxLifetime(Duration.of(5, ChronoUnit.MINUTES))
                        .acquisitionTimeout(Duration.of(30, ChronoUnit.SECONDS))
                );
        dataSource = AgroalDataSource.from(config);
    }


    @PreDestroy
    void close() throws Exception {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private String constructJdbcUrl() {
        return "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + db;
    }

    @Produces
    public AgroalDataSource getDataSource() {
        return dataSource;
    }
}
