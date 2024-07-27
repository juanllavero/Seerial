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
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.executablelauncher.utils.Utils.*;

public class VideoPlayerController {
    //region FXML ATTRIBUTES
    @FXML
    private Label chaptersTitle;

    @FXML
    private HBox chapterContainer;

    @FXML
    private HBox desktopVolumeSliderBox;

    @FXML
    private ScrollPane chapterScroll;

    @FXML
    private VBox leftTouchArea;

    @FXML
    private VBox rightTouchArea;

    @FXML
    private StackPane mainPane;

    @FXML
    private ScrollPane rightOptions;

    @FXML
    private Label currentTime;

    @FXML
    private Label episodeInfo;

    @FXML
    private Button leftTouchButton;

    @FXML
    private Button rightTouchButton;

    @FXML
    private Button audiosButton;

    @FXML
    private Button videoButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button fullScreenButton;

    @FXML
    private Button playButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button subtitlesButton;

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private JFXSlider runtimeSlider;

    @FXML
    private JFXSlider volumeSlider;

    @FXML
    private JFXSlider volumeSlider2;

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
    private BorderPane touchPane;

    @FXML
    private VBox optionsContainer;

    @FXML
    private Label optionsTitle;

    @FXML
    private HBox volumeBox;
    //endregion

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
    Season season = null;
    List<Episode> episodeList = new ArrayList<>();
    Episode episode = null;
    List<VideoTrack> videoTracks = new ArrayList<>();
    List<AudioTrack> audioTracks = new ArrayList<>();
    List<SubtitleTrack> subtitleTracks = new ArrayList<>();
    List<Button> chapterButtons = new ArrayList<>();
    boolean subsActivated = true;
    boolean fullscreen = false;
    int currentDisc = 0;
    int buttonCount = 0;
    boolean movingSlider = false;
    int currentTimeSeconds = 0;
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
        onLoad();

        currentDisc = episodeList.indexOf(episode);

