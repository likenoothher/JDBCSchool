package com.foxminded.sqlschool.schoolrepository;

import com.foxminded.sqlschool.connection.ConnectionBuilder;
import com.foxminded.sqlschool.dto.Group;
import com.foxminded.sqlschool.dto.Student;
import com.foxminded.sqlschool.scriptExecutor.SqlScriptExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupDaoTest {
    private com.foxminded.sqlschool.scriptExecutor.SqlScriptExecutor scriptExecutor;
    private DaoFactory daoFactory;
    private GroupDao groupDao;
    private StudentDao studentDao;
    private List<Group> groups;
    private List<Student> students;

    @BeforeEach
    public void loadData() {
        daoFactory = new DaoFactory();
        scriptExecutor = new SqlScriptExecutor(ConnectionBuilder.getConnection());
        scriptExecutor.executeSQLScript("src\\main\\resources\\createTablesScript.sql");

        groups = new ArrayList<>();
        groups.add(new Group("group1"));
        groups.add(new Group("group2"));
        groups.add(new Group("group3"));

        groupDao = (GroupDao) daoFactory.getDao(DaoType.GROUP_DAO);
        groups.forEach(group -> {
            try {
                groupDao.insert(group);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        });

        students = new ArrayList<>();
        students.add(new Student(1, "firstName1", "lastName1"));
        students.add(new Student(1, "firstName2", "lastName2"));
        students.add(new Student(2, "firstName3", "lastName3"));
        students.add(new Student(3, "firstName4", "lastName4"));

        studentDao = (StudentDao) daoFactory.getDao(DaoType.STUDENT_DAO);
        students.forEach(student -> {
            try {
                studentDao.insert(student);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void whenGetGroupsWithAMountLessOrEqual_thenReturnGroupList() throws DaoException {
        List<Group> groups = groupDao.getGroupsWithAMountLessOrEqual(1);

        assertEquals(2, groups.size());

        assertTrue(groups.contains(groupDao.getById(2).get()));
        assertTrue(groups.contains(groupDao.getById(3).get()));
        assertFalse(groups.contains(groupDao.getById(1).get()));
    }
}
