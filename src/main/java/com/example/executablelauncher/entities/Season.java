package com.example.executablelauncher.entities;

import com.example.executablelauncher.App;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Season implements Serializable {
    public final String id;
    public String name = "";
    public String year = "";
    public String logoSrc = "";
    public String backgroundSrc = "";
    public String videoSrc = "";
    public String musicSrc = "";
    public String collectionName = "";
    public String fullScreenBlurImageSrc = "";
    public String desktopBackgroundEffect = "";
    public int order = 0;
    public boolean showName = true;
    public final List<String> discs = new ArrayList<>();

    public Season() {
        String uuid = UUID.randomUUID().toString();
        while (App.isRepeatedSeasonID(uuid))
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

    public List<String> getDiscs() {
        return discs;
    }

    public void setDisc(Disc disc) {
        this.discs.add(disc.getId());
    }

    public void removeDisc(String id){
        discs.remove(id);
    }

    public void addDisc(Disc d){
        discs.add(d.getId());
    }

    public String getFullScreenBlurImageSrc() {
        return fullScreenBlurImageSrc;
    }

    public void setFullScreenBlurImageSrc(String fullScreenBlurImageSrc) {
        this.fullScreenBlurImageSrc = fullScreenBlurImageSrc;
    }

    public String getDesktopBackgroundEffect() {
        return desktopBackgroundEffect;
    }

    public void setDesktopBackgroundEffect(String desktopBackgroundEffect) {
        this.desktopBackgroundEffect = desktopBackgroundEffect;
    }
}
