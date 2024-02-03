package com.example.executablelauncher.tmdbMetadata.groups;

import com.example.executablelauncher.tmdbMetadata.groups.SeasonsGroupEpisode;

import java.util.ArrayList;

public class EpisodeGroup {
    public String id;
    public String name;
    public int order;
    public ArrayList<SeasonsGroupEpisode> episodes;
    public boolean locked;
}
