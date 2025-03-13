package com.tales.koreanvp.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Simple database class that handles the connection to a MySQL DB running on localhost:3306
 * This class was changed so we can write tests for it.
 * Ideally we would have a private static final PASSWORD variable
 */
public class DatabaseConnection {
    // Use system properties for database connection details
    private static final String URL = System.getProperty("DB_URL", "jdbc:mysql://localhost:3306/video_poker");
    private static final String USER = System.getProperty("DB_USER", "root");

    static {
        try {
            // Manually register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    // Load the password dynamically from system properties or a file
    // This is helping test this class otherwise we would make the PASSWORD static final
    private static String getPassword() {
        String password = System.getProperty("DB_PASSWORD");
        if (password == null) {
            // Fallback to loading from file if system property is not set
            password = loadPasswordFromFile("db_password.txt");
        }
        return password;
    }

    // Load the password from a file
    private static String loadPasswordFromFile(String filePath) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            String password = scanner.nextLine().trim(); // Read the first line as the password
            scanner.close();
            return password;
        } catch (FileNotFoundException e) {
            System.err.println("Error: Password file not found.");
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getConnection() throws SQLException {
        String password = getPassword(); // Retrieve the password dynamically
        if (password == null) {
            throw new SQLException("Database password not loaded.");
        }
        return DriverManager.getConnection(URL, USER, password);
    }
}