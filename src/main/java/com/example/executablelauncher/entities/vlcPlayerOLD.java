package com.example.executablelauncher.entities;

import com.example.executablelauncher.SeasonController;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.jfoenix.controls.JFXSlider;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

public class vlcPlayerOLD {
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
    private ImageView videoImage;

    @FXML
    private VBox controlsBox;

    @FXML
    private HBox volumeBox;
    //endregion

    public MediaPlayerFactory mediaPlayerFactory;

    public EmbeddedMediaPlayer embeddedMediaPlayer;

    boolean isPaused = false;
    boolean controlsShown = false;
    SeasonController parentController = null;
    Timeline timeline = null;
    Timeline volumeCount = null;

    private final AtomicBoolean tracking = new AtomicBoolean();

    private Timer clockTimer;
    double percentageStep = 0;

    List<Episode> episodeList = new ArrayList<>();
    int currentDisc = 0;
    /*
    public void setVideo(SeasonController parent, List<Disc> discList, Disc disc, String seriesName, Scene scene){
        parentController = parent;
        this.discList = discList;
        onLoad();

        currentDisc = discList.indexOf(disc);

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        videoImage.setFitWidth(screenWidth);
        videoImage.setFitHeight(screenHeight);

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

                if (e.getCode().equals(KeyCode.M))
                    embeddedMediaPlayer.menu().activate();
            }

        });

        seriesTitle.setText(seriesName);
        setDiscValues(disc);

    }*/

    /*

    private void onLoad(){
        mediaPlayerFactory = new MediaPlayerFactory(
                "--no-xlib",
                "--fullscreen",
                "--vout=opengl",
                "--tone-mapping=3",
                "--tone-mapping-param=0.5");
        embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImage));
        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                startTimer();
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                stopTimer();
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                stopTimer();
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                stopTimer();
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                stopTimer();
            }

            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                Platform.runLater(() -> updateSliderPosition(newPosition));
            }
        });

        runtimeSlider.setOnMousePressed(mouseEvent -> beginTracking());
        runtimeSlider.setOnMouseReleased(mouseEvent -> endTracking());

        runtimeSlider.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                timeline.stop();
                beginTracking();
            }else {
                timeline.playFromStart();
                tracking.set(false);
            }
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

        runtimeSlider.setBlockIncrement(percentageStep);

        nextButton.setDisable(discList.indexOf(disc) == (discList.size() - 1));

        if (disc.isWatched())
            disc.setUnWatched();

        float position;

        if (disc.getTimeWatched() > 0){
            position = (float) ((disc.runtime * 60L * 1000) - disc.getTimeWatched()) / 100;
        } else {
            position = 0;
        }

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), videoImage);
        fade.setFromValue(0);
        fade.setToValue(1.0);

        fade.setOnFinished(event -> {
            String[] options = {
                    "--no-xlib",
                    "--fullscreen",
                    "--vout=opengl",
                    "--enable-hdr",
            };
            //Set video and start
            embeddedMediaPlayer.media().play(disc.executableSrc, options);
            embeddedMediaPlayer.controls().setPosition(position);
        });

        fade.play();
    }

    private void switchSubtitleTrack(int trackNumber) {
        embeddedMediaPlayer.subpictures().setTrack(trackNumber);
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
        disc.setTime(embeddedMediaPlayer.status().time() / 1000);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.7), videoImage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            stopTimer();
            embeddedMediaPlayer.controls().stop();
            embeddedMediaPlayer.release();
            mediaPlayerFactory.release();

            parentController.stopVideo();

            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        });

        fadeOut.play();
    }

    public void resume(){
        hideControls();
        isPaused = false;
        embeddedMediaPlayer.controls().play();
    }

    public void pause(){
        isPaused = true;
        embeddedMediaPlayer.controls().pause();

        if (controlsShown)
            playButton.setGraphic(new ImageView(new Image("file:src/main/resources/img/icons/playSelected.png", 35, 35, true, true)));
    }

    public void volumeUp(){
        volumeCount.playFromStart();
        volumeBox.setVisible(true);

        if (embeddedMediaPlayer.audio().volume() < 100){
            embeddedMediaPlayer.audio().setVolume(embeddedMediaPlayer.audio().volume() + 5);
            volumeSlider.setValue(embeddedMediaPlayer.audio().volume());
        }
    }

    public void volumeDown(){
        volumeCount.playFromStart();
        volumeBox.setVisible(true);
        embeddedMediaPlayer.audio().setVolume(embeddedMediaPlayer.audio().volume() - 5);
        volumeSlider.setValue(embeddedMediaPlayer.audio().volume());
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
        disc.setTime(embeddedMediaPlayer.status().time() / 1000);

        setDiscValues(discList.get(++currentDisc));
    }

    public void prevEpisode(){
        if ((embeddedMediaPlayer.status().time() / 1000) > 2000){
            runtimeSlider.setValue(0);
        }else{
            if (currentDisc > 0) {
                Disc disc = discList.get(currentDisc);
                disc.setTime(embeddedMediaPlayer.status().time() / 1000);

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

    private void startTimer() {
        clockTimer = new Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    currentTime.setText(formatTime(embeddedMediaPlayer.status().time()));
                    toFinishTime.setText(formatTime(embeddedMediaPlayer.status().length() - embeddedMediaPlayer.status().time()));
                });
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        clockTimer.cancel();
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
            embeddedMediaPlayer.controls().setPosition(newValue);
        }
    }

    private synchronized void beginTracking() {
        tracking.set(true);
    }

    private synchronized void endTracking() {
        tracking.set(false);
        // This deals with the case where there was an absolute click in the timeline rather than a drag
        embeddedMediaPlayer.controls().setPosition((float) runtimeSlider.getValue() / 100);
    }

    private synchronized void updateSliderPosition(float newValue) {
        if (!tracking.get()) {
            runtimeSlider.setValue(newValue * 100);
        }
    }
    */
}
