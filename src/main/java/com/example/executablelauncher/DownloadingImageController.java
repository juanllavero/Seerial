package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DownloadingImageController {
    @FXML
    private Label downloadingText;

    public void initValues(){
        downloadingText.setText("Downloading...");
    }
}
