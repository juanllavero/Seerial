package com.example.executablelauncher.entities;

import com.example.executablelauncher.VideoPlayerController;
import com.example.executablelauncher.fileMetadata.ChaptersContainer;
import com.example.executablelauncher.tmdbMetadata.images.Images;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class VideoMetadataExample extends Application {

    public static void main(String[] args) throws IOException {
        // Ruta del archivo de vídeo
        /*String pathToVideo = "F:\\The Middle-Earth Collection\\La Comunidad Del Anillo\\El Señor de los Anillos - La Comunidad del Anillo (2001).mkv";
        String pathToVideo2 = "F:\\ANIME\\[BDMV] FullMetal Alchemist Brotherhood\\S1\\Fullmetal Alchemist Brotherhood S01E01-E02.m2ts";

        // Obtener metadatos del vídeo
        getVideoMetadata(pathToVideo);

        // Obtener metadatos del audio
        getAudioMetadata(pathToVideo);

        // Obtener metadatos de los subtítulos
        getSubtitleMetadata(pathToVideo);*/

        Episode episode = new Episode();
        episode.setVideoSrc("F:\\[TEST]\\Dune\\Dune (2021).mkv");
        episode.setName("Dune (2021)");

        getChapters(episode);

        for (Chapter chapter : episode.getChapters()){
            generateThumbnail(chapter);
        }
    }

    private static void getChapters(Episode episode){
        try {
            ProcessBuilder processBuilder;
            processBuilder = new ProcessBuilder("ffprobe"
                    , "-v", "quiet", "-print_format", "json", "-show_chapters", episode.getVideoSrc());

            Process process = processBuilder.start();

            String stdout = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());

            if (stdout != null){
                ObjectMapper objectMapper = new ObjectMapper();
                ChaptersContainer chaptersContainer = objectMapper.readValue(stdout, ChaptersContainer.class);


                for (com.example.executablelauncher.fileMetadata.Chapter c : chaptersContainer.chapters){
                    if (c.tags != null){
                        double milliseconds = c.start / 1_000_000.0;
                        Chapter chapter = new Chapter(c.tags.title, milliseconds);
                        System.out.println(chapter.title);
                        System.out.println(chapter.time);
                        System.out.println(chapter.displayTime);

                        episode.addChapter(chapter);
                    }
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("getChapters: Error getting chapters");
        }
    }

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
                    "-nostdin",
                    "-y",
                    "-ss",
                    chapter.getDisplayTime(),
                    "-i",
                    "F:\\[TEST]\\Dune\\Dune (2021).mkv",
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

    private static void getVideoMetadata(String videoFilePath) {
        String[] command = {
                "ffmpeg",
                "-i", videoFilePath
        };

        executeFFmpegCommand(command, "Metadatos del Vídeo");
    }

    private static void getAudioMetadata(String videoFilePath) {
        String[] command = {
                "ffmpeg",
                "-i", videoFilePath,
                "-select_streams", "a",
                "-show_entries", "stream=index,codec_name,bit_rate,channels",
                "-of", "default=noprint_wrappers=1:nokey=1"
        };

        executeFFmpegCommand(command, "Metadatos del Audio");
    }

    private static void getSubtitleMetadata(String videoFilePath) {
        String[] command = {
                "ffmpeg",
                "-i", videoFilePath,
                "-map", "0:s",
                "-show_entries", "stream=index,codec_name,language,title,forced",
                "-of", "default=noprint_wrappers=1:nokey=1"
        };

        executeFFmpegCommand(command, "Metadatos de Subtítulos");
    }

    private static void executeFFmpegCommand(String[] command, String metadataType) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            System.out.println(metadataType + ":");

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}