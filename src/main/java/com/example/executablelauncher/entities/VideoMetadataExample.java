package com.example.executablelauncher.entities;

import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;

public class VideoMetadataExample {
    public static void main(String[] args) {
        String pathToVideo = "F:\\The Middle-Earth Collection\\La Comunidad Del Anillo\\El Señor de los Anillos - La Comunidad del Anillo (2001).mkv";
        String pathToVideo2 = "F:\\ANIME\\[BDMV] FullMetal Alchemist Brotherhood\\S1\\Fullmetal Alchemist Brotherhood S01E01-E02.m2ts";

        FFprobeResult result = FFprobe.atPath()
                .setShowStreams(true)
                .setInput(pathToVideo)
                .execute();

        for (Stream stream : result.getStreams()) {
            System.out.println("Stream #" + stream.getIndex()
                    + " type: " + stream.getCodecType()
                    + " codec: " + stream.getCodecName());
            System.out.println(stream.getProfile());
            System.out.println(stream.getMaxBitRate());
        }

    }
}