package com.example.executablelauncher.videoPlayer;

import com.example.executablelauncher.App;
import com.example.executablelauncher.VideoPlayerController;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.LongByReference;
import javafx.application.Platform;
import javafx.scene.media.MediaView;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayer extends MediaView {
    long handle;
    private Timer clockTimer;
    private volatile long currentTimeMillis = 0;
    private VideoPlayerController parentController;

    public void setParent(VideoPlayerController parent){
        parentController = parent;
    }

    public void playVideo(String src) {
        // Get interface to MPV DLL
        MPV mpv = MPV.INSTANCE;

        // Create MPV player instance
        handle = mpv.mpv_create();

        // Get the native window id by looking up a window by title:
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, App.textBundle.getString("season"));

        // Tell MPV on which window video should be displayed:
        LongByReference longByReference =
                new LongByReference(Pointer.nativeValue(hwnd.getPointer()));
        mpv.mpv_set_option(handle, "wid", 4, longByReference.getPointer());

        int error;

        // Initialize MPV after setting basic options:
        if((error = mpv.mpv_initialize(handle)) != 0) {
            throw new IllegalStateException("Initialization failed with error: " + error);
        }

        // Load and play a video:
        if((error = mpv.mpv_command(handle, new String[] {"loadfile", src})) != 0) {
            throw new IllegalStateException("Playback failed with error: " + error);
        }

        clockTimer = new Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateCurrentTime());
            }
        }, 0, 1000);
    }

    //region VIDEO CONTROLS
    public void stop() {
        mpvCommand("stop");
    }
    public void togglePause() {
        mpvCommand("cycle", "pause");
    }
    public void adjustVolume(boolean increase) {
        mpvCommand("add", "volume", increase ? "5%" : "-5%");
    }
    public double getVolume() {
        String volumeString = mpvGetProperty("volume");
        return Double.parseDouble(volumeString);
    }
    public long getCurrentTime() {
        return currentTimeMillis;
    }
    private void updateCurrentTime() {
        long newCurrentTime = getCurrentTime();
        if (newCurrentTime != currentTimeMillis) {
            currentTimeMillis = newCurrentTime;
            parentController.notifyChanges(currentTimeMillis);
        }
    }
    public long getDuration() {
        String durationString = mpvGetProperty("duration");
        return (long) (Double.parseDouble(durationString) * 1000);
    }
    public List<String> getAudioTracks() {
        String audioList = mpvGetProperty("track-list/audio");
        return Arrays.asList(audioList.split("\n"));
    }
    public List<String> getSubtitleTracks() {
        String subtitleList = mpvGetProperty("track-list/sub");
        return Arrays.asList(subtitleList.split("\n"));
    }
    public void setAudioTrack(int audioId) {
        mpvSetProperty("aid", Integer.toString(audioId));
    }
    public void setSubtitleTrack(int subtitleId) {
        mpvSetProperty("sid", Integer.toString(subtitleId));
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
        double currentZoom = Double.parseDouble(mpvGetProperty("video-zoom"));
        double newZoom = currentZoom + 0.05;
        mpvSetProperty("video-zoom", Double.toString(newZoom));
    }
    public void zoomOut() {
        double currentZoom = Double.parseDouble(mpvGetProperty("video-zoom"));
        double newZoom = Math.max(currentZoom - 0.05, 0.05);
        mpvSetProperty("video-zoom", Double.toString(newZoom));
    }
    //endregion

    //region AUX METHODS TO EXECUTE COMMANDS AND GET PROPERTIES
    private void mpvCommand(String... args) {
        MPV.INSTANCE.mpv_command(handle, args);
    }

    private String mpvGetProperty(String property) {
        Pointer result = MPV.INSTANCE.mpv_get_property_string(handle, property);
        String value = result.getString(0);
        MPV.INSTANCE.mpv_free(result);
        return value;
    }

    private void mpvSetProperty(String property, String value) {
        MPV.INSTANCE.mpv_set_property_string(handle, property, value);
    }
    //endregion
}