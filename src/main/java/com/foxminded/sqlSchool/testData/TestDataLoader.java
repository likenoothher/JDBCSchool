package com.foxminded.sqlSchool.testData;

import com.foxminded.sqlSchool.DAO.*;
import com.foxminded.sqlSchool.DTO.Course;
import com.foxminded.sqlSchool.DTO.Group;
import com.foxminded.sqlSchool.DTO.Student;
import com.foxminded.sqlSchool.DTO.StudentCourse;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class TestDataLoader {
    private final TestDataGenerator testDataGenerator;

    public TestDataLoader(TestDataGenerator testDataGenerator) {
        this.testDataGenerator = testDataGenerator;
    }

    public void loadTestData() {
        loadGroups();
        loadStudents();
        loadCourses();
        bindStudentsToCourses();
    }

    private void loadGroups() {
        GenericDao<Group> groupDao = new GroupDao();
        String[] groupNames = testDataGenerator.getGroupNames();
        for (int i = 0; i < groupNames.length; i++) {
            groupDao.insert(new Group(groupNames[i]));
        }
    }

    private void loadStudents() {
        AtomicInteger distributedStudentsAmount = new AtomicInteger(20);

        Map<Group, Integer> groupSizes = calculateGroupSizes();

        groupSizes.entrySet().forEach(groupStudentsAmount -> insertStudentWithGroups(groupStudentsAmount, distributedStudentsAmount));

        insertStudentWithoutGroup(distributedStudentsAmount);
    }

    private void insertStudentWithGroups(Map.Entry<Group, Integer> groupStudentsAmount, AtomicInteger distributedStudentsAmount) {
        GenericDao<Student> studentDao = new StudentDao();
        int minGroupSize = 1;
        int maxGroupSize = 3;
        {
            if (groupStudentsAmount.getValue() >= minGroupSize && groupStudentsAmount.getValue() <= maxGroupSize) {
                distributedStudentsAmount.addAndGet(-groupStudentsAmount.getValue());
                for (int i = 0; i < groupStudentsAmount.getValue(); i++) {
                    studentDao.insert(new Student(groupStudentsAmount.getKey().getId(),
                        testDataGenerator.getRandomFirstName(), testDataGenerator.getRandomLastName()));
                }
            }
        }
    }

    private void insertStudentWithoutGroup(AtomicInteger distributedStudentsAmount) {
        GenericDao<Student> studentDao = new StudentDao();
        for (int i = distributedStudentsAmount.get(); i > 0; i--) {
            studentDao.insert(new Student(testDataGenerator.getRandomFirstName(), testDataGenerator.getRandomLastName()));
        }
    }

    private Map<Group, Integer> calculateGroupSizes() {
        Map<Group, Integer> groupSizes = new HashMap<>();
        GenericDao<Group> groupDao = new GroupDao();
        List<Group> groupList = groupDao.getAll();

        int minGroupSize = 1;
        int maxGroupSize = 3;
        int distributedStudentsAmount = 20;

        for (Group group : groupList) {
            int currentGroupSize = ThreadLocalRandom.current().nextInt(minGroupSize, maxGroupSize + 1);

            if ((distributedStudentsAmount - currentGroupSize >= minGroupSize)) {
                groupSizes.put(group, currentGroupSize);
                distributedStudentsAmount -= currentGroupSize;
            } else if (distributedStudentsAmount >= minGroupSize) {
                groupSizes.put(group, distributedStudentsAmount);
                distributedStudentsAmount -= distributedStudentsAmount;
            } else {
                groupSizes.put(group, 0);
            }
        }
        return groupSizes;
    }

    private void loadCourses() {
        GenericDao<Course> groupDao = new CourseDao();
        String[] courses = testDataGenerator.getCourses();
        for (int i = 0; i < courses.length; i++) {
            groupDao.insert(new Course(courses[i], courses[i] + " description"));
        }
    }

    private void bindStudentsToCourses() {
        GenericDao<Student> studentDao = new StudentDao();
        GenericDao<Course> courseDao = new CourseDao();
        GenericDao<StudentCourse> studentCourseDao = new StudentCoursesDao();

        int minCourseAmount = 1;
        int maxCourseAmount = 3;

        List<Student> students = studentDao.getAll();
        List<Course> courses = courseDao.getAll();

        for (Student student : students) {
            int coursesAmount = ThreadLocalRandom.current().nextInt(minCourseAmount, maxCourseAmount + 1);
            List<Course> pickedCourses = getRandomCourses(coursesAmount, courses);
            for (Course course : pickedCourses) {
                StudentCourse studentCourse = new StudentCourse(student.getId(),
                    course.getId());
                studentCourseDao.insert(studentCourse);
            }
        }

    }

    private List<Course> getRandomCourses(int coursesAmount, List<Course> courses) {
        List<Course> pickedCourses = new ArrayList<>();
        Random random = new Random();
        while (pickedCourses.size() < coursesAmount) {
            int courseIndex = random.nextInt(courses.size());
            if (!pickedCourses.contains(courses.get(courseIndex))) {
                pickedCourses.add(courses.get(courseIndex));
            }
        }

        return pickedCourses;
    }


}
