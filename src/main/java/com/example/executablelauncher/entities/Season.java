package com.example.executablelauncher.entities;

import com.example.executablelauncher.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Season implements Serializable {
    final String id;
    String name = "";
    String overview = "";
    String year = "";
    int order = 0;
    float score = 0;
    int seasonNumber = 0;
    String logoSrc = "";
    String coverSrc = "";
    String backgroundSrc = "";
    String videoSrc = "";
    String musicSrc = "";
    String seriesID = "";
    int themdbID = -1;
    String imdbID = "";
    int lastDisc = 0;
    String folder = "";
    boolean showName = true;
    String audioTrackLanguage = "";
    int selectedAudioTrack = -1;
    String subtitleTrackLanguage = "";
    int selectedSubtitleTrack = -1;
    List<Episode> episodes = new CopyOnWriteArrayList<>();
    List<String> genres = new ArrayList<>();
    int currentlyWatchingEpisode = -1;

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

    public String getAudioTrackLanguage() {
        if (audioTrackLanguage == null)
            return "";

        return audioTrackLanguage;
    }

    public void setAudioTrackLanguage(String audioTrackLanguage) {
        this.audioTrackLanguage = audioTrackLanguage;
    }

    public String getSubtitleTrackLanguage() {
        if (subtitleTrackLanguage == null)
            return "";

        return subtitleTrackLanguage;
    }

    public void setSubtitleTrackLanguage(String subtitleTrackLanguage) {
        this.subtitleTrackLanguage = subtitleTrackLanguage;
    }

    public int getSelectedAudioTrack() {
        return selectedAudioTrack;
    }

    public void setSelectedAudioTrack(int selectedAudioTrack) {
        this.selectedAudioTrack = selectedAudioTrack;
    }

    public int getSelectedSubtitleTrack() {
        return selectedSubtitleTrack;
    }

    public void setSelectedSubtitleTrack(int selectedSubtitleTrack) {
        this.selectedSubtitleTrack = selectedSubtitleTrack;
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

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes){ this.episodes = episodes; }

    public void addEpisode(Episode episode) {
        this.episodes.add(episode);
    }

    public int getSeasonNumber(){ return seasonNumber; }

    public void removeEpisode(Episode e){
        episodes.remove(e);
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

    public Episode getEpisode(int episodeNumber){
        for (Episode episode : episodes)
            if (episode.getEpisodeNumber() == episodeNumber)
                return episode;

        return null;
    }

    public Episode getEpisode(String id){
        for (Episode episode : episodes)
            if (episode.getId().equals(id))
                return episode;

        return null;
    }

    public boolean isBeingWatched(){
        return currentlyWatchingEpisode != -1;
    }

    public Episode getCurrentlyWatchingEpisode(){
        if (currentlyWatchingEpisode != -1 && currentlyWatchingEpisode < episodes.size())
            return episodes.get(currentlyWatchingEpisode);

        return null;
    }

    public int getCurrentlyWatchingEpisodeIndex(){
        if (currentlyWatchingEpisode < episodes.size())
            return currentlyWatchingEpisode;

        return -1;
    }

    public void setCurrentlyWatchingEpisode(int index){
        currentlyWatchingEpisode = index;
    }
}
