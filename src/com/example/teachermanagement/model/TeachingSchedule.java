package com.example.teachermanagement.model;

public class TeachingSchedule {
    private int id;
    private int teacherId;
    private int courseId;

    public TeachingSchedule(int id, int teacherId, int courseId) {
        this.id = id;
        this.teacherId = teacherId;
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
