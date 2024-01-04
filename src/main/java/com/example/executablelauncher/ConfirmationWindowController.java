package com.example.executablelauncher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmationWindowController {

    @FXML
    private Button cancelButton;

    @FXML
    private Label message;

    @FXML
    private Button removeButton;

    @FXML
    private Label title;

    private DesktopViewController parentController = null;

    public void setParent(DesktopViewController parent){
        parentController = parent;
    }
    public void initValues(String title, String message){
        this.title.setText(title);
        this.message.setText(message);
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        removeButton.setText(App.buttonsBundle.getString("removeButton"));
    }

    @FXML
    void cancelButton(ActionEvent event) {
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void remove(ActionEvent event) {
        parentController.acceptRemove();
        cancelButton(event);
    }
}