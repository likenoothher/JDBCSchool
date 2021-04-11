package com.foxminded.sqlschool.scriptExecutor;

import com.ibatis.common.jdbc.ScriptRunner;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlScriptExecutor {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(SqlScriptExecutor.class);

    public SqlScriptExecutor(Connection connection) {
        this.connection = connection;
    }

    public void executeSQLScript(String scriptFilePath) {
        logger.info("Script runner was started");
        ScriptRunner scriptRunner = new ScriptRunner(connection, false, true);
        Reader sqlScriptReader;
        try {
            sqlScriptReader = new BufferedReader(new FileReader(scriptFilePath));
        } catch (FileNotFoundException e) {
            logger.warn("Script file wasn't found", e);
            throw new RuntimeException("File doesn't exist");
        }
        try {
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(sqlScriptReader);
            logger.info("Script runner finished successfully");
        } catch (SQLException | IOException throwables) {
            logger.warn("Error during executing the script", throwables);
            throwables.printStackTrace();
        }

        try {
            sqlScriptReader.close();
        } catch (IOException e) {
            logger.warn("Error during closing script runner", e);
            e.printStackTrace();
        }
    }
}
