package com.cpen321group.accountability;

public class CourseModel {

    private String course_name;
    private int course_rating;
    private int course_image;

    // Constructor
    public CourseModel(String course_name, int course_rating, int course_image) {
        this.course_name = course_name;
        this.course_rating = course_rating;
        this.course_image = course_image;
    }

    // Getter and Setter
    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public int getCourse_rating() {
        return course_rating;
    }

    public void setCourse_rating(int course_rating) {
        this.course_rating = course_rating;
    }

    public int getCourse_image() {
        return course_image;
    }

    public void setCourse_image(int course_image) {
        this.course_image = course_image;
    }
}
