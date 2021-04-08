package com.foxminded.sqlSchool;

import com.foxminded.sqlSchool.connection.ConnectionBuilder;
import com.foxminded.sqlSchool.data.DataGenerator;
import com.foxminded.sqlSchool.data.DataLoader;
import com.foxminded.sqlSchool.menu.ApplicationMenu;
import com.foxminded.sqlSchool.scriptExecutor.SqlScriptExecutor;

import java.sql.Connection;
import java.sql.SQLException;

public class SchoolApp {
    public static void main(String[] args) {

        try (Connection connection = ConnectionBuilder.getConnection()) {
            SqlScriptExecutor scriptExecutor = new SqlScriptExecutor(connection);
            scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        DataGenerator dataGenerator = DataGenerator.getInstance();
        DataLoader dataLoader = new DataLoader(dataGenerator);
        dataLoader.loadTestData();
        ApplicationMenu applicationMenu = new ApplicationMenu();
        applicationMenu.callApplicationMenu();

    }

}
