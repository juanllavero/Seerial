package com.example.executablelauncher.entities;

import com.example.executablelauncher.App;
import com.example.executablelauncher.DesktopViewController;
import com.example.executablelauncher.SeasonController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.executablelauncher.utils.Utils.processCurrentlyWatching;

public class VlcjJavaFxApplication extends Application {
    Stage primaryStage;
    DesktopViewController desktopParent;
    SeasonController seasonParent;
    Episode episode;
    Season season;
    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;
    private ImageView videoImageView;
    private Slider timeSlider;
    private Label currentTimeLabel;
    private Label durationLabel;
    private HBox controlBar;
    private Slider volumeSlider;
    private ContextMenu contextMenu;
    private Timer hideControlsTimer;

    public VlcjJavaFxApplication() {
        String[] args = {"--vout=xcb", "--aout=alsa"};
        this.mediaPlayerFactory = new MediaPlayerFactory(args);
        this.embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        this.embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                Platform.runLater(() -> updateSlider());
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                Platform.runLater(() -> stop());
            }
        });
    }

    @Override
    public void init() {
        this.videoImageView = new ImageView();
        this.videoImageView.setPreserveRatio(true);
        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.videoImageView));
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        videoImageView.fitWidthProperty().bind(root.widthProperty());
        videoImageView.fitHeightProperty().bind(root.heightProperty());
        root.getChildren().add(videoImageView);

        primaryStage.setOnCloseRequest(e -> stop());

        Scene scene = new Scene(root, 1200, 675, Color.BLACK);
        primaryStage.setTitle("Video Player");
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> {
            setupContextMenu(root);
            setupControls(root);
            root.addEventFilter(MouseEvent.MOUSE_MOVED, event -> showControls());
            root.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        });

        startHideControlsTimer();
    }

    public void setDesktopParent(DesktopViewController desktopParent){
        this.desktopParent = desktopParent;
    }

    public void setSeasonParent(SeasonController seasonParent){
        this.seasonParent = seasonParent;
    }

    public void startVideo(Season season, Episode episode){
        this.season = season;
        this.episode = episode;
        embeddedMediaPlayer.media().play(episode.getVideoSrc());
        float position = (float) episode.getTimeWatched() / episode.getRuntime();
        embeddedMediaPlayer.controls().setPosition(position);
    }

    private void setupContextMenu(StackPane root) {
        contextMenu = new ContextMenu();

        Menu videoMenu = new Menu("Video");
        List<TrackDescription> videoTracks = embeddedMediaPlayer.video().trackDescriptions();
        ToggleGroup videoToggleGroup = new ToggleGroup();
        for (TrackDescription track : videoTracks) {
            RadioMenuItem item = new RadioMenuItem(track.description());
            item.setToggleGroup(videoToggleGroup);
            item.setOnAction(event -> embeddedMediaPlayer.video().setTrack(track.id()));
            videoMenu.getItems().add(item);
        }
        contextMenu.getItems().add(videoMenu);

        Menu audioMenu = new Menu("Audio");
        List<TrackDescription> audioTracks = embeddedMediaPlayer.audio().trackDescriptions();
        ToggleGroup audioToggleGroup = new ToggleGroup();
        for (TrackDescription track : audioTracks) {
            RadioMenuItem item = new RadioMenuItem(track.description());
            item.setToggleGroup(audioToggleGroup);
            item.setOnAction(event -> embeddedMediaPlayer.audio().setTrack(track.id()));
            audioMenu.getItems().add(item);
        }
        contextMenu.getItems().add(audioMenu);

        Menu subtitlesMenu = new Menu("Subtítulos");
        List<TrackDescription> subtitleTracks = embeddedMediaPlayer.subpictures().trackDescriptions();
        ToggleGroup subtitlesToggleGroup = new ToggleGroup();
        for (TrackDescription track : subtitleTracks) {
            RadioMenuItem item = new RadioMenuItem(track.description());
            item.setToggleGroup(subtitlesToggleGroup);
            item.setOnAction(event -> embeddedMediaPlayer.subpictures().setTrack(track.id()));
            subtitlesMenu.getItems().add(item);
        }
        contextMenu.getItems().add(subtitlesMenu);

        videoImageView.setOnContextMenuRequested(event -> contextMenu.show(videoImageView, event.getScreenX(), event.getScreenY()));

        root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (contextMenu.isShowing() && !contextMenu.getOwnerWindow().equals(primaryStage)) {
                contextMenu.hide();
            }
        });
    }

    private void setupControls(StackPane root) {
        controlBar = new HBox(20);
        controlBar.setMaxHeight(50);
        controlBar.setMinHeight(50);
        controlBar.setPadding(new Insets(20, 0, 20, 0));
        controlBar.setAlignment(Pos.CENTER);
        controlBar.setStyle("-fx-background-color: black;");

        Button playPauseButton = new Button("Play/Pause");
        playPauseButton.setOnAction(event -> {
            if (embeddedMediaPlayer.status().isPlaying()) {
                embeddedMediaPlayer.controls().pause();
            } else {
                embeddedMediaPlayer.controls().play();
            }
        });

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(event -> stop());

        Button skipForwardButton = new Button(">>");
        skipForwardButton.setOnAction(event -> embeddedMediaPlayer.controls().skipTime(10000));

        Button skipBackwardButton = new Button("<<");
        skipBackwardButton.setOnAction(event -> embeddedMediaPlayer.controls().skipTime(-10000));

        timeSlider = new Slider();
        timeSlider.setMin(0);
        timeSlider.setMax(1000);
        timeSlider.setValue(0);
        timeSlider.setPrefWidth(400);

        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (timeSlider.isValueChanging()) {
                float position = newValue.floatValue() / 1000.0f;
                embeddedMediaPlayer.controls().setPosition(position);
            }
        });

        currentTimeLabel = new Label("00:00:00");
        durationLabel = new Label("00:00:00");
        currentTimeLabel.setTextFill(Color.WHITE);
        durationLabel.setTextFill(Color.WHITE);

        volumeSlider = new Slider(0, 200, 100);
        volumeSlider.setPrefWidth(150);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            embeddedMediaPlayer.audio().setVolume(newValue.intValue());
        });
        embeddedMediaPlayer.audio().setVolume(100); // Set initial volume

        Label volumeLabel = new Label("Vol:");
        volumeLabel.setTextFill(Color.WHITE);

        controlBar.getChildren().addAll(skipBackwardButton, playPauseButton, stopButton, skipForwardButton, currentTimeLabel, timeSlider, durationLabel, volumeLabel, volumeSlider);
        root.getChildren().add(controlBar);
        StackPane.setAlignment(controlBar, Pos.BOTTOM_CENTER);
        controlBar.setVisible(false);
    }

    private void updateSlider() {
        long currentTime = embeddedMediaPlayer.status().time();
        long totalDuration = embeddedMediaPlayer.status().length();
        if (totalDuration > 0) {
            float position = embeddedMediaPlayer.status().position();
            timeSlider.setValue(position * 1000);
            currentTimeLabel.setText(formatTime(currentTime));
            durationLabel.setText(formatTime(totalDuration - currentTime));
        }
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours % 24, minutes % 60, seconds % 60);
    }

    private void showControls() {
        controlBar.setVisible(true);
        controlBar.setOpacity(1);
        resetHideControlsTimer();
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        switch (keyCode) {
            case PLUS:
            case EQUALS: // Some keyboards use "=" instead of "+"
                volumeSlider.setValue(volumeSlider.getValue() + 10);
                break;
            case MINUS:
                volumeSlider.setValue(volumeSlider.getValue() - 10);
                break;
            case RIGHT:
                embeddedMediaPlayer.controls().skipTime(10000); // Skip 10 seconds forward
                break;
            case LEFT:
                embeddedMediaPlayer.controls().skipTime(-10000); // Skip 10 seconds backward
                break;
            default:
                break;
        }
    }

    private void startHideControlsTimer() {
        hideControlsTimer = new Timer(true);
        hideControlsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> controlBar.setVisible(false));
            }
        }, 5000);
    }

    private void resetHideControlsTimer() {
        if (hideControlsTimer != null) {
            hideControlsTimer.cancel();
        }
        startHideControlsTimer();
    }

    @Override
    public final void stop() {
        long currentPositionMillis = embeddedMediaPlayer.status().time();
        double currentPositionMinutes = currentPositionMillis / (1000.0 * 60.0);
        episode.setTimeWatched((int) currentPositionMinutes);

        embeddedMediaPlayer.controls().stop();
        embeddedMediaPlayer.release();
        mediaPlayerFactory.release();

        hideControlsTimer.cancel();

        processCurrentlyWatching(App.getSelectedSeries(), season, episode);

        if (seasonParent != null)
            seasonParent.stopVideo();
        else
            desktopParent.stopPlayer();

        if (primaryStage != null)
            primaryStage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
