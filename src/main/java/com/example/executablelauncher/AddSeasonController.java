package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class AddSeasonController {
    @FXML
    private TextField backgroundField;

    @FXML
    private Label errorBackground;

    @FXML
    private Label errorLogo;

    @FXML
    private Label errorMusic;

    @FXML
    private Label errorName;

    @FXML
    private Label errorVideo;

    @FXML
    private Label errorYear;

    @FXML
    private TextField logoField;

    @FXML
    private TextField musicField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField videoField;

    @FXML
    private TextField yearField;

    @FXML
    private TextField orderField;

    @FXML
    private Label orderError;

    private Series collection = null;
    public Season seasonToEdit = null;
    private File selectedLogo = null;
    private File seletedBackground = null;
    private File selectedVideo = null;
    private File selectedMusic = null;

    public void setSeason(Season s){
        seasonToEdit = s;

        nameField.setText(s.getName());
        yearField.setText((s.getYear()));
        logoField.setText(s.getLogoSrc());
        backgroundField.setText(s.getBackgroundSrc());
        videoField.setText(s.getVideoSrc());
        musicField.setText(s.getMusicSrc());
        collection = Main.findSeriesByName(s.getCollectionName());

        if (s.getOrder() > 0)
            orderField.setText(Integer.toString(s.getOrder()));
    }

    public void setCollection(Series s){
        collection = s;
    }

    @FXML
    void loadBackground(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All images", "*.jpg", "*.png", "*.jpeg"));
        seletedBackground = fileChooser.showOpenDialog((Stage)((Button) event.getSource()).getScene().getWindow());
        backgroundField.setText(seletedBackground.getPath());
    }

    @FXML
    void loadLogo(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All images", "*.jpg", "*.png", "*.jpeg"));
        selectedLogo = fileChooser.showOpenDialog((Stage)((Button) event.getSource()).getScene().getWindow());
        logoField.setText(selectedLogo.getPath());
    }

    @FXML
    void loadVideo(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a video");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All videos", "*.mp4"));
        selectedVideo = fileChooser.showOpenDialog((Stage)((Button) event.getSource()).getScene().getWindow());
        videoField.setText(selectedVideo.getPath());
    }

    @FXML
    void loadMusic(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a song");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All audio", "*.mp3", "*.wav", "*.flac", "*.aac"));
        selectedMusic = fileChooser.showOpenDialog((Stage)((Button) event.getSource()).getScene().getWindow());
        musicField.setText(selectedMusic.getPath());
    }

    @FXML
    void cancelButton(MouseEvent event) {
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(MouseEvent event) {
        boolean save = true;

        //Process name
        if (nameField.getText().isEmpty()){
            save = false;
            errorName.setText("This field cannot be empty");
        }else{
            errorName.setText("");
        }

        //Process year
        if (yearField.getText().isEmpty()) {
            save = false;
            errorYear.setText("This field cannot be empty");
        }else if (!yearField.getText().matches("\\d{3,}")){
            save = false;
            errorYear.setText("The value has to be a number");
        }else{
            errorYear.setText("");
        }

        //Process Logo
        if (!logoField.getText().isEmpty()){
            File logo = new File(logoField.getText());
            if (!logo.exists() && !logoField.getText().isEmpty()) {
                save = false;
                errorLogo.setText("Image not found");
            }else if (logo.exists()){
                String imageExtension = logoField.getText().substring(logoField.getText().length() - 4);
                imageExtension = imageExtension.toLowerCase();

                if (!imageExtension.equals(".jpg") && !imageExtension.equals(".png") && !imageExtension.equals("jpeg")){
                    save = false;
                    errorLogo.setText("Image has to be '.jpg', '.png' or '.jpeg'");
                }else{
                    errorLogo.setText("");
                }
            }
        }

        //Process background
        if (!backgroundField.getText().isEmpty()){
            File background = new File(backgroundField.getText());
            if (!background.exists()) {
                save = false;
                errorBackground.setText("Image not found");
            }else{
                String imageExtension = backgroundField.getText().substring(backgroundField.getText().length() - 4);
                imageExtension = imageExtension.toLowerCase();

                if (!imageExtension.equals(".jpg") && !imageExtension.equals(".png") && !imageExtension.equals("jpeg")){
                    save = false;
                    errorBackground.setText("Image has to be '.jpg' or '.png'");
                }else{
                    errorBackground.setText("");
                }
            }
        }

        //Process video
        if (!videoField.getText().isEmpty()){
            File video = new File(videoField.getText());
            if (!video.exists() && !videoField.getText().isEmpty()) {
                save = false;
                errorVideo.setText("Video not found");
            }else if (video.exists()){
                String videoExtension = videoField.getText().substring(videoField.getText().length() - 3);
                videoExtension = videoExtension.toLowerCase();

                if (!videoExtension.equals("mkv") && !videoExtension.equals("mp4")){
                    save = false;
                    errorVideo.setText("Video has to be '.mkv' or '.mp4'");
                }else{
                    errorVideo.setText("");
                }
            }
        }

        //Process music
        if (!musicField.getText().isEmpty()){
            File music = new File(musicField.getText());
            if (!music.exists() && !musicField.getText().isEmpty()) {
                save = false;
                errorMusic.setText("Audio not found");
            }else if (music.exists()){
                String audioExtension = musicField.getText().substring(musicField.getText().length() - 4);
                audioExtension = audioExtension.toLowerCase();

                if (!audioExtension.equals(".mp3") && !audioExtension.equals(".wav") && !audioExtension.equals("flac") && !audioExtension.equals(".aac")){
                    save = false;
                    errorMusic.setText("Audio has to be '.mp3', '.wav', '.flac' or '.aac'");
                }else{
                    errorMusic.setText("");
                }
            }
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            save = false;
            orderError.setText("Sorting order has to be a number");
        }else{
            orderError.setText("");
        }

        //Save the season
        if (save){
            Season season;

            season = Objects.requireNonNullElseGet(seasonToEdit, Season::new);

            if (seletedBackground == null)
                seletedBackground = new File(backgroundField.getText());

            String extension = "";

            int i = seletedBackground.getName().lastIndexOf('.');
            if (i > 0) {
                extension = seletedBackground.getName().substring(i+1);
            }

            File newBackground = new File("src/main/resources/img/backgrounds/"+ collection.getName() + "_" + collection.getSeasons().size() + "_sb." + extension);

            if (!logoField.getText().isEmpty()){
                if (selectedLogo == null)
                    selectedLogo = new File(logoField.getText());

                i = selectedLogo.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = selectedLogo.getName().substring(i+1);
                }
                File newLogo = new File("src/main/resources/img/logos/"+ collection.getName() + "_" + collection.getSeasons().size() + "_sl." + extension);

                try{
                    Files.copy(selectedLogo.toPath(), newLogo.toPath());
                }catch (IOException e){
                    System.err.println("File not copied");
                }
                season.setLogoSrc(newLogo.getAbsolutePath());
            }else{
                season.setLogoSrc("NO_LOGO");
            }

            if (!videoField.getText().isEmpty()){
                if (selectedVideo == null)
                    selectedVideo = new File(videoField.getText());

                i = selectedVideo.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = selectedVideo.getName().substring(i+1);
                }
                File newVideo = new File("src/main/resources/video/" + collection.getName() + "_" + collection.getSeasons().size() + "_sv." + extension);

                try{
                    Files.copy(selectedVideo.toPath(), newVideo.toPath());
                }catch (IOException e){
                    System.err.println("File not copied");
                }
                season.setVideoSrc(newVideo.getAbsolutePath());
            }else{
                season.setVideoSrc("NO_VIDEO");
            }

            if (!musicField.getText().isEmpty()){
                if (selectedMusic == null)
                    selectedMusic = new File(musicField.getText());

                i = selectedMusic.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = selectedMusic.getName().substring(i+1);
                }
                File newMusic = new File("src/main/resources/music/" + collection.getName() + "_" + collection.getSeasons().size() + "_sm." + extension);

                try{
                    Files.copy(selectedMusic.toPath(), newMusic.toPath());
                }catch (IOException e){
                    System.err.println("File not copied");
                }

                season.setMusicSrc(newMusic.getAbsolutePath());
            }else{
                season.setMusicSrc("NO_MUSIC");
            }

            try{
                Files.copy(seletedBackground.toPath(), newBackground.toPath());
            }catch (IOException e){
                System.err.println("File not copied");
            }

            season.setName(nameField.getText());
            season.setYear(yearField.getText());
            season.setBackgroundSrc(newBackground.getAbsolutePath());
            season.setCollectionName(collection.getName());

            if (!orderField.getText().isEmpty() && !orderField.getText().equals("0")){
                season.setOrder(Integer.parseInt(orderField.getText()));
            }

            Main.addSeason(season, season.getCollectionName());

            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
}
