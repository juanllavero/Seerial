package com.example.executablelauncher.entities;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public Series() {
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

    public int getThemdbID() {
        return themdbID;
    }

    public void setThemdbID(int themdbID) {
        this.themdbID = themdbID;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public String getLogoSrc() {
        return logoSrc;
    }

    public void setLogoSrc(String logoSrc) {
        this.logoSrc = logoSrc;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setSeasons(List<String> seasons) {
        this.seasons = seasons;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public boolean isPlaySameMusic() {
        return playSameMusic;
    }

    public void setPlaySameMusic(boolean playSameMusic) {
        this.playSameMusic = playSameMusic;
    }

    public static class SeriesConverter implements EntityConverter<Series> {
        @Override
        public Class<Series> getEntityType() {
            return Series.class;
        }

        @Override
        public Document toDocument(Series entity, NitriteMapper nitriteMapper) {
            return Document.createDocument()
                    .put("id", entity.getId())
                    .put("name", entity.getName())
                    .put("themdbID", entity.getThemdbID())
                    .put("overview", entity.getOverview())
                    .put("score", entity.getScore())
                    .put("year", entity.getYear())
                    .put("category", entity.getCategory())
                    .put("tagline", entity.getTagline())
                    .put("numberOfSeasons", entity.getNumberOfSeasons())
                    .put("numberOfEpisodes", entity.getNumberOfEpisodes())
                    .put("order", entity.getOrder())
                    .put("coverSrc", entity.getCoverSrc())
                    .put("logoSrc", entity.getLogoSrc())
                    .put("folder", entity.getFolder())
                    .put("seasons", entity.getSeasons())
                    .put("genres", entity.getGenres())
                    .put("playSameMusic", entity.isPlaySameMusic());
        }

        @Override
        public Series fromDocument(Document document, NitriteMapper nitriteMapper) {
            Series series = new Series();
            series.id = document.get("id", String.class);
            series.setName(document.get("name", String.class));
            series.setThemdbID(document.get("themdbID", Integer.class) != null ? document.get("themdbID", Integer.class) : -1);
            series.setName(document.get("name", String.class));
            series.setOverview(document.get("overview", String.class));
            series.setScore(document.get("score", Float.class) != null ? document.get("score", Float.class) : 0.0f);
            series.setYear(document.get("year", String.class));
            series.setCategory(document.get("category", String.class));
            series.setTagline(document.get("tagline", String.class));
            series.setNumberOfSeasons(document.get("numberOfSeasons", Integer.class) != null ? document.get("numberOfSeasons", Integer.class) : 0);
            series.setNumberOfEpisodes(document.get("numberOfEpisodes", Integer.class) != null ? document.get("numberOfEpisodes", Integer.class) : 0);
            series.setOrder(document.get("order", Integer.class) != null ? document.get("order", Integer.class) : 0);
            series.setCoverSrc(document.get("coverSrc", String.class));
            series.setLogoSrc(document.get("logoSrc", String.class));
            series.setFolder(document.get("folder", String.class));
            series.setSeasons(document.get("seasons", List.class));
            series.setGenres(document.get("genres", List.class));
            series.setPlaySameMusic(document.get("playSameMusic", Boolean.class) != null ? document.get("playSameMusic", Boolean.class) : false);
            return series;
        }
    }
}
