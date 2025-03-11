package com.tales.koreanvp;

import java.sql.Connection;
import java.sql.SQLException;

public class App
{
    public static void main( String[] args ) throws SQLException {
        Connection connection = new DatabaseConnection().getConnection();

    }
}