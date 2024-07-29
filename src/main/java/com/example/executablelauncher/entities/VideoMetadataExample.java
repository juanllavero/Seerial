package com.example.executablelauncher.entities;

import com.example.executablelauncher.utils.SVGIcon;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VideoMetadataExample extends Application {
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
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #000000");

        String svgFilePath = "resources/img/icons/plex.svg";
        SVGIcon svgIcon = new SVGIcon(svgFilePath);
        SVGIcon svgIcon2 = new SVGIcon(svgFilePath);
        SVGIcon svgIcon3 = new SVGIcon(svgFilePath);

        svgIcon2.setScaleFactor(1);
        svgIcon3.setScaleFactor(1.5);
        svgIcon.setScaleFactor(0.5);

        HBox container = new HBox(svgIcon, svgIcon2, svgIcon3);

        root.getChildren().add(container);

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("SVG Icon Loader Demo");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.E))
                svgIcon2.setColor(Color.WHITE);

            if (e.getCode().equals(KeyCode.F))
                svgIcon2.setColor(Color.TRANSPARENT);

            if (e.getCode().equals(KeyCode.K))
                svgIcon2.setStroke(Color.BLUE);
        });
    }

    private static ImageView loadSVG(String filePath) {
        try {
            InputStream inputStream = new FileInputStream(filePath);
            // Cargar la imagen SVG
            Image svgImage = new Image(inputStream);
            return new ImageView(svgImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}