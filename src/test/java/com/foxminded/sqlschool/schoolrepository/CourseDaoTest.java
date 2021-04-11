package com.foxminded.sqlschool.schoolrepository;

import com.foxminded.sqlschool.connection.ConnectionBuilder;
import com.foxminded.sqlschool.dto.Course;
import com.foxminded.sqlschool.dto.Student;
import com.foxminded.sqlschool.scriptExecutor.SqlScriptExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseDaoTest {

    private SqlScriptExecutor scriptExecutor;
    private DaoFactory daoFactory;
    private CourseDao courseDao;
    private StudentDao studentDao;
    private List<Course> courses;

    @BeforeEach
    public void loadData() {
        daoFactory = new DaoFactory();
        scriptExecutor = new SqlScriptExecutor(ConnectionBuilder.getConnection());
        scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");

        courses = new ArrayList<>();
        courses.add(new Course(1, "testCourse1", "testCourseDescription1"));
        courses.add(new Course(2, "testCourse2", "testCourseDescription2"));
        courses.add(new Course(3, "testCourse3", "testCourseDescription3"));

        courseDao = (CourseDao) daoFactory.getDao(DaoType.COURSE_DAO);
        courses.forEach(course -> {
            try {
                courseDao.insert(course);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void whenExistByName_thenReturnTrueIfExistFalseOtherwise() throws DaoException {
        courseDao.delete(3);

        courseDao.insert(new Course(4, "testCourse4", "testCourseDescription4"));

        assertTrue(courseDao.existByName("testCourse1"));
        assertTrue(courseDao.existByName("testCourse2"));
        assertFalse(courseDao.existByName("testCourse3"));
        assertTrue(courseDao.existByName("testCourse4"));
    }

    @Test
    public void whenGetAvailableCoursesToAddExistedStudent_thenReturnAvailableCoursesToAdd() throws DaoException {
        studentDao = (StudentDao) daoFactory.getDao(DaoType.STUDENT_DAO);

        studentDao.insert(new Student("firstName", "lastName"));
        studentDao.addStudentToCourse(1, 1);

        List<Course> availableCourses = courseDao.getAvailableCoursesToAdd(1);

        assertNotEquals(courses.size(), availableCourses.size());

        assertTrue(availableCourses.contains(new Course(2, "testCourse2", "testCourseDescription2")));
        assertTrue(availableCourses.contains(new Course(3, "testCourse3", "testCourseDescription3")));

        assertFalse(availableCourses.contains(new Course(1, "testCourse1", "testCourseDescription1")));

    }

    @Test
    public void whenGetAvailableCoursesToAddNotExistedStudent_thenReturnListOfAllCourses() throws DaoException {
        List<Course> availableCourses = courseDao.getAvailableCoursesToAdd(1);

        assertEquals(courses.size(), availableCourses.size());

        courses.forEach(course -> assertTrue(availableCourses.contains(course)));

    }

    @Test
    public void whenGetStudentCoursesExistedStudent_thenReturnListOfCourses() throws DaoException {
        studentDao = (StudentDao) daoFactory.getDao(DaoType.STUDENT_DAO);

        studentDao.insert(new Student("firstName", "lastName"));
        studentDao.addStudentToCourse(1, 1);


        List<Course> availableCourses = courseDao.getStudentCourses(1);

        assertTrue(availableCourses.contains(new Course(1, "testCourse1", "testCourseDescription1")));

    }

    @Test
    public void whenGetStudentCoursesNotExistedStudent_thenReturnEmptyList() throws DaoException {
        List<Course> availableCourses = courseDao.getStudentCourses(1);

        assertTrue(availableCourses.isEmpty());
    }

}
