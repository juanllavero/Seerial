package com.example.executablelauncher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SeriesMenuController {
    @FXML
    private Button cancelButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;
    @FXML
    private Label contextMenuLabel;

    @FXML
    private FlowPane mainBox;

    private Controller parentController = null;

    public void setParentController(Controller c){
        parentController = c;

        mainBox.setPrefHeight(Screen.getPrimary().getBounds().getHeight());
        mainBox.setPrefWidth(Screen.getPrimary().getBounds().getWidth());

        editButton.setText(App.buttonsBundle.getString("editButton"));
        removeButton.setText(App.buttonsBundle.getString("removeButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
    }

    public void setLabel(String label){
        contextMenuLabel.setText(label);
    }

    @FXML
    void cancelButton(ActionEvent event) {
        parentController.hideMenuShadow();
        Stage stage = (Stage)mainBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    void editSeason(ActionEvent event) {
        parentController.editSeries();
        cancelButton(event);
    }

    @FXML
    void removeSeason(ActionEvent event) {
        //parentController.Remove();
        cancelButton(event);
    }
}
