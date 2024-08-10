package com.example.executablelauncher;

import com.example.executablelauncher.entities.Chapter;
import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.fileMetadata.AudioTrack;
import com.example.executablelauncher.fileMetadata.SubtitleTrack;
import com.example.executablelauncher.fileMetadata.VideoTrack;
import com.example.executablelauncher.utils.Configuration;
import com.example.executablelauncher.videoPlayer.VideoPlayer;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.executablelauncher.utils.Utils.*;

public class VideoPlayerController {
    //region FXML ATTRIBUTES
    @FXML ImageView volumeImage;
    @FXML Button downAdjustment;
    @FXML Button upAdjustment;
    @FXML Button restartAdjustment;
    @FXML Button chaptersButton;
    @FXML Label adjustmentText;
    @FXML HBox adjustmentPane;
    @FXML HBox desktopVolumeSliderBox;
    @FXML ScrollPane mainMenuScroll;
    @FXML ScrollPane simpleMenuScroll;
    @FXML ScrollPane chapterScroll;
    @FXML VBox chapterPane;
    @FXML Label chapterTitle;
    @FXML VBox chapterContainer;
    @FXML VBox leftTouchArea;
    @FXML VBox rightTouchArea;
    @FXML StackPane mainPane;
    @FXML ScrollPane rightOptions;
    @FXML Label currentTime;
    @FXML Label episodeInfo;
    @FXML Button leftTouchButton;
    @FXML Button rightTouchButton;
    @FXML Button audiosButton;
    @FXML Button videoButton;
    @FXML Button nextButton;
    @FXML Button fullScreenButton;
    @FXML Button playButton;
    @FXML Button prevButton;
    @FXML Button closeButton;
    @FXML Button subtitlesButton;
    @FXML JFXSlider runtimeSlider;
    @FXML JFXSlider volumeSlider;
    @FXML JFXSlider volumeSlider2;
    @FXML Label seriesTitle;
    @FXML ImageView shadowImage;
    @FXML Label toFinishTime;
    @FXML BorderPane controlsBox;
    @FXML BorderPane optionsBox;
    @FXML BorderPane touchPane;
    @FXML BorderPane sliderBox;
    @FXML BorderPane buttonsBox;
    @FXML Label optionsTitle;
    @FXML HBox volumeBox;
    @FXML HBox mainMenu;
    @FXML VBox leftOptions;
    @FXML VBox centerOptions;
    @FXML VBox optionsContainer;
    @FXML VBox simpleMenu;
    //endregion

    //region ATTRIBUTES
    Stage videoStage;
    Stage controlsStage;
    VideoPlayer videoPlayer;
    boolean controlsShown = false;
    DesktopViewController parentControllerDesktop = null;
    SeasonController parentController = null;
    Timeline timeline = null;
    Timeline touchTimeline = new Timeline();
    Timeline volumeCount = null;
    Timeline runtimeTimeline = new Timeline();
    Timeline hideMouse = null;
    Season season = null;
    List<Episode> episodeList = new ArrayList<>();
    Episode episode = null;
    List<VideoTrack> videoTracks = new ArrayList<>();
    List<AudioTrack> audioTracks = new ArrayList<>();
    List<SubtitleTrack> subtitleTracks = new ArrayList<>();
    PauseTransition hideSlider;
    boolean fullscreen = false;
    int currentDisc = 0;
    boolean movingSlider = false;
    int currentTimeSeconds = 0;
    boolean cursorHidden = false;
    //endregion

