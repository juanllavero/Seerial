package com.example.executablelauncher;

import com.example.executablelauncher.utils.Configuration;
import com.example.executablelauncher.utils.Utils;
import com.jfoenix.controls.JFXSlider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;

public class MainMenuDesktopController {
    @FXML
    private Button applyButton;

    @FXML
    private ChoiceBox<Locale> audioLanguage;

    @FXML
    private CheckBox interpolation;

    @FXML
    private CheckBox highSettingsPlayer;

    @FXML Label interpolationText;

    @FXML
    private CheckBox autoScan;

    @FXML
    private CheckBox music;

    @FXML
    private JFXSlider backgroundVolume;

    @FXML
    private Label backgroundVolumeText;

    @FXML
    private Button cancelButton;

    @FXML
    private Button fullscreenButton;

    @FXML
    private VBox fullscreenContainer;

    @FXML
    private Button generalCatButton;

    @FXML
    private VBox generalContainer;

    @FXML
    private ChoiceBox<Locale> languageChoice;

    @FXML
    private Label languageText;

    @FXML
    private Button languagesButton;

    @FXML
    private VBox languagesContainer;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label preferAudioText;

    @FXML
    private Label preferSubsText;

    @FXML
    private Button saveButton;

    @FXML
    private JFXSlider secondsBeforeVideo;

    @FXML
    private Label secondsBeforeVideoText;

    @FXML
    private CheckBox showClock;

    @FXML
    private ChoiceBox<Locale> subsLanguage;

    @FXML
    private ChoiceBox<String> subsMode;

    @FXML
    private Label subtitleModeText;

    @FXML
    private Label title;

    private DesktopViewController parent = null;
    boolean firstTime = true;

