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

public class StudentDaoTest {
    private com.foxminded.sqlschool.scriptExecutor.SqlScriptExecutor scriptExecutor;
    private DaoFactory daoFactory;
    private CourseDao courseDao;
    private StudentDao studentDao;

    @BeforeEach
    public void loadData() {
        daoFactory = new DaoFactory();
        scriptExecutor = new SqlScriptExecutor(ConnectionBuilder.getConnection());
        scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");

        List<Course> courses = new ArrayList<>();
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
        studentDao = (StudentDao) daoFactory.getDao(DaoType.STUDENT_DAO);

    }

    @Test
    public void whenAddNotExistedBondStudentToCourse_thenAddStudentToCourse() throws DaoException {
        studentDao.insert(new Student("firstName", "lastName"));
        studentDao.addStudentToCourse(1, 1);

        List<Student> courseParticipants = studentDao.getCourseParticipants("testCourse1");

        assertTrue(courseParticipants.contains(studentDao.getById(1).get()));
    }

    @Test
    public void whenAddExistedBondStudentToCourse_thenThrowDaoException() throws DaoException {
        studentDao.insert(new Student("firstName", "lastName"));
        studentDao.addStudentToCourse(1, 1);

        assertThrows(DaoException.class, () -> studentDao.addStudentToCourse(1, 1));
    }


    @Test
    public void whenDeleteExistedBondStudentToCourse_thenDeleteStudentToCourse() throws DaoException {
        studentDao.insert(new Student("firstName", "lastName"));
        studentDao.addStudentToCourse(1, 1);
        studentDao.deleteStudentFromCourse(1, 1);

        List<Student> courseParticipants = studentDao.getCourseParticipants("testCourse1");

        assertTrue(courseParticipants.isEmpty());
    }

    @Test
    public void whenDeleteNotExistedBondStudentToCourse_thenNothingHappened() throws DaoException {
        studentDao.insert(new Student("firstName", "lastName"));
        studentDao.addStudentToCourse(1, 1);
        studentDao.deleteStudentFromCourse(1, 2);

        List<Student> courseParticipants = studentDao.getCourseParticipants("testCourse1");

        assertEquals(1, courseParticipants.size());

    }

    @Test
    public void whenGetCourseParticipantsExistedCourseName_thenReturnParticipantsList() throws DaoException {
        studentDao.insert(new Student("firstName1", "lastName1"));
        studentDao.addStudentToCourse(1, 1);

        studentDao.insert(new Student("firstName2", "lastName2"));
        studentDao.addStudentToCourse(1, 2);
        studentDao.addStudentToCourse(2, 2);

        List<Student> courseParticipants = studentDao.getCourseParticipants("testCourse1");
        assertEquals(1, courseParticipants.size());
        assertTrue(courseParticipants.contains(studentDao.getById(1).get()));

        courseParticipants = studentDao.getCourseParticipants("testCourse2");
        assertEquals(2, courseParticipants.size());
        assertTrue(courseParticipants.contains(studentDao.getById(1).get()));
        assertTrue(courseParticipants.contains(studentDao.getById(2).get()));

    }

    @Test
    public void whenGetCourseParticipantsNotExistedCourseName_thenReturnEmptyList() throws DaoException {
        studentDao.insert(new Student("firstName1", "lastName1"));
        List<Student> courseParticipants = studentDao.getCourseParticipants("not_existed_course");

        assertTrue(courseParticipants.isEmpty());

    }
}
