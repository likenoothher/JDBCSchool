package com.foxminded.sqlSchool.DTO;

import java.util.Objects;

public class StudentCourse implements GenericDTO {
    private int studentID;
    private int courseID;

    public StudentCourse() {
    }

    public StudentCourse(int studentID, int courseID) {
        this.studentID = studentID;
        this.courseID = courseID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentCourse that = (StudentCourse) o;
        return studentID == that.studentID &&
            courseID == that.courseID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID, courseID);
    }
}
