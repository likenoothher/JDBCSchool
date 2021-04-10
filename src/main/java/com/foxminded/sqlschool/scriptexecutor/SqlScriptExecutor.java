package com.foxminded.sqlschool.scriptExecutor;

import com.ibatis.common.jdbc.ScriptRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlScriptExecutor {
    private final Connection connection;

    public SqlScriptExecutor(Connection connection) {
        this.connection = connection;
    }

    public void executeSQLScript(String scriptFilePath) {
        ScriptRunner scriptRunner = new ScriptRunner(connection, false, true);
        Reader sqlScriptReader;
        try {
            sqlScriptReader = new BufferedReader(new FileReader(scriptFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File doesn't exist");
        }
        try {
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(sqlScriptReader);
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        try {
            sqlScriptReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
