package com.foxminded.sqlSchool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBuilder {
    public static final String URL = "jdbc:postgresql://localhost/School";
    public static final String USER = "admin";
    public static final String PASSWORD = "admin";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database ", e);
        }
    }
}
