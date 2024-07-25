package com.example.executablelauncher.fileMetadata;

public enum TrackType {
    VIDEO("v"),
    AUDIO("a"),
    SUBTITLE("s");

    private String code;

    private TrackType(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}