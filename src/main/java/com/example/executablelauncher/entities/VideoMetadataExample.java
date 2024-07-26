package com.example.executablelauncher.entities;

import com.example.executablelauncher.fileMetadata.AudioTrack;
import com.example.executablelauncher.fileMetadata.MediaInfo;
import com.example.executablelauncher.fileMetadata.SubtitleTrack;
import com.example.executablelauncher.fileMetadata.VideoTrack;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Format;
import com.github.kokorin.jaffree.ffprobe.Stream;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class VideoMetadataExample extends Application {

    public static void main(String[] args) throws IOException {

        System.out.println(Locale.of("es").getLanguage());
        System.out.println(Locale.of("es").getDisplayName());
        System.out.println(Locale.of("es").getISO3Language());

        launch(args);
    }

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

    @Override
    public void start(Stage primaryStage) throws Exception {
    }
}