module com.example.executablelauncher {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires javafx.media;
    requires org.jsoup;
    requires javafx.swing;
    requires com.google.gson;
    requires javafx.web;
    requires com.fasterxml.jackson.annotation;
    requires org.apache.commons.io;
    requires FX.BorderlessScene;
    requires com.sun.jna.platform;
    requires com.sun.jna;
    requires thetvdb.java.api;
    requires thumbnailator;
    requires info.movito.themoviedbapi;
    requires org.slf4j;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires imgscalr.lib;
    requires TarsosDSP.core;
    requires TarsosDSP.jvm;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;
    requires com.jfoenix;

    opens com.example.executablelauncher to javafx.fxml;
    exports com.example.executablelauncher;
    exports com.example.executablelauncher.entities;
    opens com.example.executablelauncher.entities to javafx.fxml;
    exports com.example.executablelauncher.tmdbMetadata.movies;
    opens com.example.executablelauncher.tmdbMetadata.movies to javafx.fxml;
    exports com.example.executablelauncher.tmdbMetadata.series;
    opens com.example.executablelauncher.tmdbMetadata.series to javafx.fxml;
    exports com.example.executablelauncher.tmdbMetadata.images;
    opens com.example.executablelauncher.tmdbMetadata.images to javafx.fxml;
    exports com.example.executablelauncher.tmdbMetadata.common;
    opens com.example.executablelauncher.tmdbMetadata.common to javafx.fxml;
}