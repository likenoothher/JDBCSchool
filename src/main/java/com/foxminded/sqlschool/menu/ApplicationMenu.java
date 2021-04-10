package com.foxminded.sqlschool.menu;

import com.foxminded.sqlschool.menuhandler.MenuCommandHandler;
import com.foxminded.sqlschool.outputinputtools.ConsolePrinter;
import com.foxminded.sqlschool.outputinputtools.ConsoleReader;

import java.util.List;


public class ApplicationMenu {

    private final List<String> appropriateChoices = List.of("a", "b", "c", "d", "e", "f");

    private final ConsolePrinter printer = new ConsolePrinter();
    private final ConsoleReader reader = new ConsoleReader();
    private final MenuCommandHandler commandHandler = new MenuCommandHandler();


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
            commandHandler.findGroupsLessOrEqualAmount();
            callApplicationMenu();
        }
        if (chooseItem.equals("b")) {
            commandHandler.findCourseParticipants();
            callApplicationMenu();
        }
        if (chooseItem.equals("c")) {
            commandHandler.addNewStudent();
            callApplicationMenu();
        }
        if (chooseItem.equals("d")) {
            commandHandler.deleteStudentById();
            callApplicationMenu();
        }
        if (chooseItem.equals("e")) {
            commandHandler.addStudentToCourse();
            callApplicationMenu();
        }
        if (chooseItem.equals("f")) {
            commandHandler.deleteStudentFromCourse();
            callApplicationMenu();
        }
    }

}
