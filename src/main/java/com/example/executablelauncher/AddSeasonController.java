package com.example.executablelauncher;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
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
    private DesktopViewController parentController = null;

    public void setParentController(DesktopViewController parent){
        parentController = parent;
    }

    public void setSeason(Season s){
        seasonToEdit = s;

        nameField.setText(s.getName());
        yearField.setText((s.getYear()));
        if (!s.getLogoSrc().equals("NO_LOGO"))
            logoField.setText(s.getLogoSrc());
        else
            logoField.setText("");
        backgroundField.setText(s.getBackgroundSrc());
        if (!s.getVideoSrc().equals("NO_VIDEO"))
            videoField.setText(s.getVideoSrc());
        else
            videoField.setText("");
        if (!s.getMusicSrc().equals("NO_MUSIC"))
            musicField.setText(s.getMusicSrc());
        else
            musicField.setText("");
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

            season.setName(nameField.getText());
            season.setYear(yearField.getText());
            season.setCollectionName(collection.getName());

            if (seasonToEdit != null && !backgroundField.getText().equals(season.getBackgroundSrc())) {
                try{
                    Files.delete(FileSystems.getDefault().getPath(season.getBackgroundSrc()));
                    saveBackground(season, collection.getSeasons().indexOf(season.getId()));
                } catch (IOException e) {
                    System.err.println("Background not deleted");
                }
            }else{
                saveBackground(season, collection.getSeasons().size() + 1);
            }

            if (seasonToEdit != null && !logoField.getText().equals(season.getLogoSrc())) {
                try{
                    if (!season.getLogoSrc().equals("NO_LOGO"))
                        Files.delete(FileSystems.getDefault().getPath(season.getLogoSrc()));
                    saveLogo(season, collection.getSeasons().indexOf(season.getId()));
                } catch (IOException e) {
                    System.err.println("Logo not deleted");
                }
            }else{
                saveLogo(season, collection.getSeasons().size() + 1);
            }

            if (seasonToEdit != null && !videoField.getText().equals(season.getVideoSrc())) {
                try{
                    if (!season.getVideoSrc().equals("NO_VIDEO"))
                        Files.delete(FileSystems.getDefault().getPath(season.getVideoSrc()));
                    saveVideo(season, collection.getSeasons().indexOf(season.getId()));
                } catch (IOException e) {
                    System.err.println("Video not deleted");
                }
            }else{
                saveVideo(season, collection.getSeasons().size() + 1);
            }

            if (seasonToEdit != null && !musicField.getText().equals(season.getMusicSrc())) {
                try{
                    if (!season.getMusicSrc().equals("NO_MUSIC"))
                        Files.delete(FileSystems.getDefault().getPath(season.getMusicSrc()));
                    saveMusic(season, collection.getSeasons().indexOf(season.getId()));
                } catch (IOException e) {
                    System.err.println("Background not deleted");
                }
            }else{
                saveMusic(season, collection.getSeasons().size() + 1);
            }

            if (!orderField.getText().isEmpty() && !orderField.getText().equals("0")){
                season.setOrder(Integer.parseInt(orderField.getText()));
            }

            if (seasonToEdit == null)
                Main.addSeason(season, season.getCollectionName());

            parentController.updateDiscView();

            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    private void saveBackground(Season s, int seasonNumber){
        if (seletedBackground == null)
            seletedBackground = new File(backgroundField.getText());

        String extension = "";

        int i = seletedBackground.getName().lastIndexOf('.');
        if (i > 0) {
            extension = seletedBackground.getName().substring(i+1);
        }

        File newBackground = new File("src/main/resources/img/backgrounds/"+ collection.getName() + "_" + seasonNumber + "_sb.png");

        try{
            Files.copy(seletedBackground.toPath(), newBackground.toPath());
        }catch (IOException e){
            System.err.println("Background not copied");
        }

        s.setBackgroundSrc(newBackground.getAbsolutePath());

        ImageView backgroundBlur = new ImageView(new Image(newBackground.getAbsolutePath(), Screen.getPrimary().getBounds().getWidth()
                , Screen.getPrimary().getBounds().getHeight(), false, true));

        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(27);
        backgroundBlur.setEffect(blur);


        File backgroundFullscreenBlur = new File("src/main/resources/img/backgrounds/" + collection.getName() + "_" + s.getName() + "_fullBlur.png");
        BufferedImage bImageFull = SwingFXUtils.fromFXImage(backgroundBlur.snapshot(null, null), null);

        try {
            ImageIO.write(bImageFull, "png", backgroundFullscreenBlur);
        } catch (IOException e) {
            System.err.println("Blur images error");
        }

        File file = new File(backgroundFullscreenBlur.getAbsolutePath());
        Image image;
        try{
            image = new Image(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        PixelReader reader = image.getPixelReader();
        WritableImage newImage = new WritableImage(reader
                , (int) (image.getWidth() * 0.03), (int) (image.getHeight() * 0.05)
                , (int) (image.getWidth() * 0.93), (int) (image.getHeight() * 0.9));

        try{
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(newImage, null);
            ImageIO.write(renderedImage,"png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        s.setFullScreenBlurImageSrc(backgroundFullscreenBlur.getAbsolutePath());
    }

    private void saveLogo(Season s, int seasonNumber){
        if (!logoField.getText().isEmpty()){
            if (selectedLogo == null)
                selectedLogo = new File(logoField.getText());

            File newLogo = new File("src/main/resources/img/logos/"+ collection.getName() + "_" + seasonNumber + "_sl.png");

            try{
                Files.copy(selectedLogo.toPath(), newLogo.toPath());
            }catch (IOException e){
                System.err.println("Logo not copied");
            }
            s.setLogoSrc(newLogo.getAbsolutePath());
        }else{
            s.setLogoSrc("NO_LOGO");
        }
    }

    private void saveVideo(Season s, int seasonNumber){
        if (!videoField.getText().isEmpty()){
            if (selectedVideo == null)
                selectedVideo = new File(videoField.getText());

            int i = selectedVideo.getName().lastIndexOf('.');
            String extension = "mp4";
            if (i > 0) {
                extension = selectedVideo.getName().substring(i+1);
            }
            File newVideo = new File("src/main/resources/video/" + collection.getName() + "_" + seasonNumber + "_sv." + extension);

            try{
                Files.copy(selectedVideo.toPath(), newVideo.toPath());
            }catch (IOException e){
                System.err.println("Video not copied");
            }
            s.setVideoSrc(newVideo.getAbsolutePath());
        }else{
            s.setVideoSrc("NO_VIDEO");
        }
    }

    private void saveMusic(Season s, int seasonNumber){
        if (!musicField.getText().isEmpty()){
            if (selectedMusic == null)
                selectedMusic = new File(musicField.getText());

            int i = selectedMusic.getName().lastIndexOf('.');
            String extension = "mp3";
            if (i > 0) {
                extension = selectedMusic.getName().substring(i+1);
            }
            File newMusic = new File("src/main/resources/music/" + collection.getName() + "_" + seasonNumber + "_sm." + extension);

            try{
                Files.copy(selectedMusic.toPath(), newMusic.toPath());
            }catch (IOException e){
                System.err.println("Music not copied");
            }

            s.setMusicSrc(newMusic.getAbsolutePath());
        }else{
            s.setMusicSrc("NO_MUSIC");
        }


    }
}
