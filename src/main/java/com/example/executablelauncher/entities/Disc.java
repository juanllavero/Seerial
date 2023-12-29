package com.example.executablelauncher.entities;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Disc implements Serializable {
    public static AtomicLong NextID = new AtomicLong();
    public final long id;
    public String name = "";
    public String episodeNumber = "";
    public String executableSrc = "";
    public String type = "";    //Folder or File
    public long seasonID = -1;
    public String imgSrc = "";

    public Disc() {
        this.id = NextID.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(long seasonID) {
        this.seasonID = seasonID;
    }

    public String getExecutableSrc() {
        return executableSrc;
    }

    public void setExecutableSrc(String executableSrc) {
        this.executableSrc = executableSrc;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }
}
