package com.example.executablelauncher;

import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.videoPlayer.Track;
import com.example.executablelauncher.videoPlayer.VideoPlayer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.jfoenix.controls.JFXSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VideoPlayerController {
    //region FXML ATTRIBUTES
    @FXML
    private StackPane mainPane;

    @FXML
    private Label currentTime;

    @FXML
    private Label episodeDate;

    @FXML
    private Label episodeTitle;

    @FXML
    private Button audiosButton;

    @FXML
    private Button videoButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button optionsButton;

    @FXML
    private Button playButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button subtitlesButton;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

    @FXML
    private Button button4;

    @FXML
    private Button button1;

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
    private Label toFinishTime;

    @FXML
    private VBox controlsBox;

    @FXML
    private BorderPane optionsBox;

    @FXML
    private VBox optionsContainer;

    @FXML
    private Label optionsTitle;

    @FXML
    private HBox volumeBox;
    //endregion

    private VideoPlayer videoPlayer;
    boolean controlsShown = false;
    SeasonController parentController = null;
    Timeline timeline = null;
    Timeline volumeCount = null;
    double percentageStep = 0;
    List<Episode> episodeList = new ArrayList<>();
    private List<Track> videoTracks = new ArrayList<>();
    private List<Track> audioTracks = new ArrayList<>();
    private List<Track> subtitleTracks = new ArrayList<>();
    private int videoTrackID = -1;
    private int audioTrackID = -1;
    private int subtitleTrackID = -1;
    int currentDisc = 0;

    //region INITIALIZATION
    public void setVideo(SeasonController parent, List<Episode> episodeList, Episode episode, String seriesName, Scene scene){
        parentController = parent;
        this.episodeList = episodeList;
        onLoad();

        currentDisc = episodeList.indexOf(episode);

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        videoPlayer = new VideoPlayer();
        videoPlayer.setParent(this);
        mainPane.getChildren().add(0, videoPlayer);

        shadowImage.setFitWidth(screenWidth);
        shadowImage.setFitHeight(screenHeight);
        shadowImage.setPreserveRatio(false);

        shadowImage.setVisible(false);
        controlsBox.setVisible(false);

        volumeBox.setVisible(false);
        optionsBox.setVisible(false);

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
            if (e.getCode().equals(KeyCode.ESCAPE) || e.getCode().equals(KeyCode.BACK_SPACE)){
                if (optionsBox.isVisible())
                    hideOptions();
                else
                    stop();
            }
        });

        scene.setOnKeyPressed(e -> {
            if (!controlsShown){
                if (e.getCode().equals(KeyCode.RIGHT))
                    goAhead();
                else if (e.getCode().equals(KeyCode.LEFT))
                    goBack();
                else if (e.getCode().equals(KeyCode.SPACE)){
                    showControls();
                    pause();
                }
                else
                    showControls();
            }else if (!optionsBox.isVisible()){
                if (e.getCode().equals(KeyCode.PLUS))
                    volumeUp();
                else if (e.getCode().equals(KeyCode.MINUS))
                    volumeDown();

                timeline.playFromStart();
            }
        });

        addInteractionSound(audiosButton);
        addInteractionSound(videoButton);
        addInteractionSound(nextButton);
        addInteractionSound(optionsButton);
        addInteractionSound(playButton);
        addInteractionSound(prevButton);
        addInteractionSound(subtitlesButton);
        addInteractionSound(button1);
        addInteractionSound(button2);
        addInteractionSound(button3);
        addInteractionSound(button4);

        seriesTitle.setText(seriesName);
        setDiscValues(episode);
    }
    private void onLoad(){
        runtimeSlider.setOnKeyPressed(e -> {
            if (runtimeSlider.isFocused() && e.getCode().equals(KeyCode.UP))
                hideControls();
            else{
                timeline.playFromStart();
            }

            if (e.getCode().equals(KeyCode.LEFT))
                videoPlayer.seekBackward();
            else if (e.getCode().equals(KeyCode.RIGHT))
                videoPlayer.seekForward();
        });

        playButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)){
                if (!videoPlayer.isPaused())
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
                if (videoPlayer.isPaused())
                    playButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/playSelected.png", 35, 35, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/pauseSelected.png", 35, 35, true, true)));
            }else{
                if (videoPlayer.isPaused())
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

        audiosButton.setDisable(true);
        audiosButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                audiosButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/audioSelected.png", 35, 35, true, true)));
            else
                audiosButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/audio.png", 35, 35, true, true)));
        });

        subtitlesButton.setDisable(true);
        subtitlesButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                subtitlesButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/subsSelected.png", 35, 35, true, true)));
            else
                subtitlesButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/subs.png", 35, 35, true, true)));
        });

        videoButton.setDisable(true);
        videoButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                videoButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/dvdMenuSelected.png", 35, 35, true, true)));
            else
                videoButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/dvdMenu.png", 35, 35, true, true)));
        });
        //endregion
    }
    private void setDiscValues(Episode episode){
        episodeTitle.setText(episode.name);
        episodeDate.setText(episode.year);
        seasonEpisode.setText(App.textBundle.getString("seasonLetter") + episode.seasonNumber + " " + App.textBundle.getString("episodeLetter") + episode.episodeNumber);
        runtime.setText(parentController.setRuntime(episode.runtime));

        double durationInSeconds = episode.runtime * 60;
        double fiveSecondsPercentage = (5.0 / durationInSeconds) * 100.0;
        percentageStep = (fiveSecondsPercentage / 100.0) * 100;

        nextButton.setDisable(episodeList.indexOf(episode) == (episodeList.size() - 1));

        if (episode.isWatched())
            episode.setUnWatched();

        float position;

        if (episode.getTimeWatched() > 0){
            position = (float) ((episode.runtime * 60L * 1000) - episode.getTimeWatched()) / 100;
        } else {
            position = 0;
        }

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), videoPlayer);
        fade.setFromValue(0);
        fade.setToValue(1.0);

        fade.setOnFinished(event -> {
            //Set video and start
            videoPlayer.playVideo(episode.videoSrc, episode.getTimeWatched());
            runtimeSlider.setBlockIncrement(percentageStep);

            loadTracks();
        });

        fade.play();
    }
    private void loadTracks(){
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(() -> {
            videoPlayer.loadTracks();

            videoTracks = videoPlayer.getVideoTracks();
            audioTracks = videoPlayer.getAudioTracks();
            subtitleTracks = videoPlayer.getSubtitleTracks();

            //Initialize Buttons
            videoButton.setDisable(videoTracks == null || videoTracks.isEmpty());
            audiosButton.setDisable(audioTracks == null || audioTracks.isEmpty());
            subtitlesButton.setDisable(subtitleTracks == null || subtitleTracks.isEmpty());

            scheduler.shutdown();
        }, 1, TimeUnit.SECONDS);
    }
    private void addInteractionSound(Button btn){
        btn.focusedProperty().addListener(e -> {
            parentController.getParent().playInteractionSound();
        });
    }
    //endregion

    //region BEHAVIOR AND EFFECTS
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
    //endregion

    //region CONTROLS
    public void stop() {
        Episode episode = episodeList.get(currentDisc);
        episode.setTime(videoPlayer.getCurrentTime());

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
        videoPlayer.togglePause();
    }
    public void pause(){
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
        Episode episode = episodeList.get(currentDisc);
        episode.setTime(videoPlayer.getCurrentTime());

        videoPlayer.stop();

        setDiscValues(episodeList.get(++currentDisc));
    }
    public void prevEpisode(){
        if (videoPlayer.getCurrentTime() > 2000){
            runtimeSlider.setValue(0);
        }else{
            if (currentDisc > 0) {
                Episode episode = episodeList.get(currentDisc);
                episode.setTime(videoPlayer.getCurrentTime());

                videoPlayer.stop();

                setDiscValues(episodeList.get(--currentDisc));
            }else
                runtimeSlider.setValue(0);
        }
    }
    public void showVideoOptions(){
        if (!videoPlayer.isPaused())
            pause();
        optionsBox.setVisible(true);
        timeline.stop();
        shadowImage.setVisible(true);
        controlsBox.setVisible(false);
        optionsTitle.setText(App.textBundle.getString("video"));

        button1.setText(App.textBundle.getString("video"));
        button2.setVisible(true);
        button2.setText(App.textBundle.getString("zoom"));
        button3.setVisible(false);
        button4.setVisible(false);

        button1.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                showVideoTracks();
        });
        button2.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                showZoomOptions();
        });

        optionsBox.requestFocus();
        button1.requestFocus();
    }
    private void showVideoTracks(){
        optionsContainer.getChildren().clear();
        for (Track track : videoTracks){
            addOptionCard(track.demux_h + " (" + track.codec.toUpperCase() + ")");

            if (track.selected) {
                Button btn = (Button) optionsContainer.getChildren().get(videoTracks.indexOf(track));
                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");
            }
        }
    }
    private void showZoomOptions(){
        optionsContainer.getChildren().clear();
        addOptionCard("Normal");

        Button btn = (Button) optionsContainer.getChildren().get(0);
        btn.getStyleClass().clear();
        btn.getStyleClass().add("playerOptionsSelected");

        addOptionCard("Zoom");
        addOptionCard("Fixed 4:3");
        addOptionCard("Fixed 16:9");
        addOptionCard("Expanded");
    }
    public void showAudioOptions(){
        if (!videoPlayer.isPaused())
            pause();
        optionsBox.setVisible(true);
        timeline.stop();
        shadowImage.setVisible(true);
        controlsBox.setVisible(false);
        optionsTitle.setText(App.textBundle.getString("audio"));

        button1.setText(App.textBundle.getString("audio"));
        button2.setVisible(false);
        button3.setVisible(false);
        button4.setVisible(false);

        button1.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                showAudioTracks();
        });
        button2.focusedProperty().addListener((obs, oldVal, newVal) -> {
            /*if (newVal)
                showAudioDevices();*/
        });

        optionsBox.requestFocus();
        button1.requestFocus();
    }
    private void showAudioTracks(){
        optionsContainer.getChildren().clear();
        for (Track track : audioTracks){
            String channels = switch (track.audio_channels) {
                case 1 -> "1.0";
                case 2 -> "2.0";
                case 3 -> "2.1";
                case 6 -> "5.1";
                case 8 -> "7.1";
                default -> track.audio_channels + " ch";
            };

            String title = track.title;
            Locale lang = Locale.forLanguageTag(track.lang);
            if (lang != null){
                title = lang.getDisplayLanguage();
            }

            addOptionCard( title + " (" + track.lang.toUpperCase() + ") " + track.codec.toUpperCase() + " " + channels);

            if (track.selected) {
                Button btn = (Button) optionsContainer.getChildren().get(audioTracks.indexOf(track));
                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");
            }
        }
    }
    public void showSubtitleOptions(){
        if (!videoPlayer.isPaused())
            pause();
        optionsBox.setVisible(true);
        timeline.stop();
        shadowImage.setVisible(true);
        controlsBox.setVisible(false);
        optionsTitle.setText(App.textBundle.getString("subs"));

        button1.setText(App.textBundle.getString("languageText"));
        button2.setVisible(true);
        button2.setText(App.textBundle.getString("color"));
        button3.setVisible(true);
        button3.setText(App.textBundle.getString("size"));
        button3.setVisible(true);
        button4.setText(App.textBundle.getString("position"));

        button1.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                showSubtitleTracks();
        });

        optionsBox.requestFocus();
        button1.requestFocus();
    }
    private void showSubtitleTracks(){
        optionsContainer.getChildren().clear();
        for (Track track : subtitleTracks){

            String forced = "";

            if (track.forced)
                forced = " - Forced";

            String title = track.title;
            Locale lang = Locale.forLanguageTag(track.lang);
            if (lang != null){
                title = lang.getDisplayLanguage();
            }

            addOptionCard( title + " (" + track.lang.toUpperCase() + ") " + forced);

            if (track.selected) {
                Button btn = (Button) optionsContainer.getChildren().get(subtitleTracks.indexOf(track));
                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");
            }
        }
    }
    private void addOptionCard(String title){
        Button btn = new Button();
        btn.setText(title);
        btn.setFont(new Font("Arial", 24));
        btn.setTextFill(Color.WHITE);
        btn.setMaxWidth(Integer.MAX_VALUE);
        btn.setPrefWidth(Integer.MAX_VALUE);
        btn.setAlignment(Pos.BOTTOM_LEFT);

        btn.getStyleClass().add("playerOptionsButton");

        addInteractionSound(btn);

        optionsContainer.getChildren().add(btn);
    }
    public void hideOptions(){
        optionsBox.setVisible(false);
        showControls();
    }
    //endregion

    private String formatTime(long time){
        long totalSec = time / 1000;
        long h = totalSec / 3600;
        long m = (totalSec % 3600) / 60;
        long s = totalSec % 60;

        if (h > 0)
            return String.format("%d:%2d:%02d", h, m, s);

        return String.format("%2d:%02d", m, s);
    }

    public void notifyChanges(long timeMillis){
        currentTime.setText(formatTime(timeMillis));
        toFinishTime.setText(formatTime(videoPlayer.getDuration() - timeMillis));

        runtimeSlider.setValue((double) 100 / ((double) videoPlayer.getDuration() / timeMillis));
    }
}
