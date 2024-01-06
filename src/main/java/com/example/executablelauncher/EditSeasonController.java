package com.example.executablelauncher;

import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.example.executablelauncher.App.*;

public class EditSeasonController {
    //region FXML ATTRIBUTES
    @FXML
    private ImageView backgroundImageView;

    @FXML
    private Label backgroundResolution;

    @FXML
    private Label backgroundText;

    @FXML
    private Button cancelButton;

    @FXML
    private Button downloadImagesButton;

    @FXML
    private VBox generalBox;

    @FXML
    private Button generalViewButton;

    @FXML
    private ScrollPane logosBox;

    @FXML
    private Button logosViewButton;

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
    private FlowPane imagesContainer;

    @FXML
    private Button saveButton;

    @FXML
    private Button selectImageButton;

    @FXML
    private CheckBox showName;

    @FXML
    private Label sortingText;

    @FXML
    private Label title;

    @FXML
    private Button videoButton;

    @FXML
    private TextField videoField;

    @FXML
    private Label videoText;

    @FXML
    private TextField yearField;

    @FXML
    private Label yearText;
    //endregion

    //region ATTRIBUTES
    public Season seasonToEdit = null;
    public String seriesName = "";
    private List<File> logoFiles = new ArrayList<>();
    private File selectedLogo = null;
    private File selectedBackground = null;
    private File selectedVideo = null;
    private File selectedMusic = null;
    private DesktopViewController parentController = null;
    private FileChooser fileChooser = new FileChooser();

    //Old values to check
    private String oldBackgroundPath = "";
    private String oldVideoPath = "";
    private String oldMusicPath = "";
    private boolean croppedImage = false;
    //endregion

    //region INITIALIZATION
    public void setParentController(DesktopViewController parent){
        parentController = parent;
    }

