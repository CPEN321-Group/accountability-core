package com.cpen321group.accountability.mainScreen.chat;

public class Review {
    private String rating;
    private String date;
    private String content;
    private String title;

    public Review(String rating,String date,String content,String title){
        this.rating = rating;
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public String getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
