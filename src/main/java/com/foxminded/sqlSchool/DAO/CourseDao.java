package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.ConnectionBuilder;
import com.foxminded.sqlSchool.DTO.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDao implements GenericDao<Course> {
    private static final String FIND_COURSE_BY_ID = "SELECT * FROM courses WHERE COURSE_ID = ?";
    private static final String INSERT_COURSE = "INSERT INTO courses" + " (COURSE_NAME, COURSE_DESCRIPTION) VALUES" +
        " (?, ?);";
    private static final String FIND_ALL_COURSES = "SELECT * FROM courses";

    @Override
    public Optional<Course> getById(int id) {
        Course course;
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_COURSE_BY_ID)) {
            course = new Course();
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            course.setId(resultSet.getInt("COURSE_ID"));
            course.setName(resultSet.getString("COURSE_NAME"));
            course.setCourseDescription(resultSet.getString("COURSE_DESCRIPTION"));

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting course from the database ", e);
        }
        Optional<Course> courseOptional = Optional.of(course);

        return courseOptional;

    }

    @Override
    public List<Course> getAll() {
        List<Course> courses = new ArrayList<>();
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_COURSES);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Course course = new Course();
                course.setId(resultSet.getInt("COURSE_ID"));
                course.setName(resultSet.getString("COURSE_NAME"));
                course.setCourseDescription(resultSet.getString("COURSE_DESCRIPTION"));
                courses.add(course);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all groups from the database ", e);
        }

        return courses;
    }

    @Override
    public void insert(Course course) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COURSE);) {
            preparedStatement.setString(1, course.getName());
            preparedStatement.setString(2, course.getCourseDescription());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting course to the database ", e);
        }
    }

    @Override
    public void delete(Course course) {

    }

    @Override
    public void update(Course course) {

    }
}
