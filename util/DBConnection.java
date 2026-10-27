
package com.employee.management.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static HikariDataSource dataSource;

    private DBConnection() {
    }

    private static synchronized void initializePool()
            throws SQLException {

        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        Properties properties = new Properties();

        try (InputStream input = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new SQLException(
                        "db.properties not found in src/main/resources");
            }

            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));

            config.setMaximumPoolSize(
                    Integer.parseInt(
                            properties.getProperty("pool.maximumSize", "10")));

            config.setMinimumIdle(
                    Integer.parseInt(
                            properties.getProperty("pool.minimumIdle", "2")));

            config.setPoolName("EmployeeManagementPool");

            try {
                dataSource = new HikariDataSource(config);
            } catch (RuntimeException e) {
                throw new SQLException(
                        "Unable to initialize database connection pool: "
                                + e.getMessage(), e);
            }

        } catch (IOException | IllegalArgumentException e) {
            throw new SQLException(
                    "Unable to load database configuration: "
                            + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        initializePool();
        return dataSource.getConnection();
    }

    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
