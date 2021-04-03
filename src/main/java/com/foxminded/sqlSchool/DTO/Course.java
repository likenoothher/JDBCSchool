package com.foxminded.sqlSchool.DTO;

public class Course {
    private int id;
    private String name;
    private String courseDescription;

    public Course() {
    }

    public Course(String name, String courseDescription) {
        this.name = name;
        this.courseDescription = courseDescription;
    }

    public Course(int id, String name, String courseDescription) {
        this.id = id;
        this.name = name;
        this.courseDescription = courseDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }
}
