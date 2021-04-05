package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.ConnectionBuilder;
import com.foxminded.sqlSchool.DTO.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

public class StudentDao implements GenericDao<Student> {
    private static final String INSERT_STUDENT = "INSERT INTO students" + " (GROUP_ID, FIRST_NAME, LAST_NAME) VALUES" +
        " (?, ?, ?);";

    Connection connection;
    PreparedStatement preparedStatement;

    @Override
    public Optional<Student> getById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Student> getAll() {
        return null;
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
            preparedStatement.setString(3, student.getSecondName());
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
