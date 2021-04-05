package com.foxminded.sqlSchool;

import com.foxminded.sqlSchool.DAO.GroupDao;
import com.foxminded.sqlSchool.DAO.StudentDao;
import com.foxminded.sqlSchool.DTO.Group;
import com.foxminded.sqlSchool.DTO.Student;
import com.foxminded.sqlSchool.scriptExecutor.SqlScriptExecutor;
import com.foxminded.sqlSchool.testData.TestDataGenerator;
import com.foxminded.sqlSchool.testData.TestDataLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SchoolApp {
    public static void main(String[] args) throws IOException, SQLException {

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



    }

}
