package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.videoPlayer.VideoPlayer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.jfoenix.controls.JFXSlider;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.FullScreenStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoPlayerController {
    //region FXML ATTRIBUTES
    @FXML
    private StackPane mainPane;

    @FXML
    private Button audiosButton;

    @FXML
    private Label currentTime;

    @FXML
    private Label episodeDate;

    @FXML
    private Label episodeTitle;

    @FXML
    private Button menuButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button optionsButton;

    @FXML
    private Button playButton;

    @FXML
    private Button prevButton;

    @FXML
    private Label runtime;

    @FXML
    private JFXSlider runtimeSlider;

    @FXML
    private JFXSlider volumeSlider;

    @FXML
    private Label seasonEpisode;

    @FXML
    private Label seriesTitle;

    @FXML
    private ImageView shadowImage;

    @FXML
    private Button subtitlesButton;

    @FXML
    private Label toFinishTime;

    @FXML
    private VBox controlsBox;

    @FXML
    private HBox volumeBox;
    //endregion

    private VideoPlayer videoPlayer;

    boolean isPaused = false;
    boolean controlsShown = false;
    SeasonController parentController = null;
    Timeline timeline = null;
    Timeline volumeCount = null;

    private final AtomicBoolean tracking = new AtomicBoolean();

    private Timer clockTimer;
    double percentageStep = 0;

    List<Disc> discList = new ArrayList<>();
    int currentDisc = 0;

    public void setVideo(SeasonController parent, List<Disc> discList, Disc disc, String seriesName, Scene scene){
        parentController = parent;
        this.discList = discList;
        onLoad();

        currentDisc = discList.indexOf(disc);

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        videoPlayer = new VideoPlayer();
        mainPane.getChildren().add(0, videoPlayer);

        shadowImage.setFitWidth(screenWidth);
        shadowImage.setFitHeight(screenHeight);
        shadowImage.setPreserveRatio(false);

        shadowImage.setVisible(false);
        controlsBox.setVisible(false);

        volumeBox.setVisible(false);

        timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.seconds(5), event -> {
                hideControls();
            })
        );
        timeline.play();

        volumeCount = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(2), event -> {
                    volumeBox.setVisible(false);
                })
        );

        scene.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ESCAPE) || e.getCode().equals(KeyCode.BACK_SPACE))
                stop();
        });

        scene.setOnKeyPressed(e -> {
            if (!controlsShown){
                if (e.getCode().equals(KeyCode.RIGHT))
                    goAhead();
                else if (e.getCode().equals(KeyCode.LEFT))
                    goBack();
                else if (e.getCode().equals(KeyCode.PLUS))
                    volumeUp();
                else if (e.getCode().equals(KeyCode.MINUS))
                    volumeDown();
                else if (e.getCode().equals(KeyCode.SPACE)){
                    showControls();
                    pause();
                }
                else
                    showControls();
            }else{
                timeline.playFromStart();
            }

            /*else if (e.getCode().equals(KeyCode.DIGIT1)) {
                switchSubtitleTrack(1);

                List<TrackDescription> tracks = embeddedMediaPlayer.subpictures().trackDescriptions();

                for (TrackDescription trackDescription : tracks){
                    System.out.println(trackDescription.description());
                }

                List<TrackDescription> audioTracks = embeddedMediaPlayer.audio().trackDescriptions();

                for (TrackDescription trackDescription : audioTracks){
                    System.out.println(trackDescription.description());
                }
            }*/

        });

        seriesTitle.setText(seriesName);
        setDiscValues(disc);



        //Set dvd menu button visible if file extension equals .iso
        //menuButton.setVisible(false);
    }

    private void onLoad(){
        /*runtimeSlider.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                timeline.stop();
                beginTracking();
            }else {
                timeline.playFromStart();
                tracking.set(false);
            }
        });*/

        runtimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            videoPlayer.seekToTime((long) (newValue.doubleValue() * 1000));
        });

        runtimeSlider.setOnKeyPressed(e -> {
            if (runtimeSlider.isFocused() && e.getCode().equals(KeyCode.UP))
                hideControls();
        });

        runtimeSlider.valueProperty().addListener((obs, oldValue, newValue) -> updateMediaPlayerPosition(newValue.floatValue() / 100));

        playButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)){
                if (!isPaused)
                    pause();
                else
                    resume();
            }
        });

        nextButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER))
                nextEpisode();
        });

        prevButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER))
                prevEpisode();
        });

        //region BUTTON ICONS
        playButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                if (isPaused)
                    playButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/playSelected.png", 35, 35, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/pauseSelected.png", 35, 35, true, true)));
            }else{
                if (isPaused)
                    playButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/play.png", 35, 35, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/pause.png", 35, 35, true, true)));
            }
        });

        nextButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                nextButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/nextTrackSelected.png", 35, 35, true, true)));
            else
                nextButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/nextTrack.png", 35, 35, true, true)));
        });

        prevButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                prevButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/prevTrackSelected.png", 35, 35, true, true)));
            else
                prevButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/prevTrack.png", 35, 35, true, true)));
        });

        optionsButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                optionsButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/playerOptionsSelected.png", 35, 35, true, true)));
            else
                optionsButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/playerOptions.png", 35, 35, true, true)));
        });

        audiosButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                audiosButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/audioSelected.png", 35, 35, true, true)));
            else
                audiosButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/audio.png", 35, 35, true, true)));
        });

        subtitlesButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                subtitlesButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/subsSelected.png", 35, 35, true, true)));
            else
                subtitlesButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/subs.png", 35, 35, true, true)));
        });

        menuButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                menuButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/dvdMenuSelected.png", 35, 35, true, true)));
            else
                menuButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/dvdMenu.png", 35, 35, true, true)));
        });
        //endregion
    }

    private void setDiscValues(Disc disc){
        episodeTitle.setText(disc.name);
        episodeDate.setText(disc.year);
        seasonEpisode.setText(App.textBundle.getString("seasonLetter") + disc.seasonNumber + " " + App.textBundle.getString("episodeLetter") + disc.episodeNumber);
        runtime.setText(parentController.setRuntime(disc.runtime));

        double durationInSeconds = disc.runtime * 60;
        double fiveSecondsPercentage = (5.0 / durationInSeconds) * 100.0;
        percentageStep = (fiveSecondsPercentage / 100.0) * 100;

        nextButton.setDisable(discList.indexOf(disc) == (discList.size() - 1));

        if (disc.isWatched())
            disc.setUnWatched();

        float position;

        if (disc.getTimeWatched() > 0){
            position = (float) ((disc.runtime * 60L * 1000) - disc.getTimeWatched()) / 100;
        } else {
            position = 0;
        }

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), videoPlayer);
        fade.setFromValue(0);
        fade.setToValue(1.0);

        fade.setOnFinished(event -> {
            //Set video and start
            videoPlayer.playVideo(disc.executableSrc);
            videoPlayer.seekToTime(0);
            runtimeSlider.setMin(0);
            runtimeSlider.setMax(videoPlayer.getDuration() / 1000.0);
            runtimeSlider.setValue(0);
            runtimeSlider.setBlockIncrement(percentageStep);
        });

        fade.play();
    }

    private void showControls(){
        controlsShown = true;
        timeline.playFromStart();
        fadeInEffect(controlsBox);
        fadeInEffect(shadowImage);
        playButton.requestFocus();
    }

    private void hideControls(){
        timeline.stop();
        fadeOutEffect(shadowImage);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), controlsBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        controlsBox.setVisible(false);
        fadeOut.setOnFinished(e -> controlsShown = false);
    }

    public void fadeOutEffect(ImageView img){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), img);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        img.setVisible(false);
    }

    public void fadeInEffect(Pane pane){
        pane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void fadeInEffect(ImageView img){
        img.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), img);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void stop() {
        Disc disc = discList.get(currentDisc);
        disc.setTime(videoPlayer.getCurrentTime());

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.7), videoPlayer);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            videoPlayer.stop();

            parentController.stopVideo();

            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        });

        fadeOut.play();
    }

    public void resume(){
        hideControls();
        isPaused = false;
        videoPlayer.togglePause();
    }

    public void pause(){
        isPaused = true;
        videoPlayer.togglePause();

        if (controlsShown)
            playButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/playSelected.png", 35, 35, true, true)));
    }

    public void volumeUp(){
        volumeCount.playFromStart();
        volumeBox.setVisible(true);

        videoPlayer.adjustVolume(true);
        volumeSlider.setValue(videoPlayer.getVolume());
    }

    public void volumeDown(){
        volumeCount.playFromStart();
        volumeBox.setVisible(true);
        videoPlayer.adjustVolume(false);
        volumeSlider.setValue(videoPlayer.getVolume());
    }

    public void goAhead(){
        showControls();
        runtimeSlider.requestFocus();
    }

    public void goBack(){
        showControls();
        runtimeSlider.requestFocus();
    }

    public void nextEpisode(){
        Disc disc = discList.get(currentDisc);
        disc.setTime(videoPlayer.getCurrentTime());
        setDiscValues(discList.get(++currentDisc));
    }

    public void prevEpisode(){
        if (videoPlayer.getCurrentTime() > 2000){
            runtimeSlider.setValue(0);
        }else{
            if (currentDisc > 0) {
                Disc disc = discList.get(currentDisc);
                disc.setTime(videoPlayer.getCurrentTime());
                setDiscValues(discList.get(--currentDisc));
            }else
                runtimeSlider.setValue(0);
        }
    }

    public void showAudioTracks(){
        pause();
    }

    public void showSubtitleTracks(){
        pause();
    }

    private String formatTime(long time){
        long totalSec = time / 1000;
        long h = totalSec / 3600;
        long m = (totalSec % 3600) / 60;
        long s = totalSec % 60;

        if (h > 0)
            return String.format("%d:%2d:%02d", h, m, s);

        return String.format("%2d:%02d", m, s);
    }

    private synchronized void updateMediaPlayerPosition(float newValue) {
        if (tracking.get()) {
            videoPlayer.seekToTime((long) (newValue * videoPlayer.getDuration()));
        }
    }

    public void notifyChanges(long timeMillis){
        currentTime.setText(formatTime(timeMillis));
        toFinishTime.setText(formatTime(videoPlayer.getDuration() - timeMillis));

        runtimeSlider.setValue((double) 100 / ((double) videoPlayer.getDuration() / timeMillis));
    }
}
