package com.example.executablelauncher.entities;

import java.io.Serializable;
import java.util.UUID;
import com.example.executablelauncher.App;

public class Disc implements Serializable {
    public final String id;
    public String name = "";
    public String episodeNumber = "";
    public String executableSrc = "";
    public String type = "";    //Folder or File
    public String seasonID = "";
    public String imgSrc = "";

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }
}
