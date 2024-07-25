package com.example.executablelauncher.fileMetadata;

public class Track {
    TrackType type;
    String displayTitle;
    int id;
    String language;
    String languageTag;
    boolean selected;

    public Track(TrackType type, String displayTitle, int id, String language, String languageTag, boolean selected) {
        this.type = type;
        this.displayTitle = displayTitle;
        this.id = id;
        this.language = language;
        this.languageTag = languageTag;
        this.selected = selected;
    }

    public TrackType getType() {
        return type;
    }

    public void setType(TrackType type) {
        this.type = type;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
