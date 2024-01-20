package com.example.executablelauncher.entities;

import com.example.executablelauncher.tmdbMetadata.series.SeriesMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiExample {

    public static void main(String[] args){
        try {
            File directory = new File("src/main/resources/downloadedMediaCache/");

            ProcessBuilder pb;
            pb = new ProcessBuilder("pytube"
                , "https://www.youtube.com/watch?v=-jGBp5HBLFs"
                , "-t", directory.getAbsolutePath());

            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("downloadMedia: Error downloading media");
        }
    }
}