package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.ConnectionBuilder;
import com.foxminded.sqlSchool.DTO.Group;
import com.foxminded.sqlSchool.DTO.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDao implements GenericDao<Student> {
    private static final String INSERT_STUDENT = "INSERT INTO students" + " (GROUP_ID, FIRST_NAME, LAST_NAME) VALUES" +
        " (?, ?, ?);";
    private static final String FIND_ALL_STUDENTS = "SELECT * FROM students";


    @Override
    public Optional<Student> getById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_STUDENTS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt("STUDENT_ID"));
                student.setGroupId(resultSet.getInt("GROUP_ID"));
                student.setFirstName(resultSet.getString("FIRST_NAME"));
                student.setLastName(resultSet.getString("LAST_NAME"));
                students.add(student);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all students from the database ", e);
        }

        return students;

    }

    @Override
    public void insert(Student student) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT);) {
            if (student.getGroupId() != 0) {
                preparedStatement.setInt(1, student.getGroupId());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getLastName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting student to the database ", e);
        }

    }

    @Override
    public void delete(Student student) {

    }

    @Override
    public void update(Student student) {

    }
}
