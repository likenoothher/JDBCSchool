package com.foxminded.sqlschool.schoolrepository;

import com.foxminded.sqlschool.connection.ConnectionBuilder;
import com.foxminded.sqlschool.dto.Course;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDao implements GenericDao<Course, Integer> {

    private final static Logger logger = Logger.getLogger(CourseDao.class);

    private static final String FIND_COURSE_BY_ID = "SELECT COURSE_ID, COURSE_NAME, COURSE_DESCRIPTION" +
        " FROM courses WHERE COURSE_ID = ?";
    private static final String FIND_ALL_COURSES = "SELECT COURSE_ID, COURSE_NAME, COURSE_DESCRIPTION FROM courses";
    private static final String INSERT_COURSE = "INSERT INTO courses (COURSE_NAME, COURSE_DESCRIPTION) VALUES" +
        " (?, ?);";
    private static final String DELETE_COURSE_BY_ID = "DELETE FROM courses WHERE COURSE_ID = ?";
    private static final String DOES_COURSE_EXIST_BY_COURSE_NAME = "SELECT EXISTS(SELECT 1 FROM courses " +
        "WHERE course_name = ?);";

    private static final String GET_AVAILABLE_COURSES_FOR_STUDENT = "select course_id, course_name, " +
        "course_description from courses\n" +
        "where course_id not in (select course_id from students_courses where student_id = ?)";

    private static final String GET_STUDENT_COURSES = "select students_courses.course_id, course_name, course_description \n" +
        "from students_courses inner join courses on students_courses.course_id = courses.course_id \n" +
        "where student_id = ?";

    @Override
    public Optional<Course> getById(Integer id) throws DaoException {
        logger.trace("Searching for course with id=" + id);
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_COURSE_BY_ID)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int courseId = resultSet.getInt("COURSE_ID");
                    String name = resultSet.getString("COURSE_NAME");
                    String description = resultSet.getString("COURSE_DESCRIPTION");
                    logger.trace("Create course to return");
                    return Optional.of(new Course(courseId, name, description));
                }
            }

        } catch (SQLException e) {
            logger.warn("Can't get course with id=" + id + " from the database", e);
            throw new DaoException("Error getting course from the database ", e);
        }
        logger.trace("Course with id=" + id + " wasn't found. Returning Optional.empty()");
        return Optional.empty();
    }

    @Override
    public List<Course> getAll() throws DaoException {
        logger.trace("Searching for all courses");
        List<Course> courses = new ArrayList<>();
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_COURSES);
            ResultSet resultSet = statement.executeQuery()
        ) {
            logger.trace("Adding all courses to list");
            while (resultSet.next()) {
                int courseId = resultSet.getInt("COURSE_ID");
                String name = resultSet.getString("COURSE_NAME");
                String description = resultSet.getString("COURSE_DESCRIPTION");
                courses.add(new Course(courseId, name, description));
            }

        } catch (SQLException e) {
            logger.warn("Can't get all courses from database", e);
            throw new DaoException("Error getting all courses from the database ", e);
        }
        logger.trace("Returning all found courses");
        return courses;
    }

    @Override
    public void insert(Course course) throws DaoException {
        logger.trace("Inserting course " + course.getName());
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_COURSE)
        ) {

            statement.setString(1, course.getName());
            statement.setString(2, course.getCourseDescription());
            statement.executeUpdate();
            logger.trace("Course " + course.getName() + " was successfully added");
        } catch (SQLException e) {
            logger.warn("Can't insert course " + course.getName() + " to database", e);
            throw new DaoException("Error inserting course to the database ", e);
        }
    }

    @Override
    public void delete(Integer id) throws DaoException {
        logger.trace("Deleting course with id=" + id);
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_COURSE_BY_ID)
        ) {

            statement.setInt(1, id);
            statement.executeUpdate();
            logger.trace("Course with id=" + id + " was successfully deleted");
        } catch (SQLException e) {
            logger.warn("Can't delete course with id=" + id + " from the database", e);
            throw new DaoException("Error deleting course from the database ", e);
        }
    }

    public boolean existByName(String courseName) {
        logger.trace("Searching a course with a name " + courseName);
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(DOES_COURSE_EXIST_BY_COURSE_NAME)
        ) {
            statement.setString(1, courseName);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                logger.trace("Existence of course with a name " + courseName + ": " + resultSet.getBoolean("exists"));
                return resultSet.getBoolean("exists");
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Course> getAvailableCoursesToAdd(int studentId) throws DaoException {
        logger.trace("Searching for courses of student with id=" + studentId + " available to add");
        List<Course> availableCoursesToAdd = new ArrayList<>();

        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_AVAILABLE_COURSES_FOR_STUDENT)
        ) {

            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {

                logger.trace("Adding courses of student with id=" + studentId + " available to add to list");
                while (resultSet.next()) {
                    int courseId = resultSet.getInt("COURSE_ID");
                    String courseName = resultSet.getString("COURSE_NAME");
                    String courseDescription = resultSet.getString("COURSE_DESCRIPTION");
                    availableCoursesToAdd.add(new Course(courseId, courseName, courseDescription));
                }
            }
        } catch (SQLException e) {
            logger.warn("Can't get courses available to add for student with id=" + studentId, e);
            throw new DaoException("Error getting available courses of student to add", e);
        }
        logger.trace("Returning courses of student with id=" + studentId + " available to add");
        return availableCoursesToAdd;
    }

    public List<Course> getStudentCourses(int studentId) throws DaoException {
        logger.trace("Searching for courses of student with id=" + studentId);
        List<Course> studentCourses = new ArrayList<>();

        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_STUDENT_COURSES)
        ) {

            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                logger.trace("Adding courses of student with id=" + studentId + " to list");
                while (resultSet.next()) {
                    int courseId = resultSet.getInt("COURSE_ID");
                    String courseName = resultSet.getString("COURSE_NAME");
                    String courseDescription = resultSet.getString("COURSE_DESCRIPTION");
                    studentCourses.add(new Course(courseId, courseName, courseDescription));
                }
            }

        } catch (SQLException e) {
            logger.warn("Can't get courses of student with id =" + studentId, e);
            throw new DaoException("Error getting courses of student ", e);
        }
        logger.trace("Returning courses of student with id=" + studentId);
        return studentCourses;
    }

}
