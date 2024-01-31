package com.example.executablelauncher.entities;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Category {
    @Id
    String id;
    public String name;
    public String language;
    public String type;
    public List<String> folders;
    public boolean showOnFullscreen;
    public List<String> series = new ArrayList<>();
    public Map<String, String> analyzedFiles = new HashMap<>();               //<File, DiscID>
    public Map<String, String> analyzedFolders = new HashMap<>();             //<Folder, SeriesID>
    public Map<String, String> seasonFolders = new HashMap<>();               //<Folder, SeasonID>

    public Category(String n, String lang, String t, List<String> f, boolean s){
        name = n;
        language = lang;
        type = t;
        folders = f;
        showOnFullscreen = s;
    }

    public String getId(){ return id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getFolders() {
        return folders;
    }

    public void setFolders(List<String> folders) {
        this.folders = folders;
    }

    public boolean isShowOnFullscreen() {
        return showOnFullscreen;
    }

    public void setShowOnFullscreen(boolean showOnFullscreen) {
        this.showOnFullscreen = showOnFullscreen;
    }

    public List<String> getSeries() {
        return series;
    }

    public void setSeries(List<String> series) {
        this.series = series;
    }

    public Map<String, String> getAnalyzedFiles() {
        return analyzedFiles;
    }

    public void setAnalyzedFiles(Map<String, String> analyzedFiles) {
        this.analyzedFiles = analyzedFiles;
    }

    public Map<String, String> getAnalyzedFolders() {
        return analyzedFolders;
    }

    public void setAnalyzedFolders(Map<String, String> analyzedFolders) {
        this.analyzedFolders = analyzedFolders;
    }

    public Map<String, String> getSeasonFolders() {
        return seasonFolders;
    }

    public void setSeasonFolders(Map<String, String> seasonFolders) {
        this.seasonFolders = seasonFolders;
    }

    public void removeSeries(String seriesID){ series.remove(seriesID); }
}
