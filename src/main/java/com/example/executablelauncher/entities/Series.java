package com.example.executablelauncher.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Series implements Serializable {
    final String id;
    int themdbID = -1;
    String name = "";
    String overview = "";
    String year = "";
    int order = 0;
    float score = 0;
    int numberOfEpisodes = 0;
    int numberOfSeasons = 0;
    String coverSrc = "";
    String logoSrc = "";
    String folder = "";
    float videoZoom = 0;
    String episodeGroupID = "";
    List<Season> seasons = new ArrayList<>();
    List<String> genres = new ArrayList<>();
    boolean playSameMusic = false;
    boolean analyzingFiles = false;
    int currentlyWatchingSeason = -1;

    public Series() {
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

    public String getCoverSrc() {
        return coverSrc;
    }

    public void setCoverSrc(String coverSrc) {
        this.coverSrc = coverSrc;
    }

    public float getVideoZoom() {
        return videoZoom;
    }

    public void setVideoZoom(float videoZoom) {
        this.videoZoom = videoZoom;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void addSeason(Season season) {
        this.seasons.add(season);
    }

    public void removeSeason(Season s){
        seasons.remove(s);
        numberOfSeasons--;

        genres = new ArrayList<>();
    }

    public String getEpisodeGroupID() {
        return episodeGroupID;
    }

    public void setEpisodeGroupID(String episodeGroupID) {
        this.episodeGroupID = episodeGroupID;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getGenres(){
        return Season.getString(genres);
    }

    public int getThemdbID() {
        return themdbID;
    }

    public void setThemdbID(int themdbID) {
        this.themdbID = themdbID;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public String getLogoSrc() {
        return logoSrc;
    }

    public void setLogoSrc(String logoSrc) {
        this.logoSrc = logoSrc;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public boolean isPlaySameMusic() {
        return playSameMusic;
    }

    public void setPlaySameMusic(boolean playSameMusic) {
        this.playSameMusic = playSameMusic;
    }

    public Season getSeason(int seasonNumber){
        for (Season season : seasons)
            if (season.getSeasonNumber() == seasonNumber)
                return season;

        return null;
    }

    public Season getSeason(String id){
        for (Season season : seasons)
            if (season.getId().equals(id))
                return season;

        return null;
    }

    public boolean isAnalyzingFiles() {
        return analyzingFiles;
    }

    public void setAnalyzingFiles(boolean analyzingFiles) {
        this.analyzingFiles = analyzingFiles;
    }

    public boolean isBeingWatched(){
        return currentlyWatchingSeason != -1;
    }

    public Season getCurrentlyWatchingSeason(){
        if (currentlyWatchingSeason != -1 && currentlyWatchingSeason < seasons.size())
            return seasons.get(currentlyWatchingSeason);

        return null;
    }

    public void setCurrentlyWatchingSeason(int index){
        currentlyWatchingSeason = index;
    }
}
