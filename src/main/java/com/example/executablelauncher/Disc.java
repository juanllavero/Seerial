package com.example.executablelauncher;

import java.io.Serializable;

public class Disc implements Serializable {
    public static int NextID = 0;
    public final int id;
    public String name;
    public float episodeNumber;
    public String coverSrc;
    public String executableSrc;
    public String type;    //Folder or File
    public int seasonID = -1;

    public Disc() {
        this.id = NextID++;
    }

    public int getId() {
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

    public int getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(int seasonID) {
        this.seasonID = seasonID;
    }

    public String getCoverSrc() {
        return coverSrc;
    }

    public void setCoverSrc(String coverSrc) {
        this.coverSrc = coverSrc;
    }

    public String getExecutableSrc() {
        return executableSrc;
    }

    public void setExecutableSrc(String executableSrc) {
        this.executableSrc = executableSrc;
    }
}
