package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.DTO.*;
import com.foxminded.sqlSchool.connection.ConnectionBuilder;
import com.foxminded.sqlSchool.scriptExecutor.SqlScriptExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DAOTest {

    @Parameterized.Parameters
    public static Collection<Object> data(){

        return Arrays.asList(new Object[][]{
            {new Course(1,"testCourse", "testCourseDescription"),
               new CourseDao()},
            {new Group(1,"testGroup"), new GroupDao()},
            {new Student("firstName", "lastName",1), new StudentDao()},
        });
    }

    public static Collection<Object> dataForGetAllMethod(){

        return Arrays.asList(new Object[][]{
            {Arrays.asList(new Course(1,"testCourse1", "testCourseDescription1"),
                new Course(2,"testCourse2", "testCourseDescription2")),
                new CourseDao()},

            {Arrays.asList(new Group(1,"testGroup1"),new Group(2,"testGroup2")), new GroupDao()},

            {Arrays.asList(new Student("firstName1", "lastName1",1),
                new Student("firstName1", "lastName1",2)), new StudentDao()},

        });
    }

    private SqlScriptExecutor scriptExecutor;

    @BeforeEach
    public void loadData(){
        scriptExecutor = new SqlScriptExecutor(ConnectionBuilder.getConnection());
        scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");

    }

    @ParameterizedTest
    @MethodSource("dataForGetAllMethod")
    public void testGetAllExceptStudentsCoursesDao(List input, GenericDao DAO){

        input.forEach(DTObject ->DAO.insert(DTObject));

        assertEquals(input, DAO.getAll());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInsertAndGetByIdExceptStudentsCoursesDao(GenericDTO input, GenericDao DAO){
        GenericDao genericDao = DAO;
        genericDao.insert(input);
        assertEquals(input, genericDao.getById(new IdKey(1,1)).get());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testDeleteAndGetByIdExceptStudentsCoursesDao(GenericDTO input, GenericDao DAO) {
        GenericDao genericDao = DAO;
        genericDao.insert(input);
        genericDao.delete(input);
        assertThrows(RuntimeException.class, () -> genericDao.getById(new IdKey(1, 1)));
    }

    @Test
    public void testGetAllForStudentsCoursesDao() {
        GenericDao<StudentCourse> studentCourseDao = new StudentCoursesDao();
        GenericDao<Course> courseDao = new CourseDao();
        GenericDao<Group> groupDao = new GroupDao();
        GenericDao<Student> studentDao = new StudentDao();

        courseDao.insert(new Course(1,"testCourse1", "testCourseDescription1"));
        groupDao.insert(new Group("testGroup1"));
        studentDao.insert(new Student("firstName1", "lastName1"));

        courseDao.insert(new Course(2,"testCourse2", "testCourseDescription2"));
        groupDao.insert(new Group("testGroup2"));
        studentDao.insert(new Student("firstName2", "lastName2"));

        StudentCourse studentCourse1 = new StudentCourse(1,1);
        StudentCourse studentCourse2 = new StudentCourse(2,2);

        studentCourseDao.insert(studentCourse1);
        studentCourseDao.insert(studentCourse2);

        List<StudentCourse> expected = new ArrayList<>();
        expected.add(new StudentCourse(1,1));
        expected.add(new StudentCourse(2,2));

        assertEquals(expected, studentCourseDao.getAll());

    }


    @Test
    public void testInsertAndGetByIdForStudentsCoursesDao() {
        GenericDao<StudentCourse> studentCourseDao = new StudentCoursesDao();
        GenericDao<Course> courseDao = new CourseDao();
        GenericDao<Group> groupDao = new GroupDao();
        GenericDao<Student> studentDao = new StudentDao();

        courseDao.insert(new Course(1,"testCourse", "testCourseDescription"));
        groupDao.insert(new Group("testGroup"));
        studentDao.insert(new Student("firstName1", "lastName1"));
        StudentCourse studentCourse = new StudentCourse(1,1);
        studentCourseDao.insert(studentCourse);

        assertEquals(studentCourse, studentCourseDao.getById(new IdKey(1,1)).get());

    }

    @Test
    public void testDeleteAndGetByIdForStudentsCoursesDao() {
        GenericDao<StudentCourse> studentCourseDao = new StudentCoursesDao();
        GenericDao<Course> courseDao = new CourseDao();
        GenericDao<Group> groupDao = new GroupDao();
        GenericDao<Student> studentDao = new StudentDao();

        courseDao.insert(new Course(1,"testCourse", "testCourseDescription"));
        groupDao.insert(new Group("testGroup"));
        studentDao.insert(new Student("firstName1", "lastName1"));
        StudentCourse studentCourse = new StudentCourse(1,1);
        studentCourseDao.insert(studentCourse);
        studentCourseDao.delete(studentCourse);

        assertThrows(RuntimeException.class, () -> studentCourseDao.getById(new IdKey(1,1)).get());

    }




}
