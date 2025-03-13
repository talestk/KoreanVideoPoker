package com.tales.koreanvp.repo;

import com.tales.koreanvp.model.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class PlayerRepositoryTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    private static PlayerRepository playerRepository;

    @BeforeAll
    static void setUp() {
        // Start the MySQL container
        mysqlContainer.start();

        // Set system properties for database connection
        System.setProperty("DB_URL", mysqlContainer.getJdbcUrl());
        System.setProperty("DB_USER", mysqlContainer.getUsername());
        System.setProperty("DB_PASSWORD", mysqlContainer.getPassword());

        // Initialize the PlayerRepository
        playerRepository = new PlayerRepository();

        // Create the players table in the test database
        createPlayersTable();
    }

    @AfterAll
    static void tearDown() {
        // Stop the MySQL container
        mysqlContainer.stop();
    }

    // Helper method to create the players table
    private static void createPlayersTable() {
        String query = "CREATE TABLE players (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "password VARCHAR(50) NOT NULL," +
                "credits INT DEFAULT 100," +
                "wins INT DEFAULT 0," +
                "losses INT DEFAULT 0" +
                ")";
        try (Connection conn = DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRegisterPlayer() {
        // Register a new player
        boolean result = playerRepository.registerPlayer("testuser1", "testpassword1");
        assertTrue(result, "Player registration should succeed");

        // Try to register the same player again (should fail)
        boolean duplicateResult = playerRepository.registerPlayer("testuser1", "testpassword1");
        assertFalse(duplicateResult, "Duplicate player registration should fail");
    }

    @Test
    void testLoginPlayer() {
        // Register a new player
        playerRepository.registerPlayer("testuser2", "testpassword2");

        // Login with correct credentials
        Player player = playerRepository.loginPlayer("testuser2", "testpassword2");
        assertNotNull(player, "Login should succeed with correct credentials");
        assertEquals("testuser2", player.getUsername(), "Username should match");

        // Login with incorrect credentials
        Player invalidPlayer = playerRepository.loginPlayer("testuser2", "wrongpassword");
        assertNull(invalidPlayer, "Login should fail with incorrect credentials");
    }

    @Test
    void testSaveAndLoadPlayer() {
        // Register a new player
        playerRepository.registerPlayer("testuser3", "testpassword3");

        // Load the player
        Player player = playerRepository.loadPlayer("testuser3");
        assertNotNull(player, "Player should be loaded");

        // Update player stats
        player.setCredits(200);
        player.setWins(5);
        player.setLosses(3);

        // Save the updated player
        playerRepository.savePlayer(player);

        // Load the player again and verify updates
        Player updatedPlayer = playerRepository.loadPlayer("testuser3");
        assertNotNull(updatedPlayer, "Updated player should be loaded");
        assertEquals(200, updatedPlayer.getCredits(), "Credits should be updated");
        assertEquals(5, updatedPlayer.getWins(), "Wins should be updated");
        assertEquals(3, updatedPlayer.getLosses(), "Losses should be updated");
    }
}