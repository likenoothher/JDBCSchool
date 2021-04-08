package com.foxminded.sqlSchool.outputInputTools;

import java.util.List;
import java.util.Map;

public class ConsolePrinter {

    public void print(Map<Integer, String> mapToPrint) {
        mapToPrint.forEach((key, value) -> System.out.println(key + "." + value));
    }

    public void print(List<String> listToPrint) {
        listToPrint.forEach(System.out::println);
    }

    public void print(String stringToPrint) {
        System.out.println(stringToPrint);
    }
}
