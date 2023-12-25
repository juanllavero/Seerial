package com.example.executablelauncher;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ZoomableImageView extends Application {

    private double ASPECT_RATIO = 16.0 / 9.0;

    @Override
    public void start(Stage primaryStage) {
        // Cargar la imagen
        Image image = new Image("file:src/main/resources/img/backgrounds/0_sb.png");

        ASPECT_RATIO = image.getWidth() / image.getHeight();

        // Crear el ImageView y configurar su propiedad de preservación de relación de aspecto
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        // Crear un StackPane y agregar el ImageView
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageView);

        // Configurar la posición del ImageView en el centro
        StackPane.setAlignment(imageView, javafx.geometry.Pos.CENTER);

        // Crear la escena
        Scene scene = new Scene(stackPane, 500, 500);

        // Configurar el comportamiento de redimensionamiento
        ChangeListener<Number> widthListener = (obs, oldWidth, newWidth) -> {
            double scaleFactor;
            double currentAspectRatio = newWidth.doubleValue() / primaryStage.heightProperty().doubleValue();

            if (currentAspectRatio >= ASPECT_RATIO) {
                scaleFactor = newWidth.doubleValue() / imageView.getImage().getWidth();
                imageView.setFitWidth(newWidth.doubleValue());
                imageView.setFitHeight(imageView.getImage().getHeight() * scaleFactor);
            }
        };

        ChangeListener<Number> heightListener = (obs, oldHeight, newHeight) -> {
            double scaleFactor;
            double currentAspectRatio = primaryStage.widthProperty().doubleValue() / newHeight.doubleValue();
            double ratioHeight = primaryStage.widthProperty().doubleValue() / ASPECT_RATIO;
            if (ratioHeight < newHeight.doubleValue()){
                scaleFactor = newHeight.doubleValue() / imageView.getImage().getHeight();
                imageView.setFitHeight(newHeight.doubleValue());
                imageView.setFitWidth(imageView.getImage().getWidth() * scaleFactor);
            }
        };

        primaryStage.widthProperty().addListener(widthListener);
        primaryStage.heightProperty().addListener(heightListener);

        // Configurar la ventana principal
        primaryStage.setTitle("Zoomable ImageView");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}