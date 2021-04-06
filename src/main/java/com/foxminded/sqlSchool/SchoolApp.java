package com.foxminded.sqlSchool;

import com.foxminded.sqlSchool.scriptExecutor.SqlScriptExecutor;
import com.foxminded.sqlSchool.testData.TestDataGenerator;
import com.foxminded.sqlSchool.testData.TestDataLoader;
import com.foxminded.sqlSchool.testData.view.ApplicationMenu;

import java.sql.Connection;
import java.sql.SQLException;

public class SchoolApp {
    public static void main(String[] args) {

        Connection connection = ConnectionBuilder.getConnection();
        SqlScriptExecutor scriptExecutor = new SqlScriptExecutor(connection);
        scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        TestDataGenerator testDataGenerator = TestDataGenerator.getInstance();
        TestDataLoader testDataLoader = new TestDataLoader(testDataGenerator);
        testDataLoader.loadTestData();
        ApplicationMenu.showMenu();


    }

}
