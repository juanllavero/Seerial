package com.example.executablelauncher.fileMetadata;

import java.text.DecimalFormat;

public class VideoTrack {
    String displayTitle;
    int id;
    boolean selected;
    String codec;
    String codecExt;
    String bitrate;
    String framerate;
    String codedHeight;
    String codedWidth;
    String chromaLocation;
    String colorSpace;
    String aspectRatio;
    String profile;
    String refFrames;
    String colorRange;

    public VideoTrack(int id) {
        this.selected = false;
        this.id = id;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getCodecExt() {
        return codecExt;
    }

    public void setCodecExt(String codecExt) {
        this.codecExt = codecExt;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate + " kbps";
    }

    public String getFramerate() {
        return framerate;
    }

    public void setFramerate(float framerate) {
        DecimalFormat decimalFormatExtended = new DecimalFormat("#.###");
        this.framerate = decimalFormatExtended.format(framerate) + " fps";
    }

    public String getCodedHeight() {
        return codedHeight;
    }

    public void setCodedHeight(int codedHeight) {
        this.codedHeight = String.valueOf(codedHeight);
    }

    public String getCodedWidth() {
        return codedWidth;
    }

    public void setCodedWidth(int codedWidth) {
        this.codedWidth = String.valueOf(codedWidth);
    }

    public String getChromaLocation() {
        return chromaLocation;
    }

    public void setChromaLocation(String chromaLocation) {
        this.chromaLocation = chromaLocation;
    }

    public String getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(String colorSpace) {
        this.colorSpace = colorSpace;
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        DecimalFormat decimalFormatShort = new DecimalFormat("#.##");
        this.aspectRatio = decimalFormatShort.format(aspectRatio);
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRefFrames() {
        return refFrames;
    }

    public void setRefFrames(int refFrames) {
        this.refFrames = String.valueOf(refFrames);
    }

    public String getColorRange() {
        return colorRange;
    }

    public void setColorRange(String colorRange) {
        this.colorRange = colorRange;
    }
}
