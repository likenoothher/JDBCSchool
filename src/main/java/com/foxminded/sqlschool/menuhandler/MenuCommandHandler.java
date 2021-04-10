package com.foxminded.sqlschool.menuhandler;

import com.foxminded.sqlschool.dto.Course;
import com.foxminded.sqlschool.dto.Group;
import com.foxminded.sqlschool.dto.Student;
import com.foxminded.sqlschool.outputinputtools.ConsolePrinter;
import com.foxminded.sqlschool.outputinputtools.ConsoleReader;
import com.foxminded.sqlschool.schoolrepository.*;
import org.apache.log4j.Logger;

import java.util.List;

public class MenuCommandHandler {
    private final static Logger logger = Logger.getLogger(MenuCommandHandler.class);

    private final ConsolePrinter printer = new ConsolePrinter();
    private final ConsoleReader reader = new ConsoleReader();

    public void findGroupsLessOrEqualAmount() {
        printer.print("Enter limit of group participants: ");
        int participantsLimit = reader.readNumber();

        GroupDao groupDao = new GroupDao();
        List<Group> groups = null;

        try {
            groups = groupDao.getGroupsWithAMountLessOrEqual(participantsLimit);
        } catch (DaoException e) {
            logger.warn("Error occurred during getting groups with" +
                " amount of participants less or equal then" + participantsLimit, e);
        }

        if (groups == null) {
            printer.print("Error occurred during getting groups name");
        } else {
            printer.print("Groups with participants amount less or equal than " + participantsLimit + " are:");
            groups.forEach(group -> printer.print(group.getGroupName()));
        }
    }

    public void findCourseParticipants() {
        printer.print("Insert course name:");
        CourseDao courseDao = new CourseDao();
        String courseName = reader.readString();

        if (courseDao.exist(courseName)) {
            StudentDao studentDao = new StudentDao();

            List<Student> courseParticipants = null;

            try {
                courseParticipants = studentDao.getCourseParticipants(courseName);
            } catch (DaoException e) {
                logger.warn("Error occurred during getting participants of course" + courseName, e);
            }
            if (courseParticipants == null) {
                printer.print("Error occurred during getting participants list");
            } else {
                printer.print("Participants of " + courseName + " are:");
                courseParticipants.forEach(course -> printer.print(course.getLastName() + " " + course.getFirstName()));
            }
        } else {
            printer.print("Course " + courseName + " doesn't exists");
        }
    }

    public void addNewStudent() {
        printer.print("Insert student first name:");
        String firstName = reader.readString();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        printer.print("Insert student last name:");
        String lastName = reader.readString();
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();

        try {
            new StudentDao().insert(new Student(firstName, lastName));
            printer.print("Student was successfully added");
        } catch (DaoException e) {
            logger.warn("Error occurred during adding new student " + firstName +
                " " +lastName + " to the database", e);
        }
    }

    public void deleteStudentById() {
        printer.print("Insert id of student for deleting: ");
        int studentId = reader.readNumber();
        GenericDao<Student, Integer> studentDao = new StudentDao();
        try {
            if (studentDao.getById(studentId).isPresent()) {
                studentDao.delete(studentId);
                printer.print("Student with id " + studentId + " was successfully deleted");
            } else {
                printer.print("Student with " + studentId + " doesn't exist");
            }
        } catch (DaoException e) {
            logger.warn("Error occurred during deleting student with id=" + studentId +
                " from the database", e);
        }
    }

    public void addStudentToCourse() {
        printer.print("Insert id of student you want to add a course to: ");
        int studentId = reader.readNumber();
        StudentDao studentDao = new StudentDao();

        List<Course> availableCoursesToAdd;

        try {
            if (studentDao.getById(studentId).isPresent()) {
                availableCoursesToAdd = new CourseDao().getAvailableCoursesToAdd(studentId);

                printer.print("Insert course id to add:");
                availableCoursesToAdd.forEach(course -> printer.print(course.getId() + "." + course.getName()));
                int chooseCourseId = reader.readNumber();
                if (isChooseCourseIdBondCourse(chooseCourseId, availableCoursesToAdd)) {

                    studentDao.addStudentToCourse(studentId, chooseCourseId);
                    printer.print("Course with id " + chooseCourseId + " was successfully added to student"
                        + " with id " + studentId);
                } else {
                    printer.print("Incorrect choice of course");
                }
            } else {
                printer.print("Student with id " + studentId + " doesn't exist");
            }
        } catch (DaoException e) {
            logger.warn("Error occurred during adding new course to student with id=" + studentId, e);
        }
    }

    public void deleteStudentFromCourse() {
        printer.print("Insert id of student for deleting one of his/her courses: ");
        int studentId = reader.readNumber();
        CourseDao courseDao = new CourseDao();

        List<Course> studentCourses = null;
        try {
            studentCourses = courseDao.getStudentCourses(studentId);

            if (studentCourses != null && !studentCourses.isEmpty()) {
                printer.print("What course do you want to delete? Insert course id:");
                studentCourses.forEach(course -> printer.print(course.getId() + "." + course.getName()));
                int chooseId = reader.readNumber();
                if (isChooseCourseIdBondCourse(chooseId, studentCourses)) {
                    StudentDao studentDao = new StudentDao();
                    studentDao.deleteStudentFromCourse(studentId, chooseId);
                    printer.print("Course was successfully deleted");
                } else {
                    printer.print("Incorrect choice of course");
                }

            } else {
                printer.print("Student is not enrolled in any course");
            }

        } catch (DaoException e) {
            logger.warn("Error occurred during deleting student with id="+ studentId + " from a course", e);
        }
    }


    private boolean isChooseCourseIdBondCourse(int chooseCourseId, List<Course> availableCoursesToAdd) {
        for (Course course : availableCoursesToAdd) {
            if (course.getId() == chooseCourseId) {
                return true;
            }
        }
        return false;
    }

}
