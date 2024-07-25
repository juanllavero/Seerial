package com.example.executablelauncher.entities;

import com.example.executablelauncher.fileMetadata.Track;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Episode implements Serializable {
    final String id;
    String name = "";
    String overview = "";
    String year = "";
    int order = 0;
    float score = 0;
    float imdbScore = 0;
    int runtime = 0;
    int episodeNumber = 0;
    int seasonNumber = 0;
    String videoSrc = "";
    String imgSrc = "";
    String seasonID = "";
    boolean watched = false;
    long lastMilisecond = 0;
    List<Chapter> chapters = new ArrayList<>();
    String mediaDetails = "";
    String tracksDetails = "";
    List<Track> tracks = new ArrayList<>();

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
        long runtimeMilliseconds = (long) runtime * 60 * 1000;
        if (lastMilisecond > (runtimeMilliseconds * 0.9)){
            setWatched();
        }
    }

    public long getTimeWatched(){ return lastMilisecond; }
    public List<Chapter> getChapters() { return chapters; }
    public void addChapter(Chapter chapter) { chapters.add(chapter); }

    public String getMediaDetails() {
        return mediaDetails;
    }

    public void setMediaDetails(String mediaDetails) {
        this.mediaDetails = mediaDetails;
    }

    public String getTracksDetails() {
        return tracksDetails;
    }

    public void setTracksDetails(String tracksDetails) {
        this.tracksDetails = tracksDetails;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
