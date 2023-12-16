module com.example.executablelauncher {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires javafx.media;
    requires org.jsoup;
    requires javafx.swing;
    requires com.google.gson;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;
    requires javafx.web;
    requires com.fasterxml.jackson.annotation;
    requires org.apache.commons.io;

    opens com.example.executablelauncher to javafx.fxml;
    exports com.example.executablelauncher;
}