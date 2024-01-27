package com.example.executablelauncher.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VideoMetadataExample {

    public static void main(String[] args) {
        // Ruta del archivo de vídeo
        String pathToVideo = "F:\\The Middle-Earth Collection\\La Comunidad Del Anillo\\El Señor de los Anillos - La Comunidad del Anillo (2001).mkv";
        String pathToVideo2 = "F:\\ANIME\\[BDMV] FullMetal Alchemist Brotherhood\\S1\\Fullmetal Alchemist Brotherhood S01E01-E02.m2ts";

        // Obtener metadatos del vídeo
        getVideoMetadata(pathToVideo);

        // Obtener metadatos del audio
        getAudioMetadata(pathToVideo);

        // Obtener metadatos de los subtítulos
        getSubtitleMetadata(pathToVideo);
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
}