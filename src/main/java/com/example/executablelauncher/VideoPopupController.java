package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class VideoPopupController {

    @FXML
    private WebView webView;

    public void initialize(String videoID) {
        String videoUrl = "https://www.youtube.com/embed/" + videoID;
        webView.getEngine().load(videoUrl);

        Stage stage = (Stage) webView.getScene().getWindow();

        stage.setOnHiding(event -> stopVideo());
    }

    private void stopVideo() {
        // Detener la reproducción del video
        webView.getEngine().load(null);
    }
}