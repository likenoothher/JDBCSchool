package com.foxminded.sqlschool.schoolrepository;

import com.foxminded.sqlschool.connection.ConnectionBuilder;
import com.foxminded.sqlschool.dto.Course;
import com.foxminded.sqlschool.dto.GenericDto;
import com.foxminded.sqlschool.dto.Group;
import com.foxminded.sqlschool.dto.Student;
import com.foxminded.sqlschool.scriptExecutor.SqlScriptExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenericDAOTest {

    @Parameterized.Parameters
    static Collection<Object> data() {

        return Arrays.asList(new Object[][]{
            {new Course(1, "testCourse", "testCourseDescription"),
                DaoType.COURSE_DAO},
            {new Group(1, "testGroup"), DaoType.GROUP_DAO},
            {new Student("firstName", "lastName", 1), DaoType.STUDENT_DAO},
        });
    }

    @Parameterized.Parameters
    public static Collection<Object> dataForGetAllMethod() {

        return Arrays.asList(new Object[][]{
            {Arrays.asList(new Course(1, "testCourse1", "testCourseDescription1"),
                new Course(2, "testCourse2", "testCourseDescription2")),
                DaoType.COURSE_DAO},

            {Arrays.asList(new Group(1, "testGroup1"), new Group(2, "testGroup2")), DaoType.GROUP_DAO},

            {Arrays.asList(new Student("firstName1", "lastName1", 1),
                new Student("firstName1", "lastName1", 2)), DaoType.STUDENT_DAO},

        });
    }

    private SqlScriptExecutor scriptExecutor;
    private DaoFactory daoFactory;

    @BeforeEach
    public void loadData() {
        daoFactory = new DaoFactory();
        scriptExecutor = new SqlScriptExecutor(ConnectionBuilder.getConnection());
        scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");

    }

//    @ParameterizedTest
//    @MethodSource("dataForGetAllMethod")
//    public void testGetAll(List input, DaoType type) {
//        GenericDao testDao = daoFactory.getDao(type);
//        input.forEach(DTObject -> {
//            try {
//                testDao.insert(DTObject);
//            } catch (DaoException e) {
//                e.printStackTrace();
//            }
//        });
//
//        try {
//            assertEquals(input, testDao.getAll());
//        } catch (DaoException e) {
//            e.printStackTrace();
//        }
//    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInsertAndGetById(GenericDto input, DaoType type) throws DaoException {
        GenericDao testDao = daoFactory.getDao(type);

        testDao.insert(input);
        assertEquals(input, testDao.getById(input.getId()).get());

    }

    @ParameterizedTest
    @MethodSource("data")
    public void testDeleteAndGetById(GenericDto input, DaoType type) throws DaoException {
        GenericDao testDao = daoFactory.getDao(type);

        testDao.insert(input);
        testDao.delete(input.getId());
        assertEquals(null, testDao.getById(input.getId()).orElse(null));

    }

}
