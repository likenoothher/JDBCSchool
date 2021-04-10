package com.foxminded.sqlschool.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DataGeneratorTest {
    private final List<String> firstNames = List.of("Alex", "Fedor", "Yana", "Olya", "Anatolii",
        "Igor", "Ekaterina", "Olga", "Mariya", "Ivan", "Andrei", "Anton", "Alesya", "Anastasiya",
        "Nikolai", "Evgenii", "Nika", "Gleb", "Oleg", "Svetlana");

    private final List<String> lastNames = List.of("Shushkevich", "Davidovich", "Ostreiko", "Rabinovich",
        "Lappo", "Rumas", "Shnaider", "Markes", "Morales", "Sedih", "Chernih", "Slepchenko", "Tereschenko",
        "Grishko", "Timoshuk", "Tolkach", "Semenovich", "Mihalchenko", "Garciya", "Torres");

    private final List<String> courses = List.of("Math", "Art", "Biology", "Chemistry", "Music", "Pe",
        "English", "Psychology", "Social studies", "History");

    private static DataGenerator dataGenerator;

    @BeforeAll
    public static void getInstance() {
        dataGenerator = DataGenerator.getInstance();
    }


    @Test
    public void whenGetInstanceMultiplyTimes_thenReturnSameInstance() {
        dataGenerator = DataGenerator.getInstance();
        assertEquals(dataGenerator, DataGenerator.getInstance());
    }

    @Test
    public void whenGetRandomFirstName_thenRandomNameFromFirstNameList() {
        assertTrue(firstNames.contains(dataGenerator.getRandomFirstName()));
    }

    @Test
    public void whenGetRandomLastName_thenRandomNameFromLastNameList() {
        assertTrue(lastNames.contains(dataGenerator.getRandomLastName()));
    }

    @Test
    public void whenGetCourses_thenArrayOfCoursesFromCoursesVariable() {
        Stream<String> stream = Arrays.stream(dataGenerator.getCourses());
        List<String> actual = stream.collect(Collectors.toList());

        assertEquals(courses, actual);
    }

    @Test
    public void whenGetGroupNames_thenArrayOfTenGroupNames() {
        Stream<String> stream = Arrays.stream(dataGenerator.getGroupNames());
        List<String> groupNames = stream.collect(Collectors.toList());

        assertEquals(10, groupNames.size());

        groupNames.forEach(groupName -> {
            assertTrue(Pattern.matches("[A-Z]{2}[-]{1}[0-9]{2}", groupName));
        });

    }


}
