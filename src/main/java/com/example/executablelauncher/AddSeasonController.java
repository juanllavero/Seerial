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
import javafx.scene.layout.BackgroundImage;
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

    public void setCroppedImage(boolean croppedImage) {
        this.croppedImage = croppedImage;
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
        seasonToEdit.setBackgroundSrc("");
        seasonToEdit.setDesktopBackgroundEffect("");
        seasonToEdit.setFullScreenBlurImageSrc("");
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


        //Save Background
        String newName = "";
        if (selectedBackground != null)
            newName = selectedBackground.getName();
        String oldName = "";
        if (!oldBackgroundPath.isEmpty())
            oldName = oldBackgroundPath.substring(oldBackgroundPath.lastIndexOf("/")+1);
        if (oldBackgroundPath.isEmpty() || !newName.equals(oldName) || croppedImage){
            saveBackground(season);
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

    private void saveBackground(Season s){
        if (selectedBackground != null){
            //Clear old images
            try{
                File f;
                if (!croppedImage){
                    f = new File(s.getBackgroundSrc());
                    if (f.exists())
                        Files.delete(f.toPath());
                }
                f = new File("src/main/resources/img/backgrounds/" + s.getId() + "_fullBlur.png");
                if (f.exists())
                    Files.delete(f.toPath());
                f = new File("src/main/resources/img/backgrounds/" + s.getId() + "_transparencyEffect.png");
                if (f.exists())
                    Files.delete(f.toPath());
                f = new File("src/main/resources/img/backgrounds/" + s.getId() + "_desktopBlur.png");
                if (f.exists())
                    Files.delete(f.toPath());
            } catch (IOException e) {
                System.err.println("AddSeasonController: Error removing old images");
            }

            File newBackground;
            if (!croppedImage || seasonToEdit == null){
                newBackground = new File("src/main/resources/img/backgrounds/" + s.getId() + "_sb.png");

                try{
                    Files.copy(selectedBackground.toPath(), newBackground.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }catch (IOException e){
                    System.err.println("Background not copied");
                }

                s.setBackgroundSrc("src/main/resources/img/backgrounds/" + newBackground.getName());
            }else{
                newBackground = new File(seasonToEdit.getBackgroundSrc());
            }

            Image image = null;
            try{
                image = new Image(newBackground.toURI().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                System.err.println("Background image not created");
            }

            ImageView backgroundBlur = new ImageView(image);
            GaussianBlur blur = new GaussianBlur();
            blur.setRadius(27);
            backgroundBlur.setEffect(blur);

            File backgroundFullscreenBlur = new File("src/main/resources/img/backgrounds/" + s.getId() + "_fullBlur.png");
            BufferedImage bImageFull = SwingFXUtils.fromFXImage(backgroundBlur.snapshot(null, null), null);

            try {
                ImageIO.write(bImageFull, "png", backgroundFullscreenBlur);
            } catch (IOException e) {
                System.err.println("Blur image fullscreen error");
            }

            bImageFull.flush();

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

            ImageView backgroundBlurDesktop = new ImageView(image);
            blur.setRadius(80);
            backgroundBlurDesktop.setEffect(blur);

            File backgroundDesktopBlur = new File("src/main/resources/img/backgrounds/" + s.getId() + "_desktopBlur.png");
            BufferedImage bImageDesktop = SwingFXUtils.fromFXImage(backgroundBlurDesktop.snapshot(null, null), null);

            try {
                ImageIO.write(bImageDesktop, "png", backgroundDesktopBlur);
            } catch (IOException e) {
                System.err.println("Blur image desktop error");
            }

            bImageDesktop.flush();

            file = new File(backgroundDesktopBlur.getAbsolutePath());
            try{
                image = new Image(file.toURI().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                System.err.println("Background image creation error");
            }

            reader = image.getPixelReader();
            newImage = new WritableImage(reader
                    , (int) (image.getWidth() * 0.08), (int) (image.getHeight() * 0.1)
                    , (int) (image.getWidth() * 0.86), (int) (image.getHeight() * 0.8));

            try{
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(newImage, null);
                ImageIO.write(renderedImage,"png", file);
            } catch (IOException e) {
                System.err.println("Background image copy error");
            }

            s.setFullScreenBlurImageSrc("src/main/resources/img/backgrounds/" + backgroundFullscreenBlur.getName());

            s.setDesktopBackgroundEffect(setTransparencyEffect(s.getBackgroundSrc(), s.getId()));

            setDesktopBackgroundBlur(s.getId());
        }
    }

    private void saveLogo(Season s){
        if (selectedLogo != null){
            File newLogo = new File("src/main/resources/img/logos/" + s.getId() + "_sl.png");

            try{
                Files.copy(selectedLogo.toPath(), newLogo.toPath());
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

    private String setTransparencyEffect(String src, int seasonId){
        try {
            //Load image
            BufferedImage originalImage = ImageIO.read(new File(src));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            //Create copy
            BufferedImage blendedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            //Apply gradual opacity
            for (int y = 0; y < height; y++) {
                float opacity = 1.0f - ((float) y / (height / 1.15f));

                //Make sure the opacity value is valid
                opacity = Math.min(1.0f, Math.max(0.0f, opacity));

                for (int x = 0; x < width; x++) {
                    //Obtain original pixel's color
                    Color originalColor = new Color(originalImage.getRGB(x, y), true);

                    //Apply opacity
                    int blendedAlpha = (int) (originalColor.getAlpha() * opacity);

                    //Create new color with opacity
                    Color blendedColor = new Color(originalColor.getRed(), originalColor.getGreen(),
                            originalColor.getBlue(), blendedAlpha);

                    //Apply color to the new image
                    blendedImage.setRGB(x, y, blendedColor.getRGB());
                }
            }

            //Save the image with the progressive transparency effect
            ImageIO.write(blendedImage, "png"
                    , new File("src/main/resources/img/backgrounds/" + seasonId + "_transparencyEffect.png"));
            originalImage.flush();
            blendedImage.flush();
        } catch (IOException e) {
            System.err.println("AddSeasonController: error applying transparency effect to background");
        }
        return "src/main/resources/img/backgrounds/" + seasonId + "_transparencyEffect.png";
    }

    private void setDesktopBackgroundBlur(int seasonId){
        try {
            BufferedImage backgroundEffect = ImageIO.read(new File("src/main/resources/img/Background.png"));
            BufferedImage originalImage = ImageIO.read(new File("src/main/resources/img/backgrounds/" + seasonId + "_desktopBlur.png"));

            float contrastFactor = 0.1f;
            BufferedImage highContrastImage = applyContrast(backgroundEffect, contrastFactor);

            BufferedImage resultImage = applyNoiseEffect(highContrastImage, originalImage);
            resultImage = applyNoiseEffect(resultImage, originalImage);

            ImageIO.write(resultImage, "png", new File("src/main/resources/img/backgrounds/" + seasonId + "_desktopBlur.png"));
            backgroundEffect.flush();
            originalImage.flush();
            highContrastImage.flush();
            resultImage.flush();
        } catch (IOException e) {
            System.err.println("AddSeasonController: Error creating Desktop Blur and Noise Effects");
        }
    }

    private static BufferedImage scaleImageTo(BufferedImage originalImage, int targetWidth, int targetHeight) {
        java.awt.Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH);

        BufferedImage scaledBufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledBufferedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return scaledBufferedImage;
    }

    private static BufferedImage applyNoiseEffect(BufferedImage originalImage, BufferedImage noiseImage) {
        BufferedImage scaledNoiseImage = scaleImageTo(noiseImage, originalImage.getWidth(), originalImage.getHeight());

        int width = Math.min(originalImage.getWidth(), scaledNoiseImage.getWidth());
        int height = Math.min(originalImage.getHeight(), scaledNoiseImage.getHeight());

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        float blendFactor = 0.2f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = new Color(originalImage.getRGB(x, y), true);

                Color noiseColor = new Color(scaledNoiseImage.getRGB(x, y));

                int blendedRed = (int) (originalColor.getRed() * (1 - blendFactor) + noiseColor.getRed() * blendFactor);
                int blendedGreen = (int) (originalColor.getGreen() * (1 - blendFactor) + noiseColor.getGreen() * blendFactor);
                int blendedBlue = (int) (originalColor.getBlue() * (1 - blendFactor) + noiseColor.getBlue() * blendFactor);
                int blendedAlpha = originalColor.getAlpha();

                Color blendedColor = new Color(blendedRed, blendedGreen, blendedBlue, blendedAlpha);
                resultImage.setRGB(x, y, blendedColor.getRGB());
            }
        }

        return resultImage;
    }

    private static BufferedImage applyContrast(BufferedImage image, float contrastFactor) {
        RescaleOp rescaleOp = new RescaleOp(contrastFactor, 0, null);
        return rescaleOp.filter(image, null);
    }
}
