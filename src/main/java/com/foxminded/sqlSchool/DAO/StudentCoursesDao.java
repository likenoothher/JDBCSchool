package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.ConnectionBuilder;
import com.foxminded.sqlSchool.DTO.StudentCourse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentCoursesDao implements GenericDao<StudentCourse> {
    private static final String FIND_COURSE_STUDENT_BOND_BY_STUDENT_ID = "SELECT * FROM students_courses WHERE STUDENT_ID = ?";
    private static final String INSERT_COURSE_STUDENT_BOND = "INSERT INTO students_courses" + " (STUDENT_ID, COURSE_ID) VALUES" +
        " (?, ?);";
    private static final String DELETE_COURSE_STUDENT_BOND_BY_IDs = "DELETE FROM students_courses WHERE (STUDENT_ID = ?) AND (COURSE_ID = ?)";

    @Override
    public Optional<StudentCourse> getById(int id) {
//        List<StudentCourse> studentCourses = new ArrayList<>();
//        try (Connection connection = ConnectionBuilder.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(FIND_COURSE_STUDENT_BOND_BY_STUDENT_ID)) {
//
//            preparedStatement.setInt(1, id);
//
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                StudentCourse studentCourse = new StudentCourse();
//                studentCourse.setStudentID(resultSet.getInt("STUDENT_ID"));
//                studentCourse.setCourseID(resultSet.getInt("COURSE_ID"));
//                studentCourses.add(studentCourse);
//            }
//
//            resultSet.close();
//        } catch (SQLException e) {
//            throw new RuntimeException("Error getting student from the database ", e);
//        }
//
//
//        return studentCourses;
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
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COURSE_STUDENT_BOND_BY_IDs);) {
            preparedStatement.setInt(1, studentCoursesDao.getStudentID());
            preparedStatement.setInt(2, studentCoursesDao.getCourseID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student/course bond from the database ", e);
        }
    }

    @Override
    public void update(StudentCourse studentCoursesDao) {

    }
}
