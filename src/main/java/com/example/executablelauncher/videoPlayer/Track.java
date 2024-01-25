package com.example.executablelauncher.videoPlayer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Track {
    public int id;
    public String type;
    @JsonProperty("src-id")
    public int src_id;
    public String title;
    public boolean image;
    public boolean albumart;
    @JsonProperty("default")
    public boolean mydefault;
    public boolean forced;
    public boolean dependent;
    @JsonProperty("visual-impaired")
    public boolean visual_impaired;
    @JsonProperty("hearing-impaired")
    public boolean hearing_impaired;
    public boolean external;
    public boolean selected;
    @JsonProperty("main-selection")
    public int main_selection;
    @JsonProperty("ff-index")
    public int ff_index;
    @JsonProperty("decoder-desc")
    public String decoder_desc;
    public String codec;
    @JsonProperty("demux-w")
    public int demux_w;
    @JsonProperty("demux-h")
    public int demux_h;
    @JsonProperty("demux-fps")
    public double demux_fps;
    @JsonProperty("demux-par")
    public double demux_par;
    public String lang;
    @JsonProperty("audio-channels")
    public int audio_channels;
    @JsonProperty("demux-channel-count")
    public int demux_channel_count;
    @JsonProperty("demux-channels")
    public String demux_channels;
    @JsonProperty("demux-samplerate")
    public int demux_samplerate;
}
