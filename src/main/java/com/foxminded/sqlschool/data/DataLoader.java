package com.foxminded.sqlschool.data;

import com.foxminded.sqlschool.dto.Course;
import com.foxminded.sqlschool.dto.Group;
import com.foxminded.sqlschool.dto.Student;
import com.foxminded.sqlschool.schoolrepository.*;
import org.apache.log4j.Logger;

import java.util.*;

public class DataLoader {
    private final static Logger logger = Logger.getLogger(DataLoader.class);

    private final int DEFAULT_MIN_GROUP_SIZE = 1;
    private final int DEFAULT_MAX_GROUP_SIZE = 3;

    private final int DEFAULT_MIN_COURSE_AMOUNT = 1;
    private final int DEFAULT_MAX_COURSE_AMOUNT = 3;

    private final int DEFAULT_DISTRIBUTED_STUDENTS_AMOUNT = 20;

    private final DataGenerator dataGenerator;

    public DataLoader(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    public void loadTestData() {
        loadGroups();
        loadStudents();
        loadCourses();
        bindStudentsToCourses();
    }

    private void loadGroups() {

        GenericDao<Group, Integer> groupDao = new GroupDao();
        String[] groupNames = dataGenerator.getGroupNames();
        for (int i = 0; i < groupNames.length; i++) {
            try {
                groupDao.insert(new Group(groupNames[i]));
            } catch (DaoException e) {
                logger.warn("Can't load group " + groupNames[i] + " to database", e);
            }
        }
    }

    private void loadStudents() {
        final Integer[] studentsForDistribution = {DEFAULT_DISTRIBUTED_STUDENTS_AMOUNT};

        Map<Group, Integer> groupSizes = getCalculatedGroupSizes();

        groupSizes.entrySet().forEach(groupStudentsAmount ->
        {
            insertStudentToGroups(groupStudentsAmount);
            studentsForDistribution[0] -= groupStudentsAmount.getValue();
        });

        insertStudentWithoutGroup(studentsForDistribution[0]);
    }

    private void insertStudentToGroups(Map.Entry<Group, Integer> groupStudentsAmount) {
        GenericDao<Student, Integer> studentDao = new StudentDao();

        if (groupStudentsAmount.getValue() >= DEFAULT_MIN_GROUP_SIZE && groupStudentsAmount.getValue() <= DEFAULT_MAX_GROUP_SIZE) {
            for (int i = 0; i < groupStudentsAmount.getValue(); i++) {
                int id = groupStudentsAmount.getKey().getId();
                String firstName = dataGenerator.getRandomFirstName();
                String lastName = dataGenerator.getRandomLastName();
                try {

                    studentDao.insert(new Student(id, firstName, lastName));
                } catch (DaoException e) {
                    logger.warn("Can't insert student " + firstName +
                        " " + lastName + " with group id " + id + " to database", e);
                }
            }
        }

    }

    private void insertStudentWithoutGroup(int distributedStudentsAmount) {
        GenericDao<Student, Integer> studentDao = new StudentDao();
        for (int i = distributedStudentsAmount; i > 0; i--) {
            String firstName = dataGenerator.getRandomFirstName();
            String lastName = dataGenerator.getRandomLastName();

            try {
                studentDao.insert(new Student(firstName, lastName));
            } catch (DaoException e) {
                logger.warn("Can't insert student " + firstName +
                    " " + lastName + " to database", e);
            }
        }
    }

    private Map<Group, Integer> getCalculatedGroupSizes() {
        Map<Group, Integer> groupSizes = new HashMap<>();
        GenericDao<Group, Integer> groupDao = new GroupDao();
        List<Group> groupList = null;
        try {
            groupList = groupDao.getAll();
        } catch (DaoException e) {
            logger.warn("Can't get all groups from database", e);
        }

        Integer distributedStudentsAmount = DEFAULT_DISTRIBUTED_STUDENTS_AMOUNT;

        for (Group group : groupList) {
            calculateGroupSize(group, groupSizes, distributedStudentsAmount);
        }
        return groupSizes;
    }

    private void calculateGroupSize(Group group, Map<Group, Integer> groupSizes, int distributedStudentsAmount) {
        int currentGroupSize = getRandomNumber(DEFAULT_MIN_GROUP_SIZE, DEFAULT_MAX_GROUP_SIZE + 1);

        if ((distributedStudentsAmount - currentGroupSize >= DEFAULT_MIN_GROUP_SIZE)) {
            groupSizes.put(group, currentGroupSize);
            distributedStudentsAmount -= currentGroupSize;
        } else if (distributedStudentsAmount >= DEFAULT_MIN_GROUP_SIZE) {
            groupSizes.put(group, distributedStudentsAmount);
            distributedStudentsAmount -= distributedStudentsAmount;
        } else {
            groupSizes.put(group, 0);
        }
    }

    private void loadCourses() {
        GenericDao<Course, Integer> courseDao = new CourseDao();
        String[] courses = dataGenerator.getCourses();
        for (int i = 0; i < courses.length; i++) {
            try {
                courseDao.insert(new Course(courses[i], courses[i] + " description"));
            } catch (DaoException e) {
                logger.warn("Can't insert course " + courses[i] + " to database", e);
            }
        }
    }

    private void bindStudentsToCourses() {
        StudentDao studentDao = new StudentDao();
        GenericDao<Course, Integer> courseDao = new CourseDao();

        List<Student> students = null;
        try {
            students = studentDao.getAll();
        } catch (DaoException e) {
            logger.warn("Can't get all students from database", e);
        }
        List<Course> courses = null;
        try {
            courses = courseDao.getAll();
        } catch (DaoException e) {
            logger.warn("Can't get all courses from database", e);
        }

        for (Student student : students) {
            int coursesAmount = getRandomNumber(DEFAULT_MIN_COURSE_AMOUNT, DEFAULT_MAX_COURSE_AMOUNT + 1);
            List<Course> pickedCourses = getRandomCourses(coursesAmount, courses);
            for (Course course : pickedCourses) {
                try {
                    studentDao.addStudentToCourse(student.getId(), course.getId());
                } catch (DaoException e) {
                    logger.warn("Can't add student with Id" + student.getId() + " to course " +
                        "with Id" + course.getId(), e);
                }
            }
        }
    }

    private List<Course> getRandomCourses(int coursesAmount, List<Course> courses) {
        List<Course> pickedCourses = new ArrayList<>();
        while (pickedCourses.size() < coursesAmount) {
            int courseIndex = getRandomNumber(courses.size());
            if (!pickedCourses.contains(courses.get(courseIndex))) {
                pickedCourses.add(courses.get(courseIndex));
            }
        }

        return pickedCourses;
    }

    private int getRandomNumber(int bound) {
        return new Random().nextInt(bound);
    }

    private int getRandomNumber(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }


}
