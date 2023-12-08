package com.example.executablelauncher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SeasonMenuController {
    @FXML
    private Label contextMenuLabel;

    @FXML
    private FlowPane mainBox;

    private SeasonController parentController = null;

    public void setParentController(SeasonController c){
        parentController = c;

        mainBox.setPrefHeight(Screen.getPrimary().getBounds().getHeight());
        mainBox.setPrefWidth(Screen.getPrimary().getBounds().getWidth());
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
        parentController.editSeason();
        cancelButton(event);
    }

    @FXML
    void removeSeason(ActionEvent event) {
        parentController.removeSeason();
        cancelButton(event);
    }
}
