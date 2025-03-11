package com.tales.koreanvp;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/video_poker";
    private static final String USER = "root"; // Replace with your MySQL username
    private static String PASSWORD = loadPasswordFromFile("C:\\Users\\tales\\IdeaProjects\\KoreanVideoPoker\\src\\main\\resources\\secrets.txt");

    static {
        try {
            // Manually register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    // Load the password from a file
    private static String loadPasswordFromFile(String filePath) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            String password = scanner.nextLine().trim(); // Read the first line as the password
            scanner.close();
            PASSWORD = password;
        } catch (FileNotFoundException e) {
            System.err.println("Error: Password file not found.");
            e.printStackTrace();
            PASSWORD = null;
        }
        return filePath;
    }

    public Connection getConnection() throws SQLException {
        if (PASSWORD == null) {
            throw new SQLException("Database password not loaded.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}