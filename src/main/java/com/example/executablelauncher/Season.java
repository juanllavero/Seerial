package com.example.executablelauncher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Season implements Serializable {
    public static int NextID = 0;
    public final int id;
    public String name;
    public String year;
    public String logoSrc;
    public String backgroundSrc;
    public String videoSrc;
    public String musicSrc;
    public String collectionName;
    public int order = 0;
    public final List<Integer> discs = new ArrayList<>();

    public Season() {
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getLogoSrc() {
        return logoSrc;
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

    public List<Integer> getDiscs() {
        return discs;
    }

    public void setDisc(Disc disc) {
        this.discs.add(disc.getId());
    }

    public void removeDisc(Disc d){
        int i = 0;
        for (int disc : discs){
            if (disc == d.getId()) {
                discs.remove(i);
                break;
            }
            i++;
        }
    }
}
