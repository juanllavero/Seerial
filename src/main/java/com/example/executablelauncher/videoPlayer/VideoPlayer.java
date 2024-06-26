package com.example.executablelauncher.videoPlayer;

import com.example.executablelauncher.App;
import com.example.executablelauncher.VideoPlayerController;
import com.example.executablelauncher.utils.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.LongByReference;
import javafx.application.Platform;
import javafx.scene.media.MediaView;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.executablelauncher.videoPlayer.MPV.MPV_EVENT_FILE_LOADED;

public class VideoPlayer extends MediaView {
    long handle;
    private boolean paused = false;
    private ScheduledExecutorService clockExecutor;
    private volatile long currentTimeMillis = 0;
    private VideoPlayerController parentController;
    private final List<Track> videoTracks = new ArrayList<>();
    private final List<Track> audioTracks = new ArrayList<>();
    private final List<Track> subtitleTracks = new ArrayList<>();
    private boolean nextVideo = false;

    public void setParent(VideoPlayerController parent){
        parentController = parent;
    }

    public void playVideo(String src, long seekTimeMillis) {
        //Get interface to MPV DLL
        MPV mpv = MPV.INSTANCE;

        //Create MPV player instance
        handle = mpv.mpv_create();

        //Get the native window id by looking up a window by title:
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, App.textBundle.getString("season"));

        //Tell MPV on which window video should be displayed:
        LongByReference longByReference =
                new LongByReference(Pointer.nativeValue(hwnd.getPointer()));
        mpv.mpv_set_option(handle, "wid", 4, longByReference.getPointer());

        int error;

        //Initialize MPV after setting basic options:
        if((error = mpv.mpv_initialize(handle)) != 0) {
            throw new IllegalStateException("Initialization failed with error: " + error);
        }

        //Load and play a video:
        if((error = mpv.mpv_command(handle, new String[] {"loadfile", src})) != 0) {
            throw new IllegalStateException("Playback failed with error: " + error);
        }

        if (seekTimeMillis > 5000){
            //Detect when the video id loaded
            boolean[] videoLoaded = {false};
            mpv.mpv_request_event(handle, MPV_EVENT_FILE_LOADED, 1);

            while (!videoLoaded[0]) {
                MPV.mpv_event event = mpv.mpv_wait_event(handle, -1);
                if (event.event_id == MPV_EVENT_FILE_LOADED) {
                    videoLoaded[0] = true;
                }
            }

            seekToTime(seekTimeMillis - 5000);
        }

        startClock();

        //Player base settings
        mpvSetProperty("profile", "fast");
        mpvSetProperty("vo", "gpu-next");
        mpvSetProperty("target-colorspace-hint", "yes");

        boolean interpolation = Boolean.parseBoolean(Configuration.loadConfig("interpolation", "false"));

