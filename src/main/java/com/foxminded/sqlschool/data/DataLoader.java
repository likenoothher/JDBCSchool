package com.foxminded.sqlschool.data;

import com.foxminded.sqlschool.dto.Course;
import com.foxminded.sqlschool.dto.Group;
import com.foxminded.sqlschool.dto.Student;
import com.foxminded.sqlschool.schoolrepository.*;
import org.apache.log4j.Logger;

import java.util.*;

public class DataLoader {
    private final static Logger logger = Logger.getLogger(DataLoader.class);

    private final int DEFAULT_MIN_GROUP_SIZE = 10;
    private final int DEFAULT_MAX_GROUP_SIZE = 30;

    private final int DEFAULT_MIN_COURSE_AMOUNT_STUDENT = 1;
    private final int DEFAULT_MAX_COURSE_AMOUNT_STUDENT = 3;

    private final int DEFAULT_DISTRIBUTED_STUDENTS_AMOUNT = 200;

    private final DaoFactory daoFactory = new DaoFactory();

    private final DataGenerator dataGenerator;

    public DataLoader(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    public void loadTestData() {
        logger.info("Starting loading test data");
        loadGroups();
        loadStudents();
        loadCourses();
        bindStudentsToCourses();
        logger.info("Test data loaded successfully");
    }

    private void loadGroups() {
        logger.info("Starting loading groups");
        GenericDao groupDao = daoFactory.getDao(DaoType.GROUP_DAO);
        String[] groupNames = dataGenerator.getGroupNames();
        for (int i = 0; i < groupNames.length; i++) {
            try {
                groupDao.insert(new Group(groupNames[i]));
            } catch (DaoException e) {
                logger.warn("Can't load group " + groupNames[i] + " to database", e);
            }
        }
        logger.info("Groups loaded successfully");
    }

    private void loadStudents() {
        logger.info("Starting loading students");
        final Integer[] studentsForDistribution = {DEFAULT_DISTRIBUTED_STUDENTS_AMOUNT};

        Map<Group, Integer> groupSizes = getCalculatedGroupSizes();

        groupSizes.entrySet().forEach(groupStudentsAmount ->
        {
            insertStudentToGroups(groupStudentsAmount);
            studentsForDistribution[0] -= groupStudentsAmount.getValue();
        });

        insertStudentWithoutGroup(studentsForDistribution[0]);
        logger.info("Groups loaded successfully");
    }

    private void insertStudentToGroups(Map.Entry<Group, Integer> groupStudentsAmount) {
        logger.info("Starting inserting students from group "
            + groupStudentsAmount.getKey().getGroupName() + " to the database");
        GenericDao studentDao = daoFactory.getDao(DaoType.STUDENT_DAO);

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
        logger.info("Students from group " + groupStudentsAmount.getKey().getGroupName()
            + " successfully added to the database");
    }

    private void insertStudentWithoutGroup(int distributedStudentsAmount) {
        logger.info("Starting inserting students without groups to the database");
        GenericDao<Student, Integer> studentDao = daoFactory.getDao(DaoType.STUDENT_DAO);
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
        logger.info("Students with groups successfully added to the database");
    }

    private Map<Group, Integer> getCalculatedGroupSizes() {
        logger.info("Starting calculating sizes of groups");
        Map<Group, Integer> groupSizes = new HashMap<>();
        GenericDao<Group, Integer> groupDao = daoFactory.getDao(DaoType.GROUP_DAO);
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
        logger.info("Sizes of groups successfully calculated");
        return groupSizes;
    }

    private void calculateGroupSize(Group group, Map<Group, Integer> groupSizes, Integer distributedStudentsAmount) {
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
        logger.info("Starting loading courses");
        GenericDao<Course, Integer> courseDao = daoFactory.getDao(DaoType.COURSE_DAO);
        String[] courses = dataGenerator.getCourses();
        for (String course : courses) {
            try {
                courseDao.insert(new Course(course, course + " description"));
            } catch (DaoException e) {
                logger.warn("Can't insert course " + course + " to database", e);
            }
        }
        logger.info("Groups loaded successfully");
    }

    private void bindStudentsToCourses() {
        logger.info("Starting binding students to courses");
        StudentDao studentDao = (StudentDao) daoFactory.getDao(DaoType.STUDENT_DAO);
        GenericDao<Course, Integer> courseDao = daoFactory.getDao(DaoType.COURSE_DAO);

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
            int coursesAmount = getRandomNumber(DEFAULT_MIN_COURSE_AMOUNT_STUDENT, DEFAULT_MAX_COURSE_AMOUNT_STUDENT + 1);
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
        logger.info("Students have successfully bind to courses");
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
