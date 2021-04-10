package com.foxminded.sqlschool.data;

import com.foxminded.sqlschool.connection.ConnectionBuilder;
import com.foxminded.sqlschool.scriptExecutor.SqlScriptExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataLoaderTest {

    private com.foxminded.sqlschool.data.DataLoader dataLoader;
    private SqlScriptExecutor scriptExecutor;

    @BeforeEach
    public void loadData() {
        scriptExecutor = new SqlScriptExecutor(ConnectionBuilder.getConnection());

        scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");
        dataLoader = new com.foxminded.sqlschool.data.DataLoader(com.foxminded.sqlschool.data.DataGenerator.getInstance());
        dataLoader.loadTestData();

    }

    @Test
    public void whenDataLoaded_thenStudentAmountInStudentTableIs200() {
        int amountOfStudents = 0;
        String countStudentQuery = "select count(students.student_id) from students";
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(countStudentQuery);
            ResultSet resultSet = statement.executeQuery()
        ) {
            resultSet.next();
            amountOfStudents = resultSet.getInt("count");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertEquals(200, amountOfStudents);
    }

    @Test
    public void whenDataLoaded_thenEveryStudentHasMin1Course() {
        int minCoursesAmount = 1;
        int minCoursesAmountInTable = 0;
        String countStudentQuery = "select student_id, count(course_id)\n" +
            "from students_courses\n" +
            "group by student_id\n" +
            "order by count";
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(countStudentQuery);
            ResultSet resultSet = statement.executeQuery()
        ) {
            resultSet.next();
            minCoursesAmountInTable = resultSet.getInt("count");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertTrue(minCoursesAmount >= minCoursesAmountInTable);
    }

    @Test
    public void whenDataLoaded_thenEveryStudentHasMax3Course() {
        int maxCoursesAmount = 1;
        int maxCoursesAmountInTable = 0;
        String countStudentQuery = "select student_id, count(course_id)\n" +
            "from students_courses\n" +
            "group by student_id\n" +
            "order by count desc";
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(countStudentQuery);
            ResultSet resultSet = statement.executeQuery()
        ) {
            resultSet.next();
            maxCoursesAmountInTable = resultSet.getInt("count");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertTrue(maxCoursesAmount <= maxCoursesAmountInTable);
    }

    @Test
    public void whenDataLoaded_thenEveryGroupHasMin10Students() {
        int minStudentsAllowed = 10;
        int minStudentsAmountInGroup = 0;
        String countStudentQuery = "select group_id, count(student_id)\n" +
            "from students\n" +
            "group by group_id\n" +
            "order by count";
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(countStudentQuery);
            ResultSet resultSet = statement.executeQuery()
        ) {
            resultSet.next();
            minStudentsAmountInGroup = resultSet.getInt("count");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(minStudentsAllowed <= minStudentsAmountInGroup);
    }

    @Test
    public void whenDataLoaded_thenEveryGroupHasMax30Students() {
        int maxStudentsAllowed = 30;
        int maxStudentsAmountInGroup = 0;
        String countStudentQuery = "select group_id, count(student_id)\n" +
            "from students\n" +
            "group by group_id\n" +
            "order by count desc";
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(countStudentQuery);
            ResultSet resultSet = statement.executeQuery()
        ) {
            resultSet.next();
            maxStudentsAmountInGroup = resultSet.getInt("count");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertTrue(maxStudentsAmountInGroup >= maxStudentsAllowed);
    }

    @Test
    public void whenDataLoaded_thenDBHas10Groups() {
        int requiredGroupAmount = 10;
        int groupAmountInDb = 0;
        String countStudentQuery = "select count(*) from groups";
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(countStudentQuery);
            ResultSet resultSet = statement.executeQuery()
        ) {
            resultSet.next();
            groupAmountInDb = resultSet.getInt("count");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertEquals(requiredGroupAmount, groupAmountInDb);
    }

    @Test
    public void whenDataLoaded_thenDBHas10Courses() {
        int requiredCourseAmount = 10;
        int courseAmountInDb = 0;
        String countStudentQuery = "select count(*) from courses";
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(countStudentQuery);
            ResultSet resultSet = statement.executeQuery()
        ) {
            resultSet.next();
            courseAmountInDb = resultSet.getInt("count");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertEquals(requiredCourseAmount, courseAmountInDb);
    }

}