    public void initValues(DesktopViewController controller){
        parent = controller;
        applyButton.setText(App.buttonsBundle.getString("applyButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        generalCatButton.setText(App.buttonsBundle.getString("generalButton"));
        languageText.setText(App.textBundle.getString("languageText"));
        title.setText(App.buttonsBundle.getString("settings"));
        fullscreenButton.setText(App.buttonsBundle.getString("fullscreenButton"));
        languagesButton.setText(App.buttonsBundle.getString("languagesButton"));
        interpolationText.setText(App.textBundle.getString("interpolationMeaning"));
        interpolation.setText(App.textBundle.getString("interpolationCheck"));
        interpolation.setSelected(Boolean.parseBoolean(Configuration.loadConfig("interpolation", "false")));
        highSettingsPlayer.setText(App.textBundle.getString("highSettingsPlayer"));
        highSettingsPlayer.setSelected(Boolean.parseBoolean(Configuration.loadConfig("highSettingsPlayer", "true")));
        autoScan.setText(App.textBundle.getString("autoScan"));
        autoScan.setSelected(Boolean.parseBoolean(Configuration.loadConfig("autoScan", "true")));
        secondsBeforeVideoText.setText(App.textBundle.getString("backgroundDelay"));
        secondsBeforeVideo.setValue(Float.parseFloat(Configuration.loadConfig("backgroundDelay", "3")));
        backgroundVolumeText.setText(App.textBundle.getString("backgroundVolume"));

        double volume = Double.parseDouble(Configuration.loadConfig("backgroundVolume", "3"));
        backgroundVolume.setValue((int) volume);

        music.setText(App.textBundle.getString("playMusicDesktop"));
        music.setSelected(Boolean.parseBoolean(Configuration.loadConfig("playMusicDesktop", "false")));

        showClock.setText(App.textBundle.getString("showClock"));
        showClock.setSelected(Boolean.parseBoolean(Configuration.loadConfig("showClock", "true")));
        preferAudioText.setText(App.textBundle.getString("preferAudio"));
        preferSubsText.setText(App.textBundle.getString("preferSubs"));
        subtitleModeText.setText(App.textBundle.getString("subsMode"));

        audioLanguage.setConverter(new Utils.LocaleStringConverter());
        audioLanguage.getItems().clear();
        audioLanguage.getItems().addAll(App.languages);
        audioLanguage.setValue(Locale.forLanguageTag(Configuration.loadConfig("preferAudioLan", "es-ES")));

        subsLanguage.setConverter(new Utils.LocaleStringConverter());
        subsLanguage.getItems().clear();
        subsLanguage.getItems().addAll(App.languages);
        subsLanguage.setValue(Locale.forLanguageTag(Configuration.loadConfig("preferSubsLan", "es-ES")));

        subsMode.getItems().clear();
        subsMode.getItems().add(App.textBundle.getString("manualSubs"));
        subsMode.getItems().add(App.textBundle.getString("autoSubs"));
        subsMode.getItems().add(App.textBundle.getString("alwaysSubs"));

        String mode = Configuration.loadConfig("subsMode", "1");
        String modeName = switch (mode) {
            case "2" -> App.textBundle.getString("autoSubs");
            case "3" -> App.textBundle.getString("alwaysSubs");
            default -> App.textBundle.getString("manualSubs");
        };
        subsMode.setValue(modeName);

        languageChoice.setConverter(new Utils.LocaleStringConverter());
        languageChoice.getItems().clear();
        languageChoice.getItems().addAll(App.languages);
        languageChoice.setValue(App.globalLanguage);

        if (firstTime){
            firstTime = false;
            showGeneralView();
        }
    }

    @FXML
    void apply(ActionEvent event) {
        App.changeLanguage(languageChoice.getValue());

        Configuration.saveConfig("autoScan", String.valueOf(autoScan.isSelected()));
        Configuration.saveConfig("backgroundDelay", String.valueOf(secondsBeforeVideo.getValue()));
        Configuration.saveConfig("backgroundVolume", String.valueOf((int) backgroundVolume.getValue()));
        Configuration.saveConfig("showClock", String.valueOf(showClock.isSelected()));
        Configuration.saveConfig("interpolation", String.valueOf(interpolation.isSelected()));

        Configuration.saveConfig("preferAudioLan", audioLanguage.getValue().toLanguageTag());
        Configuration.saveConfig("preferSubsLan", subsLanguage.getValue().toLanguageTag());

        String autoSubs = App.textBundle.getString("autoSubs");
        String alwaysSubs = App.textBundle.getString("alwaysSubs");
        String mode;
        if (subsMode.getValue().equals(autoSubs))
            mode = "2";
        else if (subsMode.getValue().equals(alwaysSubs))
            mode = "3";
        else
            mode = "1";
        Configuration.saveConfig("subsMode", mode);
        Configuration.saveConfig("playMusicDesktop", String.valueOf(music.isSelected()));

        initValues(parent);
        parent.updateLanguage();

        if (!music.isSelected())
            parent.stopMusic();
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

    //region SELECTION
    @FXML
    void showGeneralView() {
        generalContainer.setVisible(true);
        fullscreenContainer.setVisible(false);
        languagesContainer.setVisible(false);

        generalCatButton.getStyleClass().clear();
        generalCatButton.getStyleClass().add("buttonSelected");

        fullscreenButton.getStyleClass().clear();
        fullscreenButton.getStyleClass().add("editButton");

        languagesButton.getStyleClass().clear();
        languagesButton.getStyleClass().add("editButton");
    }
    @FXML
    void showFullScreenView() {
        generalContainer.setVisible(false);
        fullscreenContainer.setVisible(true);
        languagesContainer.setVisible(false);

        generalCatButton.getStyleClass().clear();
        generalCatButton.getStyleClass().add("editButton");

        fullscreenButton.getStyleClass().clear();
        fullscreenButton.getStyleClass().add("buttonSelected");

        languagesButton.getStyleClass().clear();
        languagesButton.getStyleClass().add("editButton");
    }
    @FXML
    void showLanguagesView() {
        generalContainer.setVisible(false);
        fullscreenContainer.setVisible(false);
        languagesContainer.setVisible(true);

        generalCatButton.getStyleClass().clear();
        generalCatButton.getStyleClass().add("editButton");

        fullscreenButton.getStyleClass().clear();
        fullscreenButton.getStyleClass().add("editButton");

        languagesButton.getStyleClass().clear();
        languagesButton.getStyleClass().add("buttonSelected");
    }
    //endregion

}
