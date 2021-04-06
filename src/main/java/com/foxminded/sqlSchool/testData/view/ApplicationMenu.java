package com.foxminded.sqlSchool.testData.view;

import com.foxminded.sqlSchool.ConnectionBuilder;
import com.foxminded.sqlSchool.DAO.GenericDao;
import com.foxminded.sqlSchool.DAO.StudentCoursesDao;
import com.foxminded.sqlSchool.DAO.StudentDao;
import com.foxminded.sqlSchool.DTO.Student;
import com.foxminded.sqlSchool.DTO.StudentCourse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationMenu {
    private static final String GET_AVAILABLE_COURSES_FOR_STUDENT = "select course_id, course_name from courses\n" +
        "where course_id not in (select course_id from students_courses where student_id = ?)";

    private static final String GET_STUDENT_COURSES = "select student_id, students_courses.course_id, course_name\n" +
        "from students_courses inner join courses on students_courses.course_id = courses.course_id\n" +
        "where student_id = ?;";

    private static final String GET_COURSE_PARTICIPANTS = "select courses.course_id, courses.course_name,\n" +
        "students_courses.student_id, students.first_name, students.last_name\n" +
        "from students_courses  \n" +
        "inner join courses on courses.course_id = students_courses.course_id \n" +
        "inner join students on students.student_id = students_courses.student_id\n" +
        "where courses.course_name = ?";

    private static final String GET_GROUPS_NAMES_AMOUNT_LESS_OR_EQUAL = "select students.group_id,\n" +
        "groups.group_name, count (students.group_id) \n" +
        "from students\n" +
        "inner join groups on students.group_id = groups.group_id\n" +
        "group by students.group_id, groups.group_id\n" +
        "having count (students.group_id) <= ?;";

    private static final List<String> appropriateChoices = List.of("a", "b", "c", "d", "e", "f");


    public static void callApplicationMenu() {
        print("Choose one from suggested options, insert item letter and press Enter:\n" +
            "a. Find all groups with less or equals student count\n" +
            "b. Find all students related to course with given name\n" +
            "c. Add new student\n" +
            "d. Delete student by STUDENT_ID\n" +
            "e. Add a student to the course (from a list)\n" +
            "f. Remove the student from one of his or her courses\n");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                String chooseItem = reader.readLine().toLowerCase();
                if (appropriateChoices.contains(chooseItem)) {
                    callMenuItem(chooseItem);
                    break;
                } else {
                    print("Incorrect choice");
                    callApplicationMenu();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during picking menu item ");
        }

    }

    private static void callMenuItem(String chooseItem) {
        if (chooseItem.equals("a")) {
            findGroupsLessOrEqualAmount();
            callApplicationMenu();
        }
        if (chooseItem.equals("b")) {
            findCourseParticipants();
            callApplicationMenu();
        }
        if (chooseItem.equals("c")) {
            addNewStudent();
            callApplicationMenu();
        }
        if (chooseItem.equals("d")) {
            deleteStudentById();
            callApplicationMenu();
        }
        if (chooseItem.equals("e")) {
            addStudentToCourse();
            callApplicationMenu();
        }
        if (chooseItem.equals("f")) {
            deleteStudentFromCourse();
            callApplicationMenu();
        }
    }

    private static void findGroupsLessOrEqualAmount() {
        print("Enter limit of group participants: ");
        int participantsLimit = readNumber();
        List<String> groupNames = new ArrayList<>();

        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_GROUPS_NAMES_AMOUNT_LESS_OR_EQUAL);) {
            preparedStatement.setInt(1, participantsLimit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String groupName = (resultSet.getString("GROUP_NAME"));
                groupNames.add(groupName);
            }
            print("Groups with participants amount less or equal than " + participantsLimit + " are:");
            print(groupNames);

        } catch (SQLException e) {
            throw new RuntimeException("Error during getting groups ", e.getCause());
        }

    }

    private static void findCourseParticipants() {
        print("Insert course name:");
        String courseName = readString();
        List<String> courseParticipants = new ArrayList<>();

        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_COURSE_PARTICIPANTS);) {
            preparedStatement.setString(1, courseName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("FIRST_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                courseParticipants.add(lastName + " " + firstName);
            }
            print("Participants of " + courseName + " are:");
            print(courseParticipants);

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all students from the database ", e.getCause());
        }

    }

    private static void addNewStudent() {
        print("Insert student first name:");
        String firstName = readString();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        print("Insert student last name:");
        String lastName = readString();
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();

        new StudentDao().insert(new Student(firstName, lastName));
        print("Student was successfully added");
    }

    private static void deleteStudentById() {
        print("Insert ID of student for deleting: ");
        int studentIdInt = readNumber();
        GenericDao<Student> studentDao = new StudentDao();
        studentDao.delete(studentDao.getById(studentIdInt).get()); // fix it
        print("Student with ID " + studentIdInt + " was successfully deleted");
    }

    private static void addStudentToCourse() {
        GenericDao<StudentCourse> studentCoursesDao = new StudentCoursesDao();
        print("Insert ID of student you want to add a course to: ");
        int studentId = readNumber();

        Map<Integer, String> availableCoursesToAdd = new HashMap<>();

        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_AVAILABLE_COURSES_FOR_STUDENT);) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int courseId = resultSet.getInt("COURSE_ID");
                String courseName = resultSet.getString("COURSE_NAME");
                availableCoursesToAdd.put(courseId, courseName);
            }
            print("Insert course id to add:");
            print(availableCoursesToAdd);
            int chooseId = readNumber();
            studentCoursesDao.insert(new StudentCourse(studentId, chooseId));
            print("Student was successfully added to course");
        } catch (SQLException e) {
           throw  new RuntimeException("Something went wrong during adding additional course to student");
        }
    }


    private static void deleteStudentFromCourse() {
        print("Insert ID of student for deleting one of his/her courses: ");

        int studentId = readNumber();

        Map<Integer, String> studentCourses = new HashMap<>();

        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENT_COURSES)) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int courseId = resultSet.getInt("COURSE_ID");
                String courseName = (resultSet.getString("COURSE_NAME"));
                studentCourses.put(courseId, courseName);
            }
            print("What course do you want to delete? Insert course id:");
            print(studentCourses);
            int courseIdForDelete = readNumber();

            new StudentCoursesDao().delete(new StudentCourse(studentId, courseIdForDelete));
            print("Course was successfully deleted");
        } catch (SQLException e) {
            throw new RuntimeException("Error getting groups ", e.getCause());
        }


    }

    private static String readString() {  // add closing!!!!!!!!!!!!!!
        String line = new String();
        while (line.isEmpty()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                line = reader.readLine();
            } catch (IOException e) {
                System.out.println("Some problems occurred during reading input");
            }

        }
        return line;
    }

    private static int readNumber() {
        int number = -1;
        while (number == -1) {
            try {
                number = Integer.parseInt(readString());
            } catch (NumberFormatException e) {
                System.out.println("Some problems occurred during parsing input. Try again");
            }
        }
        return number;
    }

    private static void print(Map<Integer, String> mapToPrint) {
        mapToPrint.forEach((key, value) -> System.out.println(key + "." + value));
    }

    private static void print(List<String> listToPrint){
        listToPrint.forEach(System.out::println);
    }

    private static void print(String stringToPrint){
        System.out.println(stringToPrint);
    }
}
