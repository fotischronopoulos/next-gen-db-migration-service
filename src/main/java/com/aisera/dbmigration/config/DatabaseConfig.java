package com.aisera.dbmigration.config;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
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

    @ConfigProperty(name = "agroal_pool_init_size", defaultValue = "10")
    int poolInitSize;

    @ConfigProperty(name = "agroal_pool_max_size", defaultValue = "10")
    int poolMaxSize;

    @ConfigProperty(name = "agroal_pool_min_size", defaultValue = "10")
    int poolMinSize;

    @ConfigProperty(name = "agroal_pool_idle_timeout", defaultValue = "5")
    int poolIdleTimeoutMinutes;

    @ConfigProperty(name = "agroal_pool_max_lifetime_timeout", defaultValue = "5")
    int poolMaxLifetimeMinutes;

    @ConfigProperty(name = "agroal_pool_acquisition_timeout", defaultValue = "30")
    int poolAcquisitionTimeoutSeconds;


    @PostConstruct
    void init() throws Exception {
        AgroalDataSourceConfigurationSupplier config = new AgroalDataSourceConfigurationSupplier()
                .connectionPoolConfiguration(cp -> cp
                        .connectionFactoryConfiguration(cf -> cf
                                .jdbcUrl(constructJdbcUrl())
                                .credential(new NamePrincipal(dbUser))
                                .credential(new SimplePassword(dbPassword))

                        )
                        .initialSize(poolInitSize)
                        .maxSize(poolMaxSize)
                        .minSize(poolMinSize)
                        .idleValidationTimeout(Duration.of(poolIdleTimeoutMinutes, ChronoUnit.MINUTES))
                        .maxLifetime(Duration.of(poolMaxLifetimeMinutes, ChronoUnit.MINUTES))
                        .acquisitionTimeout(Duration.of(poolAcquisitionTimeoutSeconds, ChronoUnit.SECONDS))
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
