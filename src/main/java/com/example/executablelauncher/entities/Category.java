package com.example.executablelauncher.entities;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import java.io.Serializable;
import java.util.*;

@Entity
public class Category implements Serializable {
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

    public Category() {
        id = UUID.randomUUID().toString();
    }

    public Category(String n, String lang, String t, List<String> f, boolean s){
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

    public static class CategoryConverter implements EntityConverter<Category> {
        @Override
        public Class<Category> getEntityType() {
            return Category.class;
        }

        @Override
        public Document toDocument(Category entity, NitriteMapper nitriteMapper) {
            return Document.createDocument()
                    .put("id", entity.getId())
                    .put("name", entity.getName())
                    .put("language", entity.getLanguage())
                    .put("type", entity.getType())
                    .put("folders", entity.getFolders())
                    .put("showOnFullscreen", entity.isShowOnFullscreen())
                    .put("series", entity.getSeries())
                    .put("analyzedFiles", entity.getAnalyzedFiles())
                    .put("analyzedFolders", entity.getAnalyzedFolders())
                    .put("seasonFolders", entity.getSeasonFolders());
        }

        @Override
        public Category fromDocument(Document document, NitriteMapper nitriteMapper) {
            Category category = new Category();
            category.id = document.get("id", String.class);
            category.setName(document.get("name", String.class));
            category.setLanguage(document.get("language", String.class));
            category.setType(document.get("type", String.class));
            category.setFolders(document.get("folders", List.class));
            category.setShowOnFullscreen(document.get("showOnFullscreen", Boolean.class) != null ? document.get("showOnFullscreen", Boolean.class) : false);
            category.setSeries(document.get("series", List.class));
            category.setAnalyzedFiles(document.get("analyzedFiles", Map.class));
            category.setAnalyzedFolders(document.get("analyzedFolders", Map.class));
            category.setSeasonFolders(document.get("seasonFolders", Map.class));
            return category;
        }
    }
}
