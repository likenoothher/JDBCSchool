package com.foxminded.sqlSchool.DTO;

public class Student {
    private int id;
    private int groupId;
    private String firstName;
    private String secondName;

    public Student() {
    }

    public Student(String firstName, String secondName) {
        this.groupId = groupId;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public Student(int groupId, String firstName, String secondName) {
        this.groupId = groupId;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public Student(int id, int groupId, String firstName, String secondName) {
        this.id = id;
        this.groupId = groupId;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
}
