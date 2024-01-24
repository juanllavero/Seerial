package com.example.executablelauncher.videoPlayer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class VideoPlayerApp extends Application {

    private VideoPlayer videoPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Reproductor de Video con MPV");

        videoPlayer = new VideoPlayer();
        StackPane root = new StackPane();
        root.getChildren().add(videoPlayer);
        Scene scene = new Scene(root);

        // Agrega un escuchador de eventos de teclado a la escena
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode().toString()));

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setMaximized(true);
        primaryStage.show();

        String videoSrc = "F:\\The Middle-Earth Collection\\IV - La Comunidad Del Anillo\\El Señor de los Anillos - La Comunidad del Anillo (2001).mkv";
        String video = "F:\\[TEST]\\El Caballero Oscuro\\El Caballero Oscuro\\El Caballero Oscuro (2008).mkv";

        // Inicia la reproducción del video
        videoPlayer.playVideo(videoSrc);
    }

    private void handleKeyPress(String key) {
        switch (key) {
            case "SPACE":
                videoPlayer.togglePause();
                break;
            case "ADD":
                videoPlayer.adjustVolume(true);
                break;
            case "SUBTRACT":
                videoPlayer.adjustVolume(false);
                break;
            case "RIGHT":
                videoPlayer.seekForward();
                break;
            case "LEFT":
                videoPlayer.seekBackward();
                break;
            case "A":
                System.out.println(videoPlayer.getAudioTracks());
                break;
            case "S":
                System.out.println(videoPlayer.getSubtitleTracks());
                break;
            case "M":
                videoPlayer.changeAspectRatio();
                break;
            case "Z":
                videoPlayer.fixZoom(0.45);
                break;
            case "X":
                videoPlayer.zoomOut();
                break;
        }
    }
}