package com.example.executablelauncher.entities;

public class EpisodeMetadata {
    public String tvdbID;
    public String imdbID;
    public String seriesID;
    public String name;
    public long absoluteEpisode;
    public long episodeNumber;
    public long seasonNumber;
    public String posterSrc = "";

    public EpisodeMetadata(String tvdbID, String imdbID, String seriesID, String name, long absoluteEpisode, long episodeNumber, long seasonNumber) {
        this.tvdbID = tvdbID;
        this.imdbID = imdbID;
        this.seriesID = seriesID;
        this.name = name;
        this.absoluteEpisode = absoluteEpisode;
        this.episodeNumber = episodeNumber;
        this.seasonNumber = seasonNumber;
    }
}
