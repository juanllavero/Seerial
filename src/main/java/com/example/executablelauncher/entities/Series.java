package com.example.executablelauncher.entities;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Series implements Serializable {
    @Id
    String id;
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
    public String folder = "";
    public List<String> seasons = new ArrayList<>();
    public List<String> genres = new ArrayList<>();
    public boolean playSameMusic = false;

    public Series() { }

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

    public String getGenres(){
        return Season.getString(genres);
    }
}
