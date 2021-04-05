package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.ConnectionBuilder;
import com.foxminded.sqlSchool.DTO.StudentCourse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StudentCoursesDao implements GenericDao<StudentCourse>{
    private static final String INSERT_COURSE_STUDENT_BOND = "INSERT INTO students_courses" + " (STUDENT_ID, COURSE_ID) VALUES" +
        " (?, ?);";

    @Override
    public Optional<StudentCourse> getById(int id) {
        return Optional.empty();
    }

    @Override
    public List<StudentCourse> getAll() {
        return null;
    }

    @Override
    public void insert(StudentCourse studentCourse) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COURSE_STUDENT_BOND);) {
            preparedStatement.setInt(1, studentCourse.getStudentID());
            preparedStatement.setInt(2, studentCourse.getCourseID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting student/course bond to the database ", e);
        }
    }

    @Override
    public void delete(StudentCourse studentCoursesDao) {

    }

    @Override
    public void update(StudentCourse studentCoursesDao) {

    }
}
