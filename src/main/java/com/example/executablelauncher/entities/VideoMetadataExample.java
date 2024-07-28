package com.example.executablelauncher.entities;

import de.androidpit.colorthief.ColorThief;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VideoMetadataExample extends Application {
    static float x = 0.5f;
    static float y = 0.4f;
    static float r = 0.8f;
    static float opacity = 0.05f;
    static StackPane mainBox = new StackPane();
    static Pane shade = new Pane();
    static Pane fill = new Pane();

    static List<File> images = new ArrayList<>();
    static File currentImage;
    ImageView img = new ImageView();
    ImageView noiseImage = new ImageView();
    //region TEST
    private static void generateThumbnail(Chapter chapter){
        try{
            Files.createDirectories(Paths.get("resources/img/chaptersCovers/" + "test" + "/"));
        } catch (IOException e) {
            System.err.println("generateThumbnail: Error creating directory");
        }

        File thumbnail = new File("resources/img/chaptersCovers/" + "test" + "/" + chapter.getTime() + ".jpg");

        chapter.setThumbnailSrc("resources/img/chaptersCovers/" + "test" + "/" + chapter.getTime() + ".jpg");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg",
                    "-y",
                    "-ss",
                    chapter.getDisplayTime(),
                    "-i",
                    "F:\\UHD\\Dune\\Dune (2021).mkv",
                    "-vframes",
                    "1",
                    thumbnail.getAbsolutePath());

            Process process = processBuilder.start();
            InputStream stderr = process.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null);
            process.waitFor();
        } catch (IOException e) {
            chapter.setThumbnailSrc("");
            System.err.println("Error generating thumbnail for chapter " + "test" + "/" + chapter.getTime());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion
    public static void main(String[] args) throws IOException {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        File folder = new File("resources/img/test/");

        images.addAll(List.of(Objects.requireNonNull(folder.listFiles())));

        noiseImage = new ImageView(new Image("file:resources/img/noise.png"));

        img.setPreserveRatio(true);
        img.setOpacity(0.6);

        mainBox.getChildren().add(img);
        StackPane.setAlignment(img, Pos.TOP_RIGHT);

        HBox shadeBox = new HBox(fill, shade);
        mainBox.getChildren().add(shadeBox);

        noiseImage.setFitWidth(3440);
        noiseImage.setFitHeight(1440);
        noiseImage.setPreserveRatio(false);
        noiseImage.setOpacity(opacity);
        mainBox.getChildren().add(noiseImage);

        shade.setMaxWidth((double) 2560 * 1);
        shade.setMinWidth((double) 2560 * 1);
        shade.setMaxHeight(1440);
        shade.setMinHeight(1440);
        //StackPane.setAlignment(shade, Pos.TOP_RIGHT);

        primaryStage.setScene(new Scene(mainBox));
        primaryStage.setMaximized(true);
        primaryStage.show();

        primaryStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.RIGHT))
                x += 0.1f;
            else if (e.getCode().equals(KeyCode.LEFT))
                x -= 0.1f;
            else if (e.getCode().equals(KeyCode.DOWN))
                y += 0.1f;
            else if (e.getCode().equals(KeyCode.UP))
                y -= 0.1f;
            else if (e.getCode().equals(KeyCode.ADD))
                r += 0.1f;
            else if (e.getCode().equals(KeyCode.SUBTRACT))
                r -= 0.1f;
            else if (e.getCode().equals(KeyCode.ENTER))
                opacity += 0.1f;
            else if (e.getCode().equals(KeyCode.ESCAPE))
                opacity -= 0.1f;

            BufferedImage bufferedImage = null;

            if (e.getCode().equals(KeyCode.E)){
                if (currentImage != null && images.indexOf(currentImage) < images.size() - 1){
                    currentImage = images.get(images.indexOf(currentImage) + 1);
                }else{
                    currentImage = images.getFirst();
                }

                Image image = null;
                try {
                    image = new Image(currentImage.toURI().toURL().toExternalForm());
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }

                img.setImage(image);
                img.setFitHeight(Screen.getPrimary().getBounds().getHeight());

                double aspectRatio = image.getWidth() / image.getHeight();

                if (aspectRatio < (double) 16/9){
                    img.setImage(image);

                    double originalWidth = img.getImage().getWidth();
                    double originalHeight = img.getImage().getHeight();

                    double targetAspectRatio = (double) 16/9;
                    double newWidth = originalWidth;
                    double newHeight = originalWidth / targetAspectRatio;

                    int xOffset = 0;
                    int yOffset = (int) ((image.getHeight() - newHeight) / 2);

                    PixelReader pixelReader = img.getImage().getPixelReader();
                    WritableImage croppedImage = new WritableImage(pixelReader, xOffset, 0, (int) newWidth, (int) newHeight);

                    img.setImage(croppedImage);
                }

                try {
                    bufferedImage = ImageIO.read(currentImage);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            noiseImage.setOpacity(opacity);
            applyGradient(bufferedImage);
            System.out.println(x + ", " + y + ", " + r + ", " + opacity);
        });
    }

    private static void applyGradient(BufferedImage bufferedImage){
        int[] colorValues = ColorThief.getColor(bufferedImage, 10, true);
        Color dominantColor = Color.BLACK;

        if (colorValues != null)
            dominantColor = Color.rgb(colorValues[0], colorValues[1], colorValues[2]);

        bufferedImage.flush();

        RadialGradient shadePaint = new RadialGradient(
                0, 1, x, y, r, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.5, Color.TRANSPARENT),
                new Stop(0.8, dominantColor)
        );

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double aspectRatio = screenWidth / screenHeight;

        if (aspectRatio > (double) 16/9){
            shade.setPrefHeight(screenHeight);
            shade.setPrefWidth(screenWidth / (double) 16/9);

            fill.setPrefWidth(screenWidth - shade.getPrefWidth());
            fill.setPrefHeight(screenHeight);
        }else{
            shade.setPrefWidth(screenWidth);
            shade.setPrefHeight(screenHeight);

            fill.setPrefWidth(0);
        }

        fill.setBackground(
                new Background(
                        new BackgroundFill(
                                dominantColor, null, new Insets(-10)
                        )
                )
        );

        shade.setBackground(
                new Background(
                        new BackgroundFill(
                                shadePaint, null, new Insets(-10)
                        )
                )
        );

        mainBox.setBackground(
                new Background(
                        new BackgroundFill(
                                dominantColor, null, new Insets(-10)
                        )
                )
        );

        shade.setEffect(new BoxBlur(10, 10, 4));
    }
}