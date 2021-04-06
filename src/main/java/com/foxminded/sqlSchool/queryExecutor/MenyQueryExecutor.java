package com.foxminded.sqlSchool.queryExecutor;

import com.foxminded.sqlSchool.DAO.GenericDao;
import com.foxminded.sqlSchool.DAO.StudentDao;
import com.foxminded.sqlSchool.DTO.Group;
import com.foxminded.sqlSchool.DTO.Student;
import com.foxminded.sqlSchool.testData.view.ApplicationMenu;

import java.util.HashSet;
import java.util.Set;

public class MenyQueryExecutor {

    public void findGroupsWitLessOrEqual(int studentAmount) {
        Set<Group> findedGroups = new HashSet<>();
    }

//    public List<Student> findStudentsByCourseName(){
//
//    }

    public void addNewStudent() {
    }

    public void deleteStudentById(int id) {
        GenericDao<Student> studentDao = new StudentDao();
        studentDao.delete(studentDao.getById(id).get()); // fix it
        System.out.println("Student with ID " + id + " was successfully deleted");
        ApplicationMenu.showMenu();
    }
}
