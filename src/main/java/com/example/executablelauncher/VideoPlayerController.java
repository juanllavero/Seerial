package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.List;

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
    private Slider runtimeSlider;

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
    //endregion

    public MediaPlayerFactory mediaPlayerFactory;

    public EmbeddedMediaPlayer embeddedMediaPlayer;

    boolean isPaused = false;

    public void setVideo(String videoSrc){
        onLoad();

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        videoImage.setFitWidth(screenWidth);
        videoImage.setFitHeight(screenHeight);

        shadowImage.setFitWidth(screenWidth);
        shadowImage.setFitHeight(screenHeight);
        shadowImage.setPreserveRatio(false);

        mainPane.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ESCAPE))
                stop();
            else if (e.getCode().equals(KeyCode.SPACE)){
                if (!isPaused)
                    pause();
                else
                    resume();
            }else if (e.getCode().equals(KeyCode.F11))
                embeddedMediaPlayer.video().setAdjustVideo(true);
            else if (e.getCode().equals(KeyCode.DIGIT1)) {
                switchSubtitleTrack(1);

                List<TrackDescription> tracks = embeddedMediaPlayer.subpictures().trackDescriptions();

                for (TrackDescription trackDescription : tracks){
                    System.out.println(trackDescription.description());
                }

                List<TrackDescription> audioTracks = embeddedMediaPlayer.audio().trackDescriptions();

                for (TrackDescription trackDescription : audioTracks){
                    System.out.println(trackDescription.description());
                }
            }else if (e.getCode().equals(KeyCode.RIGHT))
                goAhead();
            else if (e.getCode().equals(KeyCode.LEFT))
                goBack();
            else if (e.getCode().equals(KeyCode.M))
                embeddedMediaPlayer.menu().activate();
            else if (e.getCode().equals(KeyCode.UP))
                volumeUp();
            else if (e.getCode().equals(KeyCode.DOWN))
                volumeDown();
        });

        embeddedMediaPlayer.media().play(videoSrc);

        embeddedMediaPlayer.controls().setPosition(0);
    }

    private void onLoad(){
        mediaPlayerFactory = new MediaPlayerFactory("--tone-mapping=3", "--tone-mapping-param=0.5");
        embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImage));
        this.embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
            }
        });

        playButton.setOnKeyPressed(e -> {
            if (!isPaused)
                pause();
            else
                resume();
        });

        nextButton.setOnKeyPressed(e -> {
            goAhead();
        });

        prevButton.setOnKeyPressed(e -> {
            goBack();
        });
    }

    private void switchSubtitleTrack(int trackNumber) {
        embeddedMediaPlayer.subpictures().setTrack(trackNumber);
    }

    public void stop() {
        embeddedMediaPlayer.controls().stop();
        embeddedMediaPlayer.release();
        mediaPlayerFactory.release();

        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    public void resume(){
        embeddedMediaPlayer.controls().play();
    }

    public void pause(){
        embeddedMediaPlayer.controls().pause();
    }

    public void volumeUp(){
        embeddedMediaPlayer.audio().setVolume(embeddedMediaPlayer.audio().volume() + 10);
    }

    public void volumeDown(){
        embeddedMediaPlayer.audio().setVolume(embeddedMediaPlayer.audio().volume() - 10);
    }

    public void goAhead(){
        embeddedMediaPlayer.controls().setPosition((float) (embeddedMediaPlayer.status().position() + 0.005));
    }

    public void goBack(){
        embeddedMediaPlayer.controls().setPosition((float) (embeddedMediaPlayer.status().position() - 0.005));
    }

    public void showAudioTracks(){
        pause();
    }

    public void showSubtitleTracks(){
        pause();
    }
}
