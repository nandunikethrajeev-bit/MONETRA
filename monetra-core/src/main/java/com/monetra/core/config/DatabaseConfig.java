package com.monetra.core.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());

    @Value("${spring.datasource.url:#{null}}")
    private String configuredUrl;

    @Value("${spring.datasource.username:root}")
    private String configuredUsername;

    @Value("${spring.datasource.password:}")
    private String configuredPassword;

    @Value("${spring.datasource.driver-class-name:#{null}}")
    private String configuredDriver;

    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource() {
        // Priority 1: Environment Variables
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");

        String targetUrl = envUrl != null ? envUrl : configuredUrl;
        String targetUser = envUser != null ? envUser : configuredUsername;
        String targetPass = envPass != null ? envPass : configuredPassword;

        DataSource ds = null;

        if (targetUrl != null && !targetUrl.trim().isEmpty()) {
            try {
                LOGGER.info("Attempting to connect to configured database: " + targetUrl);
                DataSource candidate = DataSourceBuilder.create()
                        .url(targetUrl)
                        .username(targetUser)
                        .password(targetPass)
                        .build();

                try (Connection conn = candidate.getConnection()) {
                    LOGGER.info("Successfully connected to primary database: " + conn.getMetaData().getDatabaseProductName());
                    ds = candidate;
                }
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Configured primary database not accessible: " + ex.getMessage());
            }
        }

        // If target was not specified, try default local MySQL on localhost:3306
        if (ds == null && targetUrl == null) {
            String defaultMysqlUrl = "jdbc:mysql://localhost:3306/monetra_db?connectTimeout=1000&socketTimeout=1000&createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            try {
                DataSource candidate = DataSourceBuilder.create()
                        .url(defaultMysqlUrl)
                        .username(targetUser != null ? targetUser : "root")
                        .password(targetPass != null ? targetPass : "root")
                        .driverClassName("com.mysql.cj.jdbc.Driver")
                        .build();

                try (Connection conn = candidate.getConnection()) {
                    LOGGER.info("Successfully connected to local MySQL on port 3306");
                    ds = candidate;
                }
            } catch (Exception ex) {
                LOGGER.info("Local MySQL server not running (" + ex.getMessage() + "). Initializing embedded H2 in MySQL compatibility mode.");
            }
        }

        // Fallback: H2 Database in MySQL mode
        if (ds == null) {
            String h2Url = "jdbc:h2:mem:monetradb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
            LOGGER.info("Using embedded database: " + h2Url);
            ds = DataSourceBuilder.create()
                    .url(h2Url)
                    .username("sa")
                    .password("")
                    .driverClassName("org.h2.Driver")
                    .build();
        }

        // Run Flyway migrations
        runFlywayMigration(ds);

        return ds;
    }

    private void runFlywayMigration(DataSource ds) {
        try {
            LOGGER.info("Executing Flyway database migrations...");
            Flyway flyway = Flyway.configure()
                    .dataSource(ds)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .outOfOrder(true)
                    .load();
            flyway.migrate();
            LOGGER.info("Flyway database migrations applied successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Flyway migration error: " + e.getMessage(), e);
            throw new RuntimeException("Failed to run database migrations", e);
        }
    }

    @Bean
    @ConditionalOnMissingBean(JdbcTemplate.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
