package com.foxminded.sqlSchool;

import com.foxminded.sqlSchool.scriptExecutor.SqlScriptExecutor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SchoolApp {
    public static void main(String[] args) throws IOException {

        Connection connection = DataSource.getConnection();
        SqlScriptExecutor scriptExecutor = new SqlScriptExecutor(connection);
        scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
