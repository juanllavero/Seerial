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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class ApiExample {

    public static void main(String[] args){
        String searchText = "Jujutsu Kaisen opening";

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/resources/python/YoutubeSearch.py", searchText);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            while ((line = reader.readLine()) != null) {
               output.append(line).append("\n");
            }

            System.out.println(output);
            process.waitFor();

            ObjectMapper objectMapper = new ObjectMapper();
            YoutubeVideo[] searchResults = objectMapper.readValue(output.toString(), YoutubeVideo[].class);
            System.out.println(searchResults.length);
        } catch (IOException | InterruptedException e) {
        System.err.println("Error downloading images");
        }
    }
}