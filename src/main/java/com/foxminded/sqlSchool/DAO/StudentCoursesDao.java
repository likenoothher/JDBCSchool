package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.DTO.StudentCourse;
import com.foxminded.sqlSchool.connection.ConnectionBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentCoursesDao implements GenericDao<StudentCourse> {
    private static final String FIND_COURSE_STUDENT_BOND_BY_ID = "SELECT * FROM students_courses WHERE (STUDENT_ID = ?) AND (COURSE_ID = ?)";
    private static final String FIND_ALL_COURSE_STUDENT_BOND = "SELECT * FROM students_courses";
    private static final String INSERT_COURSE_STUDENT_BOND = "INSERT INTO students_courses" + " (STUDENT_ID, COURSE_ID) VALUES" +
        " (?, ?);";
    private static final String DELETE_COURSE_STUDENT_BOND_BY_ID = "DELETE FROM students_courses WHERE (STUDENT_ID = ?) AND (COURSE_ID = ?)";

    @Override
    public Optional<StudentCourse> getById(IdKey id) {
        StudentCourse studentCourseBond;
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_COURSE_STUDENT_BOND_BY_ID)) {
            studentCourseBond = new StudentCourse();
            preparedStatement.setInt(1, id.getFirstId());
            preparedStatement.setInt(2, id.getSecondId());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            studentCourseBond.setStudentID(resultSet.getInt("STUDENT_ID"));
            studentCourseBond.setCourseID(resultSet.getInt("COURSE_ID"));

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting student/courses bond from the database " + e.getLocalizedMessage());
        }

        return Optional.of(studentCourseBond);
    }

    @Override
    public List<StudentCourse> getAll() {

        List<StudentCourse> studentCourses = new ArrayList<>();
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_COURSE_STUDENT_BOND);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                StudentCourse studentCourseBond = new StudentCourse();
                studentCourseBond.setStudentID(resultSet.getInt("STUDENT_ID"));
                studentCourseBond.setCourseID(resultSet.getInt("COURSE_ID"));
                studentCourses.add(studentCourseBond);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all students courses bonds from the database: " + e.getLocalizedMessage());
        }

        return studentCourses;
    }

    @Override
    public void insert(StudentCourse studentCourse) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COURSE_STUDENT_BOND)) {
            preparedStatement.setInt(1, studentCourse.getStudentID());
            preparedStatement.setInt(2, studentCourse.getCourseID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting student/course bond to the database " + e.getLocalizedMessage());
        }
    }

    @Override
    public void delete(StudentCourse studentCoursesDao) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COURSE_STUDENT_BOND_BY_ID)) {
            preparedStatement.setInt(1, studentCoursesDao.getStudentID());
            preparedStatement.setInt(2, studentCoursesDao.getCourseID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student/course bond from the database " + e.getLocalizedMessage());
        }
    }

}
