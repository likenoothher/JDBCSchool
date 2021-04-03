package com.foxminded.sqlSchool.scriptExecutor;

import com.ibatis.common.jdbc.ScriptRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlScriptExecutor {
    private final Connection connection;

    public SqlScriptExecutor(Connection connection) {
        this.connection = connection;
    }

    public void executeSQLScript(String scriptFilePath) throws IOException {
        ScriptRunner scriptRunner = new ScriptRunner(connection, false,true);
        Reader sqlScriptReader = new BufferedReader(new FileReader(scriptFilePath));
        try {
            scriptRunner.runScript(sqlScriptReader);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        sqlScriptReader.close();
    }
}
