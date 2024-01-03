package com.example.executablelauncher;

import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static com.example.executablelauncher.App.*;

public class AddSeasonController {
    @FXML
    private Button addButton;

    @FXML
    private Label backgroundText;

    @FXML
    private Button cancelButton;

    @FXML
    private Label logoText;

    @FXML
    private Button musicButton;

    @FXML
    private Label logoResolution;

    @FXML
    private Label backgroundResolution;

    @FXML
    private TextField musicField;

    @FXML
    private Label musicText;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameText;

    @FXML
    private TextField orderField;

    @FXML
    private Label sortingText;

    @FXML
    private Label title;

    @FXML
    private Label videText;

    @FXML
    private Button videoButton;

    @FXML
    private TextField videoField;

    @FXML
    private TextField yearField;

    @FXML
    private Label yearText;

    @FXML
    private ImageView logoImageView;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private CheckBox showName;

    private Series collection = null;
    public Season seasonToEdit = null;
    private File selectedLogo = null;
    private File selectedBackground = null;
    private File selectedVideo = null;
    private File selectedMusic = null;
    private DesktopViewController parentController = null;
    private FileChooser fileChooser = new FileChooser();
    private boolean isLogo = false;
    private boolean isBackground = false;

    //Old values to check
    private String oldLogoPath = "";
    private String oldBackgroundPath = "";
    private String oldVideoPath = "";
    private String oldMusicPath = "";
    private boolean croppedImage = false;

    public void setParentController(DesktopViewController parent){
        parentController = parent;
    }

    public void setSeason(Season s){
        seasonToEdit = s;

        oldBackgroundPath = s.getBackgroundSrc();
        oldLogoPath = s.getLogoSrc();
        oldVideoPath = s.getVideoSrc();
        oldMusicPath = s.getMusicSrc();

        nameField.setText(s.getName());
        yearField.setText((s.getYear()));
        if (!s.getLogoSrc().equals(""))
            loadLogo(s.getLogoSrc());
        if (!s.getBackgroundSrc().equals(""))
            loadBackground(s.getBackgroundSrc());
        if (!s.getVideoSrc().equals(""))
            videoField.setText(s.getVideoSrc());
        else
            videoField.setText("");
        if (!s.getMusicSrc().equals(""))
            musicField.setText(s.getMusicSrc());
        else
            musicField.setText("");
        collection = App.findSeriesByName(s.getCollectionName());

        if (s.getOrder() > 0)
            orderField.setText(Integer.toString(s.getOrder()));

        showName.setSelected(s.showName);

        initValues();
        title.setText(App.textBundle.getString("seasonWindowTitleEdit"));
        addButton.setText(App.buttonsBundle.getString("saveButton"));
    }

