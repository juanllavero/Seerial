package com.example.executablelauncher.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Season implements Serializable {
    final String id;
    public String name = "";
    public String overview = "";
    public String year = "";
    public int order = 0;
    public float score = 0;
    public int seasonNumber = 0;
    public String logoSrc = "";
    public String coverSrc = "";
    public String backgroundSrc = "";
    public String videoSrc = "";
    public String musicSrc = "";
    public String seriesID = "";
    public int themdbID = -1;
    public String imdbID = "";
    public int lastDisc = 0;
    public String folder = "";
    public boolean showName = true;
    public List<Episode> episodes = new ArrayList<>();
    public List<String> genres = new ArrayList<>();

    public Season() {
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSeriesID() {
        return seriesID;
    }

    public void setSeriesID(String seriesID) {
        this.seriesID = seriesID;
    }

    public String getLogoSrc() {
        return this.logoSrc;
    }

    public void setLogoSrc(String logoSrc) {
        this.logoSrc = logoSrc;
    }

    public String getBackgroundSrc() {
        return backgroundSrc;
    }

    public void setBackgroundSrc(String backgroundSrc) {
        this.backgroundSrc = backgroundSrc;
    }

    public String getVideoSrc() {
        return videoSrc;
    }

    public void setVideoSrc(String videoSrc) {
        this.videoSrc = videoSrc;
    }

    public String getMusicSrc() {
        return musicSrc;
    }

    public void setMusicSrc(String musicSrc) {
        this.musicSrc = musicSrc;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes){ this.episodes = episodes; }

    public void addEpisode(Episode episode) {
        this.episodes.add(episode);
    }

    public int getSeasonNumber(){ return seasonNumber; }

    public void removeEpisode(Episode e){
        episodes.remove(e);
    }

    public String getGenres(){
        return getString(genres);
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

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getCoverSrc() {
        return coverSrc;
    }

    public void setCoverSrc(String coverSrc) {
        this.coverSrc = coverSrc;
    }

    public int getThemdbID() {
        return themdbID;
    }

    public void setThemdbID(int themdbID) {
        this.themdbID = themdbID;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getLastDisc() {
        return lastDisc;
    }

    public void setLastDisc(int lastDisc) {
        this.lastDisc = lastDisc;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public static String getString(List<String> genres) {
        StringBuilder genresText = new StringBuilder();

        if (genres == null)
            return "";

        if (genres.size() == 1){
            return genres.get(0);
        }

        for (String genre : genres){
            if (genres.indexOf(genre) > 0)
                genresText.append(", ");
            genresText.append(genre);
        }

        return genresText.toString();
    }

    public Episode getEpisode(int episodeNumber){
        for (Episode episode : episodes)
            if (episode.getEpisodeNumber() == episodeNumber)
                return episode;

        return null;
    }

    public Episode getEpisode(String id){
        for (Episode episode : episodes)
            if (episode.getId().equals(id))
                return episode;

        return null;
    }
}
