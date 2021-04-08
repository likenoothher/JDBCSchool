package com.foxminded.sqlSchool.menu;

import com.foxminded.sqlSchool.DAO.GenericDao;
import com.foxminded.sqlSchool.DAO.IdKey;
import com.foxminded.sqlSchool.DAO.StudentCoursesDao;
import com.foxminded.sqlSchool.DAO.StudentDao;
import com.foxminded.sqlSchool.DTO.Student;
import com.foxminded.sqlSchool.DTO.StudentCourse;
import com.foxminded.sqlSchool.connection.ConnectionBuilder;
import com.foxminded.sqlSchool.outputInputTools.ConsolePrinter;
import com.foxminded.sqlSchool.outputInputTools.ConsoleReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationMenu {
    private static final String GET_GROUPS_NAMES_AMOUNT_LESS_OR_EQUAL = "select students.group_id,\n" +
        "groups.group_name, count (students.group_id) \n" +
        "from students\n" +
        "inner join groups on students.group_id = groups.group_id\n" +
        "group by students.group_id, groups.group_id\n" +
        "having count (students.group_id) <= ?;";

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


    private final List<String> appropriateChoices = List.of("a", "b", "c", "d", "e", "f");

    private final ConsolePrinter printer = new ConsolePrinter();
    private final ConsoleReader reader = new ConsoleReader();


    public void callApplicationMenu() {
        printer.print("Choose one from suggested options, insert item letter and press Enter:\n" +
            "a. Find all groups with less or equals student count\n" +
            "b. Find all students related to course with given name\n" +
            "c. Add new student\n" +
            "d. Delete student by STUDENT_ID\n" +
            "e. Add a student to the course (from a list)\n" +
            "f. Remove the student from one of his or her courses\n");

        while (true) {
            String chooseItem = reader.readString().toLowerCase();
            if (appropriateChoices.contains(chooseItem)) {
                callMenuItem(chooseItem);
                break;
            } else {
                printer.print("Incorrect choice");
                callApplicationMenu();
            }
        }
    }

    private void callMenuItem(String chooseItem) {
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

    private void findGroupsLessOrEqualAmount() {
        printer.print("Enter limit of group participants: ");
        int participantsLimit = reader.readNumber();
        List<String> groupNames = new ArrayList<>();

        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_GROUPS_NAMES_AMOUNT_LESS_OR_EQUAL)) {
            preparedStatement.setInt(1, participantsLimit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String groupName = (resultSet.getString("GROUP_NAME"));
                groupNames.add(groupName);
            }
            resultSet.close();

            printer.print("Groups with participants amount less or equal than " + participantsLimit + " are:");
            printer.print(groupNames);

        } catch (SQLException e) {
            throw new RuntimeException("Error during getting groups ", e.getCause());
        }
    }

    private void findCourseParticipants() {
        printer.print("Insert course name:");
        String courseName = reader.readString();
        List<String> courseParticipants = new ArrayList<>();

        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_COURSE_PARTICIPANTS)) {
            preparedStatement.setString(1, courseName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String firstName = resultSet.getString("FIRST_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                courseParticipants.add(lastName + " " + firstName);
            }
            resultSet.close();

            printer.print("Participants of " + courseName + " are:");
            printer.print(courseParticipants);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all students from the database ", e.getCause());
        }
    }

    private void addNewStudent() {
        printer.print("Insert student first name:");
        String firstName = reader.readString();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        printer.print("Insert student last name:");
        String lastName = reader.readString();
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();

        new StudentDao().insert(new Student(firstName, lastName));
        printer.print("Student was successfully added");
    }

    private void deleteStudentById() {
        printer.print("Insert ID of student for deleting: ");
        IdKey studentId = new IdKey(reader.readNumber());
        GenericDao<Student> studentDao = new StudentDao();
        studentDao.delete(studentDao.getById(studentId).get()); // fix it
        printer.print("Student with ID " + studentId.getFirstId() + " was successfully deleted");
    }

    private void addStudentToCourse() {
        GenericDao<StudentCourse> studentCoursesDao = new StudentCoursesDao();
        printer.print("Insert ID of student you want to add a course to: ");
        int studentId = reader.readNumber();

        Map<Integer, String> availableCoursesToAdd = new HashMap<>();

        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_AVAILABLE_COURSES_FOR_STUDENT)) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int courseId = resultSet.getInt("COURSE_ID");
                String courseName = resultSet.getString("COURSE_NAME");
                availableCoursesToAdd.put(courseId, courseName);
            }
            resultSet.close();

            printer.print("Insert course id to add:");
            printer.print(availableCoursesToAdd);
            int chooseId = reader.readNumber();
            studentCoursesDao.insert(new StudentCourse(studentId, chooseId));
            printer.print("Student was successfully added to course");
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong during adding additional course to student");
        }
    }


    private void deleteStudentFromCourse() {
        printer.print("Insert ID of student for deleting one of his/her courses: ");

        int studentId = reader.readNumber();

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
            resultSet.close();

            printer.print("What course do you want to delete? Insert course id:");
            printer.print(studentCourses);
            int courseIdForDelete = reader.readNumber();

            new StudentCoursesDao().delete(new StudentCourse(studentId, courseIdForDelete));
            printer.print("Course was successfully deleted");
        } catch (SQLException e) {
            throw new RuntimeException("Error getting groups ", e.getCause());
        }

    }

}