        if (interpolation){
            mpvSetProperty("video-sync", "display-resample");
            mpvSetProperty("interpolation", "yes");
            mpvSetProperty("tscale", "mitchell");
            mpvSetProperty("interpolation-preserve", "no");

            mpvSetProperty("tscale-window", "sphinx");
            mpvSetProperty("tscale-blur", "0.6991556596428412");
            mpvSetProperty("tscale-radius", "1.0");
            mpvSetProperty("tscale-clamp", "0.0");
        }
    }
    public void loadTracks(){
        String tracklist = mpvGetProperty("track-list");

        if (tracklist == null)
            return;

        //Convert to json
        try{
            ObjectMapper om = new ObjectMapper();
            Track[] trackList = om.readValue(tracklist, Track[].class);

            for (Track track : trackList){
                switch (track.type){
                    case "video":
                        videoTracks.add(track);
                        break;
                    case "audio":
                        audioTracks.add(track);
                        break;
                    case "sub":
                        subtitleTracks.add(track);
                        break;
                }
            }
        } catch (JsonProcessingException e) {
            System.err.println("VideoPlayer: error converting to json");
        }
    }
    public void pauseClock() {
        if (clockExecutor != null && !clockExecutor.isShutdown()) {
            clockExecutor.shutdown();
        }
    }
    public void startClock() {
        if (clockExecutor == null || clockExecutor.isShutdown()) {
            clockExecutor = Executors.newSingleThreadScheduledExecutor();
            int UPDATE_INTERVAL = 100;
            clockExecutor.scheduleAtFixedRate(() -> Platform.runLater(this::updateCurrentTime), 0, UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    //region VIDEO CONTROLS
    public boolean isPaused(){
        return clockExecutor.isShutdown();
    }
    public void stop() {
        mpvCommand("stop");
    }
    public void togglePause() {
        if (clockExecutor.isShutdown()){
            startClock();
        }else{
            pauseClock();
        }
        paused = !paused;
        mpvCommand("cycle", "pause");
    }
    public void adjustVolume(boolean increase) {
        double currentVolume = getVolume();
        double newVolume = increase ? Math.min(currentVolume + 5, 100) : Math.max(currentVolume - 5, 0.0);

        mpvSetProperty("volume", String.valueOf(newVolume));
    }
    public double getVolume() {
        String volumeString = mpvGetProperty("volume");

        if (volumeString == null)
            return 0;

        return Double.parseDouble(volumeString);
    }
    public long getCurrentTime() {
        String currentTimeString = mpvGetProperty("time-pos");

        if (currentTimeString == null)
            return 0;

        return (long) (Double.parseDouble(currentTimeString) * 1000);
    }
    private void updateCurrentTime() {
        long newCurrentTime = getCurrentTime();
        if (newCurrentTime != currentTimeMillis) {
            currentTimeMillis = newCurrentTime;
            parentController.notifyChanges(currentTimeMillis);
        }

        if (getDuration() - newCurrentTime <= 200 && !nextVideo){
            nextVideo = true;
            parentController.nextEpisode();
        }
    }
    public long getDuration() {
        String durationString = mpvGetProperty("duration");

        if (durationString == null)
            return 0;

        return (long) (Double.parseDouble(durationString) * 1000);
    }
    public String getChapters(){
        return mpvGetProperty("chapter-list");
    }
    public List<Track> getVideoTracks() {
        return videoTracks;
    }
    public List<Track> getAudioTracks() {
        return audioTracks;
    }
    public List<Track> getSubtitleTracks() {
        return subtitleTracks;
    }
    public void setVideoTrack(int videoId) {
        mpvSetProperty("vid", Integer.toString(videoId));
    }
    public void setAudioTrack(int audioId) {
        mpvSetProperty("aid", Integer.toString(audioId));
    }
    public void setSubtitleTrack(int subtitleId) {
        mpvSetProperty("sid", Integer.toString(subtitleId));
    }
    public void disableSubtitles(){
        mpvSetProperty("sid", "no");
    }
    public void changeAspectRatio() {
        mpvCommand("cycle", "video-aspect");
    }
    public void seekForward() {
        mpvCommand("seek", "10");
    }
    public void seekBackward() {
        mpvCommand("seek", "-5");
    }
    public void seekToTime(long milliseconds) {
        mpvCommand("seek", String.valueOf(milliseconds / 1000.0));
    }
    public void fixZoom(double zoomValue) {
        mpvSetProperty("video-zoom", Double.toString(zoomValue));
    }
    public void zoomIn() {
        double currentZoom = Double.parseDouble(Objects.requireNonNull(mpvGetProperty("video-zoom")));
        double newZoom = currentZoom + 0.05;
        mpvSetProperty("video-zoom", Double.toString(newZoom));
    }
    public void zoomOut() {
        double currentZoom = Double.parseDouble(Objects.requireNonNull(mpvGetProperty("video-zoom")));
        double newZoom = Math.max(currentZoom - 0.05, 0.05);
        mpvSetProperty("video-zoom", Double.toString(newZoom));
    }
    public void setSubtitleVerticalPosition(double position) {
        mpvSetProperty("sub-pos", Double.toString(position));
    }
    public void setSubtitleSize(double size) {
        mpvSetProperty("sub-scale", Double.toString(size));
    }
    //endregion

    //region AUX METHODS TO EXECUTE COMMANDS AND GET PROPERTIES
    private void mpvCommand(String... args) {
        MPV.INSTANCE.mpv_command(handle, args);
    }

    private String mpvGetProperty(String property) {
        Pointer result = MPV.INSTANCE.mpv_get_property_string(handle, property);

        if (result == null)
            return null;

        String value = result.getString(0);
        MPV.INSTANCE.mpv_free(result);
        return value;
    }

    private void mpvSetProperty(String property, String value) {
        MPV.INSTANCE.mpv_set_property_string(handle, property, value);
    }
    //endregion
}