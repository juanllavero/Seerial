package com.example.executablelauncher;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class UrlPasterController {
    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label pasteURLText;

    @FXML
    private Label title;

    @FXML
    private TextField urlField;

    private boolean isLogo = false;
    private AddSeasonController addSeasonController = null;

    public void setParent(AddSeasonController controller){
        addSeasonController = controller;
    }

    public void initValues(boolean isLogo){
        this.isLogo = isLogo;
        title.setText("Select URL");
        pasteURLText.setText("Paste a valid URL here");
        addButton.setText("Add");
        cancelButton.setText("Cancel");
    }

    @FXML
    void cancelButton(ActionEvent event) {
        Stage stage = (Stage) title.getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(ActionEvent event) {
        Image newImage = new Image(urlField.getText());
        if (newImage.isError())
            System.out.println("Error loading image from " + urlField.getText());

        File file = new File("src/main/resources/img/DownloadCache/newUrlImage.png");
        try{
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(newImage, null);
            ImageIO.write(renderedImage,"png", file);
        } catch (IOException e) {
            System.err.println("Image not saved");
        }

        if (isLogo)
            addSeasonController.loadLogo("src/main/resources/img/DownloadCache/newUrlImage.png");
        else
            addSeasonController.loadBackground("src/main/resources/img/DownloadCache/newUrlImage.png");

        cancelButton(event);
    }
}
