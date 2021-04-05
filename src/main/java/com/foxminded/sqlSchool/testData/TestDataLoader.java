package com.foxminded.sqlSchool.testData;

import com.foxminded.sqlSchool.DAO.GenericDao;
import com.foxminded.sqlSchool.DAO.GroupDao;
import com.foxminded.sqlSchool.DAO.StudentDao;
import com.foxminded.sqlSchool.DTO.Group;
import com.foxminded.sqlSchool.DTO.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class TestDataLoader {
    private final TestDataGenerator testDataGenerator;

    public TestDataLoader(TestDataGenerator testDataGenerator) {
        this.testDataGenerator = testDataGenerator;
    }

    public void loadTestData() {
        loadGroups();
        loadStudentsToGroups();
    }

    private void loadGroups() {
        GenericDao<Group> groupDao = new GroupDao();
        String[] groupNames = testDataGenerator.getGroupNames();
        for (int i = 0; i < groupNames.length; i++) {
            groupDao.insert(new Group(i + 1, groupNames[i]));
        }
    }

    private void loadStudentsToGroups() {
        GenericDao<Student> studentDao = new StudentDao();
        GenericDao<Group> groupDao = new GroupDao();
        List<Group> groupList = groupDao.getAll();
        AtomicInteger allowedStudentAmount = new AtomicInteger(200);
        int minGroupSize = 10;
        int maxGroupSize = 30;

        Map<Group, Integer> studentAmountInGroup = calculateGroupSizes();

        studentAmountInGroup.entrySet().forEach(group -> {
            if (group.getValue() >= minGroupSize && group.getValue() <= maxGroupSize) {
                allowedStudentAmount.addAndGet(-group.getValue());
                for (int i = 0; i < group.getValue(); i++) {
                    studentDao.insert(new Student(group.getKey().getId(), testDataGenerator.getRandomFirstName(), testDataGenerator.getRandomLastName()));
                }
            }
        });

        for (int i = allowedStudentAmount.get(); i > 0; i--) {
            studentDao.insert(new Student(testDataGenerator.getRandomFirstName(), testDataGenerator.getRandomLastName()));
        }
    }

    private Map<Group, Integer> calculateGroupSizes() {
        Map<Group, Integer> studentAmountInGroup = new HashMap<>();
        GenericDao<Group> groupDao = new GroupDao();
        List<Group> groupList = groupDao.getAll();

        int minGroupSize = 10;
        int maxGroupSize = 30;
        int allowedStudentAmount = 200;

        for (Group group : groupList) {
            int groupSize = ThreadLocalRandom.current().nextInt(minGroupSize, maxGroupSize + 1);
            if ((allowedStudentAmount - groupSize >= minGroupSize)) {
                studentAmountInGroup.put(group, groupSize);
                allowedStudentAmount -= groupSize;
            } else {
                studentAmountInGroup.put(group, 0);
            }
        }

        studentAmountInGroup.entrySet().forEach(g -> System.out.println(g.getKey() + " " + g.getValue()));
        return studentAmountInGroup;
    }


}
