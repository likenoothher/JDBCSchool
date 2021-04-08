package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.DTO.Course;
import com.foxminded.sqlSchool.connection.ConnectionBuilder;

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
    private static final String DELETE_COURSE_BY_ID = "DELETE FROM courses WHERE COURSE_ID = ?";

    @Override
    public Optional<Course> getById(IdKey id) {
        Course course;
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_COURSE_BY_ID)) {
            course = new Course();
            preparedStatement.setInt(1, id.getFirstId());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            course.setId(resultSet.getInt("COURSE_ID"));
            course.setName(resultSet.getString("COURSE_NAME"));
            course.setCourseDescription(resultSet.getString("COURSE_DESCRIPTION"));

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting course from the database " + e.getLocalizedMessage());
        }

        return Optional.of(course);

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
            throw new RuntimeException("Error getting all groups from the database " + e.getLocalizedMessage());
        }

        return courses;
    }

    @Override
    public void insert(Course course) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COURSE)) {
            preparedStatement.setString(1, course.getName());
            preparedStatement.setString(2, course.getCourseDescription());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting course to the database " + e.getLocalizedMessage());
        }
    }

    @Override
    public void delete(Course course) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COURSE_BY_ID)) {
            preparedStatement.setInt(1, course.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting course from the database " + e.getLocalizedMessage());
        }
    }

}
