package com.foxminded.sqlSchool.outputInputTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleReader {

    public String readString() {
        String line = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            line = reader.readLine();
        } catch (IOException e) {
            System.out.println("Some problems occurred during reading input");
        }
        return line;
    }

    public int readNumber() {
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
}
