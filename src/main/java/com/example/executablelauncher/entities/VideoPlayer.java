package com.example.executablelauncher.entities;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.List;

public class VideoPlayer extends Application {

    public final MediaPlayerFactory mediaPlayerFactory;

    public final EmbeddedMediaPlayer embeddedMediaPlayer;

    public ImageView videoImageView;

    boolean isPaused = false;

    public VideoPlayer() {
        this.mediaPlayerFactory = new MediaPlayerFactory("--tone-mapping=3", "--tone-mapping-param=0.5");
        this.embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
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
    }

    @Override
    public void init() {
        this.videoImageView = new ImageView();
        this.videoImageView.setPreserveRatio(true);

        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.videoImageView));
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        String sdrVideo = "F:\\[ANIME TEST]\\One Piece\\One Piece - 1015.mkv";
        String hdrVideo = "F:\\The Middle-Earth Collection\\IV - La Comunidad Del Anillo\\El Señor de los Anillos - La Comunidad del Anillo (2001).mkv";
        String blurayISO = "F:\\BD\\Devilman Crybaby\\DISC1.iso";

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        videoImageView.fitWidthProperty().bind(root.widthProperty());
        videoImageView.fitHeightProperty().bind(root.heightProperty());

        root.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            // If you need to know about resizes
        });

        root.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            // If you need to know about resizes
        });

        root.setCenter(videoImageView);

        Scene scene = new Scene(root, 1200, 675, Color.BLACK);

        scene.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ESCAPE))
                stop();
            else if (e.getCode().equals(KeyCode.SPACE)){
                if (!isPaused)
                    embeddedMediaPlayer.controls().pause();
                else
                    embeddedMediaPlayer.controls().play();
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
                embeddedMediaPlayer.controls().setPosition((float) (embeddedMediaPlayer.status().position() + 0.005));
            else if (e.getCode().equals(KeyCode.LEFT))
                embeddedMediaPlayer.controls().setPosition((float) (embeddedMediaPlayer.status().position() - 0.005));
            else if (e.getCode().equals(KeyCode.M))
                embeddedMediaPlayer.menu().activate();
            else if (e.getCode().equals(KeyCode.UP))
                embeddedMediaPlayer.audio().setVolume(embeddedMediaPlayer.audio().volume() + 10);
            else if (e.getCode().equals(KeyCode.DOWN))
                embeddedMediaPlayer.audio().setVolume(embeddedMediaPlayer.audio().volume() - 10);
        });

        primaryStage.setTitle("vlcj JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        embeddedMediaPlayer.media().play(hdrVideo);

        embeddedMediaPlayer.controls().setPosition(0.4f);
    }

    private void switchSubtitleTrack(int trackNumber) {
        embeddedMediaPlayer.subpictures().setTrack(trackNumber);
    }

    @Override
    public final void stop() {
        embeddedMediaPlayer.controls().stop();
        embeddedMediaPlayer.release();
        mediaPlayerFactory.release();
    }

    public static void main(String[] args) {
        launch(args);
    }
}