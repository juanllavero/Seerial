package com.example.executablelauncher.entities;

import com.github.kokorin.jaffree.ffmpeg.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

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
        String pathToSrc = "F:\\Fullmetal Alchemist Brotherhood - Creditless OP-ED\\[A&C] Fullmetal Alchemist Brotherhood - NCOP 01 [BDRip 1080p] [V2] [66687D1D].mkv";
        String pathToDst = "F:\\Fullmetal Alchemist Brotherhood - Creditless OP-ED\\test.mp4";

        final AtomicLong duration = new AtomicLong();
        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(pathToSrc))
                .setOverwriteOutput(true)
                .addOutput(new NullOutput())
                .setProgressListener(progress -> duration.set(progress.getTimeMillis()))
                .execute();

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(pathToSrc))
                .setOverwriteOutput(true)
                .addArguments("-movflags", "faststart")
                .addArguments("-preset", "ultrafast")
                .addOutput(UrlOutput.toUrl(pathToDst))
                .setProgressListener(progress -> {
                    double percents = 100. * progress.getTimeMillis() / duration.get();
                    System.out.printf("Progress: %.2f%%%n", percents);
                })
                .execute();

        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
    }
}