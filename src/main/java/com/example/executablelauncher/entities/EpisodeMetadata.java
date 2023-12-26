package com.example.executablelauncher.entities;

import java.util.ArrayList;
import java.util.List;

public class EpisodeMetadata {
    public String tvdbID = "";
    public String imdbID = "";
    public String seriesID = "";
    public String name = "";
    public String posterSrc = "";

    public EpisodeMetadata(String tvdbID, String imdbID, String seriesID, String name) {
        this.tvdbID = tvdbID;
        this.imdbID = imdbID;
        this.seriesID = seriesID;
        this.name = name;
    }
}
