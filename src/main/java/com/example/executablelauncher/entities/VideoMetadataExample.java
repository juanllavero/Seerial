package com.example.executablelauncher.entities;

import javafx.application.Application;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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
    public static BufferedImage processBlurAndSave(String imagePath, String outputFilePath) {
        try {
            // Load the image
            Mat originalImage = Imgcodecs.imread(imagePath);

            // Apply GaussianBlur effect
            Mat blurredImage = new Mat();
            int kernelSize = 71;
            Imgproc.GaussianBlur(originalImage, blurredImage, new Size(kernelSize, kernelSize), 0);

            // Crop a portion of the blurred image
            int cropX = (int) (blurredImage.cols() * 0.03);
            int cropY = (int) (blurredImage.rows() * 0.05);
            int cropWidth = (int) (blurredImage.cols() * 0.93);
            int cropHeight = (int) (blurredImage.rows() * 0.9);

            Mat croppedImage = new Mat(blurredImage, new Rect(cropX, cropY, cropWidth, cropHeight));

            // Save the cropped blurred image to cache
            Imgcodecs.imwrite(outputFilePath, croppedImage);

            // Release resources
            originalImage.release();
            blurredImage.release();
            croppedImage.release();

            return ImageIO.read(new File(outputFilePath));
        } catch (Exception e) {
            System.err.println("Image processing error: " + e.getMessage());
        }

        return null;
    }
    private static BufferedImage mergeImages(BufferedImage originalImage, BufferedImage secondImage, float opacity){
        Image resizedNoiseImage = secondImage.getScaledInstance(originalImage.getWidth(), originalImage.getHeight(), Image.SCALE_SMOOTH);
        BufferedImage resizedNoiseBufferedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2dResized = resizedNoiseBufferedImage.createGraphics();
        g2dResized.drawImage(resizedNoiseImage, 0, 0, null);
        g2dResized.dispose();

        BufferedImage combinedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dCombined = combinedImage.createGraphics();
        g2dCombined.drawImage(originalImage, 0, 0, null);
        g2dCombined.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2dCombined.drawImage(resizedNoiseBufferedImage, 0, 0, null);
        g2dCombined.dispose();

        resizedNoiseImage.flush();
        resizedNoiseBufferedImage.flush();

        return combinedImage;
    }

    public static void main(String[] args) throws IOException {
        OpenCV.loadLocally();
        try {
            String originalImagePath = "resources/img/test/TEST.jpg";
            String noiseImagePath = "resources/img/noise.png";
            String shadowImagePath = "resources/img/desktopBackgroundShadow.png";

            BufferedImage originalImage = processBlurAndSave("resources/img/test/testDBZ.png", originalImagePath);
            BufferedImage noiseImage = ImageIO.read(new File(noiseImagePath));
            BufferedImage shadowImage = ImageIO.read(new File(shadowImagePath));

            if (originalImage == null)
                return;

            BufferedImage resultImage = mergeImages(originalImage, noiseImage, 0.08f);
            BufferedImage result2 = mergeImages(resultImage, shadowImage, 0.5f);

            try {
                Thumbnails.of(result2)
                        .scale(1)
                        .outputQuality(0.9)
                        .toFile("resources/img/test/FINAL.jpg");
            } catch (IOException e) {
                System.err.println("saveBackground: error compressing background image");
            }

            originalImage.flush();
            noiseImage.flush();
            shadowImage.flush();
            resultImage.flush();
            result2.flush();

            System.out.println("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
    }
}