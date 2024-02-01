package com.example.executablelauncher.entities;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
public class Episode implements Serializable {
    @Id
    String id;
    public String name = "";
    public String overview = "";
    public String year = "";
    public float score = 0;
    public float imdbScore = 0;
    public int order = 0;
    public int runtime = 0;
    public int episodeNumber = 0;
    public int seasonNumber = 0;
    public String executableSrc = "";
    public String seasonID = "";
    public String imgSrc = "";
    public boolean watched = false;
    public long lastMilisecond = 0;

    public Episode() {
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(String seasonID) {
        this.seasonID = seasonID;
    }

    public String getExecutableSrc() {
        return executableSrc;
    }

    public void setExecutableSrc(String executableSrc) {
        this.executableSrc = executableSrc;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getImdbScore() {
        return imdbScore;
    }

    public void setImdbScore(float imdbScore) {
        this.imdbScore = imdbScore;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public long getLastMilisecond() {
        return lastMilisecond;
    }

    public void setLastMilisecond(long lastMilisecond) {
        this.lastMilisecond = lastMilisecond;
    }

    public void setWatched(){
        lastMilisecond = 0;
        watched = true;
    }

    public void setUnWatched(){
        lastMilisecond = 0;
        watched = false;
    }

    public boolean isWatched(){ return watched; }

    public void setTime(long miliseconds){
        lastMilisecond = miliseconds;

        //If we have watched more than 90% of the video, it is marked as watched
        /*if (((runtime * 60L) - ((double) lastMilisecond / 100)) < (runtime * 60L * 0.1))
            setWatched();*/
    }

    public long getTimeWatched(){ return lastMilisecond; }

    public static class EpisodeConverter implements EntityConverter<Episode> {
        @Override
        public Class<Episode> getEntityType() {
            return Episode.class;
        }

        @Override
        public Document toDocument(Episode entity, NitriteMapper nitriteMapper) {
            return Document.createDocument()
                    .put("id", entity.getId())
                    .put("name", entity.getName())
                    .put("overview", entity.getOverview())
                    .put("year", entity.getYear())
                    .put("score", entity.getScore())
                    .put("imdbScore", entity.getImdbScore())
                    .put("order", entity.getOrder())
                    .put("runtime", entity.getRuntime())
                    .put("episodeNumber", entity.getEpisodeNumber())
                    .put("seasonNumber", entity.getSeasonNumber())
                    .put("executableSrc", entity.getExecutableSrc())
                    .put("seasonID", entity.getSeasonID())
                    .put("imgSrc", entity.getImgSrc())
                    .put("watched", entity.isWatched())
                    .put("lastMilisecond", entity.getLastMilisecond());
        }

        @Override
        public Episode fromDocument(Document document, NitriteMapper nitriteMapper) {
            Episode episode = new Episode();
            episode.id = document.get("id", String.class);
            episode.setOverview(document.get("overview", String.class));
            episode.setYear(document.get("year", String.class));
            episode.setScore(document.get("score", Float.class) != null ? document.get("score", Float.class) : 0.0f);
            episode.setImdbScore(document.get("imdbScore", Float.class) != null ? document.get("imdbScore", Float.class) : 0.0f);
            episode.setOrder(document.get("order", Integer.class) != null ? document.get("order", Integer.class) : 0);
            episode.setRuntime(document.get("runtime", Integer.class) != null ? document.get("runtime", Integer.class) : 0);
            episode.setEpisodeNumber(document.get("episodeNumber", Integer.class) != null ? document.get("episodeNumber", Integer.class) : 0);
            episode.setSeasonNumber(document.get("seasonNumber", Integer.class) != null ? document.get("seasonNumber", Integer.class) : 0);
            episode.setExecutableSrc(document.get("executableSrc", String.class));
            episode.setSeasonID(document.get("seasonID", String.class));
            episode.setImgSrc(document.get("imgSrc", String.class));
            episode.setWatched(document.get("watched", Boolean.class) != null ? document.get("watched", Boolean.class) : false);
            episode.setLastMilisecond(document.get("lastMilisecond", Long.class) != null ? document.get("lastMilisecond", Long.class) : 0L);
            return episode;
        }
    }
}
