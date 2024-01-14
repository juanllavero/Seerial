package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
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

    @FXML
    private VBox controlsBox;
    //endregion

    public MediaPlayerFactory mediaPlayerFactory;

    public EmbeddedMediaPlayer embeddedMediaPlayer;

    boolean isPaused = false;
    SeasonController parentController = null;
    Timeline timeline = null;

    public void setVideo(SeasonController parent, Disc disc, String seriesName, Scene scene){
        parentController = parent;
        onLoad();

        seriesTitle.setText(seriesName);
        setDiscValues(disc);


        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        videoImage.setFitWidth(screenWidth);
        videoImage.setFitHeight(screenHeight);

        shadowImage.setFitWidth(screenWidth);
        shadowImage.setFitHeight(screenHeight);
        shadowImage.setPreserveRatio(false);

        shadowImage.setVisible(false);
        controlsBox.setVisible(false);

        timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.seconds(5), event -> {
                hideControls();
            })
        );
        timeline.play();

        scene.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ESCAPE) || e.getCode().equals(KeyCode.BACK_SPACE))
                stop();
        });

        scene.setOnKeyPressed(e -> {
            if (!controlsBox.isVisible()){
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

        embeddedMediaPlayer.media().play(disc.executableSrc);

        embeddedMediaPlayer.controls().setPosition(0);

        /*InvalidationListener sliderChangeListener = o-> {
            Duration seekTo = Duration.seconds(runtimeSlider.getValue());
            embeddedMediaPlayer.controls().setTime((long) seekTo.toMinutes());
        });
        runtimeSlider.valueProperty().addListener(sliderChangeListener);

        embeddedMediaPlayer.media().events().addMediaEventListener(l-> {
            runtimeSlider.valueProperty().removeListener(sliderChangeListener);

            long currentTime = embeddedMediaPlayer.status().time();
            runtimeSlider.setValue(currentTime);

            runtimeSlider.valueProperty().addListener(sliderChangeListener);
        });*/
    }

    private void onLoad(){
        mediaPlayerFactory = new MediaPlayerFactory("--tone-mapping=3", "--tone-mapping-param=0.5");
        embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImage));
        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
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

    private void setDiscValues(Disc disc){
        episodeTitle.setText(disc.name);
        episodeDate.setText(disc.year);
        seasonEpisode.setText(App.textBundle.getString("seasonLetter") + disc.seasonNumber + " " + App.textBundle.getString("episodeLetter") + disc.episodeNumber);
        runtime.setText(parentController.setRuntime(disc.runtime));

        currentTime.setText("00:00");
        //toFinishTime.setText(formatTime(disc.runtime));
    }

    private void switchSubtitleTrack(int trackNumber) {
        embeddedMediaPlayer.subpictures().setTrack(trackNumber);
    }

    private void showControls(){
        timeline.playFromStart();
        controlsBox.setVisible(true);
        shadowImage.setVisible(true);
    }

    private void hideControls(){
        timeline.stop();
        controlsBox.setVisible(false);
        shadowImage.setVisible(false);
    }

    public void stop() {
        embeddedMediaPlayer.controls().stop();
        embeddedMediaPlayer.release();
        mediaPlayerFactory.release();

        parentController.stopVideo();

        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    public void resume(){
        hideControls();
        isPaused = false;
        embeddedMediaPlayer.controls().play();
    }

    public void pause(){
        isPaused = true;
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
