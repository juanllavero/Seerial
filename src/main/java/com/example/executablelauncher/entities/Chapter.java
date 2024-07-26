package com.example.executablelauncher.entities;

public class Chapter {
    String title;
    double time;
    String displayTime = "";
    String thumbnailSrc = "";

    public Chapter(String title, double time){
        this.title = title;
        this.time = time;
        displayTime = convertTime(time);
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
        displayTime = convertTime(time);
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getThumbnailSrc() {
        return thumbnailSrc;
    }

    public void setThumbnailSrc(String thumbnailSrc) {
        this.thumbnailSrc = thumbnailSrc;
    }

    private String convertTime(double milliseconds) {
        double seconds = milliseconds / 1000;
        int h = (int) (seconds / 3600);
        int m = (int) ((seconds % 3600) / 60);
        int s = (int) (seconds % 60);

        return String.format("%02d:%02d:%02d", h, m, s);
    }
}