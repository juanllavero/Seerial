package com.example.executablelauncher.entities;

import com.example.executablelauncher.fileMetadata.AudioTrack;
import com.example.executablelauncher.fileMetadata.MediaInfo;
import com.example.executablelauncher.fileMetadata.SubtitleTrack;
import com.example.executablelauncher.fileMetadata.VideoTrack;

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
    float runtimeInSeconds = 0;
    int episodeNumber = 0;
    int seasonNumber = 0;
    String videoSrc = "";
    String imgSrc = "";
    String seasonID = "";
    boolean watched = false;
    int timeWatched = 0;       //In seconds
    List<Chapter> chapters = new ArrayList<>();
    MediaInfo mediaInfo;
    List<VideoTrack> videoTracks = new ArrayList<>();
    List<AudioTrack> audioTracks = new ArrayList<>();
    List<SubtitleTrack> subtitleTracks = new ArrayList<>();

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

    public float getRuntimeInSeconds() {
        if (runtimeInSeconds <= 0)
            return runtime * 60;

        return runtimeInSeconds;
    }

    public void setRuntimeInSeconds(float runtimeInSeconds) {
        this.runtimeInSeconds = runtimeInSeconds;
        this.runtime = (int) (runtimeInSeconds / 60);
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
        timeWatched = 0;
        watched = true;
    }

    public void setUnWatched(){
        timeWatched = 0;
        watched = false;
    }

    public boolean isWatched(){ return watched; }

    public void setTimeWatched(int seconds){
        timeWatched = seconds;

        //If we have watched more than 90% of the video, it is marked as watched
        if (timeWatched > (runtimeInSeconds * 0.9)){
            setWatched();
        }
    }

    public int getTimeWatched(){ return timeWatched; }
    public List<Chapter> getChapters() { return chapters; }
    public void addChapter(Chapter chapter) { chapters.add(chapter); }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public List<VideoTrack> getVideoTracks() {
        return videoTracks;
    }

    public void setVideoTracks(List<VideoTrack> videoTracks) {
        this.videoTracks = videoTracks;
    }

    public List<AudioTrack> getAudioTracks() {
        return audioTracks;
    }

    public void setAudioTracks(List<AudioTrack> audioTracks) {
        this.audioTracks = audioTracks;
    }

    public List<SubtitleTrack> getSubtitleTracks() {
        return subtitleTracks;
    }

    public void setSubtitleTracks(List<SubtitleTrack> subtitleTracks) {
        this.subtitleTracks = subtitleTracks;
    }
}
