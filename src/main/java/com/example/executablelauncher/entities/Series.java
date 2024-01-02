package com.example.executablelauncher.entities;

import com.example.executablelauncher.App;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Series implements Serializable {
    public final String id;
    public long thetvdbID = 0;
    public int themdbID = -1;
    public String name = "";
    public String resume = "";
    public String category = "";
    public int order = 0;
    public String coverSrc = "";
    public boolean playSameMusic = false;
    public List<String> seasons = new ArrayList<>();
    public int seasonsNumber = 0;

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

    public void clearSeasons(){
        seasons.clear();
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