    public void setCollection(Series s){
        collection = s;

        if (!collection.getSeasons().isEmpty()){
            Season season = App.findSeason(collection.getSeasons().get(0));
            if (season != null){
                if (!season.getLogoSrc().isEmpty()) {
                    File file = new File(season.getLogoSrc());
                    File newLogo = new File("src/main/resources/img/DownloadCache/logoCopy.png");
                    try{
                        Files.copy(file.toPath(), newLogo.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        loadLogo(newLogo.getAbsolutePath());
                    } catch (IOException e) {
                        System.err.println("AddSeasonController: Error trying to copy old Logo");
                    }
                }
            }
        }

        initValues();
    }

    private void initValues(){
        title.setText(App.textBundle.getString("seasonWindowTitle"));
        nameText.setText(App.textBundle.getString("name"));
        logoText.setText(App.textBundle.getString("logo"));
        yearText.setText(App.textBundle.getString("year"));
        sortingText.setText(App.textBundle.getString("sortingOrder"));
        backgroundText.setText(App.textBundle.getString("backgroundImage"));
        videText.setText(App.textBundle.getString("backgroundVideo"));
        musicText.setText(App.textBundle.getString("backgroundMusic"));
        addButton.setText(App.buttonsBundle.getString("addButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
    }

    public void setCroppedImage(boolean croppedImage) {
        this.croppedImage = croppedImage;
    }

    public void loadLogo(String src){
        File file = new File(src);
        if (file.exists()) {
            selectedLogo = file;
            try{
                Image img = new Image(file.toURI().toURL().toExternalForm());
                logoResolution.setText((int)img.getWidth() + "x" + (int)img.getHeight());
                logoImageView.setImage(img);
            } catch (MalformedURLException e) {
                selectedLogo = null;
                System.err.println("Logo not loaded");
            }
        }else{
            selectedLogo = null;
        }
    }

    public void loadBackground(String src){
        File file = new File(src);
        if (file.exists()) {
            selectedBackground = file;
            try{
                Image img = new Image(file.toURI().toURL().toExternalForm());
                backgroundResolution.setText((int)img.getWidth() + "x" + (int)img.getHeight());
                backgroundImageView.setImage(img);
            } catch (MalformedURLException e) {
                selectedBackground = null;
                System.err.println("Background not loaded");
            }
        }else{
            selectedBackground = null;
        }
    }

    private File getImageFile(){
        fileChooser.setTitle(App.textBundle.getString("selectImage"));
        if (App.lastDirectory != null && Files.exists(Path.of(App.lastDirectory)))
            fileChooser.setInitialDirectory(new File(App.lastDirectory));
        else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(App.textBundle.getString("allImages"), "*.jpg", "*.png", "*.jpeg"));
        return fileChooser.showOpenDialog(title.getScene().getWindow());
    }

    @FXML
    void loadBackgroundFile(ActionEvent event) {
        selectedBackground = getImageFile();
        if (selectedBackground != null){
            loadBackground(selectedBackground.getPath());
            App.lastDirectory = selectedBackground.getPath().substring(0, (selectedBackground.getPath().length() - selectedBackground.getName().length()));
        }
    }

    @FXML
    void loadLogoFile(ActionEvent event) {
        selectedLogo = getImageFile();
        if (selectedLogo != null){
            loadLogo(selectedLogo.getPath());
            App.lastDirectory = selectedLogo.getPath().substring(0, (selectedLogo.getPath().length() - selectedLogo.getName().length()));
        }
    }

    @FXML
    void cropBackground(ActionEvent event){
        if (selectedBackground != null){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("image-cropper-view.fxml"));
                Parent root1 = fxmlLoader.load();
                ImageCropper controller = fxmlLoader.getController();
                controller.setSeasonParent(this);
                controller.initValues(selectedBackground.getAbsolutePath(), true);
                controller.loadImageToCrop(selectedBackground);
                Stage stage = new Stage();
                stage.setTitle("ImageDownloader");
                stage.setResizable(true);
                stage.setMaximized(false);
                stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
                Scene scene = new Scene(root1);
                stage.setScene(scene);
                App.setPopUpProperties(stage);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void downloadBackground(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageDownloader-view.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("ImageDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            //ResizeHelper.addResizeListener(stage);
            App.setPopUpProperties(stage);
            stage.show();
            ImageDownloaderController controller = fxmlLoader.getController();
            controller.setSeasonParent(this);
            controller.initValues(stage, collection.getName() + " " + nameField.getText() + " wallpaper", String.valueOf((int)Screen.getPrimary().getBounds().getWidth())
                    , String.valueOf((int)Screen.getPrimary().getBounds().getHeight()), false, false, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void downloadLogo(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageDownloader-view.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("ImageDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            //ResizeHelper.addResizeListener(stage);
            App.setPopUpProperties(stage);
            stage.show();
            ImageDownloaderController controller = fxmlLoader.getController();
            controller.setSeasonParent(this);
            controller.initValues(stage, collection.getName() + " logo", Integer.toString(353)
                    , Integer.toString(122), false, true, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void loadBackgroundURL(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("urlPaster-view.fxml"));
            Parent root1 = fxmlLoader.load();
            UrlPasterController controller = fxmlLoader.getController();
            controller.setParent(this);
            controller.initValues(false);
            Stage stage = new Stage();
            stage.setTitle("ImageDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            //ResizeHelper.addResizeListener(stage);
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void loadLogoURL(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("urlPaster-view.fxml"));
            Parent root1 = fxmlLoader.load();
            UrlPasterController controller = fxmlLoader.getController();
            controller.setParent(this);
            controller.initValues(true);
            Stage stage = new Stage();
            stage.setTitle("ImageDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            //ResizeHelper.addResizeListener(stage);
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void loadVideo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(App.textBundle.getString("selectVideo"));
        if (lastVideoDirectory != null && Files.exists(Path.of(lastVideoDirectory)))
            fileChooser.setInitialDirectory(new File(lastVideoDirectory));
        else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(App.textBundle.getString("allVideos"), "*.mp4"));
        selectedVideo = fileChooser.showOpenDialog((Stage)((Button) event.getSource()).getScene().getWindow());
        if (selectedVideo != null){
            videoField.setText(selectedVideo.getPath());
            lastVideoDirectory = selectedVideo.getPath();
        }
    }

    @FXML
    void loadMusic(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(App.textBundle.getString("selectSong"));
        if (lastMusicDirectory != null && Files.exists(Path.of(lastMusicDirectory)))
            fileChooser.setInitialDirectory(new File(lastMusicDirectory));
        else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(App.textBundle.getString("allAudios"), "*.mp3", "*.wav", "*.flac", "*.aac"));
        selectedMusic = fileChooser.showOpenDialog((Stage)((Button) event.getSource()).getScene().getWindow());
        if (selectedMusic != null){
            musicField.setText(selectedMusic.getPath());
            lastMusicDirectory = selectedMusic.getPath();
        }
    }

    @FXML
    void cancelButton(ActionEvent event) {
        try{
            FileUtils.cleanDirectory(new File("src/main/resources/img/DownloadCache"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        parentController.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void removeBackground(ActionEvent event) {
        selectedBackground = null;
        backgroundImageView.setImage(null);
        if (seasonToEdit != null){
            seasonToEdit.setBackgroundSrc("");
        }
        oldBackgroundPath = "";
    }

    @FXML
    void removeLogo(ActionEvent event) {
        selectedLogo = null;
        logoImageView.setImage(null);
        seasonToEdit.setLogoSrc("");
        oldLogoPath = "";
    }

    @FXML
    void save(ActionEvent event) {

        //Process name
        if (nameField.getText().isEmpty()){
            App.showErrorMessage("Invalid data", "", textBundle.getString("emptyField"));
            return;
        }

        //Process year
        if (!yearField.getText().isEmpty() && !yearField.getText().matches("\\d{3,}")){
            App.showErrorMessage("Invalid data", "", textBundle.getString("yearError"));
            return;
        }

        //Process video
        if (!videoField.getText().isEmpty()){
            File video = new File(videoField.getText());
            if (!video.exists() && !videoField.getText().isEmpty()) {
                App.showErrorMessage("Invalid data", "", App.textBundle.getString("videoNotFound"));
                return;
            }else if (video.exists()){
                String videoExtension = videoField.getText().substring(videoField.getText().length() - 3);
                videoExtension = videoExtension.toLowerCase();

                if (!videoExtension.equals("mkv") && !videoExtension.equals("mp4")){
                    App.showErrorMessage("Invalid data", "", App.textBundle.getString("videoErrorFormat"));
                    return;
                }
            }
        }

        //Process music
        if (!musicField.getText().isEmpty()){
            File music = new File(musicField.getText());
            if (!music.exists() && !musicField.getText().isEmpty()) {
                App.showErrorMessage("Invalid data", "", App.textBundle.getString("audioNotFound"));
                return;
            }else if (music.exists()){
                String audioExtension = musicField.getText().substring(musicField.getText().length() - 4);
                audioExtension = audioExtension.toLowerCase();

                if (!audioExtension.equals(".mp3") && !audioExtension.equals(".wav") && !audioExtension.equals("flac") && !audioExtension.equals(".aac")){
                    App.showErrorMessage("Invalid data", "", App.textBundle.getString("audioErrorFormat"));
                    return;
                }
            }
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            App.showErrorMessage("Invalid data", "", textBundle.getString("sortingError"));
            return;
        }

        //Save the season
        Season season;
        season = Objects.requireNonNullElseGet(seasonToEdit, Season::new);

        season.setName(nameField.getText());
        season.setYear(yearField.getText());
        season.setCollectionName(collection.getName());
        season.showName = showName.isSelected();

        //Save Background
        String newName = "";
        if (selectedBackground != null)
            newName = selectedBackground.getName();
        String oldName = "";
        if (!oldBackgroundPath.isEmpty())
            oldName = oldBackgroundPath.substring(oldBackgroundPath.lastIndexOf("/")+1);
        if (oldBackgroundPath.isEmpty() || !newName.equals(oldName) || croppedImage){
            if (selectedBackground != null)
                parentController.saveBackground(season, seasonToEdit != null, selectedBackground.getAbsolutePath(), croppedImage);
        }

        //Save Logo
        if (selectedLogo != null)
            newName = selectedLogo.getName();
        oldName = "";
        if (!oldLogoPath.isEmpty())
            oldName = oldLogoPath.substring(oldLogoPath.lastIndexOf("/")+1);
        if (oldLogoPath.isEmpty() || !newName.equals(oldName)){
            try {
                File f = new File(season.getLogoSrc());
                if (f.exists())
                    Files.delete(FileSystems.getDefault().getPath(season.getLogoSrc()));
                saveLogo(season);
            } catch (IOException e) {
                System.err.println("Logo not deleted");
            }
        }

        //Save Video
        if (!videoField.getText().isEmpty()){
            newName = videoField.getText().substring(oldVideoPath.lastIndexOf("/") + 1);
            oldName = "";
            if (!oldVideoPath.isEmpty())
                oldName = oldVideoPath.substring(oldVideoPath.lastIndexOf("/") + 1);
            System.out.println(oldName);
            System.out.println(newName);
            if (oldVideoPath.isEmpty() || !newName.equals(oldName)){
                try{
                    if (!season.getVideoSrc().isEmpty())
                        Files.delete(FileSystems.getDefault().getPath(season.getVideoSrc()));
                    saveVideo(season);
                } catch (IOException e) {
                    System.err.println("Video not deleted");
                }
            }
        }

        //Save Music
        if (!musicField.getText().isEmpty()){
            newName = musicField.getText().substring(oldMusicPath.lastIndexOf("/") + 1);
            oldName = "";
            if (!oldMusicPath.isEmpty())
                oldName = oldMusicPath.substring(oldMusicPath.lastIndexOf("/") + 1);
            if (oldMusicPath.isEmpty() || !newName.equals(oldName)){
                try{
                    if (!season.getMusicSrc().isEmpty())
                        Files.delete(FileSystems.getDefault().getPath(season.getMusicSrc()));
                    saveMusic(season);
                } catch (IOException e) {
                    System.err.println("Music not deleted");
                }
            }
        }

        if (!orderField.getText().isEmpty() && !orderField.getText().equals("0")){
            season.setOrder(Integer.parseInt(orderField.getText()));
        }

        if (seasonToEdit == null){
            App.addSeason(season, season.getCollectionName());
            parentController.updateSeasons();
        }

        parentController.hideBackgroundShadow();
        parentController.refreshSeason(season);

        try{
            FileUtils.cleanDirectory(new File("src/main/resources/img/DownloadCache"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void saveLogo(Season s){
        if (selectedLogo != null){
            File newLogo = new File("src/main/resources/img/logos/" + s.getId() + "_sl.png");

            try{
                Files.copy(selectedLogo.toPath(), newLogo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                System.err.println("Logo not copied");
            }
            s.setLogoSrc("src/main/resources/img/logos/" + newLogo.getName());
        }
    }

    private void saveVideo(Season s){
        if (!videoField.getText().isEmpty()){
            if (selectedVideo == null)
                selectedVideo = new File(videoField.getText());

            int i = selectedVideo.getName().lastIndexOf('.');
            String extension = "mp4";
            if (i > 0) {
                extension = selectedVideo.getName().substring(i+1);
            }
            File newVideo = new File("src/main/resources/video/" + s.getId() + "_sv." + extension);

            try{
                Files.copy(selectedVideo.toPath(), newVideo.toPath());
            }catch (IOException e){
                System.err.println("Video not copied");
            }
            s.setVideoSrc("src/main/resources/video/" + newVideo.getName());
        }else{
            s.setVideoSrc("");
        }
    }

    private void saveMusic(Season s){
        if (!musicField.getText().isEmpty()){
            if (selectedMusic == null)
                selectedMusic = new File(musicField.getText());

            int i = selectedMusic.getName().lastIndexOf('.');
            String extension = "mp3";
            if (i > 0) {
                extension = selectedMusic.getName().substring(i+1);
            }
            File newMusic = new File("src/main/resources/music/" + s.getId() + "_sm." + extension);

            try{
                Files.copy(selectedMusic.toPath(), newMusic.toPath());
            }catch (IOException e){
                System.err.println("Music not copied");
            }

            s.setMusicSrc("src/main/resources/music/" + newMusic.getName());
        }else{
            s.setMusicSrc("");
        }
    }
}
