package com.example.executablelauncher.entities;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class ApiExample {

    public static void main(String[] args){
       try {
        ProcessBuilder pb;
        pb = new ProcessBuilder("python", "pytube"
                    , "url"
                    , "-t", "src/main/resources/downloadedMediaCache/");

        //Hacer búsqueda de vídeos en python

        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor();
    } catch (IOException | InterruptedException e) {
        System.err.println("Error downloading images");
    }
    }
}