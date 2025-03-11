package com.tales.koreanvp;

import com.tales.koreanvp.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private static final String PASSWORD_FILE = "C:\\Users\\tales\\IdeaProjects\\KoreanVideoPoker\\src\\test\\resources\\test_password.txt";

    @BeforeAll
    static void setUp() throws IOException {
        // Start the MySQL container
        mysqlContainer.start();

        // Create a temporary password file for testing
        try (FileWriter writer = new FileWriter(PASSWORD_FILE)) {
            writer.write("testpassword"); // Write the correct password
        }
    }

    @AfterAll
    static void tearDown() {
        // Stop the MySQL container
        mysqlContainer.stop();

        // Delete the temporary password file
        new File(PASSWORD_FILE).delete();
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
    void testGetConnectionWithInvalidCredentials() throws IOException {
        // Arrange
        // Overwrite the password file with an invalid password
        try (FileWriter writer = new FileWriter(PASSWORD_FILE)) {
            writer.write("wrongpassword"); // Write an invalid password
        }

        DatabaseConnection databaseConnection = new DatabaseConnection();

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, databaseConnection::getConnection,
                "Expected SQLException due to invalid credentials");

        assertTrue(exception.getMessage().contains("Access denied"),
                "Exception message should indicate access denial");
    }
}