package com.foxminded.sqlSchool.testData.view;

import com.foxminded.sqlSchool.ConnectionBuilder;
import com.foxminded.sqlSchool.DAO.*;
import com.foxminded.sqlSchool.DTO.Course;
import com.foxminded.sqlSchool.DTO.Group;
import com.foxminded.sqlSchool.DTO.Student;
import com.foxminded.sqlSchool.DTO.StudentCourse;
import com.foxminded.sqlSchool.queryExecutor.MenyQueryExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationMenu {
    private static final MenyQueryExecutor menyQueryExecutor = new MenyQueryExecutor();
    private static final List<String> appropriateChoices = List.of("a", "b", "c", "d", "e", "f");

    public static void showMenu() {
        System.out.println("Choose one from suggested options, insert item letter and press Enter:\n" +
            "a. Find all groups with less or equals student count\n" +
            "b. Find all students related to course with given name\n" +
            "c. Add new student\n" +
            "d. Delete student by STUDENT_ID\n" +
            "e. Add a student to the course (from a list)\n" +
            "f. Remove the student from one of his or her courses\n");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                String choosedItem = reader.readLine().toLowerCase();
                if (appropriateChoices.contains(choosedItem)) {
                    invokeMenuItem(choosedItem);
                    break;
                } else {
                    System.out.println("Incorrect choice");
                    showMenu();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during picking menu item ");
        }

    }

    private static void invokeMenuItem(String choosedItem) {
        if (choosedItem.equals("a")) {
            findGroupsLessOrEqualAmount();
        }
        if (choosedItem.equals("b")) {
            findCourseParticipants();
        }
        if (choosedItem.equals("c")) {
            addNewStudent();
        }
        if (choosedItem.equals("d")) {
            deleteStudentById();
        }
        if (choosedItem.equals("e")) {
            addStudentToCourse();
        }
        if (choosedItem.equals("f")) {
            deleteStudentFromCourse();
        }
    }

    private static void findGroupsLessOrEqualAmount() {
        System.out.println("Enter limit of group entrance: ");

        String sqlQuery = "select students.group_id, groups.group_name, count (students.group_id) \n" +
            "from students\n" +
            "inner join groups on students.group_id = groups.group_id\n" +
            "group by students.group_id, groups.group_id\n" +
            "having count (students.group_id) <= ?;";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            GenericDao<Group> groupDao = new GroupDao();
            List<String> groupNames = new ArrayList<>();
            int participantsLimit = Integer.parseInt(reader.readLine());
            System.out.println("Groups with participants amount less or equal than " + participantsLimit + " are:");

            try (Connection connection = ConnectionBuilder.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ) {
                preparedStatement.setInt(1, participantsLimit);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String groupName = (resultSet.getString("GROUP_NAME"));
                    groupNames.add(groupName);
                }

                groupNames.forEach(System.out::println);

            } catch (SQLException e) {
                throw new RuntimeException("Error getting groups ", e.getCause());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during searching groups ");
        }
    }

    private static void findCourseParticipants() {
        System.out.println("Insert course name:");
        String sqlQuery = "select courses.course_id, courses.course_name, students_courses.course_id, students_courses.student_id\n" +
            "from courses  inner join students_courses on courses.course_id = students_courses.course_id\n" +
            "where courses.course_name = ?;";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String courseName = reader.readLine();
            GenericDao<Student> studentDao = new StudentDao();
            List<Student> courseParticipants = new ArrayList<>();


            try (Connection connection = ConnectionBuilder.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ) {
                preparedStatement.setString(1, courseName);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int studentId = (resultSet.getInt("STUDENT_ID"));
                    courseParticipants.add(studentDao.getById(studentId).get());
                }
                System.out.println("Participants of " + courseName + " are:");
                courseParticipants.forEach(courseParticipant -> {
                    System.out.println(courseParticipant.getFirstName() + " " + courseParticipant.getLastName());
                });

            } catch (SQLException e) {
                throw new RuntimeException("Error getting all students from the database ", e.getCause());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during searching course participants ");
        }
    }

    private static void addNewStudent() {
        System.out.println("Insert student first name:");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String firstName = reader.readLine();
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            System.out.println("Insert student last name:");
            String lastName = reader.readLine();
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            Student student = new Student(firstName, lastName);
            new StudentDao().insert(student);
            ApplicationMenu.showMenu();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during establishing student's name ");
        }
    }

    private static void deleteStudentById() {
        System.out.print("Insert ID of student for deleting: ");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String studentId = reader.readLine().toLowerCase(); // add checking for string!!!!!!!!!!!!!
            int studentIdInt = Integer.parseInt(studentId);
            GenericDao<Student> studentDao = new StudentDao();
            studentDao.delete(studentDao.getById(studentIdInt).get()); // fix it
            System.out.println("Student with ID " + studentIdInt + " was successfully deleted\n");
            ApplicationMenu.showMenu();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during establishing student's ID ");
        }
    }

    private static void addStudentToCourse() {
        System.out.print("Insert ID of student you want to add a course to: ");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String studentId = reader.readLine(); // add checking for string!!!!!!!!!!!!!
            int studentIdInt = Integer.parseInt(studentId);
            GenericDao<Student> studentDao = new StudentDao();
            GenericDao<Course> courseDao = new CourseDao();
            StudentCoursesDao studentCoursesDao = new StudentCoursesDao();

            Student student = studentDao.getById(studentIdInt).get();

            List<Integer> studentCoursesIds = studentCoursesDao.getStudentsCoursesIds(studentIdInt);

            System.out.print("Insert ID of course you want to add for a student with ID " + studentId + ": ");
            while (true) {

                String courseId = reader.readLine(); // add checking for string!!!!!!!!!!!!!
                int courseIdInt = Integer.parseInt(courseId);
                if (studentCoursesIds.contains(courseIdInt)) {
                    System.out.println("Student already has this course. Pick another one:");
                } else {
                    studentCoursesDao.insert(new StudentCourse(studentIdInt, courseIdInt));
                    break;
                }
            }


            System.out.println("Course was successfully addded\n");
            ApplicationMenu.showMenu();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during add course to student");
        }
    }

    private static void deleteStudentFromCourse() {
        System.out.print("Insert ID of student for deleting one of his/her courses: ");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String studentId = reader.readLine(); // add checking for string!!!!!!!!!!!!!
            int studentIdInt = Integer.parseInt(studentId);
            StudentCoursesDao studentCoursesDao = new StudentCoursesDao();
            List<Integer> studentCoursesIds = studentCoursesDao.getStudentsCoursesIds(studentIdInt);

            GenericDao<Course> courseDao = new CourseDao();

            List<Course> studentCourses = new ArrayList<>();

            System.out.println("What course do you want to delete? Insert number of string");

            for (Integer courseId : studentCoursesIds) {
                Course course = courseDao.getById(courseId).get();
                System.out.println(course.getName());
                studentCourses.add(course);
            }
            int courseItemToDelete = Integer.parseInt(reader.readLine());
            studentCoursesDao.delete(new StudentCourse(studentIdInt, studentCourses.get(courseItemToDelete - 1).getId()));

            System.out.println("Course with ID " + courseItemToDelete + " was successfully deleted from student" +
                " with ID " + studentId + "\n");
            ApplicationMenu.showMenu();

            System.out.println("Student with ID " + studentIdInt + " was successfully deleted\n");
            ApplicationMenu.showMenu();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during establishing student's ID ");
        }
    }
}
