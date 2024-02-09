package com.example.executablelauncher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainMenuDesktopController {
    @FXML
    private Label title;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button generalCatButton;

    @FXML
    private ChoiceBox<String> languageChoice;

    @FXML
    private Label languageText;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button saveButton;

    private DesktopViewController parent = null;

    public void initValues(DesktopViewController controller){
        parent = controller;
        applyButton.setText(App.buttonsBundle.getString("applyButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        generalCatButton.setText(App.buttonsBundle.getString("generalButton"));
        languageText.setText(App.textBundle.getString("languageText"));
        title.setText(App.buttonsBundle.getString("settings"));

        languageChoice.getItems().clear();
        languageChoice.getItems().addAll(App.getLanguages());
        languageChoice.setValue(App.globalLanguage.getDisplayName());
    }

    @FXML
    void apply(ActionEvent event) {
        App.changeLanguage(languageChoice.getValue());
        initValues(parent);
        parent.updateLanguage();
    }

    @FXML
    void cancel(ActionEvent event) {
        parent.hideBackgroundShadow();
        ((Stage)mainPane.getScene().getWindow()).close();
    }

    @FXML
    void save(ActionEvent event) {
        apply(event);
        cancel(event);
    }

}
