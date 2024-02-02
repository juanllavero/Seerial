package com.example.executablelauncher.entities;

import java.io.Serializable;
import java.util.UUID;

public class Episode implements Serializable {
    final String id;
    public String name = "";
    public String overview = "";
    public String year = "";
    public int order = 0;
    public float score = 0;
    public float imdbScore = 0;
    public int runtime = 0;
    public int episodeNumber = 0;
    public int seasonNumber = 0;
    public String videoSrc = "";
    public String imgSrc = "";
    public String seasonID = "";
    public boolean watched = false;
    public long lastMilisecond = 0;

    public Episode() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(String seasonID) {
        this.seasonID = seasonID;
    }

    public String getVideoSrc() {
        return videoSrc;
    }

    public void setVideoSrc(String videoSrc) {
        this.videoSrc = videoSrc;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getImdbScore() {
        return imdbScore;
    }

    public void setImdbScore(float imdbScore) {
        this.imdbScore = imdbScore;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public long getLastMilisecond() {
        return lastMilisecond;
    }

    public void setLastMilisecond(long lastMilisecond) {
        this.lastMilisecond = lastMilisecond;
    }

    public void setWatched(){
        lastMilisecond = 0;
        watched = true;
    }

    public void setUnWatched(){
        lastMilisecond = 0;
        watched = false;
    }

    public boolean isWatched(){ return watched; }

    public void setTime(long miliseconds){
        lastMilisecond = miliseconds;

        //If we have watched more than 90% of the video, it is marked as watched
        /*if (((runtime * 60L) - ((double) lastMilisecond / 100)) < (runtime * 60L * 0.1))
            setWatched();*/
    }

    public long getTimeWatched(){ return lastMilisecond; }
}
