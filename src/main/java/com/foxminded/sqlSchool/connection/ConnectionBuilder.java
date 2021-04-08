package com.foxminded.sqlSchool.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionBuilder {
    private ConnectionBuilder() {
    }

    public static Connection getConnection() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = ConnectionBuilder.class.getClassLoader().getResourceAsStream("postgreDB.properties");
            properties.load(inputStream);

            if (inputStream != null) {
                inputStream.close();
            }
            String connectionURL = properties.getProperty("jdbc.url");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            return DriverManager.getConnection(connectionURL, username, password);

        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database ", e);
        } catch (IOException e) {
            throw new RuntimeException("Error in work of input stream ", e);
        }
    }

}
