package com.foxminded.sqlSchool.DAO;

public class IdKey {
    private final int firstId;
    private int secondId;

    public IdKey(int firstId) {
        this.firstId = firstId;
    }

    public IdKey(int firstId, int secondId) {
        this.firstId = firstId;
        this.secondId = secondId;
    }

    public int getFirstId() {
        return firstId;
    }

    public int getSecondId() {
        return secondId;
    }
}
