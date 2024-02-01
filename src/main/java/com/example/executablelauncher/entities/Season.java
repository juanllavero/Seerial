package com.example.executablelauncher.entities;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public List<String> episodes = new ArrayList<>();
    public List<String> genres = new ArrayList<>();

    public Season() {
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

    public void setEpisodes(List<String> episodes){ this.episodes = episodes; }

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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getCoverSrc() {
        return coverSrc;
    }

    public void setCoverSrc(String coverSrc) {
        this.coverSrc = coverSrc;
    }

    public int getThemdbID() {
        return themdbID;
    }

    public void setThemdbID(int themdbID) {
        this.themdbID = themdbID;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getLastDisc() {
        return lastDisc;
    }

    public void setLastDisc(int lastDisc) {
        this.lastDisc = lastDisc;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public static String getString(List<String> genres) {
        StringBuilder genresText = new StringBuilder();

        if (genres == null)
            return "";

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

    public static class SeasonConverter implements EntityConverter<Season> {
        @Override
        public Class<Season> getEntityType() {
            return Season.class;
        }

        @Override
        public Document toDocument(Season entity, NitriteMapper nitriteMapper) {
            return Document.createDocument()
                    .put("id", entity.getId())
                    .put("name", entity.getName())
                    .put("overview", entity.getOverview())
                    .put("year", entity.getYear())
                    .put("tagline", entity.getTagline())
                    .put("score", entity.getScore())
                    .put("seasonNumber", entity.getSeasonNumber())
                    .put("logoSrc", entity.getLogoSrc())
                    .put("backgroundSrc", entity.getBackgroundSrc())
                    .put("videoSrc", entity.getVideoSrc())
                    .put("musicSrc", entity.getMusicSrc())
                    .put("coverSrc", entity.getCoverSrc())
                    .put("seriesID", entity.getSeriesID())
                    .put("coverSrc", entity.getCoverSrc())
                    .put("order", entity.getOrder())
                    .put("themdbID", entity.getThemdbID())
                    .put("imdbID", entity.getImdbID())
                    .put("showName", entity.isShowName())
                    .put("folder", entity.getFolder())
                    .put("lastDisc", entity.getLastDisc())
                    .put("episodes", entity.getEpisodes())
                    .put("genres", entity.getGenres());
        }

        @Override
        public Season fromDocument(Document document, NitriteMapper nitriteMapper) {
            Season season = new Season();
            season.id = document.get("id", String.class);
            season.setName(document.get("name", String.class));
            season.setOverview(document.get("overview", String.class));
            season.setYear(document.get("year", String.class));
            season.setTagline(document.get("tagline", String.class));
            season.setScore(document.get("score", Float.class) != null ? document.get("score", Float.class) : 0.0f);
            season.setSeasonNumber(document.get("seasonNumber", Integer.class) != null ? document.get("seasonNumber", Integer.class) : 0);
            season.setLogoSrc(document.get("logoSrc", String.class));
            season.setBackgroundSrc(document.get("backgroundSrc", String.class));
            season.setVideoSrc(document.get("videoSrc", String.class));
            season.setMusicSrc(document.get("musicSrc", String.class));
            season.setCoverSrc(document.get("coverSrc", String.class));
            season.setSeriesID(document.get("seriesID", String.class));
            season.setCoverSrc(document.get("coverSrc", String.class));
            season.setOrder(document.get("order", Integer.class) != null ? document.get("order", Integer.class) : 0);
            season.setThemdbID(document.get("themdbID", Integer.class) != null ? document.get("themdbID", Integer.class) : 0);
            season.setImdbID(document.get("imdbID", String.class));
            season.setShowName(document.get("showName", Boolean.class) != null ? document.get("showName", Boolean.class) : false);
            season.setFolder(document.get("folder", String.class));
            season.setLastDisc(document.get("lastDisc", Integer.class) != null ? document.get("lastDisc", Integer.class) : 0);
            season.setEpisodes(document.get("episodes", List.class));
            season.setGenres(document.get("genres", List.class));
            return season;
        }
    }
}
