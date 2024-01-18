package com.example.executablelauncher.entities;

import com.example.executablelauncher.App;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Season implements Serializable {
    public final String id;
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
    public boolean showName = true;
    public String folder = "";
    public final List<String> discs = new ArrayList<>();
    public List<String> genres = new ArrayList<>();

    public Season() {
        String uuid = UUID.randomUUID().toString();
        while (App.isRepeatedSeasonID(uuid))
            uuid = UUID.randomUUID().toString();
        id = uuid;
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

    public List<String> getDiscs() {
        return discs;
    }

    public void setDisc(Disc disc) {
        this.discs.add(disc.getId());
    }

    public void removeDisc(String id){
        discs.remove(id);
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
