package com.example.executablelauncher;

import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.utils.Configuration;
import com.example.executablelauncher.videoPlayer.Track;
import com.example.executablelauncher.videoPlayer.VideoPlayer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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
    private ScrollPane rightOptions;

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
    private Button button1;

    @FXML
    private Button button2;

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
    DesktopViewController parentControllerDesktop = null;
    SeasonController parentController = null;
    Timeline timeline = null;
    Timeline volumeCount = null;
    double percentageStep = 0;
    Season season = null;
    List<Episode> episodeList = new ArrayList<>();
    private List<Track> videoTracks = new ArrayList<>();
    private List<Track> audioTracks = new ArrayList<>();
    private List<Track> subtitleTracks = new ArrayList<>();
    private boolean subsActivated = true;
    int currentDisc = 0;

    //region INITIALIZATION
    public void setParent(DesktopViewController parent){
        parentControllerDesktop = parent;
    }
    public void setParent(SeasonController parent){
        parentController = parent;
    }
    public void setVideo(Season season, Episode episode, String seriesName, Scene scene){
        this.season = season;
        this.episodeList = season.getEpisodes();
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

        rightOptions.setPrefWidth(screenWidth * 0.5);

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
            if (App.pressedBack(e)){
                if (optionsBox.isVisible())
                    hideOptions();
                else
                    stop();
            }
        });

        scene.setOnKeyPressed(e -> {
            if (!controlsShown){
                if (App.pressedRight(e))
                    goAhead();
                else if (App.pressedLeft(e))
                    goBack();
                else if (App.pressedSelect(e)){
                    showControls();
                    pause();
                }
                else
                    showControls();
            }else if (!optionsBox.isVisible()){
                if (App.pressedRB(e))
                    volumeUp();
                else if (App.pressedLB(e))
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

        seriesTitle.setText(seriesName);
        setDiscValues(episode);
    }
    private void onLoad(){
        runtimeSlider.setOnKeyPressed(e -> {
            if (runtimeSlider.isFocused() && App.pressedUp(e))
                hideControls();
            else{
                timeline.playFromStart();
            }

            if (App.pressedLeft(e))
                videoPlayer.seekBackward();
            else if (App.pressedRight(e))
                videoPlayer.seekForward();
        });

        playButton.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                if (!videoPlayer.isPaused())
                    pause();
                else
                    resume();
            }
        });

        nextButton.setOnKeyPressed(e -> {
            if (App.pressedSelect(e))
                nextEpisode();
        });

        prevButton.setOnKeyPressed(e -> {
            if (App.pressedSelect(e))
                prevEpisode();
        });

        //region BUTTON ICONS
        playButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                if (videoPlayer.isPaused())
                    playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/playSelected.png", 35, 35, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/pauseSelected.png", 35, 35, true, true)));
            }else{
                if (videoPlayer.isPaused())
                    playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/play.png", 35, 35, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/pause.png", 35, 35, true, true)));
            }
        });

        nextButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                nextButton.setGraphic(new ImageView(new Image("file:resources/img/icons/nextTrackSelected.png", 35, 35, true, true)));
            else
                nextButton.setGraphic(new ImageView(new Image("file:resources/img/icons/nextTrack.png", 35, 35, true, true)));
        });

        prevButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                prevButton.setGraphic(new ImageView(new Image("file:resources/img/icons/prevTrackSelected.png", 35, 35, true, true)));
            else
                prevButton.setGraphic(new ImageView(new Image("file:resources/img/icons/prevTrack.png", 35, 35, true, true)));
        });

        optionsButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                optionsButton.setGraphic(new ImageView(new Image("file:resources/img/icons/playerOptionsSelected.png", 35, 35, true, true)));
            else
                optionsButton.setGraphic(new ImageView(new Image("file:resources/img/icons/playerOptions.png", 35, 35, true, true)));
        });

        audiosButton.setDisable(true);
        audiosButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                audiosButton.setGraphic(new ImageView(new Image("file:resources/img/icons/audioSelected.png", 35, 35, true, true)));
            else
                audiosButton.setGraphic(new ImageView(new Image("file:resources/img/icons/audio.png", 35, 35, true, true)));
        });

        subtitlesButton.setDisable(true);
        subtitlesButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                subtitlesButton.setGraphic(new ImageView(new Image("file:resources/img/icons/subsSelected.png", 35, 35, true, true)));
            else
                subtitlesButton.setGraphic(new ImageView(new Image("file:resources/img/icons/subs.png", 35, 35, true, true)));
        });

        videoButton.setDisable(true);
        videoButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                videoButton.setGraphic(new ImageView(new Image("file:resources/img/icons/dvdMenuSelected.png", 35, 35, true, true)));
            else
                videoButton.setGraphic(new ImageView(new Image("file:resources/img/icons/dvdMenu.png", 35, 35, true, true)));
        });
        //endregion
    }
    private void setDiscValues(Episode episode){
        episodeTitle.setText(episode.name);
        episodeDate.setText(episode.year);
        seasonEpisode.setText(App.textBundle.getString("seasonLetter") + episode.seasonNumber + " " + App.textBundle.getString("episodeLetter") + episode.episodeNumber);

        if (parentController != null)
            runtime.setText(parentController.setRuntime(episode.runtime));
        else
            runtime.setText(parentControllerDesktop.setRuntime(episode.runtime));

        double durationInSeconds = episode.runtime * 60;
        double fiveSecondsPercentage = (5.0 / durationInSeconds) * 100.0;
        percentageStep = (fiveSecondsPercentage / 100.0) * 100;

        nextButton.setDisable(episodeList.indexOf(episode) == (episodeList.size() - 1));

        if (episode.isWatched())
            episode.setUnWatched();

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), videoPlayer);
        fade.setFromValue(0);
        fade.setToValue(1.0);

        fade.setOnFinished(event -> {
            //Set video and start
            if (parentControllerDesktop == null)
                videoPlayer.playVideo(episode.getVideoSrc(), episode.getTimeWatched());
            else
                videoPlayer.playVideo(episode.getVideoSrc(), episode.getTimeWatched());
            runtimeSlider.setBlockIncrement(percentageStep);

            Series series = App.getSelectedSeries();
            if (series.getVideoZoom() == 0) {
                videoPlayer.fixZoom(0);
                series.setVideoZoom(0);
            }else{
                videoPlayer.fixZoom(0.5f);
                series.setVideoZoom(0.5f);
            }

            videoPlayer.setSubtitleSize(Float.parseFloat(Configuration.loadConfig("subtitleSize", "0.8")));

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

            String audioTrackLanguage = season.getAudioTrackLanguage();

            if (audioTrackLanguage != null && !audioTrackLanguage.isEmpty() && audioTracks.size() > 1) {
                for (Track track : audioTracks)
                    if (track.lang.equals(audioTrackLanguage)) {
                        for (Track aTrack : audioTracks)
                            aTrack.selected = false;

                        track.selected = true;

                        videoPlayer.setAudioTrack(track.id);
                        break;
                    }
            }

            String subtitleTrackLanguage = season.getSubtitleTrackLanguage();

            if (subtitleTrackLanguage != null && !subtitleTrackLanguage.isEmpty() && subtitleTracks.size() > 1){
                for (Track track : subtitleTracks)
                    if (track.lang.equals(subtitleTrackLanguage)) {
                        for (Track sTrack : subtitleTracks)
                            sTrack.selected = false;

                        track.selected = true;

                        videoPlayer.setSubtitleTrack(track.id);
                    }
            }else{
                videoPlayer.disableSubtitles();

                for (Track sTrack : subtitleTracks)
                    sTrack.selected = false;
            }

            //Initialize Buttons
            videoButton.setDisable(videoTracks == null || videoTracks.isEmpty());
            audiosButton.setDisable(audioTracks == null || audioTracks.isEmpty());
            subtitlesButton.setDisable(subtitleTracks == null || subtitleTracks.isEmpty());

            scheduler.shutdown();
        }, 1, TimeUnit.SECONDS);
    }
    private void addInteractionSound(Button btn){
        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && parentController != null)
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

        for (Track aTrack : audioTracks)
            if (aTrack.selected && !aTrack.lang.isEmpty())
                season.setAudioTrackLanguage(aTrack.lang);

        if (subsActivated){
            for (Track sTrack : subtitleTracks)
                if (sTrack.selected && !sTrack.lang.isEmpty())
                    season.setSubtitleTrackLanguage(sTrack.lang);
        }else{
            season.setSubtitleTrackLanguage("");
        }

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.7), videoPlayer);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            videoPlayer.stop();

            if (parentController != null)
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
            playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/playSelected.png", 35, 35, true, true)));
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

            if (track.demux_h == 0)
                continue;

            addOptionCard(track.demux_h + " (" + track.codec.toUpperCase() + ")");

            Button btn = (Button) optionsContainer.getChildren().get(videoTracks.indexOf(track));

            if (track.selected) {
                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");
            }

            btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event)){
                    videoPlayer.setVideoTrack(videoTracks.get(optionsContainer.getChildren().indexOf(btn)).id);

                    for (Node node : optionsContainer.getChildren()){
                        Button button = (Button) node;
                        button.getStyleClass().clear();
                        button.getStyleClass().add("playerOptionsButton");
                    }

                    btn.getStyleClass().clear();
                    btn.getStyleClass().add("playerOptionsSelected");
                }
            });
        }
    }
    private void showZoomOptions(){
        optionsContainer.getChildren().clear();
        Series series = App.getSelectedSeries();

        addOptionCard("Normal");
        Button btn = (Button) optionsContainer.getChildren().get(0);
        btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                videoPlayer.fixZoom(0);
                videoPlayer.setSubtitleVerticalPosition(100);
                videoPlayer.setSubtitleSize(0.7);
                series.setVideoZoom(0);
                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");

                Button b2 = (Button) optionsContainer.getChildren().get(1);
                b2.getStyleClass().clear();
                b2.getStyleClass().add("playerOptionsButton");
            }
        });

        addOptionCard("Zoom");
        Button button = (Button) optionsContainer.getChildren().get(1);
        button.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                videoPlayer.fixZoom(0.5f);
                videoPlayer.setSubtitleVerticalPosition(90);
                videoPlayer.setSubtitleSize(0.9);
                series.setVideoZoom(0.5f);
                button.getStyleClass().clear();
                button.getStyleClass().add("playerOptionsSelected");

                Button b1 = (Button) optionsContainer.getChildren().get(0);
                b1.getStyleClass().clear();
                b1.getStyleClass().add("playerOptionsButton");
            }
        });
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

        button1.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                showAudioTracks();
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

            setTrackButton(track, true);
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
        button2.setVisible(false);                                              //Option disabled for now
        button2.setText(App.textBundle.getString("size"));

        button1.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                showSubtitleTracks();
        });

        button2.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                showSubtitleSize();
        });

        optionsBox.requestFocus();
        button1.requestFocus();
    }
    private void showSubtitleTracks(){
        optionsContainer.getChildren().clear();

        Button btn = new Button();
        btn.setText(App.buttonsBundle.getString("none"));
        btn.setFont(new Font("Arial", 24));
        btn.setTextFill(Color.WHITE);
        btn.setMaxWidth(Integer.MAX_VALUE);
        btn.setPrefWidth(Integer.MAX_VALUE);
        btn.setAlignment(Pos.BOTTOM_LEFT);
        btn.setPadding(new Insets(10));

        btn.getStyleClass().add("playerOptionsButton");

        addInteractionSound(btn);

        optionsContainer.getChildren().add(btn);

        if (season.getSubtitleTrackLanguage() == null || season.getSubtitleTrackLanguage().isEmpty()) {
            btn.getStyleClass().clear();
            btn.getStyleClass().add("playerOptionsSelected");

            videoPlayer.disableSubtitles();
        }

        btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                for (Track sTrack : subtitleTracks){
                    sTrack.selected = false;
                }

                videoPlayer.disableSubtitles();
                subsActivated = false;

                for (Node node : optionsContainer.getChildren()){
                    Button button = (Button) node;
                    button.getStyleClass().clear();
                    button.getStyleClass().add("playerOptionsButton");
                }

                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");
            }
        });

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

            setTrackButton(track, false);
        }
    }

    private void showSubtitleSize(){
        optionsContainer.getChildren().clear();

        Button small = addSimpleButton(App.buttonsBundle.getString("small"));
        Button normal = addSimpleButton(App.buttonsBundle.getString("normal"));
        Button large = addSimpleButton(App.buttonsBundle.getString("large"));

        switch(Configuration.loadConfig("subtitleSize", "0.8")){
            case "0.8":
                small.getStyleClass().clear();
                small.getStyleClass().add("playerOptionsSelected");
                Configuration.saveConfig("subtitleSize", "0.8");
                break;
            case "0.9":
                normal.getStyleClass().clear();
                normal.getStyleClass().add("playerOptionsSelected");
                Configuration.saveConfig("subtitleSize", "0.9");
                break;
            case "1":
                large.getStyleClass().clear();
                large.getStyleClass().add("playerOptionsSelected");
                Configuration.saveConfig("subtitleSize", "1");
                break;
        }

        small.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                videoPlayer.setSubtitleSize(0.8);

                for (Node node : optionsContainer.getChildren()){
                    Button button = (Button) node;
                    button.getStyleClass().clear();
                    button.getStyleClass().add("playerOptionsButton");
                }

                small.getStyleClass().clear();
                small.getStyleClass().add("playerOptionsSelected");
            }
        });

        normal.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                videoPlayer.setSubtitleSize(0.9);

                for (Node node : optionsContainer.getChildren()){
                    Button button = (Button) node;
                    button.getStyleClass().clear();
                    button.getStyleClass().add("playerOptionsButton");
                }

                normal.getStyleClass().clear();
                normal.getStyleClass().add("playerOptionsSelected");
            }
        });

        large.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                videoPlayer.setSubtitleSize(1);

                for (Node node : optionsContainer.getChildren()){
                    Button button = (Button) node;
                    button.getStyleClass().clear();
                    button.getStyleClass().add("playerOptionsButton");
                }

                large.getStyleClass().clear();
                large.getStyleClass().add("playerOptionsSelected");
            }
        });

        optionsContainer.getChildren().add(small);
        optionsContainer.getChildren().add(normal);
        optionsContainer.getChildren().add(large);
    }

    private Button addSimpleButton(String name){
        Button btn = new Button(name);
        btn.setFont(new Font("Arial", 24));
        btn.setTextFill(Color.WHITE);
        btn.setMaxWidth(Integer.MAX_VALUE);
        btn.setPrefWidth(Integer.MAX_VALUE);
        btn.setAlignment(Pos.BOTTOM_LEFT);
        btn.setPadding(new Insets(10));
        btn.getStyleClass().add("playerOptionsButton");

        return btn;
    }

    private void setTrackButton(Track track, boolean isAudio) {
        Button btn;
        if (isAudio)
            btn = (Button) optionsContainer.getChildren().get(audioTracks.indexOf(track));
        else
            btn = (Button) optionsContainer.getChildren().get(subtitleTracks.indexOf(track) + 1);

        if (track.selected) {
            btn.getStyleClass().clear();
            btn.getStyleClass().add("playerOptionsSelected");
        }

        btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                if (isAudio) {
                    for (Track aTrack : audioTracks){
                        aTrack.selected = aTrack == track;
                    }

                    videoPlayer.setAudioTrack(audioTracks.get(optionsContainer.getChildren().indexOf(btn)).id);
                }else {
                    for (Track sTrack : subtitleTracks){
                        sTrack.selected = sTrack == track;
                    }

                    videoPlayer.setSubtitleTrack(subtitleTracks.get(optionsContainer.getChildren().indexOf(btn) - 1).id);

                    Series series = App.getSelectedSeries();
                    if (series.getVideoZoom() == 0) {
                        videoPlayer.setSubtitleSize(0.7);
                        videoPlayer.setSubtitleVerticalPosition(100);
                    }else{
                        videoPlayer.setSubtitleSize(0.9);
                        videoPlayer.setSubtitleVerticalPosition(90);
                    }
                }

                for (Node node : optionsContainer.getChildren()){
                    Button button = (Button) node;
                    button.getStyleClass().clear();
                    button.getStyleClass().add("playerOptionsButton");
                }

                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");
            }
        });
    }

    private void addOptionCard(String title){
        Button btn = new Button();
        btn.setText(title);
        btn.setFont(new Font("Arial", 24));
        btn.setTextFill(Color.WHITE);
        btn.setMaxWidth(Integer.MAX_VALUE);
        btn.setPrefWidth(Integer.MAX_VALUE);
        btn.setAlignment(Pos.BOTTOM_LEFT);
        btn.setPadding(new Insets(10));

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
