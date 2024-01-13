package com.example.executablelauncher.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Category {
    public String name;
    public String language;
    public String type;
    public List<String> folders = new ArrayList<>();
    public boolean showOnFullscreen = false;
    public List<String> series = new ArrayList<>();
    public Map<String, String> analyzedFiles = new HashMap<>();                 //<File, DiscID>
    public Map<String, String> analyzedFolders = new HashMap<>();               //<Folder, SeriesID>
    public Map<String, String> seasonFolders = new HashMap<>();               //<Folder, SeasonID>

    public Category(String n, String lang, String t, List<String> f, boolean s){
        name = n;
        language = lang;
        type = t;
        folders = f;
        showOnFullscreen = s;
    }
}
