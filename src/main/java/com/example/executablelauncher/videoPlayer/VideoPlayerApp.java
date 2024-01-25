package com.example.executablelauncher.videoPlayer;

import com.example.executablelauncher.App;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VideoPlayerApp extends Application {

    private VideoPlayer videoPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(App.textBundle.getString("season"));

        videoPlayer = new VideoPlayer();
        StackPane root = new StackPane();
        root.getChildren().add(videoPlayer);
        Scene scene = new Scene(root, 800, 600);

        // Agrega un escuchador de eventos de teclado a la escena
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode().toString()));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeyPress(String key) {
        switch (key) {
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