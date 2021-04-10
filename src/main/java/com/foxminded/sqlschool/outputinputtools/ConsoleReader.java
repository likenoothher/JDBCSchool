package com.foxminded.sqlschool.outputinputtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleReader {
    private final BufferedReader reader;

    public ConsoleReader() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public String readString() {
        String line = "";
        try {
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
