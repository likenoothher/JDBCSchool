package com.foxminded.sqlSchool.DTO;

import java.util.Objects;

public class Course implements GenericDTO {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id &&
            Objects.equals(name, course.name) &&
            Objects.equals(courseDescription, course.courseDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, courseDescription);
    }

}
