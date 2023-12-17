package com.example.executablelauncher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorMessageController {

    @FXML
    private Label errorMessage;

    public void initValues(String content){
        errorMessage.setText(content);
    }

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage)errorMessage.getScene().getWindow();
        stage.close();
    }

}
