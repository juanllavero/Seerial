package com.example.executablelauncher.entities;

import com.example.executablelauncher.App;
import com.example.executablelauncher.VideoPlayerController;
import com.example.executablelauncher.fileMetadata.ChaptersContainer;
import com.example.executablelauncher.tmdbMetadata.images.Images;
import com.example.executablelauncher.tmdbMetadata.movies.MovieMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import net.coobird.thumbnailator.Thumbnails;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        /*Episode episode = new Episode();
        episode.setVideoSrc("F:\\UHD\\Dune\\Dune (2021).mkv");
        episode.setName("Dune (2021)");

        getChapters(episode);

        for (Chapter chapter : episode.getChapters()){
            generateThumbnail(chapter);
        }*/

        // Ruta del archivo PNG original y comprimido
        /*String inputImagePath = "out/artifacts/Seerial/resources/img/DownloadCache/1315631.png";
        String outputImagePath = "out/artifacts/Seerial/resources/img/DownloadCache/COMPRESSED.png";

        setTransparencyEffect(inputImagePath, outputImagePath);*/

        /*MovieMetadata movieMetadata = downloadMovieMetadata(525);

        if (movieMetadata != null)
            System.out.println(movieMetadata.title);*/

        downloadDefaultMusic();
    }

    public static void downloadDefaultMusic(){
        Task<Void> musicDownloadTask = new Task<>() {
            @Override
            protected Void call() {
                List<YoutubeVideo> results = searchYoutube("The Lord of the Rings" + " main theme");

                if (results != null)
                    downloadMedia(results.get(0).watch_url);
                return null;
            }
        };

        Thread thread = new Thread(musicDownloadTask);
        thread.setDaemon(true);
        thread.start();
    }

    public static List<YoutubeVideo> searchYoutube(String videoName){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "resources/python/YoutubeSearch.py", videoName);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            String regex = "\\[\\{\".*?\"\\}\\]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(output);

            YoutubeVideo[] searchResults = null;
            if (matcher.find()) {
                ObjectMapper objectMapper = new ObjectMapper();
                searchResults = objectMapper.readValue(matcher.group(0), YoutubeVideo[].class);
            }

            process.waitFor();

            if (searchResults != null)
                return List.of(searchResults);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error searching in youtube");
        }

        return null;
    }

    public static boolean downloadMedia(String url){
        System.out.println(url);

        try {
            Files.createDirectories(Paths.get("resources/downloadedMediaCache/" + "IDPRUEBA" + "/"));
            File directory = new File("resources/downloadedMediaCache/" + "IDPRUEBA" + "/");

            ProcessBuilder pb;
            pb = new ProcessBuilder("pytube"
                    , url
                    , "-a", "-t", directory.getAbsolutePath());

            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            File[] filesInMediaCache = directory.listFiles();

            if (filesInMediaCache != null && filesInMediaCache.length != 0){
                File audioFile = filesInMediaCache[0];

                try{
                    Files.copy(audioFile.toPath(), Paths.get("resources/music/" + "IDPRUEBA" + ".mp4"), StandardCopyOption.REPLACE_EXISTING);

                    //FileUtils.forceDelete(directory);
                } catch (IOException error) {
                    System.err.println("downloadMedia: Could not copy downloaded audio file");
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("downloadMedia: Error downloading media");
            return false;
        }

        return true;
    }

    //region TEST
    public static MovieMetadata downloadMovieMetadata(int tmdbID){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/" + tmdbID + "?language=es-ES")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();

                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), MovieMetadata.class);
            } else {
                System.out.println("Response not successful: " + response.code());
            }
        } catch (IOException e) {
            //System.err.println("downloadMovieMetadata: movie metadata could not be downloaded");
            e.printStackTrace();
        }

        return null;
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

    //endregion

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}