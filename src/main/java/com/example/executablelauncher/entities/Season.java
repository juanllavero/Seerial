package com.example.executablelauncher.entities;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Season implements Serializable {
    @Id
    String id;
    public String name = "";
    public String overview = "";
    public String year = "";
    public String tagline = "";
    public float score = 0;
    public int seasonNumber = 0;
    public String logoSrc = "";
    public String backgroundSrc = "";
    public String videoSrc = "";
    public String musicSrc = "";

    public String coverSrc = "";
    public String seriesID = "";

    public int order = 0;
    public int themdbID = -1;
    public String imdbID = "";
    public boolean showName = true;
    public String folder = "";
    public int lastDisc = 0;
    public final List<String> episodes = new ArrayList<>();
    public List<String> genres = new ArrayList<>();

    public Season() { }

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

    public List<String> getEpisodes() {
        return episodes;
    }

    public void setDisc(Episode episode) {
        this.episodes.add(episode.getId());
    }

    public int getSeasonNumber(){ return seasonNumber; }

    public void removeEpisode(String id){
        episodes.remove(id);
    }

    public String getGenres(){
        return getString(genres);
    }

    public static String getString(List<String> genres) {
        StringBuilder genresText = new StringBuilder();

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
}
