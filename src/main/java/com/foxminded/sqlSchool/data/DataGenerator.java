package com.foxminded.sqlSchool.data;

import org.apache.commons.lang3.RandomStringUtils;

public class DataGenerator {
    private static DataGenerator dataGenerator;

    private DataGenerator() {
    }

    public static DataGenerator getInstance() {
        if (dataGenerator == null) {
            dataGenerator = new DataGenerator();
        }
        return dataGenerator;
    }

    private final String[] firstNames = new String[]{
        "Alex", "Fedor", "Yana", "Olya", "Anatolii", "Igor", "Ekaterina", "Olga", "Mariya", "Ivan",
        "Andrei", "Anton", "Alesya", "Anastasiya", "Nikolai", "Evgenii", "Nika", "Gleb", "Oleg", "Svetlana"
    };

    private final String[] lastNames = new String[]{
        "Shushkevich", "Davidovich", "Ostreiko", "Rabinovich", "Lappo", "Rumas", "Shnaider", "Markes", "Morales",
        "Sedih", "Chernih", "Slepchenko", "Tereschenko", "Grishko", "Timoshuk", "Tolkach", "Semenovich", "Mihalchenko",
        "Garciya", "Torres"
    };

    private final String[] courses = new String[]{
        "Math", "Art", "Biology", "Chemistry", "Music", "Pe", "English", "Psychology", "Social studies", "History"
    };

    private final String[] groupsNames = fillRandomGroupNames();


    public String getRandomFirstName() {
        return firstNames[(int) (Math.random() * firstNames.length)];
    }

    public String getRandomLastName() {
        return lastNames[(int) (Math.random() * lastNames.length)];
    }

    public String[] getCourses() {
        return courses;
    }

    public String[] getGroupNames() {
        return groupsNames;
    }

    private String[] fillRandomGroupNames() {
        String[] groupsName = new String[10];
        for (int i = 0; i < groupsName.length; i++) {
            groupsName[i] = RandomStringUtils.randomAlphabetic(2).toUpperCase() + "-"
                + RandomStringUtils.randomNumeric(2);
        }
        return groupsName;
    }
}
