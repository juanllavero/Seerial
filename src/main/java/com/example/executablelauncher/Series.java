package com.example.executablelauncher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Series implements Serializable {
    public static int NextID = 0;
    public final int id;
    public String name;

    public String getCoverSrc() {
        return coverSrc;
    }

    public void setCoverSrc(String coverSrc) {
        this.coverSrc = coverSrc;
    }

    public String coverSrc;
    public List<Integer> seasons = new ArrayList<>();

    public Series() {
        this.id = NextID++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getSeasons() {
        return seasons;
    }

    public void addSeason(Season season) {
        this.seasons.add(season.getId());
    }
}
