package com.foxminded.sqlschool.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionBuilder {

    private static BasicDataSource dataSource;

    static {
        try {
            dataSource = new BasicDataSource();
            Properties properties = new Properties();

            InputStream inputStream = ConnectionBuilder.class.getClassLoader().getResourceAsStream("postgreDB.properties");
            properties.load(inputStream);

            dataSource.setUrl(properties.getProperty("jdbc.url"));
            dataSource.setUsername(properties.getProperty("jdbc.username"));
            dataSource.setPassword(properties.getProperty("jdbc.password"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ConnectionBuilder() {
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database ", e);
        }
    }

}
