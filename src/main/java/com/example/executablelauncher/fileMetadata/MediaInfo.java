package com.example.executablelauncher.fileMetadata;

import java.text.DecimalFormat;

public class MediaInfo {
    String file;
    String location;
    String bitrate;
    String duration;
    String size;
    String container;

    public MediaInfo(String file, String location, double bitrate, float duration, long size, String container) {
        this.file = file;
        this.location = location;
        this.bitrate = bitrate + " kbps";
        this.duration = formatTime(duration);

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double fileSize = size;

        String sizeSufix = " GB";
        fileSize = fileSize / Math.pow(1024, 3);

        if (fileSize < 1) {
            fileSize = fileSize * Math.pow(1024, 1);
            sizeSufix = " MB";
        }

        this.size = decimalFormat.format(fileSize) + sizeSufix;
        this.container = container;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String formatTime(float time){
        int h = (int) (time / 3600);
        int m = (int) ((time % 3600) / 60);
        int s = (int) (time % 60);

        if (h > 0)
            return String.format("%02d:%02d:%02d", h, m, s);

        return String.format("%02d:%02d", m, s);
    }
}
