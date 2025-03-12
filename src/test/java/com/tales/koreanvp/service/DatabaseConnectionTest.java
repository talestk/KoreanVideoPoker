package com.tales.koreanvp.service;

import com.tales.koreanvp.service.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class DatabaseConnectionTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @BeforeAll
    static void setUp() {
        // Start the MySQL container
        mysqlContainer.start();

        // Override the DatabaseConnection URL, USER, and PASSWORD with container values
        System.setProperty("DB_URL", mysqlContainer.getJdbcUrl());
        System.setProperty("DB_USER", mysqlContainer.getUsername());
        System.setProperty("DB_PASSWORD", mysqlContainer.getPassword());
    }

    @AfterAll
    static void tearDown() {
        // Stop the MySQL container
        mysqlContainer.stop();
    }

    @Test
    void testGetConnection() throws SQLException {
        // Arrange
        DatabaseConnection databaseConnection = new DatabaseConnection();

        // Act
        Connection connection = databaseConnection.getConnection();

        // Assert
        assertNotNull(connection, "Connection should not be null");
        assertFalse(connection.isClosed(), "Connection should be open");

        // Clean up
        connection.close();
    }

    @Test
    void testGetConnectionWithInvalidCredentials() {
        // Arrange
        DatabaseConnection databaseConnection = new DatabaseConnection();
        System.setProperty("DB_PASSWORD", "wrongpassword"); // Set invalid password

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, databaseConnection::getConnection,
                "Expected SQLException due to invalid credentials");

        assertTrue(exception.getMessage().contains("Access denied"),
                "Exception message should indicate access denial");
    }
}