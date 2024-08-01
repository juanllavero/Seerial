package com.example.executablelauncher.videoPlayer;

import com.example.executablelauncher.VideoPlayerController;
import com.example.executablelauncher.utils.Configuration;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.LongByReference;

import java.util.Objects;

import static com.example.executablelauncher.videoPlayer.MPV.MPV_EVENT_FILE_LOADED;

public class VideoPlayer {
    public static final VideoPlayer INSTANCE = new VideoPlayer();
    long handle;
    boolean paused = false;
    VideoPlayerController parentController;
    boolean isVideoLoaded = false;
    public void setParent(VideoPlayerController parent){
        parentController = parent;
    }

    public void playVideo(String src, String stageName) {
        //Get interface to MPV DLL
        MPV mpv = MPV.INSTANCE;

        //Create MPV player instance
        handle = mpv.mpv_create();

        //Get the native window id by looking up a window by title:
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, stageName);

        long hwndLong = Pointer.nativeValue(hwnd.getPointer());
        LongByReference longByReference = new LongByReference(hwndLong);
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

        boolean lowSettings = true;
        if (lowSettings){
            mpvSetProperty("gpu-api", "d3d11");
            mpvSetProperty("gpu-context", "d3d11");
            mpvSetProperty("profile", "fast");
            mpvSetProperty("vo", "gpu-next");
            mpvSetProperty("hwdec", "d3d11va");
            mpvSetProperty("dither-depth", "auto");
        }else{
            //Video Settings
            mpvSetProperty("gpu-api", "vulkan");
            mpvSetProperty("profile", "high-quality");
            mpvSetProperty("vo", "gpu-next");
            mpvSetProperty("hwdec", "auto-safe");

            //HDR Settings
            mpvSetProperty("tone-mapping", "bt.2446a");
            mpvSetProperty("hdr-peak-percentile", "99.995");
            mpvSetProperty("hdr-contrast-recovery", "0.30");
            mpvSetProperty("target-colorspace-hint", "yes");
            mpvSetProperty("target-contrast", "auto");

            mpvSetProperty("deinterlace", "no");
            mpvSetProperty("dither-depth", "auto");
            mpvSetProperty("deband", "yes");
            mpvSetProperty("deband-iterations", "4");
            mpvSetProperty("deband-threshold", "35");
            mpvSetProperty("deband-range", "16");
            mpvSetProperty("deband-grain", "4");

            mpvSetProperty("cursor-autohide", "100");

            //Subtitles Settings
            mpvSetProperty("blend-subtitles", "no");
            mpvSetProperty("demuxer-mkv-subtitle-preroll", "yes");
            mpvSetProperty("embeddedfonts", "yes");
            mpvSetProperty("sub-fix-timing", "no");
            mpvSetProperty("sub-font", "Open Sans SemiBold");
            mpvSetProperty("sub-font-size", "46");
            mpvSetProperty("sub-blur", "0.3");
            mpvSetProperty("sub-border-color", "0.0/0.0/0.0/0.8");
            mpvSetProperty("sub-border-size", "3.2");
            mpvSetProperty("sub-color", "0.9/0.9/0.9/1.0");
            mpvSetProperty("sub-margin-x", "100");
            mpvSetProperty("sub-margin-y", "50");
            mpvSetProperty("sub-shadow-color", "0.0/0.0/0.0/0.25");
            mpvSetProperty("sub-shadow-offset", "0");

            //Audio Settings
            mpvSetProperty("audio-stream", "silence");
            mpvSetProperty("audio-pitch-correction", "yes");

            boolean interpolation = Boolean.parseBoolean(Configuration.loadConfig("interpolation", "false"));

            if (interpolation){
                mpvSetProperty("video-sync", "display-resample");
                mpvSetProperty("interpolation", "yes");
                mpvSetProperty("tscale", "sphinx");

                mpvSetProperty("tscale-blur", "0.6991556596428412");
                mpvSetProperty("tscale-radius", "1.05");
                mpvSetProperty("tscale-clamp", "0.0");
            }
        }

        //Detect when the video id loaded
        boolean[] videoLoaded = {false};
        mpv.mpv_request_event(handle, MPV_EVENT_FILE_LOADED, 1);

        while (!videoLoaded[0]) {
            MPV.mpv_event event = mpv.mpv_wait_event(handle, -1);
            if (event.event_id == MPV_EVENT_FILE_LOADED) {
                videoLoaded[0] = true;
            }
        }

        isVideoLoaded = true;
        parentController.startCount();
    }
    public boolean isVideoLoaded(){
        return isVideoLoaded;
    }

    //region VIDEO CONTROLS
    public boolean isPaused(){
        return paused;
    }
    public void stop() {
        isVideoLoaded = false;
        mpvCommand("stop");
    }
    public void togglePause(boolean pause) {
        if (isPaused() == pause)
            return;

        paused = !paused;
        mpvCommand("cycle", "pause");
    }
    public void adjustVolume(boolean increase) {
        double currentVolume = getVolume();
        double newVolume = increase ? Math.min(currentVolume + 5, 100) : Math.max(currentVolume - 5, 0.0);

        mpvSetProperty("volume", String.valueOf(newVolume));
    }
    public void adjustVolume(double volume) {
        mpvSetProperty("volume", String.valueOf(volume));
    }
    public double getVolume() {
        String volumeString = mpvGetProperty("volume");

        if (volumeString == null)
            return 0;

        return Double.parseDouble(volumeString);
    }
    public int getCurrentTime() {
        String currentTimeString = mpvGetProperty("time-pos");

        if (currentTimeString == null)
            return 0;

        double currentTimeSeconds = Double.parseDouble(currentTimeString);
        return (int) currentTimeSeconds;
    }
    public long getDuration() {
        String durationString = mpvGetProperty("duration");

        if (durationString == null)
            return 0;

        return (long) (Double.parseDouble(durationString) * 1000);
    }
    public void setVideoBrightness(double brightness) {
        if (brightness < -100.0 || brightness > 100.0)
            brightness = 0;

        if (isVideoLoaded)
            mpvSetProperty("brightness", String.valueOf(brightness));
    }
    public String getChapters(){
        return mpvGetProperty("chapter-list");
    }
    public String getGamma(){
        return mpvGetProperty("gamma");
    }
    public String getAudioDelay(){
        return mpvGetProperty("audio-delay");
    }
    public String getSubsDelay(){
        return mpvGetProperty("sub-delay");
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
    public void seekToTime(long seconds) {
        mpvCommand("seek", String.valueOf(seconds), "absolute");
    }
    public void fixZoom(double zoomValue) {
        mpvSetProperty("video-zoom", Double.toString(zoomValue));
    }
    public void setSubtitleVerticalPosition(double position) {
        mpvSetProperty("sub-pos", Double.toString(position));
    }
    public void setSubtitleSize(double size) {
        mpvSetProperty("sub-scale", Double.toString(size));
    }
    public void setGamma(double gammaValue) {
        if (gammaValue < -100) gammaValue = -100;
        if (gammaValue > 100) gammaValue = 100;

        mpvSetProperty("gamma", String.valueOf(gammaValue));
    }
    public void setAudioDelay(double delayMS) {
        mpvSetProperty("audio-delay", String.valueOf(delayMS / 1000));
    }
    public void setSubsDelay(double delayMS) {
        mpvSetProperty("sub-delay", String.valueOf(delayMS / 1000));
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