        if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows")){
            controlsBox.getChildren().remove(chapterScroll);
            controlsBox.getChildren().remove(chaptersTitle);
        }

        mainPane.prefWidthProperty().bind(videoStage.widthProperty().multiply(0.99));
        mainPane.prefHeightProperty().bind(videoStage.heightProperty().multiply(0.97));
        rightOptions.prefWidthProperty().bind(mainPane.prefWidthProperty().multiply(0.5));

        leftTouchArea.prefWidthProperty().bind(mainPane.prefWidthProperty().multiply(0.45));
        rightTouchArea.prefWidthProperty().bind(mainPane.prefWidthProperty().multiply(0.45));
        leftTouchArea.prefHeightProperty().bind(touchPane.heightProperty());
        rightTouchArea.prefHeightProperty().bind(touchPane.heightProperty());
        touchPane.prefHeightProperty().bind(controlsBox.heightProperty());

        syncStageSize(videoStage);
        syncStagePosition(videoStage);

        shadowImage.fitWidthProperty().bind(videoStage.widthProperty());
        shadowImage.fitHeightProperty().bind(videoStage.heightProperty());

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

        mainPane.requestFocus();

        //Configure the actions that take place when the mouse movement is detected for more than a second
        controlsStage.addEventHandler(MouseEvent.MOUSE_MOVED, event -> onMouseMovement());
        controlsStage.addEventHandler(MouseEvent.MOUSE_MOVED, event -> onMouseMovement());

        videoStage.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (!controlsShown && videoPlayer.isVideoLoaded())
                showControls();
            timeline.playFromStart();

            controlsStage.getScene().setCursor(Cursor.DEFAULT);
            videoStage.getScene().setCursor(Cursor.DEFAULT);
        });

        videoStage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (optionsBox.isVisible())
                hideOptions();
        });

        controlsStage.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (App.pressedBack(e)){
                if (optionsBox.isVisible())
                    hideOptions();
                else
                    stop();
            }
        });

        controlsStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (!controlsShown && videoPlayer.isVideoLoaded()){
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
            }else if (!optionsBox.isVisible() && videoPlayer.isVideoLoaded()){
                if (App.pressedRB(e))
                    volumeUp();
                else if (App.pressedLB(e))
                    volumeDown();

                timeline.playFromStart();
            }
        });

        volumeSlider2.setValue(100);
        volumeSlider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            videoVolume((Double) newValue);
        });

        addInteractionSound(audiosButton);
        addInteractionSound(videoButton);
        addInteractionSound(nextButton);
        addInteractionSound(playButton);
        addInteractionSound(prevButton);
        addInteractionSound(subtitlesButton);
        addInteractionSound(button1);
        addInteractionSound(button2);

        seriesTitle.setText(seriesName);
        setDiscValues(episode);
    }
    private void onMouseMovement(){
        if (!controlsShown && videoPlayer.isVideoLoaded())
            showControls();

        controlsStage.getScene().setCursor(Cursor.DEFAULT);
        videoStage.getScene().setCursor(Cursor.DEFAULT);

        timeline.playFromStart();
    }
    private void syncStageSize(Stage primaryStage) {
        if (controlsStage != null) {
            if (primaryStage.getWidth() > 0 && primaryStage.getHeight() > 0) {
                controlsStage.setWidth(primaryStage.getWidth() * 0.99);
                controlsStage.setHeight(primaryStage.getHeight() * 0.97);
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
            double y = primaryY + (primaryHeight - controlsHeight);

            controlsStage.setX(x);
            controlsStage.setY(y);
        }
    }
    private void onLoad(){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.1), leftTouchButton);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.play();

        fadeOut = new FadeTransition(Duration.seconds(0.1), rightTouchButton);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.play();

        if (parentController != null) {
            fullScreenButton.setVisible(false);
            desktopVolumeSliderBox.setVisible(false);
        }

        optionsBox.setOnMouseClicked(e -> hideOptions());

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

        fullScreenButton.setOnMouseClicked(e -> {
            fullscreen = !fullscreen;
            videoStage.setFullScreen(fullscreen);
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

            hideSlider();
        });

        runtimeSlider.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (runtimeSlider.isFocused() && App.pressedUp(e))
                hideControls();
            else{
                timeline.playFromStart();
            }

            if (App.pressedLeft(e))
                runtimeSlider.setValue(runtimeSlider.getValue() - 5);
            else if (App.pressedRight(e))
                runtimeSlider.setValue(runtimeSlider.getValue() + 10);
            else if (App.pressedDown(e))
                playButton.requestFocus();
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
                else if (!audiosButton.isDisabled())
                    audiosButton.requestFocus();
                else if (!subtitlesButton.isDisabled())
                    subtitlesButton.requestFocus();
            }else if (App.pressedDown(e))
                selectCurrentChapter();
            else if (App.pressedUp(e)) {
                runtimeSlider.requestFocus();
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
            else if (App.pressedDown(e))
                selectCurrentChapter();
            else if (App.pressedUp(e))
                runtimeSlider.requestFocus();
        });

        nextButton.setOnMouseClicked(e -> nextEpisode());

        prevButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedSelect(e))
                prevEpisode();
            else if (App.pressedLeft(e)){
                if (!audiosButton.isDisabled())
                    audiosButton.requestFocus();
                else if (!subtitlesButton.isDisabled())
                    subtitlesButton.requestFocus();
            }else if (App.pressedRight(e))
                playButton.requestFocus();
            else if (App.pressedDown(e))
                selectCurrentChapter();
            else if (App.pressedUp(e))
                runtimeSlider.requestFocus();
        });

        prevButton.setOnMouseClicked(e -> prevEpisode());

        videoButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedLeft(e)){
                if (!nextButton.isDisabled())
                    nextButton.requestFocus();
                else
                    playButton.requestFocus();
            }else if (App.pressedDown(e))
                selectCurrentChapter();
            else if (App.pressedUp(e))
                runtimeSlider.requestFocus();
        });

        audiosButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedLeft(e)){
                if (!subtitlesButton.isDisabled())
                    subtitlesButton.requestFocus();
            }else if (App.pressedRight(e)){
                if (!prevButton.isDisabled())
                    prevButton.requestFocus();
                else
                    playButton.requestFocus();
            }else if (App.pressedDown(e))
                selectCurrentChapter();
            else if (App.pressedUp(e))
                runtimeSlider.requestFocus();
        });

        subtitlesButton.setOnKeyPressed(e -> {
            timeline.playFromStart();
            if (App.pressedRight(e)){
                if (!audiosButton.isDisabled())
                    audiosButton.requestFocus();
                else if (!prevButton.isDisabled())
                    prevButton.requestFocus();
                else
                    playButton.requestFocus();
            }else if (App.pressedDown(e))
                selectCurrentChapter();
            else if (App.pressedUp(e))
                runtimeSlider.requestFocus();
        });

        //region BUTTON ICONS
        playButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                if (videoPlayer.isPaused())
                    playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/playSelected.png", 30, 30, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/pauseSelected.png", 30, 30, true, true)));
            }else{
                if (videoPlayer.isPaused())
                    playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/play.png", 30, 30, true, true)));
                else
                    playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/pause.png", 30, 30, true, true)));
            }
        });

        nextButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                nextButton.setGraphic(new ImageView(new Image("file:resources/img/icons/nextTrackSelected.png", 30, 30, true, true)));
            else
                nextButton.setGraphic(new ImageView(new Image("file:resources/img/icons/nextTrack.png", 30, 30, true, true)));
        });

        prevButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                prevButton.setGraphic(new ImageView(new Image("file:resources/img/icons/prevTrackSelected.png", 30, 30, true, true)));
            else
                prevButton.setGraphic(new ImageView(new Image("file:resources/img/icons/prevTrack.png", 30, 30, true, true)));
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
            videoPlayer.playVideo(episode.getVideoSrc(), App.textBundle.getString("season"));
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

        //region CHAPTERS
        chaptersTitle.setText(App.textBundle.getString("chapters"));
        buttonCount = 7;

        if (episode.getChapters().isEmpty()) {
            chaptersTitle.setVisible(false);
            chapterScroll.setVisible(false);
        }else{
            Platform.runLater(() -> {
                for (Chapter chapter : episode.getChapters())
                    addChapterCard(chapter);
            });
        }
        //endregion

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

        //Initialize Buttons
        videoButton.setDisable(videoTracks == null || videoTracks.isEmpty());
        audiosButton.setDisable(audioTracks == null || audioTracks.isEmpty());
        subtitlesButton.setDisable(subtitleTracks == null || subtitleTracks.isEmpty());
    }
    private void addInteractionSound(Button btn){
        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                playInteractionSound();
        });
    }
    public void startCount(){
        loadTracks();
        runtimeTimeline.getKeyFrames().setAll(new KeyFrame(Duration.seconds(1), e ->{
            currentTimeSeconds++;

            updateSliderValue();

            runtimeTimeline.playFromStart();
        }));

        currentTimeSeconds = episode.getTimeWatched();

        runtimeTimeline.play();
        runtimeSlider.setValue(currentTimeSeconds);
        videoPlayer.seekToTime(currentTimeSeconds);
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
    public void addChapterCard(Chapter chapter){
        if (episode != null){
            Button btn = new Button();
            btn.setPadding(new Insets(0));

            btn.setFocusTraversable(false);

            btn.setOnMouseClicked(e -> {
                long pos = (long) (episode.getChapters().get(chapterButtons.indexOf(btn)).getTime());
                long currentPos = videoPlayer.getCurrentTime();

                videoPlayer.seekToTime(pos - currentPos);

                if (videoPlayer.isPaused())
                    resume();
                hideControls();
            });

            btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event)){
                    long pos = (long) (episode.getChapters().get(chapterButtons.indexOf(btn)).getTime());
                    long currentPos = videoPlayer.getCurrentTime();

                    videoPlayer.seekToTime(pos - currentPos);

                    if (videoPlayer.isPaused())
                        resume();
                    hideControls();
                }
            });

            btn.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) ->{
                timeline.playFromStart();

                if (App.pressedUp(event))
                    playButton.requestFocus();

                int index = chapterContainer.getChildren().indexOf(btn);
                if (App.pressedLeft(event)){
                    if (index > 0)
                        chapterContainer.getChildren().get(index - 1).requestFocus();
                }else if (App.pressedRight(event)){
                    if (index < chapterContainer.getChildren().size() - 1)
                        chapterContainer.getChildren().get(index + 1).requestFocus();
                }
            });

            setChapterCardValues(btn, chapter);

            chapterContainer.getChildren().add(btn);
            chapterButtons.add(btn);
        }
    }
    private void setChapterCardValues(Button btn, Chapter chapter){
        double targetHeight = Screen.getPrimary().getBounds().getHeight() / 8;
        double targetWidth = ((double) 16 /9) * targetHeight;

        btn.getStyleClass().add("transparent");

        VBox buttonContent = new VBox();
        buttonContent.setAlignment(Pos.TOP_CENTER);
        buttonContent.setMinWidth(targetWidth);

        Label title = new Label(chapter.getTitle());
        title.setFont(new Font(18));
        title.setStyle("-fx-font-weight: bold;");
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow());
        title.setWrapText(true);
        Label time = new Label(chapter.getDisplayTime());
        time.setFont(new Font(16));
        time.setStyle("-fx-font-weight: bold;");
        time.setTextFill(Color.WHITE);
        time.setEffect(new DropShadow());
        time.setWrapText(true);

        buttonContent.getChildren().add(title);
        buttonContent.getChildren().add(time);

        btn.setGraphic(buttonContent);

        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                //Move ScrollPane
                handleButtonFocus(btn);

                /*ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.1), btn);
                scaleTransition.setToX(1.15);
                scaleTransition.setToY(1.15);
                scaleTransition.play();*/

                title.setFont(new Font(20));
                time.setFont(new Font(18));

                playInteractionSound();
            }else{
                /*ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.1), btn);
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);
                scaleTransition.play();*/

                title.setFont(new Font(18));
                time.setFont(new Font(16));
            }
        });
    }
    private void handleButtonFocus(Button focusedButton) {
        double screenCenter, buttonCenterX, offset, finalPos;

        if (chapterContainer.getChildren().indexOf(focusedButton) <= buttonCount / 2 || chapterContainer.getChildren().size() <= 3){
            finalPos = 0;
        }else{
            //Get center of screen
            screenCenter = Screen.getPrimary().getBounds().getWidth() / 2;

            //Check aspect ratio to limit the right end of the button list
            double aspectRatio = Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight();
            int maxButtons = (chapterContainer.getChildren().size() - (buttonCount / 2));
            if (aspectRatio > 1.8)
                maxButtons--;

            if (chapterContainer.getChildren().indexOf(focusedButton) >= (chapterContainer.getChildren().size() - (buttonCount / 2)))
                focusedButton = (Button) chapterContainer.getChildren().get(Math.max(2, maxButtons));

            //Get center of button in the screen
            Bounds buttonBounds = focusedButton.localToScene(focusedButton.getBoundsInLocal());
            buttonCenterX = buttonBounds.getMinX() + buttonBounds.getWidth() / 2;

            //Calculate offset
            offset = screenCenter - buttonCenterX;

            //Calculate position
            finalPos = chapterContainer.getTranslateX() + offset;
        }

        //Translation with animation
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.2), chapterContainer);
        transition.setToX(finalPos);
        transition.play();
    }
    private void selectCurrentChapter(){
        if (chapterContainer.getChildren().isEmpty())
            return;

        long currentTime = videoPlayer.getCurrentTime();

        List<Chapter> chapters = episode.getChapters();
        for (int i = 1; i < chapters.size(); i++){
            if (chapters.get(i).getTime() > currentTime){
                chapterContainer.getChildren().get(episode.getChapters().indexOf(chapters.get(i - 1))).requestFocus();
                return;
            }
        }

        chapterContainer.getChildren().get(0).requestFocus();
    }
    //endregion

    //region BEHAVIOR AND EFFECTS
    private void showControls(){
        controlsShown = true;
        timeline.playFromStart();
        fadeInEffect(controlsBox);

        if (parentController != null)
            fadeInEffect(shadowImage);
        else if (videoPlayer != null)
            videoPlayer.setVideoBrightness(-4);

        fadeInEffect(closeButton);
        playButton.requestFocus();
    }
    private void hideControls(){
        controlsStage.getScene().setCursor(Cursor.NONE);
        videoStage.getScene().setCursor(Cursor.NONE);

        timeline.stop();
        fadeOutEffect(closeButton);

        if (parentController != null)
            fadeOutEffect(shadowImage);
        else if (videoPlayer != null)
            videoPlayer.setVideoBrightness(0);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), controlsBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        controlsBox.setVisible(false);
        fadeOut.setOnFinished(e -> controlsShown = false);
    }
    private void showSlider(){
        if (parentController != null)
            fadeOutEffect(shadowImage);
        else
            videoPlayer.setVideoBrightness(0);

        closeButton.setVisible(false);
        runtimeSlider.setVisible(true);
        currentTime.setVisible(true);
        toFinishTime.setVisible(true);
        seriesTitle.setVisible(false);
        episodeInfo.setVisible(false);
        fullScreenButton.setVisible(false);
        videoButton.setVisible(false);
        audiosButton.setVisible(false);
        subtitlesButton.setVisible(false);
        playButton.setVisible(false);
        prevButton.setVisible(false);
        nextButton.setVisible(false);

        desktopVolumeSliderBox.setVisible(false);

        if (!episode.getChapters().isEmpty()){
            chaptersTitle.setVisible(false);
            chapterContainer.setVisible(false);
        }
    }
    private void hideSlider(){
        runtimeSlider.setVisible(true);
        currentTime.setVisible(true);
        toFinishTime.setVisible(true);
        seriesTitle.setVisible(true);
        episodeInfo.setVisible(true);

        if (parentController == null) {
            fullScreenButton.setVisible(true);
            desktopVolumeSliderBox.setVisible(true);
        }

        closeButton.setVisible(true);
        videoButton.setVisible(true);
        audiosButton.setVisible(true);
        subtitlesButton.setVisible(true);
        playButton.setVisible(true);
        prevButton.setVisible(true);
        nextButton.setVisible(true);

        if (!episode.getChapters().isEmpty()){
            chaptersTitle.setVisible(false);
            chapterContainer.setVisible(false);
        }

        hideControls();
    }
    //endregion

    //region CONTROLS
    public void stop() {
        checkTimeWatched();

        timeline.stop();
        touchTimeline.stop();
        runtimeTimeline.stop();

        videoStage.setFullScreen(false);

        processCurrentlyWatching();

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
        hideControls();
        videoPlayer.togglePause();
    }
    public void pause(){
        videoPlayer.togglePause();

        if (controlsShown)
            playButton.setGraphic(new ImageView(new Image("file:resources/img/icons/playSelected.png", 30, 30, true, true)));
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
    }
    public void goAhead(){
        showControls();
        runtimeSlider.requestFocus();
    }
    public void goBack(){
        showControls();
        runtimeSlider.requestFocus();
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
    private void processCurrentlyWatching(){
        Series series = App.getSelectedSeries();
        List<Season> seasons = series.getSeasons();

        season.setCurrentlyWatchingEpisode(-1);
        series.setCurrentlyWatchingSeason(-1);

        if (episode.isWatched()){
            if (episodeList.indexOf(episode) == episodeList.size() - 1){
                if (seasons.indexOf(season) < seasons.size() - 1){
                    Season nextSeason = seasons.get(seasons.indexOf(season) + 1);

                    if (nextSeason != null){
                        nextSeason.setCurrentlyWatchingEpisode(0);
                        series.setCurrentlyWatchingSeason(seasons.indexOf(nextSeason));
                    }
                }
            }else{
                season.setCurrentlyWatchingEpisode(episodeList.indexOf(episode) + 1);
                series.setCurrentlyWatchingSeason(seasons.indexOf(season));
            }
        }else{
            season.setCurrentlyWatchingEpisode(episodeList.indexOf(episode));
            series.setCurrentlyWatchingSeason(seasons.indexOf(season));
        }
    }
    private void checkTimeWatched(){
        Episode episode = episodeList.get(currentDisc);
        episode.setTimeWatched((int) runtimeSlider.getValue());
    }
    public void nextEpisode(){
        if (currentDisc + 1 >= episodeList.size() - 1){
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
        if (videoPlayer.getCurrentTime() > 2000){
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
    public void showVideoOptions(){
        if (!videoPlayer.isPaused())
            pause();
        optionsBox.setVisible(true);
        timeline.stop();

        if (parentController != null)
            shadowImage.setVisible(true);
        else if (videoPlayer != null)
            videoPlayer.setVideoBrightness(-15);

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
        for (VideoTrack track : videoTracks){
            addOptionCard(track.getDisplayTitle());

            Button btn = (Button) optionsContainer.getChildren().get(videoTracks.indexOf(track));

            if (track.isSelected()) {
                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");
            }

            btn.setOnMouseClicked(e -> {
                if (!track.isSelected())
                    videoButtonAction(track, btn);
            });
            btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event) && !track.isSelected())
                    videoButtonAction(track, btn);
            });
        }
    }
    private void videoButtonAction(VideoTrack track, Button btn){
        videoPlayer.setVideoTrack(videoTracks.indexOf(track) + 1);

        for (Node node : optionsContainer.getChildren()){
            Button button = (Button) node;
            button.getStyleClass().clear();
            button.getStyleClass().add("playerOptionsButton");
        }

        btn.getStyleClass().clear();
        btn.getStyleClass().add("playerOptionsSelected");
    }
    private void showZoomOptions(){
        optionsContainer.getChildren().clear();
        Series series = App.getSelectedSeries();

        addOptionCard("Normal");
        Button btn = (Button) optionsContainer.getChildren().get(0);
        btn.setOnMouseClicked(e -> zoomButtonAction(series, btn, 0, 0.7f, 100, 1));
        btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                zoomButtonAction(series, btn, 0, 0.7f, 100, 1);
            }
        });

        addOptionCard("Zoom");
        Button button = (Button) optionsContainer.getChildren().get(1);
        button.setOnMouseClicked(e -> zoomButtonAction(series, button, 0.5f, 0.9f, 90, 0));
        button.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                zoomButtonAction(series, button, 0.5f, 0.9f, 90, 0);
            }
        });
    }
    private void zoomButtonAction(Series series, Button btn, float zoom, float subSize, int position, int index){
        videoPlayer.fixZoom(zoom);
        videoPlayer.setSubtitleVerticalPosition(position);
        videoPlayer.setSubtitleSize(subSize);
        series.setVideoZoom(zoom);
        btn.getStyleClass().clear();
        btn.getStyleClass().add("playerOptionsSelected");

        Button b1 = (Button) optionsContainer.getChildren().get(index);
        b1.getStyleClass().clear();
        b1.getStyleClass().add("playerOptionsButton");
    }
    public void showAudioOptions(){
        if (!videoPlayer.isPaused())
            pause();
        optionsBox.setVisible(true);
        timeline.stop();

        if (parentController != null)
            shadowImage.setVisible(true);
        else if (videoPlayer != null)
            videoPlayer.setVideoBrightness(-15);

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
        for (AudioTrack track : audioTracks){
            Button trackButton = addSimpleButton(track.getDisplayTitle());
            optionsContainer.getChildren().add(trackButton);

            if (track.isSelected()) {
                trackButton.getStyleClass().clear();
                trackButton.getStyleClass().add("playerOptionsSelected");
            }

            trackButton.setOnMouseClicked(e -> {
                if (!track.isSelected())
                    addAudioButtonAction(track, trackButton);
            });
            trackButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event) && !track.isSelected())
                    addAudioButtonAction(track, trackButton);
            });
        }
    }
    private void addAudioButtonAction(AudioTrack track, Button trackButton) {
        playInteractionSound();

        for (AudioTrack aTrack : audioTracks)
            aTrack.setSelected(aTrack == track);

        videoPlayer.setAudioTrack(audioTracks.indexOf(track) + 1);
        season.setSelectedAudioTrack(audioTracks.indexOf(track));
        season.setAudioTrackLanguage(track.getLanguageTag());

        for (Node node : optionsContainer.getChildren()){
            Button button = (Button) node;
            button.getStyleClass().clear();
            button.getStyleClass().add("playerOptionsButton");
        }

        trackButton.getStyleClass().clear();
        trackButton.getStyleClass().add("playerOptionsSelected");
    }
    public void showSubtitleOptions(){
        if (!videoPlayer.isPaused())
            pause();
        optionsBox.setVisible(true);
        timeline.stop();

        if (parentController != null)
            shadowImage.setVisible(true);
        else if (videoPlayer != null)
            videoPlayer.setVideoBrightness(-15);

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

        //Add first button to disable subtitles
        Button btn = addSimpleButton(App.buttonsBundle.getString("none"));
        optionsContainer.getChildren().add(btn);

        if (season.getSubtitleTrackLanguage() == null || season.getSubtitleTrackLanguage().isEmpty()) {
            btn.getStyleClass().clear();
            btn.getStyleClass().add("playerOptionsSelected");

            videoPlayer.disableSubtitles();
            season.setSelectedSubtitleTrack(-1);
            season.setSubtitleTrackLanguage("");
        }

        btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                for (SubtitleTrack sTrack : subtitleTracks)
                    sTrack.setSelected(false);

                subsActivated = false;
                videoPlayer.disableSubtitles();
                season.setSelectedSubtitleTrack(-1);
                season.setSubtitleTrackLanguage("");

                for (Node node : optionsContainer.getChildren()){
                    Button button = (Button) node;
                    button.getStyleClass().clear();
                    button.getStyleClass().add("playerOptionsButton");
                }

                btn.getStyleClass().clear();
                btn.getStyleClass().add("playerOptionsSelected");
            }
        });

        for (SubtitleTrack track : subtitleTracks){
            Button trackButton = addSimpleButton(track.getDisplayTitle());
            optionsContainer.getChildren().add(trackButton);

            if (track.isSelected()) {
                trackButton.getStyleClass().clear();
                trackButton.getStyleClass().add("playerOptionsSelected");
            }

            trackButton.setOnMouseClicked(e -> {
                if (!track.isSelected())
                    addSubtitleButtonAction(track, trackButton, btn);
            });
            trackButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event) && !track.isSelected())
                    addSubtitleButtonAction(track, trackButton, btn);
            });
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

        for (Node node : optionsContainer.getChildren()){
            Button button = (Button) node;
            button.getStyleClass().clear();
            button.getStyleClass().add("playerOptionsButton");
        }

        //Disable selection of button "None"
        btn.getStyleClass().clear();
        btn.getStyleClass().add("playerOptionsButton");

        //Select current button
        trackButton.getStyleClass().clear();
        trackButton.getStyleClass().add("playerOptionsSelected");
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
    private void addOptionCard(String title){
        Button btn = addSimpleButton(title);
        optionsContainer.getChildren().add(btn);
    }
    private Button addSimpleButton(String name){
        Button btn = new Button(name);
        btn.setFont(new Font("Arial", 24));
        btn.setTextFill(Color.WHITE);
        btn.setMaxWidth(Integer.MAX_VALUE);
        btn.setPrefWidth(Integer.MAX_VALUE);
        btn.setAlignment(Pos.BOTTOM_LEFT);
        btn.setPadding(new Insets(11));

        btn.getStyleClass().clear();
        btn.getStyleClass().add("playerOptionsButton");

        addInteractionSound(btn);

        return btn;
    }
    public void hideOptions(){
        optionsBox.setVisible(false);
        showControls();
    }
    //endregion

    public VideoPlayer getVideoPlayer(){
        return videoPlayer;
    }
}
