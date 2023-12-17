package com.example.executablelauncher;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public void setParentController(DesktopViewController parent){
        parentController = parent;
    }

    public void setSeason(Season s){
        seasonToEdit = s;

        oldBackgroundPath = s.getBackgroundSrc();
        oldLogoPath = s.getLogoSrc();

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

        initValues();
        title.setText(App.textBundle.getString("seasonWindowTitleEdit"));
        addButton.setText(App.buttonsBundle.getString("saveButton"));
    }

    public void setCollection(Series s){
        collection = s;
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

    public void loadLogo(String src){
        File file = new File(src);
        if (file.exists()) {
            selectedLogo = file;
            try{
                logoImageView.setImage(new Image(file.toURI().toURL().toExternalForm(), 258, 110, true, true));
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
                backgroundImageView.setImage(new Image(file.toURI().toURL().toExternalForm(), 251, 148, true, true));
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
        if (lastDirectory != null && Files.exists(Path.of(lastDirectory)))
            fileChooser.setInitialDirectory(new File(lastDirectory));
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
            lastDirectory = selectedBackground.getPath().substring(0, (selectedBackground.getPath().length() - selectedBackground.getName().length()));
        }
    }

    @FXML
    void loadLogoFile(ActionEvent event) {
        selectedLogo = getImageFile();
        if (selectedLogo != null){
            loadLogo(selectedLogo.getPath());
            lastDirectory = selectedLogo.getPath().substring(0, (selectedLogo.getPath().length() - selectedLogo.getName().length()));
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
        videoField.setText(selectedVideo.getPath());
        lastVideoDirectory = selectedVideo.getPath();
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
        musicField.setText(selectedMusic.getPath());
        lastMusicDirectory = selectedMusic.getPath();
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
    }

    @FXML
    void removeLogo(ActionEvent event) {
        selectedLogo = null;
        logoImageView.setImage(null);
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

        if (seasonToEdit != null && selectedBackground != null) {
            String newName = selectedBackground.getName();
            String oldName = "";
            if (!oldBackgroundPath.isEmpty())
                oldName = oldBackgroundPath.substring(oldBackgroundPath.length() - newName.length());
            if (oldBackgroundPath.isEmpty() || !newName.equals(oldName)){
                try{
                    File f = new File(season.getBackgroundSrc());
                    if (f.exists())
                        Files.delete(FileSystems.getDefault().getPath(season.getBackgroundSrc()));
                    f = new File("src/main/resources/img/backgrounds/" + collection.getName() + "_" + season.getName() + "_fullBlur.png");
                    if (f.exists())
                        Files.delete(f.toPath());
                    saveBackground(season, collection.getSeasons().indexOf(season.getId()));
                } catch (IOException e) {
                    System.err.println("Background not deleted");
                }
            }
        }else if (seasonToEdit == null && selectedBackground != null){
            saveBackground(season, collection.getSeasons().size() + 1);
        }else{
            season.setBackgroundSrc("");
        }

        if (seasonToEdit != null && selectedLogo != null) {
            String newName = selectedLogo.getName();
            String oldName = "";
            if (!oldLogoPath.isEmpty())
                oldName = oldLogoPath.substring(oldLogoPath.length() - newName.length());
            if (oldLogoPath.isEmpty() || !newName.equals(oldName)){
                try {
                    File f = new File(season.getLogoSrc());
                    if (f.exists())
                        Files.delete(FileSystems.getDefault().getPath(season.getLogoSrc()));
                    saveLogo(season, collection.getSeasons().indexOf(season.getId()));
                } catch (IOException e) {
                    System.err.println("Logo not deleted");
                }
            }
        }else if (seasonToEdit == null && selectedLogo != null){
            saveLogo(season, collection.getSeasons().size() + 1);
        }else{
            season.setLogoSrc("");
        }

        if (seasonToEdit != null && !videoField.getText().equals(season.getVideoSrc())) {
            try{
                if (!season.getVideoSrc().equals(""))
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
                if (!season.getMusicSrc().equals(""))
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
            App.addSeason(season, season.getCollectionName());

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

    private void saveBackground(Season s, int seasonNumber){
        File newBackground = new File("src/main/resources/img/backgrounds/" + collection.getName() + "_" + seasonNumber + "_sb.png");

        try{
            Files.copy(selectedBackground.toPath(), newBackground.toPath());
        }catch (IOException e){
            System.err.println("Background not copied");
        }

        s.setBackgroundSrc("src/main/resources/img/backgrounds/" + newBackground.getName());

        Image image = null;
        try{
            image = new Image(newBackground.toURI().toURL().toExternalForm(), Screen.getPrimary().getBounds().getWidth()
                    , Screen.getPrimary().getBounds().getHeight(), false, true);
        } catch (MalformedURLException e) {
            System.err.println("Background image not created");
        }

        ImageView backgroundBlur = new ImageView(image);
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
        try{
            image = new Image(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            System.err.println("Background image creation error");
        }
        assert image != null;
        PixelReader reader = image.getPixelReader();
        WritableImage newImage = new WritableImage(reader
                , (int) (image.getWidth() * 0.03), (int) (image.getHeight() * 0.05)
                , (int) (image.getWidth() * 0.93), (int) (image.getHeight() * 0.9));

        try{
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(newImage, null);
            ImageIO.write(renderedImage,"png", file);
        } catch (IOException e) {
            System.err.println("Background image copy error");
        }

        s.setFullScreenBlurImageSrc("src/main/resources/img/backgrounds/" + backgroundFullscreenBlur.getName());
    }

    private void saveLogo(Season s, int seasonNumber){
        File newLogo = new File("src/main/resources/img/logos/" + collection.getName() + "_" + seasonNumber + "_sl.png");

        try{
            Files.copy(selectedLogo.toPath(), newLogo.toPath());
        }catch (IOException e){
            System.err.println("Logo not copied");
        }
        s.setLogoSrc("src/main/resources/img/logos/" + newLogo.getName());
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
            s.setVideoSrc("src/main/resources/video/" + newVideo.getName());
        }else{
            s.setVideoSrc("");
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

            s.setMusicSrc("src/main/resources/music/" + newMusic.getName());
        }else{
            s.setMusicSrc("");
        }
    }
}
