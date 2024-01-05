package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SearchResultCardController {

    @FXML
    private ImageView poster;

    @FXML
    private Label resumeText;

    @FXML
    private Label titleText;

    @FXML
    private Label yearText;

    public void initValues(String title, String year, String resume, String posterUrl){
        titleText.setText(title);
        yearText.setText(year);
        resumeText.setText(resume);

        Image img = new Image(posterUrl);
        poster.setImage(img);
    }
}