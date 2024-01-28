package com.example.executablelauncher.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import com.example.executablelauncher.App;

public class Disc implements Serializable {
    public final String id;
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

    //region WATCH HISTORY
    public boolean watched = false;
    public long lastMilisecond = 0;
    //public LocalDateTime lastWatched = null;
    //endregion

    public Disc() {
        String uuid = UUID.randomUUID().toString();
        while (App.isRepeatedID(uuid))
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
}