    public void setSeason(Season s){
        seasonToEdit = s;

        selectedLogo = new File(s.logoSrc);
        oldBackgroundPath = s.getBackgroundSrc();
        oldVideoPath = s.getVideoSrc();
        oldMusicPath = s.getMusicSrc();

        nameField.setText(s.getName());
        yearField.setText((s.getYear()));
        if (!s.getBackgroundSrc().isEmpty())
            loadBackground(s.getBackgroundSrc());
        if (!s.getVideoSrc().isEmpty())
            videoField.setText(s.getVideoSrc());
        else
            videoField.setText("");
        if (!s.getMusicSrc().isEmpty())
            musicField.setText(s.getMusicSrc());
        else
            musicField.setText("");

        seriesName = findSeries(s.getSeriesID()).name;

        if (s.getOrder() > 0)
            orderField.setText(Integer.toString(s.getOrder()));

        showName.setSelected(s.showName);

        initValues();
        showGeneralView();
    }
    private void initValues(){
        nameText.setText(App.textBundle.getString("name"));
        yearText.setText(App.textBundle.getString("year"));
        saveButton.setText(buttonsBundle.getString("saveButton"));
        musicButton.setText(buttonsBundle.getString("loadButton"));
        videoButton.setText(buttonsBundle.getString("loadButton"));
        sortingText.setText(App.textBundle.getString("sortingOrder"));
        videoText.setText(App.textBundle.getString("backgroundVideo"));
        musicText.setText(App.textBundle.getString("backgroundMusic"));
        title.setText(App.textBundle.getString("seasonWindowTitleEdit"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        backgroundText.setText(App.textBundle.getString("backgroundImage"));
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
    //endregion

    //region IMAGES
    public void setCroppedImage(boolean croppedImage) {
        this.croppedImage = croppedImage;
    }

    public void loadLogo(String src){
        File file = new File(src);
        if (file.exists()) {
            selectedLogo = file;

            int number = 0;

            for (File f : logoFiles){
                if (Integer.parseInt(f.getName().substring(0, f.getName().lastIndexOf("."))) > number){
                    number = Integer.parseInt(f.getName().substring(0, f.getName().lastIndexOf(".")));
                }
            }

            File newFile = new File("src/main/resources/img/logos/" + seasonToEdit.id + "/" + (number + 1) + ".png");

            try{
                Files.copy(selectedLogo.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                System.err.println("Thumbnail not copied");
            }

            logoFiles.add(file);
            addImage(file);
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
        openImagesDownloader(seriesName + " wallpaper", String.valueOf((int)Screen.getPrimary().getBounds().getWidth())
                , String.valueOf((int)Screen.getPrimary().getBounds().getHeight()), false, false, false);
    }
    @FXML
    void downloadLogo(ActionEvent event) {
        openImagesDownloader(seriesName + " logo", Integer.toString(353), Integer.toString(122), false, true, true);
    }
    private void openImagesDownloader(String searchText, String width, String height, boolean isCover, boolean isLogo, boolean transparent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageDownloader-view.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("ImageDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            App.setPopUpProperties(stage);
            stage.show();
            ImageDownloaderController controller = fxmlLoader.getController();
            controller.setSeasonParent(this);
            controller.initValues(stage, searchText, width, height, isCover, isLogo, transparent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void loadBackgroundURL(ActionEvent event) {
        openURLImageLoader(false);
    }
    @FXML
    void loadLogoURL(ActionEvent event) {
        openURLImageLoader(true);
    }
    private void openURLImageLoader(boolean isLogo){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("urlPaster-view.fxml"));
            Parent root1 = fxmlLoader.load();
            UrlPasterController controller = fxmlLoader.getController();
            controller.setParent(this);
            controller.initValues(isLogo);
            Stage stage = new Stage();
            stage.setTitle("ImageURLDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    //endregion

    //region SECTIONS
    private void showImages() {
        if (logoFiles.isEmpty())
            loadImages();
    }
    private void loadImages(){
        //Add images to view
        File dir = new File("src/main/resources/img/logos/" + seasonToEdit.id);
        if (dir.exists()){
            File[] files = dir.listFiles();
            assert files != null;
            logoFiles.addAll(Arrays.asList(files));

            for (File f : logoFiles){
                addImage(f);

                if (selectedLogo != null && selectedLogo.getAbsolutePath().equals(f.getAbsolutePath())){
                    selectButton((Button) imagesContainer.getChildren().get(logoFiles.indexOf(f)));
                }
            }
        }
    }
    private void addImage(File file){
        try{
            Image img = new Image(file.toURI().toURL().toExternalForm(), 150, 84, true, true);
            ImageView image = new ImageView(img);

            Button btn = new Button();
            btn.setGraphic(image);
            btn.setText("");
            btn.getStyleClass().add("downloadedImageButton");
            btn.setPadding(new Insets(2));

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                selectButton(btn);
            });

            imagesContainer.getChildren().add(btn);
        } catch (MalformedURLException e) {
            System.err.println("EditDiscController: Error loading image thumbnail");
        }
    }
    private void selectButton(Button btn){
        int index = 0;
        int i = 0;
        for (Node n : imagesContainer.getChildren()){
            Button b = (Button)n;
            b.getStyleClass().clear();
            b.getStyleClass().add("downloadedImageButton");
            if (b == btn)
                index = i;
            i++;
        }

        btn.getStyleClass().clear();
        btn.getStyleClass().add("downloadedImageButtonSelected");

        selectedLogo = logoFiles.get(index);
    }
    @FXML
    void showGeneralView() {
        logosBox.setVisible(false);
        generalBox.setVisible(true);

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("buttonSelected");

        logosViewButton.getStyleClass().clear();
        logosViewButton.getStyleClass().add("editButton");
    }
    @FXML
    void showLogosView() {
        logosBox.setVisible(true);
        generalBox.setVisible(false);

        logosViewButton.getStyleClass().clear();
        logosViewButton.getStyleClass().add("buttonSelected");

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("editButton");

        showImages();
    }
    //endregion

    //region VIDEO AND MUSIC
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
    //endregion

    //region SAVE DATA
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

        seasonToEdit.setName(nameField.getText());
        seasonToEdit.setYear(yearField.getText());
        seasonToEdit.showName = showName.isSelected();

        //Save Background
        String newName = "";
        if (selectedBackground != null)
            newName = selectedBackground.getName();
        String oldName = "";
        if (!oldBackgroundPath.isEmpty())
            oldName = oldBackgroundPath.substring(oldBackgroundPath.lastIndexOf("/")+1);
        if (oldBackgroundPath.isEmpty() || !newName.equals(oldName) || croppedImage){
            if (selectedBackground != null){
                //parentController.saveBackground(seasonToEdit, seasonToEdit != null, selectedBackground.getAbsolutePath(), croppedImage);              FIX EDIT AND CROP
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
                    if (!seasonToEdit.getVideoSrc().isEmpty())
                        Files.delete(FileSystems.getDefault().getPath(seasonToEdit.getVideoSrc()));
                    saveVideo(seasonToEdit);
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
                    if (!seasonToEdit.getMusicSrc().isEmpty())
                        Files.delete(FileSystems.getDefault().getPath(seasonToEdit.getMusicSrc()));
                    saveMusic(seasonToEdit);
                } catch (IOException e) {
                    System.err.println("Music not deleted");
                }
            }
        }

        if (!orderField.getText().isEmpty() && !orderField.getText().equals("0")){
            seasonToEdit.setOrder(Integer.parseInt(orderField.getText()));
        }

        //Save Logo
        if (selectedLogo != null)
            seasonToEdit.logoSrc = "src/main/resources/img/logos/" + seasonToEdit.id + "/" + selectedLogo.getName();

        parentController.hideBackgroundShadow();
        parentController.refreshSeason(seasonToEdit);
        parentController.clearImageCache();

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
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
    //endregion
}
