package com.example.executablelauncher.entities;

import java.io.Serializable;
import java.util.*;

public class Library implements Serializable {
    final String id;
    public String name;
    public String language;
    public String type;
    public int order = 0;
    public List<String> folders;
    public boolean showOnFullscreen;
    public List<Series> series = new ArrayList<>();
    public Map<String, String> analyzedFiles = new HashMap<>();               //<File, DiscID>
    public Map<String, String> analyzedFolders = new HashMap<>();             //<Folder, SeriesID>
    public Map<String, String> seasonFolders = new HashMap<>();               //<Folder, SeasonID>

    public Library() {
        id = UUID.randomUUID().toString();
    }

    public Library(String n, String lang, String t, List<String> f, boolean s){
        id = UUID.randomUUID().toString();
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

    public List<Series> getSeries() {
        return series;
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

    public void removeSeries(Series s){
        analyzedFolders.remove(s.folder);
        series.remove(s);
    }

    public Series getSeries(String id){
        for (Series series : series)
            if (series.getId().equals(id))
                return series;

        return null;
    }
}