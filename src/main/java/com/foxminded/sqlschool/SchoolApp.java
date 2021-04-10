package com.foxminded.sqlschool;

import com.foxminded.sqlschool.connection.ConnectionBuilder;
import com.foxminded.sqlschool.data.DataGenerator;
import com.foxminded.sqlschool.data.DataLoader;
import com.foxminded.sqlschool.menu.ApplicationMenu;
import com.foxminded.sqlschool.scriptExecutor.SqlScriptExecutor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class SchoolApp {
    private static final Logger logger = Logger.getLogger(SchoolApp.class);

    public static void main(String[] args) {

        logger.info("Program was started");
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
