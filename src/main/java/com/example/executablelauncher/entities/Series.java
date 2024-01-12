package com.example.executablelauncher.entities;

import com.example.executablelauncher.App;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Series implements Serializable {
    public final String id;
    public int themdbID = -1;
    public String name = "";
    public String overview = "";
    public float score = 0;
    public String year = "";
    public String category = "";
    public String tagline = "";
    public int numberOfSeasons = 0;
    public int numberOfEpisodes = 0;
    public int order = 0;
    public String coverSrc = "";
    public String logoSrc = "";
    public List<String> seasons = new ArrayList<>();
    public List<String> genres = new ArrayList<>();
    public boolean playSameMusic = false;

    public Series() {
        String uuid = UUID.randomUUID().toString();
        while (App.isRepeatedSeriesID(uuid))
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

    public String getCoverSrc() {
        return coverSrc;
    }

    public void setCoverSrc(String coverSrc) {
        this.coverSrc = coverSrc;
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public void addSeason(Season season) {
        this.seasons.add(season.id);
    }

    public void removeSeason(String id){
        seasons.remove(id);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
