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
    private EditSeasonController addSeasonController = null;
    private EditCollectionController editSeriesController = null;
    private EditDiscController editDiscController = null;

    public void setParent(EditSeasonController controller){
        addSeasonController = controller;
    }
    public void setParent(EditCollectionController controller){
        editSeriesController = controller;
    }
    public void setDiscParent(EditDiscController controller){
        editDiscController = controller;
    }

    public void initValues(boolean isLogo){
        this.isLogo = isLogo;
        title.setText(App.textBundle.getString("selectURL"));
        pasteURLText.setText(App.textBundle.getString("pasteURL"));
        addButton.setText(App.buttonsBundle.getString("addButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
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

        String imageURL = "resources/img/DownloadCache/newUrlImage.png";
        File file = new File(imageURL);
        try{
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(newImage, null);
            ImageIO.write(renderedImage,"png", file);
        } catch (IOException e) {
            System.err.println("Image not saved");
        }

        if (addSeasonController != null){
            if (isLogo)
                addSeasonController.loadLogo(imageURL);
            else
                addSeasonController.loadBackground(imageURL);
        }else if (editDiscController != null){
            editDiscController.loadImage(imageURL);
        }else{
            editSeriesController.loadLogo(imageURL);
        }

        cancelButton(event);
    }
}
