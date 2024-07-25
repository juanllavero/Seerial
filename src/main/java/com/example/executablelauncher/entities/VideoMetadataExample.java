package com.example.executablelauncher.entities;

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
        //String pathToVideo = "F:\\UHD\\Mad Max - Fury Road\\Mad Max - Furia en la Carretera.mkv";
        String pathToVideo = "F:\\VER\\The Strongest Magician in the Demon Lord's Army Was a Human\\S01\\" +
                "The Strongest Magician in the Demon Lord's Army Was a Human - S01E01.mkv";
        File video = new File(pathToVideo);

        try {
            FFprobeResult result = FFprobe.atPath()
                    .setShowStreams(true)
                    .setShowFormat(true)
                    .setShowChapters(true)
                    .setInput(pathToVideo)
                    .execute();

            Format format = result.getFormat();
            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            //region MEDIA INFO
            System.out.println("Información del Medio:");

            float durationInSeconds = format.getDuration();
            System.out.println("Duration: " + formatTime(durationInSeconds));
            System.out.println("File: " + video.getName());
            System.out.println("Location: " + video.getAbsolutePath());
            System.out.println("Bitrate: " + format.getBitRate() / Math.pow(10, 3) + " kbps");

            //region FILE SIZE
            double fileSize = Double.valueOf(format.getSize());

            String sizeSufix = " GB";
            fileSize = fileSize / Math.pow(1024, 3);

            if (fileSize < 1) {
                fileSize = fileSize * Math.pow(1024, 1);
                sizeSufix = " MB";
            }

            System.out.println("Size: " + decimalFormat.format(fileSize) + sizeSufix);
            //endregion
            System.out.println("Container: " + video.getName().substring(video.getName().lastIndexOf(".") + 1).toUpperCase());
            //endregion

            //Boolean to stop the loop after the last subtitle track
            boolean subtitleSectionStarted = false;

            //Tracks
            List<Stream> streams = result.getStreams();
            for (Stream stream : streams) {
                if (stream.getCodecType().equals(StreamType.SUBTITLE))
                    subtitleSectionStarted = true;
                else if (subtitleSectionStarted)
                    break;

                if (stream.getCodecType().equals(StreamType.VIDEO))
                    processVideoData(stream);
                else if (stream.getCodecType().equals(StreamType.AUDIO))
                    processAudioData(stream);
                else if (stream.getCodecType().equals(StreamType.SUBTITLE))
                    processSubtitleData(stream);
            }

            //Chapters
            List<com.github.kokorin.jaffree.ffprobe.Chapter> chapters = result.getChapters();
            if (chapters != null && chapters.size() > 1){
                //DataManager.INSTANCE.createFolder("resources/img/chaptersCovers/" + episode.getId() + "/");
                for (com.github.kokorin.jaffree.ffprobe.Chapter chapter : chapters){
                    double milliseconds = chapter.getStart() / 1_000_000.0;

                    //Chapter newChapter = new Chapter(chapter.getTag("title"), milliseconds);
                    //episode.addChapter(newChapter);

                    System.out.println("Chapter: " + chapter.getTag("title") + "; Start: " + milliseconds + " ms");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }

    private static void processVideoData(Stream stream){
        DecimalFormat decimalFormatExtended = new DecimalFormat("#.###");
        DecimalFormat decimalFormatShort = new DecimalFormat("#.##");

        String resolution = "", hdr = "", codec = "", profile = "";


        System.out.println("\nVideo");

        System.out.println(stream.getIndex());
        if (stream.getCodecName() != null) {
            codec = stream.getCodecName().toUpperCase();
            System.out.println("Codec: " + codec);
        }

        if (stream.getCodecLongName() != null)
            System.out.println("Codec Extended: " + stream.getCodecLongName());

        if (stream.getTag("BPS-eng") != null)
            System.out.println("Bitrate: " + Math.round(Double.parseDouble(stream.getTag("BPS-eng"))  / Math.pow(10, 3)) + " kbps");

        if (stream.getAvgFrameRate() != null)
            System.out.println("Frame Rate: " + decimalFormatExtended.format(stream.getAvgFrameRate().floatValue()) + " fps");

        if (stream.getCodedWidth() != null && stream.getCodedHeight() != null) {
            resolution = formatResolution(stream.getCodedWidth(), stream.getCodedHeight());
            System.out.println("Coded Height: " + stream.getCodedHeight());
            System.out.println("Coded Width: " + stream.getCodedWidth());
        }

        if (stream.getChromaLocation() != null)
            System.out.println("Chroma Location: " + stream.getChromaLocation());

        if (stream.getColorSpace() != null) {
            if (stream.getColorSpace().equals("bt2020nc"))
                hdr = " HDR10";
            System.out.println("Color Space: " + stream.getColorSpace());
        }

        if (stream.getDisplayAspectRatio() != null)
            System.out.println("Aspect Ratio: " + decimalFormatShort.format(stream.getDisplayAspectRatio().floatValue()));

        if (stream.getProfile() != null) {
            profile = stream.getProfile();
            System.out.println("Profile: " + profile);
        }

        if (stream.getRefs() != null)
            System.out.println("Ref Frames: " + stream.getRefs());

        if (stream.getColorRange() != null)
            System.out.println("Color Range: " + stream.getColorRange());

        System.out.println("Display Title: " + resolution + hdr + " (" + codec + " " + profile + ")");
    }
    private static void processAudioData(Stream stream){
        String channels = "", language = "", codecDisplayName;

        System.out.println("\nAudio");

        System.out.println(stream.getIndex());

        if (stream.getCodecName() != null)
            System.out.println("Codec: " + stream.getCodecName().toUpperCase());

        if (stream.getCodecLongName() != null)
            System.out.println("Codec Extended: " + stream.getCodecLongName());

        if (stream.getChannels() != null)
            System.out.println("Channels: " + stream.getChannels());

        if (stream.getChannelLayout() != null)
            System.out.println("Channel Layout: " + stream.getChannelLayout());

        if (stream.getTag("BPS-eng") != null)
            System.out.println("Bitrate: " + Math.round(Double.parseDouble(stream.getTag("BPS-eng"))  / Math.pow(10, 3)) + " kbps");

        if (stream.getTag("language") != null) {
            language = Locale.of(stream.getTag("language")).getDisplayName();
            System.out.println("Language: " + language);

            System.out.println("Language tag: " + stream.getTag("language"));
        }

        if (stream.getBitsPerRawSample() != null)
            System.out.println("Bit Depth: " + stream.getBitsPerRawSample());

        if (stream.getProfile() != null && stream.getProfile().equals("DTS-HD MA"))
            System.out.println("Profile: ma");

        if (stream.getSampleRate() != null)
            System.out.println("Sampling Rate: " + stream.getSampleRate() + " Hz");

        if (stream.getProfile() != null && stream.getProfile().equals("DTS-HD MA")){
            codecDisplayName = stream.getProfile();
        } else {
            codecDisplayName = stream.getCodecName().toUpperCase();
        }

        if (stream.getChannels() != null)
            channels = formatAudioChannels(stream.getChannels());

        System.out.println("Display Title: " + language + " (" + codecDisplayName + " " + channels +")");
    }
    private static void processSubtitleData(Stream stream){
        String title = "", language = "", codecDisplayName = "";

        System.out.println("\nSubtitles");

        System.out.println(stream.getIndex());

        if (stream.getCodecName() != null){
            codecDisplayName = stream.getCodecName().toUpperCase();

            if (codecDisplayName.equals("HDMV_PGS_SUBTITLE"))
                codecDisplayName = "PGS";

            System.out.println("Codec: " + codecDisplayName);
        }

        if (stream.getCodecLongName() != null)
            System.out.println("Codec Extended: " + stream.getCodecLongName());

        if (stream.getTag("language") != null) {
            language = Locale.of(stream.getTag("language")).getDisplayName();
            System.out.println("Language: " + language);
            System.out.println("Language tag: " + stream.getTag("language"));
        }

        if (stream.getTag("title") != null) {
            title = stream.getTag("title");
            System.out.println("Title: " + title);
        }

        System.out.println("Display Title: " + title + " (" + language + " " + codecDisplayName +")");
    }
    private static String formatResolution(int width, int height) {
        return switch (width) {
            case 7680 -> "8K";
            case 3840 -> "4K";
            case 2560 -> "QHD";
            case 1920 -> "1080p";
            case 1280 -> "720p";
            case 854 -> "480p";
            case 640 -> "360p";
            default -> height + "p";
        };
    }
    private static String formatAudioChannels(int channels){
        return switch (channels) {
            case 1 -> "MONO";
            case 2 -> "STEREO";
            case 3 -> "2.1";
            case 4 -> "3.1";
            case 5 -> "4.1";
            case 6 -> "5.1";
            case 7 -> "6.1";
            case 8 -> "7.1";
            case 9 -> "7.2";
            case 10 -> "9.1";
            case 11 -> "10.1";
            case 12 -> "11.1";
            default -> String.valueOf(channels);
        };
    }
    private static String formatTime(float time){
        int h = (int) (time / 3600);
        int m = (int) ((time % 3600) / 60);
        int s = (int) (time % 60);

        if (h > 0)
            return String.format("%02d:%02d:%02d", h, m, s);

        return String.format("%02d:%02d", m, s);
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