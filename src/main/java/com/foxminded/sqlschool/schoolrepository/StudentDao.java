package com.foxminded.sqlschool.schoolrepository;

import com.foxminded.sqlschool.connection.ConnectionBuilder;
import com.foxminded.sqlschool.dto.Student;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDao implements GenericDao<Student, Integer> {

    private final static Logger logger = Logger.getLogger(StudentDao.class);

    private static final String FIND_STUDENT_BY_ID = "SELECT STUDENT_ID, GROUP_ID, FIRST_NAME, LAST_NAME" +
        " FROM students WHERE STUDENT_ID = ?";
    private static final String FIND_ALL_STUDENTS = "SELECT STUDENT_ID, GROUP_ID, FIRST_NAME, LAST_NAME FROM students";
    private static final String INSERT_STUDENT = "INSERT INTO students (GROUP_ID, FIRST_NAME, LAST_NAME) VALUES" +
        " (?, ?, ?);";
    private static final String DELETE_STUDENT_BY_ID = "DELETE FROM students WHERE STUDENT_ID = ?";
    private static final String ADD_STUDENT_TO_COURSE = "INSERT INTO students_courses (STUDENT_ID, COURSE_ID) VALUES" +
        " (?, ?);";
    private static final String DELETE_STUDENT_FROM_COURSE = "DELETE FROM students_courses WHERE (STUDENT_ID = ?)" +
        " AND (COURSE_ID = ?)";
    private static final String GET_COURSE_PARTICIPANTS = "select courses.course_id, courses.course_name,\n" +
        "students_courses.student_id, students.first_name, students.last_name\n" +
        "from students_courses  \n" +
        "inner join courses on courses.course_id = students_courses.course_id \n" +
        "inner join students on students.student_id = students_courses.student_id\n" +
        "where courses.course_name = ?";


    @Override
    public Optional<Student> getById(Integer id) throws DaoException {
        logger.trace("Searching for student with id=" + id);
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_STUDENT_BY_ID)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    int studentId = resultSet.getInt("STUDENT_ID");
                    int groupId = resultSet.getInt("GROUP_ID");
                    String firstName = resultSet.getString("FIRST_NAME");
                    String lastName = resultSet.getString("LAST_NAME");
                    logger.trace("Create student to return");
                    return Optional.of(new Student(studentId, groupId, firstName, lastName));
                }
            }

        } catch (SQLException e) {
            logger.warn("Can't get student with id=" + id + " from the database", e);
            throw new DaoException("Error getting student from the database ", e);
        }
        logger.trace("Student with id=" + id + " wasn't found. Returning Optional.empty()");
        return Optional.empty();
    }

    @Override
    public List<Student> getAll() throws DaoException {
        logger.trace("Searching for all students");
        List<Student> students = new ArrayList<>();
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_STUDENTS);
            ResultSet resultSet = statement.executeQuery()
        ) {
            logger.trace("Adding all students to list");
            while (resultSet.next()) {

                int studentId = resultSet.getInt("STUDENT_ID");
                int groupId = resultSet.getInt("GROUP_ID");
                String firstName = resultSet.getString("FIRST_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                students.add(new Student(studentId, groupId, firstName, lastName));
            }
        } catch (SQLException e) {
            logger.warn("Can't get all students from database", e);
            throw new DaoException("Error getting all students from the database ", e);
        }
        logger.trace("Returning all found students");
        return students;
    }

    @Override
    public void insert(Student student) throws DaoException {
        logger.trace("Inserting student " + student.getLastName() + " " + student.getFirstName());
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_STUDENT)
        ) {

            if (student.getGroupId() != 0) {
                statement.setInt(1, student.getGroupId());
            } else {
                statement.setNull(1, Types.INTEGER);
            }
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.executeUpdate();
            logger.trace("Student " + student.getLastName() + " " + student.getFirstName() + " was successfully added");
        } catch (SQLException e) {
            logger.warn("Can't insert student " + student.getFirstName() +
                " " + student.getLastName() + " to database", e);
            throw new DaoException("Error inserting student to the database ", e);
        }
    }

    @Override
    public void delete(Integer id) throws DaoException {
        logger.trace("Deleting student with id=" + id);
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_BY_ID)
        ) {
            statement.setInt(1, id);
            statement.executeUpdate();
            logger.trace("Student with id=" + id + " was successfully deleted");
        } catch (SQLException e) {
            logger.warn("Can't delete student with id=" + id + " from the database", e);
            throw new DaoException("Error deleting student from the database ", e);
        }
    }


    public void addStudentToCourse(int studentId, int courseId) throws DaoException {
        logger.trace("Adding student with id=" + studentId + " to course " + courseId);
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(ADD_STUDENT_TO_COURSE)
        ) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
            logger.trace("Student with id=" + studentId + " was successfully added to course " + courseId);
        } catch (SQLException e) {
            logger.warn("Can't add student with id=" + studentId +
                " to course with id=" + courseId, e);
            throw new DaoException("Error adding student to course ", e);
        }
    }

    public void deleteStudentFromCourse(int studentId, int courseId) throws DaoException {
        logger.trace("Deleting student with id=" + studentId + " from course " + courseId);
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_FROM_COURSE)
        ) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
            logger.trace("Request for deleting bond studentId=" + studentId +
                " and courseId " + courseId + " was executed");
        } catch (SQLException e) {
            logger.warn("Can't delete student with id=" + studentId +
                " from course with id=" + courseId, e);
            throw new DaoException("Error deleting student from course ", e);
        }
    }


    public List<Student> getCourseParticipants(String courseName) throws DaoException {
        logger.trace("Getting students of course " + courseName);
        List<Student> courseParticipants = new ArrayList<>();

        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_COURSE_PARTICIPANTS)
        ) {

            statement.setString(1, courseName);

            try (ResultSet resultSet = statement.executeQuery()) {
                logger.trace("Adding students of course " + courseName + " to list");
                while (resultSet.next()) {
                    int studentId = resultSet.getInt("STUDENT_ID");
                    getById(studentId).ifPresent(courseParticipants::add);
                }
            }
        } catch (SQLException e) {
            logger.warn("Can't get participants of course with name=" + courseName, e);
            throw new DaoException("Error getting participants of course" + courseName + " from the database ", e);
        }
        logger.trace("Returning students of course " + courseName);
        return courseParticipants;
    }
}
