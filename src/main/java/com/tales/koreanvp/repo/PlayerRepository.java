package com.tales.koreanvp.repo;

import java.sql.*;
import com.tales.koreanvp.model.Player;
import com.tales.koreanvp.service.DatabaseConnection;

public class PlayerRepository {

    // Save player progress to the database
    public void savePlayer(Player player) {
        String query = "UPDATE players SET credits = ?, wins = ?, losses = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, player.getCredits());
            stmt.setInt(2, player.getWins());
            stmt.setInt(3, player.getLosses());
            stmt.setString(4, player.getUsername());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load player progress from the database
    public Player loadPlayer(String username) {
        String query = "SELECT * FROM players WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Player(
                        rs.getString("username"),
                        rs.getInt("credits"),
                        rs.getInt("wins"),
                        rs.getInt("losses")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Register a new player
    public boolean registerPlayer(String username, String password) {
        String query = "INSERT INTO players (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login a player
    public Player loginPlayer(String username, String password) {
        String query = "SELECT * FROM players WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Player(
                        rs.getString("username"),
                        rs.getInt("credits"),
                        rs.getInt("wins"),
                        rs.getInt("losses")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}