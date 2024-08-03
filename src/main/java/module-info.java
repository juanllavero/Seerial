module com.example.executablelauncher {
    requires javafx.controls;
    requires javafx.fxml;
    requires fx.jni;
    requires java.desktop;
    requires javafx.media;
    requires org.jsoup;
    requires javafx.swing;
    requires com.google.gson;
    requires javafx.web;
    requires com.fasterxml.jackson.annotation;
    requires org.apache.commons.io;
    requires com.sun.jna.platform;
    requires com.sun.jna;
    requires thumbnailator;
    requires info.movito.themoviedbapi;
    requires org.slf4j;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires imgscalr.lib;
    requires TarsosDSP.core;
    requires TarsosDSP.jvm;
    requires com.jfoenix;
    requires com.google.api.services.youtube;
    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.json.jackson2;
    requires org.bytedeco.ffmpeg;
    requires opencv;
    requires com.github.kokorin.jaffree;
    requires color.thief;
    requires MaterialFX;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;

    opens com.example.executablelauncher to javafx.fxml;
    exports com.example.executablelauncher;
    exports com.example.executablelauncher.entities;
    opens com.example.executablelauncher.entities;
    exports com.example.executablelauncher.tmdbMetadata.movies;
    opens com.example.executablelauncher.tmdbMetadata.movies to javafx.fxml;
    exports com.example.executablelauncher.tmdbMetadata.series;
    opens com.example.executablelauncher.tmdbMetadata.series to javafx.fxml;
    exports com.example.executablelauncher.tmdbMetadata.images;
    opens com.example.executablelauncher.tmdbMetadata.images to javafx.fxml;
    exports com.example.executablelauncher.tmdbMetadata.common;
    opens com.example.executablelauncher.tmdbMetadata.common to javafx.fxml;
    exports com.example.executablelauncher.tmdbMetadata.movieCredits;
    opens com.example.executablelauncher.tmdbMetadata.movieCredits to javafx.fxml;
    exports com.example.executablelauncher.videoPlayer;
    opens com.example.executablelauncher.videoPlayer to javafx.fxml;
    exports com.example.executablelauncher.utils;
    exports com.example.executablelauncher.tmdbMetadata.groups;
    opens com.example.executablelauncher.tmdbMetadata.groups to javafx.fxml;
    exports com.example.executablelauncher.fileMetadata;
    opens com.example.executablelauncher.utils;
}