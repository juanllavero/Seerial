package com.example.executablelauncher.entities;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestCompression extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Image originalImage = new Image("file:src/main/resources/img/testCompression.jpg", 480, 270, true, true);

        double maxWidth = 480;
        double maxHeight = 270;
        double originalWidth = originalImage.getWidth();
        double originalHeight = originalImage.getHeight();

        Image compressedImage;
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);

            BufferedImage resizedImage = Thumbnails.of(bufferedImage)
                    .size((int) maxWidth, (int) maxHeight)
                    .outputFormat("jpg")
                    .asBufferedImage();

            compressedImage = SwingFXUtils.toFXImage(resizedImage, null);
        }else{
            compressedImage = originalImage;
        }

        if (!compressedImage.isError()){
            File file = new File("src/main/resources/img/testCompressed.png");
            try{
                BufferedImage renderedImage = SwingFXUtils.fromFXImage(compressedImage, null);
                ImageIO.write(renderedImage,"jpg", file);
            } catch (IOException e) {
                System.err.println("Disc downloaded thumbnail not saved");
            }
        }
    }
}
