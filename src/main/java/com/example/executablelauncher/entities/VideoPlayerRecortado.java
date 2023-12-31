package com.example.executablelauncher.entities;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;

public class VideoPlayerRecortado extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Ruta del archivo de video (ajusta la ruta según tu caso)
        String videoFile = "src/main/resources/video/a8b73b0c-aa45-42ce-a150-fc65de656814_sv.mp4";
        File file = new File(videoFile);

        // Crear un objeto Media
        Media media = new Media(file.toURI().toString());

        // Crear el reproductor de medios
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Crear la vista de medios
        MediaView mediaView = new MediaView(mediaPlayer);

        // Obtener la pantalla principal
        Screen screen = Screen.getPrimary();

        // Obtener las dimensiones de la pantalla
        double screenWidth = screen.getBounds().getWidth();
        double screenHeight = screen.getBounds().getHeight();

        // Obtener las dimensiones originales del video
        double videoWidth = media.getWidth();
        double videoHeight = media.getHeight();

        // Calcular la relación de aspecto del video y de la pantalla
        double videoAspectRatio = videoWidth / videoHeight;
        double screenAspectRatio = screenWidth / screenHeight;

        // Configurar la vista de medios para ajustar al ancho o al alto de la pantalla
        if (videoAspectRatio > screenAspectRatio) {
            mediaView.fitWidthProperty().bind(primaryStage.widthProperty());
            mediaView.fitHeightProperty().unbind();
        } else {
            mediaView.fitHeightProperty().bind(primaryStage.heightProperty());
            mediaView.fitWidthProperty().unbind();
        }

        // Configurar la escena
        StackPane root = new StackPane();
        root.getChildren().add(mediaView);
        Scene scene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Video Player");

        // Configurar el cierre de la aplicación al finalizar el video
        mediaPlayer.setOnEndOfMedia(() -> primaryStage.close());

        // Iniciar la reproducción
        mediaPlayer.play();

        // Mostrar la ventana principal
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}