    //region INITIALIZATION
    public void setDesktopPlayer(DesktopViewController parent, Stage stage){
        parentControllerDesktop = parent;
        controlsStage = stage;
    }
    public void setFullScreenPlayer(SeasonController parent, Stage stage){
        parentController = parent;
        controlsStage = stage;
    }
    public void setVideo(Season season, Episode episode, String seriesName, Stage stage){
        videoPlayer = VideoPlayer.INSTANCE;
        videoPlayer.setParent(this);

        this.videoStage = stage;
        this.season = season;
        this.episodeList = season.getEpisodes();
        this.videoTracks = episode.getVideoTracks();
        this.audioTracks = episode.getAudioTracks();
        this.subtitleTracks = episode.getSubtitleTracks();

        if (episodeList.indexOf(episode) == episodeList.size() - 1)
            nextButton.setDisable(true);

        onLoad();

        currentDisc = episodeList.indexOf(episode);

        //region VIEW ELEMENTS SIZE ADJUSTMENT
        mainPane.prefWidthProperty().bind(controlsStage.widthProperty());
        mainPane.prefHeightProperty().bind(controlsStage.heightProperty());
        rightOptions.prefWidthProperty().bind(mainPane.prefWidthProperty().multiply(0.5));

        leftTouchArea.prefWidthProperty().bind(mainPane.prefWidthProperty().multiply(0.45));
        rightTouchArea.prefWidthProperty().bind(mainPane.prefWidthProperty().multiply(0.45));
        leftTouchArea.prefHeightProperty().bind(touchPane.heightProperty());
        rightTouchArea.prefHeightProperty().bind(touchPane.heightProperty());
        touchPane.prefHeightProperty().bind(controlsBox.heightProperty());

        syncStageSize(videoStage);
        syncStagePosition(videoStage);

        shadowImage.fitWidthProperty().bind(mainPane.widthProperty());
        shadowImage.fitHeightProperty().bind(mainPane.heightProperty());

        //Window adjustment and movement behaviour
        videoStage.widthProperty().addListener((obs, oldVal, newVal) -> syncStageSize(videoStage));
        videoStage.heightProperty().addListener((obs, oldVal, newVal) -> syncStageSize(videoStage));
        videoStage.xProperty().addListener((obs, oldVal, newVal) -> syncStagePosition(videoStage));
        videoStage.yProperty().addListener((obs, oldVal, newVal) -> syncStagePosition(videoStage));

        shadowImage.setPreserveRatio(false);
        shadowImage.setVisible(false);
        controlsBox.setVisible(false);
        volumeBox.setVisible(false);
        optionsBox.setVisible(false);

        //Main menu size adjustments
        StackPane stackParent = (StackPane) mainMenuScroll.getParent();
        stackParent.prefWidthProperty().bind(videoStage.widthProperty().divide(1.5));
        stackParent.prefHeightProperty().bind(videoStage.heightProperty());

        mainMenuScroll.prefWidthProperty().bind(videoStage.widthProperty().divide(1.5));

        leftOptions.prefWidthProperty().bind(videoStage.widthProperty().divide(3));
        centerOptions.prefWidthProperty().bind(videoStage.widthProperty().divide(3));
        rightOptions.prefWidthProperty().bind(videoStage.widthProperty().divide(3));
        optionsContainer.prefWidthProperty().bind(videoStage.widthProperty().divide(3));

        simpleMenuScroll.prefWidthProperty().bind(videoStage.heightProperty().multiply(0.8));
        simpleMenuScroll.setMaxWidth(Screen.getPrimary().getBounds().getHeight() * 0.8);
        simpleMenu.prefWidthProperty().bind(videoStage.heightProperty().multiply(0.8));
        simpleMenu.setMaxWidth(Screen.getPrimary().getBounds().getHeight() * 0.8);

        chapterPane.prefWidthProperty().bind(videoStage.heightProperty().multiply(0.5));
        chapterPane.setMaxWidth(Screen.getPrimary().getBounds().getHeight() * 0.5);
        chapterContainer.prefWidthProperty().bind(videoStage.heightProperty().multiply(0.5));
        chapterContainer.setMaxWidth(Screen.getPrimary().getBounds().getHeight() * 0.5);

        adjustmentPane.prefHeightProperty().bind(videoStage.heightProperty().multiply(0.7));
        //endregion

        chapterTitle.setText(App.textBundle.getString("chapters"));

        //region TIMELINES
        timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(5), event -> {
                    if (!optionsBox.isVisible() && !movingSlider)
                        hideControls();
                })
        );
        timeline.play();

        volumeCount = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(2), event -> volumeBox.setVisible(false))
        );

        hideMouse = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(6), event -> hideMouse())
        );
        hideMouse.play();
        //endregion

        volumeSlider2.setValue(100);
        volumeSlider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            videoVolume((Double) newValue);
        });

        mainPane.requestFocus();

        //region VIEW ELEMENTS BEHAVIOUR
        controlsStage.addEventHandler(MouseEvent.MOUSE_MOVED, event -> showMouse());
        videoStage.addEventHandler(MouseEvent.MOUSE_MOVED, event -> showMouse());

        videoStage.getScene().setOnMouseClicked(e -> {
            if (optionsBox.isVisible() && !chapterPane.isVisible())
                hideOptions();
            else if (!controlsBox.isVisible())
                showControls();
        });

        controlsStage.getScene().setOnMouseClicked(e -> {
            if (optionsBox.isVisible() && !chapterPane.isVisible())
                hideOptions();
            else if (!controlsBox.isVisible())
                showControls();
        });

        /*videoStage.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (App.pressedBack(e)){
                if (optionsBox.isVisible())
                    hideOptions();
                else if (controlsShown)
                    hideControls();
                else
                    stop();
            }
        });*/

        controlsStage.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (App.pressedBack(e)){
                if (optionsBox.isVisible())
                    hideOptions();
                else if (!controlsShown)
                    stop();
            }
        });

        controlsStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (videoPlayer.isVideoLoaded()){
                if (App.pressedRight(e))
                    goAhead();
                else if (App.pressedLeft(e))
                    goBack();
                else
                    showControls();
            }else if (!optionsBox.isVisible() && videoPlayer.isVideoLoaded()){
                if (App.pressedRB(e))
                    volumeUp();
                else if (App.pressedLB(e))
                    volumeDown();

                timeline.playFromStart();
            }
        });

        videoStage.getScene().setOnKeyReleased(e -> {
            if (!controlsShown && !App.pressedLeft(e) && !App.pressedRight(e)
                    && !App.pressedRB(e) && !App.pressedLB(e) && !App.pressedBack(e))
                showControls();
        });
        //endregion

        addInteractionSound(audiosButton);
        addInteractionSound(videoButton);
        addInteractionSound(nextButton);
        addInteractionSound(playButton);
        addInteractionSound(prevButton);
        addInteractionSound(subtitlesButton);
        addInteractionSound(chaptersButton);
        addInteractionSound(fullScreenButton);

        seriesTitle.setText(seriesName);
        setDiscValues(episode);
    }
    private void showMouse(){
        if (cursorHidden){
            controlsStage.getScene().setCursor(Cursor.DEFAULT);
            videoStage.getScene().setCursor(Cursor.DEFAULT);
            cursorHidden = false;
        }

        hideMouse.playFromStart();
    }
    private void hideMouse(){
        if (!cursorHidden){
            controlsStage.getScene().setCursor(Cursor.NONE);
            videoStage.getScene().setCursor(Cursor.NONE);
            cursorHidden = true;
        }
    }
    private void syncStageSize(Stage primaryStage) {
        if (controlsStage != null) {
            if (primaryStage.getWidth() > 0 && primaryStage.getHeight() > 0) {
                if (fullscreen){
                    controlsStage.setWidth(primaryStage.getWidth());
                    controlsStage.setHeight(primaryStage.getHeight());
                }else{
                    controlsStage.setWidth(primaryStage.getWidth() * 0.99);
                    controlsStage.setHeight(primaryStage.getHeight() * 0.98);
                }
            }
        }

        syncStagePosition(primaryStage);
    }
    private void syncStagePosition(Stage primaryStage) {
        if (controlsStage != null) {
            double primaryX = primaryStage.getX();
            double primaryY = primaryStage.getY();
            double primaryWidth = primaryStage.getWidth();
            double primaryHeight = primaryStage.getHeight();
            double controlsWidth = controlsStage.getWidth();
            double controlsHeight = controlsStage.getHeight();

            double x = primaryX + (primaryWidth - controlsWidth) / 2;
            double y = primaryY + ((primaryHeight - controlsHeight) / 2) + 7;

            controlsStage.setX(x);
            controlsStage.setY(y);
        }
    }
    private void onLoad(){
        leftTouchButton.setVisible(false);
        rightTouchButton.setVisible(false);

        adjustmentPane.setVisible(false);
        optionsBox.setVisible(false);

        if (parentController != null)
            hideButton(fullScreenButton);

        //region BUTTONS MOVEMENT
        optionsBox.setOnMouseClicked(e -> {
            if (chapterPane.isVisible()){
                hideChaptersPane();
            }else{
                hideOptions();
            }
        });

        adjustmentPane.setOnMouseClicked(e -> fadeOutEffect(adjustmentPane, 0.2f));

        rightTouchArea.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY)){
                touchTimeline.stop();

                if (e.getClickCount() >= 2) {
                    seekForward();
                } else {
                    //Timeline to let the user click two times
                    touchTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(300), ae -> hideControls()));
                    touchTimeline.play();
                }
            }
        });

        leftTouchArea.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY)){
                touchTimeline.stop();

                if (e.getClickCount() >= 2) {
                    seekBackward();
                } else {
                    touchTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(300), ae -> hideControls()));
                    touchTimeline.play();
                }
            }
        });

        runtimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (movingSlider) {
                currentTimeSeconds = newValue.intValue();
                videoPlayer.seekToTime(newValue.intValue());
                updateSliderValue();
            }
        });

        runtimeSlider.setOnMousePressed(e -> {
            movingSlider = true;
            runtimeTimeline.stop();

            showSlider();
        });

        runtimeSlider.setOnMouseReleased(e -> {
            movingSlider = false;
            runtimeTimeline.playFromStart();

            hideControls();
        });

        playButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedSelect(e)){
                if (!videoPlayer.isPaused())
                    pause();
                else
                    resume();
            }else if (App.pressedRight(e)){
                if (!nextButton.isDisabled())
                    nextButton.requestFocus();
                else
                    videoButton.requestFocus();
            }else if (App.pressedLeft(e)) {
                if (!prevButton.isDisabled())
                    prevButton.requestFocus();
                else if (!chaptersButton.isDisabled())
                    chaptersButton.requestFocus();
                else if (!audiosButton.isDisabled())
                    audiosButton.requestFocus();
                else if (!subtitlesButton.isDisabled())
                    subtitlesButton.requestFocus();
            }else if (App.pressedUp(e)) {
                hideControls();
            }
        });

        playButton.setOnMouseClicked(e -> {
            if (!videoPlayer.isPaused())
                pause();
            else
                resume();
        });

        nextButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedSelect(e))
                nextEpisode();
            else if (App.pressedRight(e)){
                videoButton.requestFocus();
            }else if (App.pressedLeft(e))
                playButton.requestFocus();
            else if (App.pressedUp(e))
                hideControls();
        });

        nextButton.setOnMouseClicked(e -> nextEpisode());

        prevButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedSelect(e))
                prevEpisode();
            else if (App.pressedLeft(e)){
                if (!chaptersButton.isDisabled())
                    chaptersButton.requestFocus();
                else if (!audiosButton.isDisabled())
                    audiosButton.requestFocus();
                else if (!subtitlesButton.isDisabled())
                    subtitlesButton.requestFocus();
            }else if (App.pressedRight(e))
                playButton.requestFocus();
            else if (App.pressedUp(e))
                hideControls();
        });

        prevButton.setOnMouseClicked(e -> prevEpisode());

        videoButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedLeft(e)){
                if (!nextButton.isDisabled())
                    nextButton.requestFocus();
                else
                    playButton.requestFocus();
            }else if (App.pressedUp(e))
                hideControls();
        });

        audiosButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedLeft(e)){
                if (!subtitlesButton.isDisabled())
                    subtitlesButton.requestFocus();
            }else if (App.pressedRight(e)){
                if (!chaptersButton.isDisabled())
                    chaptersButton.requestFocus();
                else if (!prevButton.isDisabled())
                    prevButton.requestFocus();
                else
                    playButton.requestFocus();
            }else if (App.pressedUp(e))
                hideControls();
        });

        subtitlesButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedRight(e)){
                if (!audiosButton.isDisabled())
                    audiosButton.requestFocus();
                else
                    chaptersButton.requestFocus();
            }else if (App.pressedUp(e))
                runtimeSlider.requestFocus();
        });

        chaptersButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedRight(e)){
                if (!prevButton.isDisabled())
                    prevButton.requestFocus();
                else
                    playButton.requestFocus();
            }else if (App.pressedUp(e))
                hideControls();
            else if (App.pressedLeft(e)){
                if (!audiosButton.isDisabled())
                    audiosButton.requestFocus();
                else if (subtitlesButton.isDisabled())
                    subtitlesButton.requestFocus();
            }
        });

        fullScreenButton.setOnMouseClicked(e -> toggleFullscreen());
        closeButton.setVisible(false);
        //endregion

        //region BUTTON ICONS
        playButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                if (videoPlayer.isPaused())
                    playButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/playSelected.png"), 30, 30, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/pauseSelected.png"), 30, 30, true, true)));
            }else{
                if (videoPlayer.isPaused())
                    playButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/play.png"), 30, 30, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/pause.png"), 30, 30, true, true)));
            }
        });

        nextButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                nextButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/nextTrackSelected.png"), 30, 30, true, true)));
            else
                nextButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/nextTrack.png"), 30, 30, true, true)));
        });

        prevButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                prevButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/prevTrackSelected.png"), 30, 30, true, true)));
            else
                prevButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/prevTrack.png"), 30, 30, true, true)));
        });

        audiosButton.setDisable(true);
        audiosButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                audiosButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/audioSelected.png"), 33, 33, true, true)));
            else
                audiosButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/audio.png"), 33, 33, true, true)));
        });

        subtitlesButton.setDisable(true);
        subtitlesButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                subtitlesButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/subsSelected.png"), 30, 30, true, true)));
            else
                subtitlesButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/subs.png"), 30, 30, true, true)));
        });

        chaptersButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                chaptersButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/chaptersSelected.png"), 30, 30, true, true)));
            else
                chaptersButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/chapters.png"), 30, 30, true, true)));
        });

        videoButton.setDisable(true);
        videoButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                videoButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/playerOptionsSelected.png"), 30, 30, true, true)));
            else
                videoButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/playerOptions.png"), 30, 30, true, true)));
        });
        //endregion
    }
    private void setDiscValues(Episode episode){
        this.episode = episode;

        String episodeRuntime = String.valueOf(episode.getRuntime());

        if (parentController != null)
            episodeRuntime = setRuntime(episode.getRuntime());
        else if (parentControllerDesktop != null)
            episodeRuntime = parentControllerDesktop.setRuntime(episode.getRuntime());

        if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows")){
            episodeInfo.setText(episode.getName() + " - " +
                    App.textBundle.getString("seasonLetter") + season.getSeasonNumber() + " " + App.textBundle.getString("episodeLetter") + episode.getEpisodeNumber() +
                    " - " + episode.getYear() + " - " + episodeRuntime);
        }else{
            episodeInfo.setText(episode.getName() + " - " + episodeRuntime);
        }

        runtimeSlider.setMax(episode.getRuntimeInSeconds());
        nextButton.setDisable(episodeList.indexOf(episode) == (episodeList.size() - 1));

        if (episode.isWatched())
            episode.setUnWatched();

        //Set video and start
        if (parentControllerDesktop == null)
            videoPlayer.playVideo(episode.getVideoSrc(), App.textBundle.getString("fullscreenMode"));
        else
            videoPlayer.playVideo(episode.getVideoSrc(), App.textBundle.getString("desktopMode"));

        Series series = App.getSelectedSeries();
        if (series.getVideoZoom() == 0) {
            videoPlayer.fixZoom(0);
            series.setVideoZoom(0);
        }else{
            videoPlayer.fixZoom(0.5f);
            series.setVideoZoom(0.5f);
        }

        videoPlayer.setSubtitleSize(Float.parseFloat(Configuration.loadConfig("subtitleSize", "0.8")));

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), mainPane);
        fade.setFromValue(0);
        fade.setToValue(1.0);

        fade.play();
    }
    private void loadTracks(){
        int selectedAudioTrack = 0;
        String selectedAudioTrackLanguage = "";
        String audioTrackLanguage = season.getAudioTrackLanguage();
        String defaultAudioTrackLanguage = Locale.forLanguageTag(Configuration.loadConfig("preferAudioLan", "es-ES")).getISO3Language();

        if (audioTrackLanguage.isEmpty())
            audioTrackLanguage = defaultAudioTrackLanguage;

        if (season.getSelectedAudioTrack() < audioTracks.size() && season.getSelectedAudioTrack() >= 0){
            for (AudioTrack aTrack : audioTracks)
                aTrack.setSelected(false);

            selectedAudioTrackLanguage = audioTrackLanguage;
            selectedAudioTrack = season.getSelectedAudioTrack();
            audioTracks.get(season.getSelectedAudioTrack()).setSelected(true);
        }else{
            if (audioTracks.size() > 1) {
                for (AudioTrack track : audioTracks) {
                    if (track.getLanguageTag().equals(audioTrackLanguage)) {
                        for (AudioTrack aTrack : audioTracks)
                            aTrack.setSelected(false);

                        track.setSelected(true);
                        selectedAudioTrackLanguage = track.getLanguageTag();
                        selectedAudioTrack = audioTracks.indexOf(track);
                        break;
                    }
                }
            }else if (!audioTracks.isEmpty()){
                selectedAudioTrackLanguage = audioTracks.getFirst().getLanguageTag();
                audioTracks.getFirst().setSelected(true);
            }
        }

        videoPlayer.setAudioTrack(selectedAudioTrack + 1);
        season.setAudioTrackLanguage(selectedAudioTrackLanguage);
        season.setSelectedAudioTrack(selectedAudioTrack);

        int subsMode = Integer.parseInt(Configuration.loadConfig("subsMode", "2"));
        String subtitleTrackLanguage = season.getSubtitleTrackLanguage();
        int selectedSubtitleTrack = -1;
        boolean foreignAudio = false;

        if (subtitleTrackLanguage.isEmpty())
            subtitleTrackLanguage = Locale.forLanguageTag(Configuration.loadConfig("preferSubsLan", "es-ES")).getISO3Language();

        if (!defaultAudioTrackLanguage.equals(selectedAudioTrackLanguage))
            foreignAudio = true;

        if (season.getSelectedSubtitleTrack() < subtitleTracks.size() && season.getSelectedSubtitleTrack() >= 0) {
            for (SubtitleTrack sTrack : subtitleTracks)
                sTrack.setSelected(false);

            selectedSubtitleTrack = season.getSelectedSubtitleTrack();
            subtitleTracks.get(selectedSubtitleTrack).setSelected(true);
        }else if (((subsMode == 2 && foreignAudio) || subsMode == 3) && !subtitleTrackLanguage.isEmpty() && !subtitleTracks.isEmpty()){
            if (subtitleTracks.size() > 1){
                for (SubtitleTrack track : subtitleTracks) {
                    if (track.getLanguageTag().equals(subtitleTrackLanguage)) {
                        for (SubtitleTrack sTrack : subtitleTracks)
                            sTrack.setSelected(false);

                        track.setSelected(true);

                        selectedSubtitleTrack = subtitleTracks.indexOf(track);
                        subtitleTracks.get(selectedSubtitleTrack).setSelected(true);
                        break;
                    }
                }
            }else {
                subtitleTracks.getFirst().setSelected(true);
                selectedSubtitleTrack = 0;
            }
        }else{
            videoPlayer.disableSubtitles();

            for (SubtitleTrack sTrack : subtitleTracks)
                sTrack.setSelected(false);
        }

        if (selectedSubtitleTrack >= 0){
            videoPlayer.setSubtitleTrack(selectedSubtitleTrack + 1);
            season.setAudioTrackLanguage(subtitleTrackLanguage);
            season.setSelectedAudioTrack(selectedSubtitleTrack);
        }

        audiosButton.setDisable(false);
        subtitlesButton.setDisable(false);
        videoButton.setDisable(false);

        if (audioTracks.isEmpty())
            audiosButton.setDisable(true);

        if (subtitleTracks.isEmpty())
            subtitlesButton.setDisable(true);
    }
    private void addInteractionSound(Button btn){
        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                playInteractionSound();
        });
    }
    public void startCount(){
        Task<Void> generateMetadataTask = new Task<>() {
            @Override
            protected Void call() {
                getMediaInfo(episode);
                return null;
            }
        };

        generateMetadataTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                videoTracks = episode.getVideoTracks();
                audioTracks = episode.getAudioTracks();
                subtitleTracks = episode.getSubtitleTracks();

                chaptersButton.setDisable(episode.getChapters().isEmpty());

                loadTracks();

                runtimeTimeline.getKeyFrames().setAll(new KeyFrame(Duration.seconds(1), event ->{
                    currentTimeSeconds++;

                    updateSliderValue();

                    runtimeTimeline.playFromStart();
                }));

                currentTimeSeconds = episode.getTimeWatched();

                runtimeTimeline.play();
                runtimeSlider.setValue(currentTimeSeconds);
                videoPlayer.seekToTime(currentTimeSeconds);
            });
        });

        App.executor.submit(generateMetadataTask);
    }
    private void updateSliderValue(){
        currentTime.setText(formatTime(currentTimeSeconds));
        toFinishTime.setText(formatTime((int) (runtimeSlider.getMax() - currentTimeSeconds)));

        if (!movingSlider)
            runtimeSlider.setValue(currentTimeSeconds);

        if (runtimeSlider.getValue() >= runtimeSlider.getMax())
            nextEpisode();
    }
    //endregion

    //region CHAPTERS
    @FXML
    void showChapters(){
        timeline.stop();
        if (!videoPlayer.isPaused())
            pause();

        movingSlider = true;
        showSlider();

        fadeInEffect(chapterPane, 0.2f);
        showChapterButtons();
    }
    private void showChapterButtons(){
        chapterContainer.getChildren().clear();
        for (Chapter chapter : episode.getChapters()){
            Button chapterButton = new Button("");
            chapterButton.getStyleClass().clear();
            chapterButton.getStyleClass().add("playerOptionsButton");

            chapterButton.setGraphic(new TextFlow(
                    new Text() {{
                        setText(chapter.getTitle() + " - ");
                        setFill(Color.WHITE);
                        setFont(Font.font("Roboto", FontWeight.BOLD, 18));
                    }},
                    new Text() {{
                        setText(chapter.getDisplayTime());
                        setFill(Color.WHITE);
                        setFont(Font.font("Roboto", FontWeight.BOLD, 16));
                    }}
            ));

            chapterButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
                TextFlow textFlow = (TextFlow) chapterButton.getGraphic();
                if (newVal) {
                    for (Node node : textFlow.getChildren())
                        ((Text) node).setFill(Color.BLACK);

                    playInteractionSound();
                }else{
                    for (Node node : textFlow.getChildren())
                        ((Text) node).setFill(Color.WHITE);
                }
            });

            chapterButton.setAlignment(Pos.CENTER_LEFT);
            chapterButton.setMaxWidth(Integer.MAX_VALUE);
            chapterButton.setPrefWidth(Integer.MAX_VALUE);
            chapterButton.setPadding(new Insets(30));

            chapterContainer.getChildren().add(chapterButton);

            chapterButton.setOnMouseClicked(e -> {
                if (chapterButton.isFocused()){
                    runtimeSlider.setValue((long) (episode.getChapters().get(chapterContainer.getChildren().indexOf(chapterButton)).getTime()));
                }else{
                    chapterButton.requestFocus();
                }
            });

            chapterButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event)){
                    runtimeSlider.setValue((long) (episode.getChapters().get(chapterContainer.getChildren().indexOf(chapterButton)).getTime()));
                }
            });

            chapterButton.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) ->{
                if (App.pressedUp(event))
                    playButton.requestFocus();

                int index = chapterContainer.getChildren().indexOf(chapterButton);
                if (App.pressedLeft(event)){
                    if (index > 0)
                        chapterContainer.getChildren().get(index - 1).requestFocus();
                }else if (App.pressedRight(event)){
                    if (index < chapterContainer.getChildren().size() - 1)
                        chapterContainer.getChildren().get(index + 1).requestFocus();
                }else if (App.pressedBack(event)){
                    hideChaptersPane();
                }
            });
        }

        chapterContainer.getChildren().getFirst().requestFocus();
        chapterScroll.setVvalue(0);
    }

    private void hideChaptersPane(){
        movingSlider = false;
        timeline.playFromStart();
        resume();
    }
    //endregion

    //region BEHAVIOR AND EFFECTS
    private void toggleFullscreen(){
        fullscreen = !fullscreen;

        if (fullscreen){
            ((ImageView) fullScreenButton.getGraphic()).setImage(new Image(getFileAsIOStream(
                    "img/icons/PantallaCompletaSalida.png"),
                    30, 30, true, true));
        }else{
            ((ImageView) fullScreenButton.getGraphic()).setImage(new Image(getFileAsIOStream(
                    "img/icons/PantallaCompleta.png"),
                    30, 30, true, true));
        }

        videoStage.setFullScreen(fullscreen);
        controlsStage.requestFocus();
    }
    public void hideOptions(){
        optionsBox.setVisible(false);

        if (!controlsShown)
            showControls();
    }
    private void showControls(){
        timeline.playFromStart();

        optionsBox.setVisible(false);
        chapterPane.setVisible(false);
        adjustmentPane.setVisible(false);

        buttonsBox.setVisible(true);
        fadeInEffect(shadowImage, 0.2f).play();

        FadeTransition fadeIn = fadeInEffect(controlsBox, 0.2f);
        fadeIn.setOnFinished(e -> controlsShown = true);
        fadeIn.play();

        closeButton.setVisible(true);
        playButton.requestFocus();
    }
    private void hideControls(){
        hideMouse();

        timeline.stop();

        if (shadowImage.isVisible())
            fadeOutEffect(shadowImage, 0.2f);

        FadeTransition fadeOut = fadeOutEffect(controlsBox, 0.2f, 0);
        fadeOut.setOnFinished(e -> {
            controlsBox.setVisible(false);
            controlsShown = false;
        });
        fadeOut.play();
    }
    private void showSlider(){
        timeline.stop();

        if (shadowImage.isVisible())
            fadeOutEffect(shadowImage, 0.2f);

        buttonsBox.setVisible(false);
        closeButton.setVisible(false);

        if (!controlsBox.isVisible())
            fadeInEffect(controlsBox, 0.2f);

        adjustmentPane.setVisible(false);
        optionsBox.setVisible(false);
    }
    private void restoreControlsButtons(){
        runtimeSlider.setVisible(true);
        currentTime.setVisible(true);
        toFinishTime.setVisible(true);
        seriesTitle.setVisible(true);
        episodeInfo.setVisible(true);

        if (parentController == null) {
            fullScreenButton.setVisible(true);
            desktopVolumeSliderBox.setVisible(true);
        }

        closeButton.setVisible(false);
        videoButton.setVisible(true);
        audiosButton.setVisible(true);
        subtitlesButton.setVisible(true);
        playButton.setVisible(true);
        prevButton.setVisible(true);
        nextButton.setVisible(true);

        if (!episode.getChapters().isEmpty())
            chaptersButton.setVisible(true);
    }
    //endregion

    //region VIDEO CONTROLS
    public void stop() {
        checkTimeWatched();

        if (timeline != null)
            timeline.stop();
        if (touchTimeline != null)
            touchTimeline.stop();
        if (volumeCount != null)
            volumeCount.stop();
        if (runtimeTimeline != null)
            runtimeTimeline.stop();
        if (hideMouse != null)
            hideMouse.stop();
        if (hideSlider != null)
            hideSlider.stop();

        videoStage.setFullScreen(false);

        processCurrentlyWatching(App.getSelectedSeries(), season, episode);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.7), controlsBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            videoPlayer.stop();

            if (parentController != null)
                parentController.stopVideo();
            else
                parentControllerDesktop.stopPlayer();

            controlsStage.close();
        });

        fadeOut.play();
    }
    public void resume(){
        videoPlayer.togglePause(false);
        runtimeTimeline.play();

        if (controlsShown)
            playButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/play.png"), 30, 30, true, true)));

        hideControls();
    }
    public void pause(){
        videoPlayer.togglePause(true);
        runtimeTimeline.pause();

        if (controlsShown)
            playButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/playSelected.png"), 30, 30, true, true)));
    }
    public void volumeUp(){
        volumeCount.playFromStart();
        volumeBox.setVisible(true);

        videoPlayer.adjustVolume(true);
        volumeSlider.setValue(videoPlayer.getVolume());
        volumeSlider2.setValue(videoPlayer.getVolume());
    }
    public void volumeDown(){
        volumeCount.playFromStart();
        volumeBox.setVisible(true);

        videoPlayer.adjustVolume(false);
        volumeSlider.setValue(videoPlayer.getVolume());
        volumeSlider2.setValue(videoPlayer.getVolume());
    }
    public void videoVolume(double volume){
        videoPlayer.adjustVolume(volume);
        volumeSlider.setValue(videoPlayer.getVolume());

        if (volume == 0)
            volumeImage.setImage(new Image(getFileAsIOStream("img/icons/audioMute.png"), 33,
                    33, true, true));
        else if (volume > 0 && volume < 40)
            volumeImage.setImage(new Image(getFileAsIOStream("img/icons/audio.png"), 33,
                    33, true, true));
        else if (volume > 40 && volume < 80)
            volumeImage.setImage(new Image(getFileAsIOStream("img/icons/audioMid.png"), 33,
                    33, true, true));
        else
            volumeImage.setImage(new Image(getFileAsIOStream("img/icons/maxVolume.png"), 33,
                    33, true, true));
    }
    public void goAhead(){
        if (hideSlider != null)
            hideSlider.stop();

        timeline.pause();
        movingSlider = true;
        runtimeSlider.setValue(runtimeSlider.getValue() + 10);

        if (!controlsShown){
            showControls();
            showSlider();
        }

        hideSlider = new PauseTransition(Duration.seconds(3));
        hideSlider.setOnFinished(e -> {
            timeline.playFromStart();
            movingSlider = false;
            hideControls();
        });
        hideSlider.play();
    }
    public void goBack(){
        if (hideSlider != null)
            hideSlider.stop();

        timeline.pause();
        movingSlider = true;
        runtimeSlider.setValue(runtimeSlider.getValue() - 5);

        if (!controlsShown){
            showControls();
            showSlider();
        }

        hideSlider = new PauseTransition(Duration.seconds(3));
        hideSlider.setOnFinished(e -> {
            timeline.playFromStart();
            movingSlider = false;
            hideControls();
        });
        hideSlider.play();
    }
    public void seekForward(){
        videoPlayer.seekForward();

        fadeInOutEffect(rightTouchButton);
    }
    public void seekBackward(){
        videoPlayer.seekBackward();

        fadeInOutEffect(leftTouchButton);
    }
    private void fadeInOutEffect(Button btn) {
        btn.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), btn);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), btn);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.play();
        });

        fadeIn.play();
    }
    private void checkTimeWatched(){
        Episode episode = episodeList.get(currentDisc);
        episode.setTimeWatched((int) runtimeSlider.getValue());
    }
    public void nextEpisode(){
        if (currentDisc + 1 > episodeList.size() - 1){
            stop();
        }else{
            Episode episode = episodeList.get(currentDisc);
            episode.setTimeWatched((int) runtimeSlider.getValue());

            checkTimeWatched();

            videoPlayer.stop();

            setDiscValues(episodeList.get(++currentDisc));
        }
    }
    public void prevEpisode(){
        if (runtimeSlider.getValue() > 2){
            runtimeSlider.setValue(0);
        }else{
            if (currentDisc > 0) {
                Episode episode = episodeList.get(currentDisc);
                episode.setTimeWatched((int) runtimeSlider.getValue());

                checkTimeWatched();

                videoPlayer.stop();

                setDiscValues(episodeList.get(--currentDisc));
            }else
                runtimeSlider.setValue(0);
        }
    }
    //endregion

    //region OPTIONS MENU
    @FXML
    private void showOptions(){
        showMenu(false, App.buttonsBundle.getString("settings"));

        if (leftOptions.getChildren().isEmpty()){
            Button video = addSimpleButton(App.textBundle.getString("video"), false, false);
            Button audio = addSimpleButton(App.textBundle.getString("audio"), false, false);
            Button subs = addSimpleButton(App.textBundle.getString("subs"), false, false);

            leftOptions.getChildren().addAll(video, audio, subs);

            video.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    loadVideoButtons(video);
                    setOnHover(video, false, false);
                    setSelectedImage(video, false);
                }else {
                    setOnExitHover(video, false, false);
                    removeSelectedImage(video, false);
                }
            });

            video.setOnKeyPressed(e -> {
                if (App.pressedSelect(e) || App.pressedRight(e))
                    centerOptions.getChildren().getFirst().requestFocus();
                else if (App.pressedDown(e))
                    audio.requestFocus();
            });

            video.setOnMouseClicked(e -> {
                loadVideoButtons(video);
                centerOptions.getChildren().getFirst().requestFocus();
                smoothScrollToHvalue(mainMenuScroll, 0);
            });

            audio.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    loadAudioButtons(audio);
                    setOnHover(audio, false, false);
                    setSelectedImage(audio, false);
                }else {
                    setOnExitHover(audio, false, false);
                    removeSelectedImage(audio, false);
                }
            });

            audio.setOnKeyPressed(e -> {
                if (App.pressedSelect(e) || App.pressedRight(e))
                    centerOptions.getChildren().getFirst().requestFocus();
                else if (App.pressedUp(e))
                    video.requestFocus();
                else if (App.pressedDown(e))
                    subs.requestFocus();
            });

            audio.setOnMouseClicked(e -> {
                loadAudioButtons(audio);
                centerOptions.getChildren().getFirst().requestFocus();
                smoothScrollToHvalue(mainMenuScroll, 0);
            });

            subs.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    loadSubsButtons(subs);
                    setOnHover(subs, false, false);
                    setSelectedImage(subs, false);
                }else {
                    setOnExitHover(subs, false, false);
                    removeSelectedImage(subs, false);
                }
            });

            subs.setOnKeyPressed(e -> {
                if (App.pressedSelect(e) || App.pressedRight(e))
                    centerOptions.getChildren().getFirst().requestFocus();
                else if (App.pressedUp(e))
                    audio.requestFocus();
            });

            subs.setOnMouseClicked(e -> {
                loadSubsButtons(subs);
                centerOptions.getChildren().getFirst().requestFocus();
                smoothScrollToHvalue(mainMenuScroll, 0);
            });
        }

        leftOptions.getChildren().getFirst().requestFocus();
        mainMenuScroll.setHvalue(0);
    }
    private void loadVideoButtons(Button video){
        centerOptions.getChildren().clear();

        Button videoTracks = addSimpleButton(App.textBundle.getString("video"), false, false);
        Button zoomOptions = addSimpleButton(App.textBundle.getString("zoom"), false, false);
        Button gammaButton = addSimpleButton(App.buttonsBundle.getString("gamma"), false, false);

        if (episode.getVideoTracks().isEmpty())
            hideButton(videoTracks);

        centerOptions.getChildren().addAll(videoTracks, zoomOptions, gammaButton);

        Label selectedVideoText = (Label) ((HBox) ((BorderPane) videoTracks.getGraphic()).getRight()).getChildren().getFirst();
        Label selectedZoomText = (Label) ((HBox) ((BorderPane) zoomOptions.getGraphic()).getRight()).getChildren().getFirst();
        Label gammaValueText = (Label) ((HBox) ((BorderPane) gammaButton.getGraphic()).getRight()).getChildren().getFirst();

        if (episode.getVideoTracks().size() > 1){
            for (VideoTrack videoTrack : episode.getVideoTracks()){
                if (videoTrack.isSelected()){
                    selectedVideoText.setText(videoTrack.getDisplayTitle());
                    break;
                }
            }
        }else if (!episode.getVideoTracks().isEmpty()){
            selectedVideoText.setText(episode.getVideoTracks().getFirst().getDisplayTitle());
        }

        Series series = App.getSelectedSeries();
        if (series.getVideoZoom() == 0.5){
            selectedZoomText.setText("Zoom");
        }else{
            selectedZoomText.setText("Normal");
        }

        gammaValueText.setText(videoPlayer.getGamma());

        setCenterButtonFocusAction(videoTracks);

        videoTracks.setOnMouseClicked(e -> {
            if (mainMenuScroll.getHvalue() == mainMenuScroll.getHmax()){
                smoothScrollToHvalue(mainMenuScroll, 0);
                video.requestFocus();
            }else{
                showVideoTracks(videoTracks, selectedVideoText);
                smoothScrollToHvalue(mainMenuScroll, mainMenuScroll.getHmax());
                optionsContainer.getChildren().getFirst().requestFocus();
            }
        });

        videoTracks.setOnKeyPressed(e -> {
            if (App.pressedSelect(e) || App.pressedRight(e)){
                showVideoTracks(videoTracks, selectedVideoText);
                smoothScrollToHvalue(mainMenuScroll, mainMenuScroll.getHmax());
                optionsContainer.getChildren().getFirst().requestFocus();
            }else if (App.pressedLeft(e)) {
                video.requestFocus();
            }else if (App.pressedDown(e))
                zoomOptions.requestFocus();
        });

        setCenterButtonFocusAction(zoomOptions);

        zoomOptions.setOnMouseClicked(e -> {
            if (mainMenuScroll.getHvalue() == mainMenuScroll.getHmax()){
                smoothScrollToHvalue(mainMenuScroll, 0);
                video.requestFocus();
            }else{
                showZoomOptions(zoomOptions, selectedZoomText);
                smoothScrollToHvalue(mainMenuScroll, mainMenuScroll.getHmax());
                optionsContainer.getChildren().getFirst().requestFocus();
            }
        });

        zoomOptions.setOnKeyPressed(e -> {
            if (App.pressedSelect(e) || App.pressedRight(e)){
                showZoomOptions(zoomOptions, selectedZoomText);
                smoothScrollToHvalue(mainMenuScroll, mainMenuScroll.getHmax());
                optionsContainer.getChildren().getFirst().requestFocus();
            }else if (App.pressedLeft(e)) {
                video.requestFocus();
            }else if (App.pressedDown(e))
                gammaButton.requestFocus();
            else if (App.pressedUp(e))
                videoTracks.requestFocus();
        });

        gammaButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                setOnHover(gammaButton, false, false);
            }else {
                setOnExitHover(gammaButton, false, false);
            }
        });

        gammaButton.setOnMouseClicked(e -> adjustGamma());

        gammaButton.setOnKeyPressed(e -> {
            if (App.pressedSelect(e) || App.pressedRight(e))
                adjustGamma();
            else if (App.pressedLeft(e)) {
                video.requestFocus();
            }else if (App.pressedUp(e))
                zoomOptions.requestFocus();
        });
    }
    private void setCenterButtonFocusAction(Button btn) {
        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                setOnHover(btn, false, false);
                setSelectedImage(btn, false);
                smoothScrollToHvalue(mainMenuScroll, 0);
            }else {
                setOnExitHover(btn, false, false);
                removeSelectedImage(btn, false);
            }
        });
    }
    private void adjustGamma(){
        hideControls();
        fadeOutEffect(optionsBox, 0.2f);
        fadeInEffect(adjustmentPane, 0.2f);

        videoPlayer.togglePause(false);

        adjustmentText.setText(App.textBundle.getString("gammaAdjustment") + " " + videoPlayer.getGamma());

        downAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setGamma(Double.parseDouble(videoPlayer.getGamma()) - 1);
            adjustmentText.setText(App.textBundle.getString("gammaAdjustment") + " " + videoPlayer.getGamma());
        });
        downAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setGamma(Double.parseDouble(videoPlayer.getGamma()) - 1);
                adjustmentText.setText(App.textBundle.getString("gammaAdjustment") + " " + videoPlayer.getGamma());
            }else if (App.pressedRight(e))
                upAdjustment.requestFocus();
        });
        downAdjustment.setOnKeyReleased(e -> {
            if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        upAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setGamma(Double.parseDouble(videoPlayer.getGamma()) + 1);
            adjustmentText.setText(App.textBundle.getString("gammaAdjustment") + " " + videoPlayer.getGamma());
        });
        upAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setGamma(Double.parseDouble(videoPlayer.getGamma()) + 1);
                adjustmentText.setText(App.textBundle.getString("gammaAdjustment") + " " + videoPlayer.getGamma());
            }else if (App.pressedRight(e))
                restartAdjustment.requestFocus();
            else if (App.pressedLeft(e))
                downAdjustment.requestFocus();
        });
        upAdjustment.setOnKeyReleased(e -> {
            if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        restartAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setGamma(0);
            adjustmentText.setText(App.textBundle.getString("gammaAdjustment") + " " + videoPlayer.getGamma());
        });
        restartAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setGamma(0);
                adjustmentText.setText(App.textBundle.getString("gammaAdjustment") + " " + videoPlayer.getGamma());
            }else if (App.pressedLeft(e))
                upAdjustment.requestFocus();
        });
        restartAdjustment.setOnKeyReleased(e -> {
            if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        upAdjustment.requestFocus();
    }
    private void showVideoTracks(Button videoButton, Label selectedVideoText){
        optionsContainer.getChildren().clear();
        for (VideoTrack track : videoTracks){
            Button btn = addSimpleButton(track.getDisplayTitle(), true, false);
            btn.setFocusTraversable(false);
            optionsContainer.getChildren().add(btn);

            if (track.isSelected()) {
                setSelectedImage(btn, true);
                selectedVideoText.setText(track.getDisplayTitle());
            }

            btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal)
                    setOnHover(btn, true, false);
                else
                    setOnExitHover(btn, true, false);
            });

            btn.setOnMouseClicked(e -> {
                if (!track.isSelected())
                    videoButtonAction(track, btn, selectedVideoText);
            });
            btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event) || App.pressedRight(event) && !track.isSelected())
                    videoButtonAction(track, btn, selectedVideoText);
                else tracksInMenuMovement(btn, event, videoButton);
            });
        }
    }
    private void videoButtonAction(VideoTrack track, Button btn, Label selectedVideoText){
        videoPlayer.setVideoTrack(videoTracks.indexOf(track) + 1);

        for (Node node : optionsContainer.getChildren())
            removeSelectedImage((Button) node, true);

        setSelectedImage(btn, true);
        selectedVideoText.setText(track.getDisplayTitle());
    }
    private void showZoomOptions(Button zoomOptions, Label text){
        optionsContainer.getChildren().clear();
        Series series = App.getSelectedSeries();

        Button normalZoom = addSimpleButton("Normal", true, false);
        normalZoom.setFocusTraversable(false);
        optionsContainer.getChildren().add(normalZoom);

        Button zoomButton = addSimpleButton("Zoom", true, false);
        zoomButton.setFocusTraversable(false);
        optionsContainer.getChildren().add(zoomButton);

        if (series.getVideoZoom() == 0.5)
            setSelectedImage(zoomButton, true);
        else
            setSelectedImage(normalZoom, true);

        normalZoom.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                setOnHover(normalZoom, true, false);
            else
                setOnExitHover(normalZoom, true, false);
        });
        normalZoom.setOnMouseClicked(e -> {
            zoomButtonAction(series, normalZoom, 0, 0.7f, 100, 1);
            text.setText("Normal");
            setSelectedImage(normalZoom, true);
            removeSelectedImage(zoomButton, true);
        });
        normalZoom.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                zoomButtonAction(series, normalZoom, 0, 0.7f, 100, 1);
                text.setText("Normal");
                setSelectedImage(normalZoom, true);
                removeSelectedImage(zoomButton, true);
            }else if (App.pressedDown(event)){
                zoomButton.requestFocus();
            }else if (App.pressedLeft(event)) {
                zoomOptions.requestFocus();
            }
        });

        zoomButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                setOnHover(zoomButton, true, false);
            else
                setOnExitHover(zoomButton, true, false);
        });
        zoomButton.setOnMouseClicked(e -> {
            zoomButtonAction(series, zoomButton, 0.5f, 0.9f, 90, 0);
            text.setText("Zoom");
            setSelectedImage(zoomButton, true);
            removeSelectedImage(normalZoom, true);
        });
        zoomButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                zoomButtonAction(series, zoomButton, 0.5f, 0.9f, 90, 0);
                text.setText("Zoom");
                setSelectedImage(zoomButton, true);
                removeSelectedImage(normalZoom, true);
            }else if (App.pressedUp(event)){
                normalZoom.requestFocus();
            }else if (App.pressedLeft(event)) {
                zoomOptions.requestFocus();
            }
        });
    }
    private void zoomButtonAction(Series series, Button btn, float zoom, float subSize, int position, int index){
        videoPlayer.fixZoom(zoom);
        videoPlayer.setSubtitleVerticalPosition(position);
        videoPlayer.setSubtitleSize(subSize);
        series.setVideoZoom(zoom);

        setSelectedImage(btn, true);

        Button b1 = (Button) optionsContainer.getChildren().get(index);
        removeSelectedImage(b1, true);
    }
    private void loadAudioButtons(Button audio){
        centerOptions.getChildren().clear();

        Button audioTracks = addSimpleButton(App.textBundle.getString("languageText"), false, false);
        Button audioDelay = addSimpleButton(App.buttonsBundle.getString("audioDelay"), false, false);

        if (episode.getAudioTracks().isEmpty())
            hideButton(audioTracks);

        centerOptions.getChildren().addAll(audioTracks, audioDelay);

        Label selectedAudioText = (Label) ((HBox) ((BorderPane) audioTracks.getGraphic()).getRight()).getChildren().getFirst();
        Label delayValueText = (Label) ((HBox) ((BorderPane) audioDelay.getGraphic()).getRight()).getChildren().getFirst();

        if (episode.getAudioTracks().size() > 1){
            for (AudioTrack track : episode.getAudioTracks()){
                if (track.isSelected()){
                    selectedAudioText.setText(track.getDisplayTitle());
                    break;
                }
            }
        }else if (!episode.getAudioTracks().isEmpty()){
            selectedAudioText.setText(episode.getAudioTracks().getFirst().getDisplayTitle());
        }

        delayValueText.setText(Double.parseDouble(videoPlayer.getAudioDelay()) * 1000 + " ms");

        setCenterButtonFocusAction(audioTracks);

        audioTracks.setOnMouseClicked(e -> {
            if (mainMenuScroll.getHvalue() == mainMenuScroll.getHmax()){
                smoothScrollToHvalue(mainMenuScroll, 0);
                audio.requestFocus();
            }else{
                showAudioTracks(audioTracks, selectedAudioText);
                smoothScrollToHvalue(mainMenuScroll, mainMenuScroll.getHmax());
                optionsContainer.getChildren().getFirst().requestFocus();
            }
        });

        audioTracks.setOnKeyPressed(e -> {
            if (App.pressedSelect(e) || App.pressedRight(e)){
                showAudioTracks(audioTracks, selectedAudioText);
                smoothScrollToHvalue(mainMenuScroll, mainMenuScroll.getHmax());
                optionsContainer.getChildren().getFirst().requestFocus();
            }else if (App.pressedLeft(e)) {
                audio.requestFocus();
            }else if (App.pressedDown(e))
                audioDelay.requestFocus();
        });

        audioDelay.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                setOnHover(audioDelay, false, false);
            }else {
                setOnExitHover(audioDelay, false, false);
            }
        });

        audioDelay.setOnMouseClicked(e -> adjustAudioDelay());

        audioDelay.setOnKeyPressed(e -> {
            if (App.pressedSelect(e) || App.pressedRight(e))
                adjustAudioDelay();
            else if (App.pressedLeft(e)) {
                audio.requestFocus();
            }else if (App.pressedUp(e))
                audioTracks.requestFocus();
        });
    }
    private void showMenu(Boolean isSimple, String title){
        if (!videoPlayer.isPaused())
            pause();

        timeline.stop();

        adjustmentPane.setVisible(false);
        mainMenuScroll.setVisible(!isSimple);
        simpleMenuScroll.setVisible(isSimple);

        fadeInEffect(optionsBox, 0.2f);

        optionsTitle.setText(title);
    }
    public void showAudioOptions(){
        showMenu(true, App.textBundle.getString("audio"));
        showAudioTracks();
    }
    private void showAudioTracks(){
        simpleMenu.getChildren().clear();
        for (AudioTrack track : audioTracks){
            Button trackButton = addSimpleButton(track.getDisplayTitle(), true, true);
            simpleMenu.getChildren().add(trackButton);

            trackButton.setOnMouseClicked(e -> {
                if (!track.isSelected())
                    addAudioButtonAction(track, trackButton);
            });
            trackButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event) && !track.isSelected())
                    addAudioButtonAction(track, trackButton);
            });
            trackButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal)
                    setOnHover(trackButton, true, true);
                else
                    setOnExitHover(trackButton, true, true);
            });
            trackButton.setOnMouseEntered(e -> setOnHover(trackButton, true, true));
            trackButton.setOnMouseExited(e -> setOnExitHover(trackButton, true, true));

            if (track.isSelected()){
                setSelectedImage(trackButton, true);
                trackButton.requestFocus();
            }
        }
    }
    private void showAudioTracks(Button audiosButton, Label text){
        optionsContainer.getChildren().clear();
        for (AudioTrack track : audioTracks){
            Button btn = addSimpleButton(track.getDisplayTitle(), true, false);
            btn.setFocusTraversable(false);
            optionsContainer.getChildren().add(btn);

            if (track.isSelected()) {
                setSelectedImage(btn, true);
                text.setText(track.getDisplayTitle());
            }

            btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    ensureNodeVisible(btn);
                    setOnHover(btn, true, false);
                }else
                    setOnExitHover(btn, true, false);
            });

            btn.setOnMouseClicked(e -> {
                if (!track.isSelected())
                    audioButtonAction(track, btn, text);
            });
            btn.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) ->{
                if (App.pressedSelect(event) || App.pressedRight(event) && !track.isSelected())
                    audioButtonAction(track, btn, text);
                else {
                    tracksInMenuMovement(btn, event, audiosButton);
                }
            });
        }
    }
    private void audioButtonAction(AudioTrack track, Button btn, Label selectedAudioText){
        playInteractionSound();

        for (AudioTrack aTrack : audioTracks)
            aTrack.setSelected(aTrack == track);

        videoPlayer.setAudioTrack(audioTracks.indexOf(track) + 1);

        for (Node node : optionsContainer.getChildren())
            removeSelectedImage((Button) node, true);

        setSelectedImage(btn, true);
        selectedAudioText.setText(track.getDisplayTitle());
    }
    private void tracksInMenuMovement(Button btn, KeyEvent event, Button parentButton) {
        if (App.pressedDown(event)){
            if (optionsContainer.getChildren().indexOf(btn) < optionsContainer.getChildren().size() - 1)
                optionsContainer.getChildren().get(optionsContainer.getChildren().indexOf(btn) + 1).requestFocus();
        }else if (App.pressedUp(event)){
            if (optionsContainer.getChildren().indexOf(btn) > 0)
                optionsContainer.getChildren().get(optionsContainer.getChildren().indexOf(btn) - 1).requestFocus();
        }else if (App.pressedLeft(event)) {
            parentButton.requestFocus();
        }
    }
    private void addAudioButtonAction(AudioTrack track, Button trackButton) {
        playInteractionSound();

        for (AudioTrack aTrack : audioTracks)
            aTrack.setSelected(aTrack == track);

        videoPlayer.setAudioTrack(audioTracks.indexOf(track) + 1);
        season.setSelectedAudioTrack(audioTracks.indexOf(track));
        season.setAudioTrackLanguage(track.getLanguageTag());

        for (Node node : simpleMenu.getChildren())
            removeSelectedImage((Button) node, true);

        setSelectedImage(trackButton, true);
    }
    private void adjustAudioDelay(){
        hideControls();
        fadeOutEffect(optionsBox, 0.2f);
        fadeInEffect(adjustmentPane, 0.2f);

        videoPlayer.togglePause(false);

        adjustmentText.setText(App.buttonsBundle.getString("audioDelay") + ": " + videoPlayer.getAudioDelay() + " ms");

        downAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setAudioDelay(Double.parseDouble(videoPlayer.getAudioDelay()) - 0.001);
            adjustmentText.setText(App.buttonsBundle.getString("audioDelay") + ": " + videoPlayer.getAudioDelay() + " ms");
        });
        downAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setAudioDelay(Double.parseDouble(videoPlayer.getAudioDelay()) - 0.001);
                adjustmentText.setText(App.buttonsBundle.getString("audioDelay") + ": " + videoPlayer.getAudioDelay() + " ms");
            }else if (App.pressedRight(e))
                upAdjustment.requestFocus();
        });
        downAdjustment.setOnKeyReleased(e -> {
            if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        upAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setAudioDelay(Double.parseDouble(videoPlayer.getAudioDelay()) + 0.001);
            adjustmentText.setText(App.buttonsBundle.getString("audioDelay") + ": " + videoPlayer.getAudioDelay() + " ms");
        });
        upAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setAudioDelay(Double.parseDouble(videoPlayer.getAudioDelay()) + 0.001);
                adjustmentText.setText(App.buttonsBundle.getString("audioDelay") + ": " + videoPlayer.getAudioDelay() + " ms");
            }else if (App.pressedRight(e))
                restartAdjustment.requestFocus();
            else if (App.pressedLeft(e))
                downAdjustment.requestFocus();
        });
        upAdjustment.setOnKeyReleased(e -> {
            if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        restartAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setAudioDelay(0);
            adjustmentText.setText(App.buttonsBundle.getString("audioDelay") + ": " + videoPlayer.getAudioDelay() + " ms");
        });
        restartAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setAudioDelay(0);
                adjustmentText.setText(App.buttonsBundle.getString("audioDelay") + ": " + videoPlayer.getAudioDelay() + " ms");
            }else if (App.pressedLeft(e))
                upAdjustment.requestFocus();
        });
        restartAdjustment.setOnKeyReleased(e -> {
            if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        upAdjustment.requestFocus();
    }
    private void loadSubsButtons(Button subs){
        centerOptions.getChildren().clear();

        Button subsTracks = addSimpleButton(App.textBundle.getString("languageText"), false, false);
        Button subsDelay = addSimpleButton(App.buttonsBundle.getString("subsDelay"), false, false);

        if (episode.getSubtitleTracks().isEmpty())
            hideButton(subsTracks);

        centerOptions.getChildren().addAll(subsTracks, subsDelay);

        Label selectedSubsText = (Label) ((HBox) ((BorderPane) subsTracks.getGraphic()).getRight()).getChildren().getFirst();
        Label delayValueText = (Label) ((HBox) ((BorderPane) subsDelay.getGraphic()).getRight()).getChildren().getFirst();

        if (episode.getSubtitleTracks().size() > 1){
            for (SubtitleTrack track : episode.getSubtitleTracks()){
                if (track.isSelected()){
                    selectedSubsText.setText(track.getDisplayTitle());
                    break;
                }
            }
        }else if (!episode.getSubtitleTracks().isEmpty()){
            selectedSubsText.setText(episode.getSubtitleTracks().getFirst().getDisplayTitle());
        }

        delayValueText.setText(Double.parseDouble(videoPlayer.getSubsDelay()) * 1000 + " ms");

        setCenterButtonFocusAction(subsTracks);

        subsTracks.setOnMouseClicked(e -> {
            if (mainMenuScroll.getHvalue() == mainMenuScroll.getHmax()){
                smoothScrollToHvalue(mainMenuScroll, 0);
                subs.requestFocus();
            }else{
                showSubtitleTracks(subsTracks, selectedSubsText);
                smoothScrollToHvalue(mainMenuScroll, mainMenuScroll.getHmax());
                optionsContainer.getChildren().getFirst().requestFocus();
            }
        });

        subsTracks.setOnKeyPressed(e -> {
            if (App.pressedSelect(e) || App.pressedRight(e)){
                showSubtitleTracks(subsTracks, selectedSubsText);
                smoothScrollToHvalue(mainMenuScroll, mainMenuScroll.getHmax());
                optionsContainer.getChildren().getFirst().requestFocus();
            }else if (App.pressedLeft(e)) {
                subs.requestFocus();
            }else if (App.pressedDown(e))
                subsDelay.requestFocus();
        });

        subsDelay.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                setOnHover(subsDelay, false, false);
            }else {
                setOnExitHover(subsDelay, false, false);
            }
        });

        subsDelay.setOnMouseClicked(e -> adjustSubsDelay());

        subsDelay.setOnKeyPressed(e -> {
            if (App.pressedSelect(e) || App.pressedRight(e))
                adjustSubsDelay();
            else if (App.pressedLeft(e)) {
                subs.requestFocus();
            }else if (App.pressedUp(e))
                subsTracks.requestFocus();
        });
    }
    private void showSubtitleTracks(Button subsButton, Label text){
        optionsContainer.getChildren().clear();

        //Add first button to disable subtitles
        Button noneButton = addSimpleButton(App.buttonsBundle.getString("none"), true, false);
        optionsContainer.getChildren().add(noneButton);

        noneButton.setOnMouseEntered(e -> setOnHover(noneButton, true, true));
        noneButton.setOnMouseExited(e -> setOnExitHover(noneButton, true, true));
        noneButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                setOnHover(noneButton, true, false);
            else
                setOnExitHover(noneButton, true, false);
        });
        noneButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                playInteractionSound();

                for (SubtitleTrack sTrack : subtitleTracks)
                    sTrack.setSelected(false);

                videoPlayer.disableSubtitles();
                season.setSelectedSubtitleTrack(-1);
                season.setSubtitleTrackLanguage("");

                for (Node node : optionsContainer.getChildren())
                    removeSelectedImage((Button) node, true);

                setSelectedImage(noneButton, true);
                text.setText("");
            }else{
                tracksInMenuMovement(noneButton, event, subsButton);
            }
        });

        if (season.getSubtitleTrackLanguage() == null || season.getSubtitleTrackLanguage().isEmpty()) {
            setSelectedImage(noneButton, true);

            videoPlayer.disableSubtitles();
            season.setSelectedSubtitleTrack(-1);
            season.setSubtitleTrackLanguage("");

            noneButton.requestFocus();
        }

        for (SubtitleTrack track : subtitleTracks){
            Button btn = addSimpleButton(track.getDisplayTitle(), true, false);
            btn.setFocusTraversable(false);
            optionsContainer.getChildren().add(btn);

            if (track.isSelected()) {
                setSelectedImage(btn, true);
                text.setText(track.getDisplayTitle());

                removeSelectedImage(btn, true);
            }

            btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    ensureNodeVisible(btn);
                    setOnHover(btn, true, false);
                }else
                    setOnExitHover(btn, true, false);
            });

            btn.setOnMouseClicked(e -> {
                if (!track.isSelected()) {
                    removeSelectedImage(btn, true);
                    subsButtonAction(track, btn, text);
                }
            });
            btn.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) ->{
                if (App.pressedSelect(event) || App.pressedRight(event) && !track.isSelected()) {
                    removeSelectedImage(btn, true);
                    subsButtonAction(track, btn, text);
                }else {
                    tracksInMenuMovement(btn, event, subsButton);
                }
            });
        }

        optionsContainer.getChildren().getFirst().requestFocus();
    }
    private void subsButtonAction(SubtitleTrack track, Button btn, Label text){
        playInteractionSound();

        for (SubtitleTrack sTrack : subtitleTracks)
            sTrack.setSelected(sTrack == track);

        videoPlayer.setSubtitleTrack(subtitleTracks.indexOf(track) + 1);

        for (Node node : optionsContainer.getChildren())
            removeSelectedImage((Button) node, true);

        setSelectedImage(btn, true);
        text.setText(track.getDisplayTitle());
    }
    private void adjustSubsDelay(){
        hideControls();
        fadeOutEffect(optionsBox, 0.2f);
        fadeInEffect(adjustmentPane, 0.2f);

        videoPlayer.togglePause(false);

        adjustmentText.setText(App.buttonsBundle.getString("subsDelay") + ": " + videoPlayer.getSubsDelay() + " ms");

        downAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setSubsDelay(Double.parseDouble(videoPlayer.getSubsDelay()) - 0.001);
            adjustmentText.setText(App.buttonsBundle.getString("subsDelay") + ": " + videoPlayer.getSubsDelay() + " ms");
        });
        downAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setSubsDelay(Double.parseDouble(videoPlayer.getSubsDelay()) - 0.001);
                adjustmentText.setText(App.buttonsBundle.getString("subsDelay") + ": " + videoPlayer.getSubsDelay() + " ms");
            }else if (App.pressedRight(e))
                upAdjustment.requestFocus();
            else if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        upAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setSubsDelay(Double.parseDouble(videoPlayer.getSubsDelay()) + 0.001);
            adjustmentText.setText(App.buttonsBundle.getString("subsDelay") + ": " + videoPlayer.getSubsDelay() + " ms");
        });
        upAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setSubsDelay(Double.parseDouble(videoPlayer.getSubsDelay()) + 0.001);
                adjustmentText.setText(App.buttonsBundle.getString("subsDelay") + ": " + videoPlayer.getSubsDelay() + " ms");
            }else if (App.pressedRight(e))
                restartAdjustment.requestFocus();
            else if (App.pressedLeft(e))
                downAdjustment.requestFocus();
            else if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        restartAdjustment.setOnMouseClicked(e -> {
            videoPlayer.setSubsDelay(0);
            adjustmentText.setText(App.buttonsBundle.getString("subsDelay") + ": " + videoPlayer.getSubsDelay() + " ms");
        });
        restartAdjustment.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                videoPlayer.setSubsDelay(0);
                adjustmentText.setText(App.buttonsBundle.getString("subsDelay") + ": " + videoPlayer.getSubsDelay() + " ms");
            }else if (App.pressedLeft(e))
                upAdjustment.requestFocus();
            else if (App.pressedBack(e))
                fadeOutEffect(adjustmentPane, 0.2f);
        });

        upAdjustment.requestFocus();
    }
    public void showSubtitleOptions(){
        showMenu(true, App.textBundle.getString("subs"));
        showSubtitleTracks();
    }
    private void showSubtitleTracks(){
        simpleMenu.getChildren().clear();

        //Add first button to disable subtitles
        Button noneButton = addSimpleButton(App.buttonsBundle.getString("none"), true, true);
        simpleMenu.getChildren().add(noneButton);

        noneButton.setOnMouseEntered(e -> setOnHover(noneButton, true, true));
        noneButton.setOnMouseExited(e -> setOnExitHover(noneButton, true, true));
        noneButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                setOnHover(noneButton, true, true);
            else
                setOnExitHover(noneButton, true, true);
        });
        noneButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                for (SubtitleTrack sTrack : subtitleTracks)
                    sTrack.setSelected(false);

                videoPlayer.disableSubtitles();
                season.setSelectedSubtitleTrack(-1);
                season.setSubtitleTrackLanguage("");

                for (Node node : simpleMenu.getChildren()){
                    setOnExitHover((Button) node, true, true);
                }

                setOnHover(noneButton, true, true);
            }
        });

        if (season.getSubtitleTrackLanguage() == null || season.getSubtitleTrackLanguage().isEmpty()) {
            setSelectedImage(noneButton, true);

            videoPlayer.disableSubtitles();
            season.setSelectedSubtitleTrack(-1);
            season.setSubtitleTrackLanguage("");

            noneButton.requestFocus();
        }

        for (SubtitleTrack track : subtitleTracks){
            Button trackButton = addSimpleButton(track.getDisplayTitle(), true, true);
            simpleMenu.getChildren().add(trackButton);

            trackButton.setOnMouseClicked(e -> {
                if (!track.isSelected())
                    addSubtitleButtonAction(track, trackButton, noneButton);
            });
            trackButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event) && !track.isSelected())
                    addSubtitleButtonAction(track, trackButton, noneButton);
            });
            trackButton.setOnMouseEntered(e -> setOnHover(trackButton, true, true));
            trackButton.setOnMouseExited(e -> setOnExitHover(trackButton, true, true));
            trackButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal)
                    setOnHover(trackButton, true, true);
                else
                    setOnExitHover(trackButton, true, true);
            });

            if (track.isSelected()) {
                setSelectedImage(trackButton, true);

                //Disable selection of button "None"
                removeSelectedImage(noneButton, true);

                trackButton.requestFocus();
            }
        }
    }
    private void addSubtitleButtonAction(SubtitleTrack track, Button trackButton, Button btn){
        playInteractionSound();

        for (SubtitleTrack sTrack : subtitleTracks)
            sTrack.setSelected(sTrack == track);

        videoPlayer.setSubtitleTrack(subtitleTracks.indexOf(track) + 1);
        season.setSelectedSubtitleTrack(subtitleTracks.indexOf(track));
        season.setSubtitleTrackLanguage(track.getLanguageTag());

        Series series = App.getSelectedSeries();
        if (series.getVideoZoom() == 0) {
            videoPlayer.setSubtitleSize(0.7);
            videoPlayer.setSubtitleVerticalPosition(100);
        }else{
            videoPlayer.setSubtitleSize(0.9);
            videoPlayer.setSubtitleVerticalPosition(90);
        }

        for (Node node : simpleMenu.getChildren())
            removeSelectedImage((Button) node, true);

        //Disable selection of button "None"
        removeSelectedImage(btn, true);

        //Select current button
        setSelectedImage(trackButton, true);
    }
    private void showSubtitleSize(){
        optionsContainer.getChildren().clear();

        Button small = addSimpleButton(App.buttonsBundle.getString("small"), true, false);
        Button normal = addSimpleButton(App.buttonsBundle.getString("normal"), true, false);
        Button large = addSimpleButton(App.buttonsBundle.getString("large"), true, false);

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

        small.setOnMouseClicked(e -> subtitleSizeButtonAction(small, 0.8f));
        small.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event))
                subtitleSizeButtonAction(small, 0.8f);
        });

        normal.setOnMouseClicked(e -> subtitleSizeButtonAction(normal, 0.9f));
        normal.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event))
                subtitleSizeButtonAction(normal, 0.9f);
        });

        large.setOnMouseClicked(e -> subtitleSizeButtonAction(large, 1f));
        large.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event))
                subtitleSizeButtonAction(large, 1f);
        });

        optionsContainer.getChildren().add(small);
        optionsContainer.getChildren().add(normal);
        optionsContainer.getChildren().add(large);
    }
    private void subtitleSizeButtonAction(Button small, float size) {
        videoPlayer.setSubtitleSize(size);

        for (Node node : optionsContainer.getChildren()){
            Button button = (Button) node;
            button.getStyleClass().clear();
            button.getStyleClass().add("playerOptionsButton");
        }

        small.getStyleClass().clear();
        small.getStyleClass().add("playerOptionsSelected");
    }
    public void ensureNodeVisible(Node node) {
        VBox vbox = (VBox) rightOptions.getContent();

        Bounds viewportBounds = rightOptions.getViewportBounds();
        Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
        Bounds vboxBounds = vbox.localToScene(vbox.getBoundsInLocal());

        double nodeMinY = nodeBounds.getMinY() - vboxBounds.getMinY();
        double nodeMaxY = nodeBounds.getMaxY() - vboxBounds.getMinY();

        double viewportHeight = viewportBounds.getHeight();

        if (nodeMinY < rightOptions.getVvalue() * (vbox.getHeight() - viewportHeight)) {
            rightOptions.setVvalue(nodeMinY / (vbox.getHeight() - viewportHeight));
        } else if (nodeMaxY > (rightOptions.getVvalue() * (vbox.getHeight() - viewportHeight)) + viewportHeight) {
            rightOptions.setVvalue((nodeMaxY - viewportHeight) / (vbox.getHeight() - viewportHeight));
        }
    }
    //endregion

    //region OPTION BUTTONS
    private void setOnHover(Button button, boolean isSimpleButton, boolean center){
        Label text;

        if (center)
            text = (Label) ((BorderPane) button.getGraphic()).getCenter();
        else
            text = (Label) ((BorderPane) button.getGraphic()).getLeft();

        text.setTextFill(Color.BLACK);

        if (isSimpleButton){
            StackPane stack = (StackPane) ((BorderPane) button.getGraphic()).getRight();

            ImageView img = (ImageView) stack.getChildren().getFirst();
            ImageView imgHover = (ImageView) stack.getChildren().getLast();

            img.setVisible(false);
            imgHover.setVisible(true);
        }else{
            HBox container = (HBox) ((BorderPane) button.getGraphic()).getRight();

            Label selectedText = (Label) container.getChildren().getFirst();
            selectedText.setTextFill(Color.BLACK);
        }
    }
    private void setOnExitHover(Button button, boolean isSimpleButton, boolean center){
        if (button.isFocused())
            return;

        Label text;

        if (center)
            text = (Label) ((BorderPane) button.getGraphic()).getCenter();
        else
            text = (Label) ((BorderPane) button.getGraphic()).getLeft();

        text.setTextFill(Color.WHITE);

        if (isSimpleButton){
            StackPane stack = (StackPane) ((BorderPane) button.getGraphic()).getRight();

            ImageView img = (ImageView) stack.getChildren().getFirst();
            ImageView imgHover = (ImageView) stack.getChildren().getLast();

            img.setVisible(true);
            imgHover.setVisible(false);
        }else{
            HBox container = (HBox) ((BorderPane) button.getGraphic()).getRight();

            Label selectedText = (Label) container.getChildren().getFirst();
            selectedText.setTextFill(Color.WHITE);
        }
    }
    private void setSelectedImage(Button button, boolean isSimpleButton){
        if (isSimpleButton){
            StackPane stack = (StackPane) ((BorderPane) button.getGraphic()).getRight();
            stack.setVisible(true);
        }else{
            HBox container = (HBox) ((BorderPane) button.getGraphic()).getRight();
            ImageView img = (ImageView) container.getChildren().getLast();
            img.setVisible(true);
        }
    }
    private void removeSelectedImage(Button button, boolean isSimpleButton){
        if (isSimpleButton){
            StackPane stack = (StackPane) ((BorderPane) button.getGraphic()).getRight();
            stack.setVisible(false);
        }else{
            HBox container = (HBox) ((BorderPane) button.getGraphic()).getRight();
            ImageView img = (ImageView) container.getChildren().getLast();
            img.setVisible(false);
        }
    }
    private void addOptionCard(String title){
        Button btn = addSimpleButton(title, true, false);
        optionsContainer.getChildren().add(btn);
    }
    private Button addSimpleButton(String name, boolean isSimpleButton, boolean alignCenter){
        Button btn = new Button("");

        if (!isSimpleButton)
            btn.setFocusTraversable(false);

        BorderPane content = new BorderPane();
        Label text = new Label(name);
        text.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, 30));
        text.setTextFill(Color.WHITE);

        int imgSize = 30;

        ImageView img = new ImageView();
        if (isSimpleButton){
            img.setImage(new Image(getFileAsIOStream("img/icons/trackSelected.png"), imgSize, imgSize, true, true));

            ImageView imgHover = new ImageView();
            imgHover.setImage(new Image(getFileAsIOStream("img/icons/trackSelectedHover.png"), imgSize, imgSize, true, true));
            imgHover.setVisible(false);

            StackPane imageStack = new StackPane(img, imgHover);
            imageStack.setVisible(false);

            content.setRight(imageStack);

            if (alignCenter)
                content.setCenter(text);
            else
                content.setLeft(text);
        }else{
            img.setImage(new Image(getFileAsIOStream("img/icons/rightArrowHover.png"), imgSize, imgSize, true, true));
            img.setVisible(false);

            Label selectedText = new Label("");
            selectedText.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, 28));
            selectedText.setTextFill(Color.WHITE);

            HBox pane = new HBox(selectedText, img);
            pane.setSpacing(10);
            content.setRight(pane);
            pane.setPadding(new Insets(3, 0, 0, 0));

            pane.setTranslateX(pane.getLayoutX() + 20);

            content.setLeft(text);
        }

        content.setPadding(new Insets(0, 10, 0, 10));

        btn.setGraphic(content);

        btn.setAlignment(Pos.CENTER);
        btn.setTextFill(Color.WHITE);
        btn.setMaxWidth(Integer.MAX_VALUE);
        btn.setPrefWidth(Integer.MAX_VALUE);
        btn.setPadding(new Insets(30));

        btn.getStyleClass().clear();
        btn.getStyleClass().add("playerOptionsButton");

        addInteractionSound(btn);

        return btn;
    }
    //endregion

    public VideoPlayer getVideoPlayer(){
        return videoPlayer;
    }
}
