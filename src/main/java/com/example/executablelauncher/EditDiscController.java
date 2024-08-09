package com.example.executablelauncher;

import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.fileMetadata.AudioTrack;
import com.example.executablelauncher.fileMetadata.MediaInfo;
import com.example.executablelauncher.fileMetadata.SubtitleTrack;
import com.example.executablelauncher.fileMetadata.VideoTrack;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.example.executablelauncher.utils.Utils.getMediaInfo;

public class EditDiscController {
    //region FXML ATTRIBUTES
    @FXML
    private Button cancelButton;

    @FXML
    private TextArea overviewField;

    @FXML
    private Label overviewText;

    @FXML
    private TextField fileField;

    @FXML
    private Label fileText;

    @FXML
    private VBox generalBox;

    @FXML
    private Button generalViewButton;

    @FXML
    private Button detailsViewButton;

    @FXML
    private BorderPane detailsBox;

    @FXML
    private VBox generalInfoBox;

    @FXML
    private VBox tracksInfoBox;

    @FXML
    private FlowPane imagesContainer;

    @FXML
    private TextField orderField;

    @FXML
    private Label orderText;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameText;

    @FXML
    private ScrollPane posterBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button selectImageButton;

    @FXML
    private Button thumbnailsViewButton;

    @FXML
    private Label titleText;

    @FXML
    private Button urlImageLoadButton;
    //endregion

    private DesktopViewController controllerParent;
    private List<File> imagesFiles = new ArrayList<>();
    private File selectedImage = null;
    public Episode episodeToEdit = null;
    boolean mediaInfoShown = false;

    //region INITIALIZATION
    public void setDisc(Episode d){
        episodeToEdit = d;
        fileField.setText(d.getVideoSrc());

        generalViewButton.setText(App.buttonsBundle.getString("generalButton"));
        thumbnailsViewButton.setText(App.buttonsBundle.getString("thumbnailsButton"));
        thumbnailsViewButton.setText(App.textBundle.getString("details"));
        selectImageButton.setText(App.buttonsBundle.getString("selectImage"));
        urlImageLoadButton.setText(App.buttonsBundle.getString("downloadImages"));
        titleText.setText(App.textBundle.getString("episodeWindowTitleEdit"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        orderText.setText(App.textBundle.getString("sortingOrder"));
        overviewText.setText(App.textBundle.getString("overview"));
        fileText.setText(App.textBundle.getString("file"));
        nameText.setText(App.textBundle.getString("name"));

        selectedImage = new File(d.getImgSrc());
        nameField.setText(d.getName());
        orderField.setText(String.valueOf(d.getOrder()));

        if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows"))
            orderField.setDisable(true);

        overviewField.setText(d.getOverview());

        fileText.setDisable(true);

        showGeneralView();
    }
    public void setParentController(DesktopViewController controller){
        controllerParent = controller;
    }
    @FXML
    void cancelButton(ActionEvent event) {
        controllerParent.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
    //endregion

    //region THUMBNAILS
    @FXML
    void loadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(App.textBundle.getString("selectImage"));
        if (App.lastDirectory != null && Files.exists(Path.of(App.lastDirectory))){
            if (new File(App.lastDirectory).getParentFile() != null)
                fileChooser.setInitialDirectory(new File(new File(App.lastDirectory).getParentFile().getAbsolutePath()));
            else
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(App.textBundle.getString("allImages"), "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            selectedImage = selectedFile;
            App.lastDirectory = selectedFile.getPath().substring(0, (selectedFile.getPath().length() - selectedFile.getName().length()));

            int number = -1;

            for (File file : imagesFiles){
                if (Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf("."))) > number){
                    number = Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }

            File newFile = new File("resources/img/discCovers/" + episodeToEdit.getId() + "/" + (number + 1) + ".jpg");

            try{
                Files.copy(selectedImage.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                System.err.println("Thumbnail not copied");
            }

            imagesFiles.add(newFile);
            addImage(newFile);
        }
    }
    @FXML
    void loadUrlThumbnail(ActionEvent event) {
        if (!App.isConnectedToInternet) {
            App.showErrorMessage(App.textBundle.getString("connectionErrorTitle"), "", App.textBundle.getString("connectionErrorMessage"));
            return;
        }

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("urlPaster-view.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(App.getBaseFontSize());
            UrlPasterController controller = fxmlLoader.getController();
            controller.setDiscParent(this);
            controller.initValues(false);
            Stage stage = new Stage();
            stage.setTitle("ImageURLDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            App.setPopUpProperties(stage, (Stage) nameField.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadImage(String src){
        File file = new File(src);
        if (file.exists()) {
            selectedImage = file;

            int number = 0;

            for (File f : imagesFiles){
                if (Integer.parseInt(f.getName().substring(0, f.getName().lastIndexOf("."))) > number){
                    number = Integer.parseInt(f.getName().substring(0, f.getName().lastIndexOf(".")));
                }
            }

            File newFile = new File("resources/img/discCovers/" + episodeToEdit.getId() + "/" + (number + 1) + ".jpg");

            try{
                Files.copy(selectedImage.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                System.err.println("Thumbnail not copied");
            }

            imagesFiles.add(file);
            addImage(file);
        }
    }
    //endregion

    //region SECTIONS
    private void showImages() {
        if (imagesFiles.isEmpty())
            loadImages();
    }
    private void loadImages(){
        //Add images to view
        File dir = new File("resources/img/discCovers/" + episodeToEdit.getId());
        if (dir.exists()){
            File[] files = dir.listFiles();
            assert files != null;
            imagesFiles.addAll(Arrays.asList(files));

            for (File f : imagesFiles){
                addImage(f);

                if (selectedImage != null && selectedImage.getAbsolutePath().equals(f.getAbsolutePath())){
                    selectButton((Button) imagesContainer.getChildren().get(imagesFiles.indexOf(f)));
                }
            }
        }
    }
    private void addImage(File file){
        try{
            Image img = new Image(file.toURI().toURL().toExternalForm(), 250, 184, true, true);
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

        selectedImage = imagesFiles.get(index);
    }
    @FXML
    void showGeneralView() {
        posterBox.setVisible(false);
        generalBox.setVisible(true);
        detailsBox.setVisible(false);

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("buttonSelected");

        thumbnailsViewButton.getStyleClass().clear();
        thumbnailsViewButton.getStyleClass().add("editButton");

        detailsViewButton.getStyleClass().clear();
        detailsViewButton.getStyleClass().add("editButton");
    }
    @FXML
    void showThumbnailsView() {
        posterBox.setVisible(true);
        generalBox.setVisible(false);
        detailsBox.setVisible(false);

        thumbnailsViewButton.getStyleClass().clear();
        thumbnailsViewButton.getStyleClass().add("buttonSelected");

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("editButton");

        detailsViewButton.getStyleClass().clear();
        detailsViewButton.getStyleClass().add("editButton");

        showImages();
    }
    //endregion

    //region DETAILS
    @FXML
    void showDetails(){
        posterBox.setVisible(false);
        generalBox.setVisible(false);
        detailsBox.setVisible(true);

        detailsViewButton.getStyleClass().clear();
        detailsViewButton.getStyleClass().add("buttonSelected");

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("editButton");

        thumbnailsViewButton.getStyleClass().clear();
        thumbnailsViewButton.getStyleClass().add("editButton");

        if (!mediaInfoShown)
            mediaInfoShown = true;
        else
            return;

        //Add a progress circle while generating metadata
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(25, 25);
        detailsBox.setTop(loadingIndicator);

        Task<Void> generateMetadataTask = new Task<>() {
            @Override
            protected Void call() {
                getMediaInfo(episodeToEdit);
                return null;
            }
        };

        generateMetadataTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                detailsBox.getChildren().remove(loadingIndicator);

                MediaInfo mediaInfo = episodeToEdit.getMediaInfo();

                //MEDIA INFO
                Text title = new Text("Media Info");
                title.setFill(Color.WHITE);
                title.getStyleClass().add("small-text");
                title.getStyleClass().add("-fx-font-weight:bold;");

                generalInfoBox.getChildren().add(title);

                addTextLine(generalInfoBox, "Duration: ", mediaInfo.getDuration());
                addTextLine(generalInfoBox, "File: ", mediaInfo.getFile());
                addTextLine(generalInfoBox, "Location: ", mediaInfo.getLocation());
                addTextLine(generalInfoBox, "Bitrate: ", mediaInfo.getBitrate());
                addTextLine(generalInfoBox, "Size: ", mediaInfo.getSize());
                addTextLine(generalInfoBox, "Container: ", mediaInfo.getContainer());

                for (VideoTrack track : episodeToEdit.getVideoTracks())
                    processVideoData(track);

                for (AudioTrack track : episodeToEdit.getAudioTracks())
                    processAudioData(track);

                for (SubtitleTrack track : episodeToEdit.getSubtitleTracks())
                    processSubtitleData(track);
            });
        });

        generateMetadataTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                detailsBox.getChildren().remove(loadingIndicator);

                Text title = new Text("Media info could not be generated");
                title.setFill(Color.WHITE);
                title.getStyleClass().add("small-text");
                title.getStyleClass().add("-fx-font-weight:bold;");

                generalInfoBox.getChildren().add(title);
            });
        });

        App.executor.submit(generateMetadataTask);
    }

    private void processVideoData(VideoTrack track){
        if (!tracksInfoBox.getChildren().isEmpty()){
            Region spacer = new Region();
            spacer.setPrefHeight(15);
            tracksInfoBox.getChildren().add(spacer);
        }

        Text title = new Text("Video");
        title.setFill(Color.WHITE);
        title.getStyleClass().add("small-text");
        title.getStyleClass().add("-fx-font-weight:bold;");

        tracksInfoBox.getChildren().add(title);

        if (track.getCodec() != null)
            addTextLine(tracksInfoBox, "Codec: ", track.getCodec());

        if (track.getCodecExt() != null)
            addTextLine(tracksInfoBox, "Codec Extended: ", track.getCodecExt());

        if (track.getBitrate() != null)
            addTextLine(tracksInfoBox, "Bitrate: ", track.getBitrate());

        if (track.getFramerate() != null)
            addTextLine(tracksInfoBox, "Frame Rate: ", track.getFramerate());

        if (track.getCodedHeight() != null && track.getCodedWidth() != null) {
            addTextLine(tracksInfoBox, "Coded Height: ", track.getCodedHeight());
            addTextLine(tracksInfoBox, "Coded Width: ", track.getCodedWidth());
        }

        if (track.getChromaLocation() != null)
            addTextLine(tracksInfoBox, "Chroma Location: ", track.getChromaLocation());

        if (track.getColorSpace() != null)
            addTextLine(tracksInfoBox, "Color Space: ", track.getColorSpace());

        if (track.getAspectRatio() != null)
            addTextLine(tracksInfoBox, "Aspect Ratio: ", track.getAspectRatio());

        if (track.getProfile() != null)
            addTextLine(tracksInfoBox, "Profile: ", track.getProfile());

        if (track.getRefFrames() != null)
            addTextLine(tracksInfoBox, "Ref Frames: ", track.getRefFrames());

        if (track.getColorRange() != null)
            addTextLine(tracksInfoBox, "Color Range: ", track.getColorRange());

        addTextLine(tracksInfoBox, "Display Title: ", track.getDisplayTitle());
    }
    private void processAudioData(AudioTrack track){
        if (!tracksInfoBox.getChildren().isEmpty()){
            Region spacer = new Region();
            spacer.setPrefHeight(15);
            tracksInfoBox.getChildren().add(spacer);
        }

        Text title = new Text("Audio");
        title.setFill(Color.WHITE);
        title.getStyleClass().add("small-text");
        title.getStyleClass().add("-fx-font-weight:bold;");

        tracksInfoBox.getChildren().add(title);

        if (track.getCodec() != null)
            addTextLine(tracksInfoBox, "Codec: ", track.getCodec());

        if (track.getCodecExt() != null)
            addTextLine(tracksInfoBox, "Codec Extended: ", track.getCodecExt());

        if (track.getChannels() != null)
            addTextLine(tracksInfoBox, "Channels: ", track.getChannels());

        if (track.getChannelLayout() != null)
            addTextLine(tracksInfoBox, "Channel Layout: ", track.getChannelLayout());

        if (track.getBitrate() != null)
            addTextLine(tracksInfoBox, "Bitrate: ", track.getBitrate());

        if (track.getLanguage() != null) {
            addTextLine(tracksInfoBox, "Language: ", track.getLanguage());
            addTextLine(tracksInfoBox, "Language tag: ", track.getLanguageTag());
        }

        if (track.getBitDepth() != null)
            addTextLine(tracksInfoBox, "Bit Depth: ", track.getBitDepth());

        if (track.getProfile() != null)
            addTextLine(tracksInfoBox, "Profile: ", track.getProfile());

        if (track.getSamplingRate() != null)
            addTextLine(tracksInfoBox, "SamplingRate: ", track.getSamplingRate());

        addTextLine(tracksInfoBox, "Display Title: ", track.getDisplayTitle());
    }
    private void processSubtitleData(SubtitleTrack track){
        if (!tracksInfoBox.getChildren().isEmpty()){
            Region spacer = new Region();
            spacer.setPrefHeight(15);
            tracksInfoBox.getChildren().add(spacer);
        }

        Text title = new Text("Subtitles");
        title.setFill(Color.WHITE);
        title.getStyleClass().add("small-text");
        title.getStyleClass().add("-fx-font-weight:bold;");

        tracksInfoBox.getChildren().add(title);

        if (track.getCodec() != null){
            addTextLine(tracksInfoBox, "Codec: ", track.getCodec());
        }

        if (track.getCodecExt() != null)
            addTextLine(tracksInfoBox, "Codec Extended: ", track.getCodecExt());

        if (track.getLanguage() != null) {
            addTextLine(tracksInfoBox, "Language: ", track.getLanguage());
            addTextLine(tracksInfoBox, "Language tag: ", track.getLanguageTag());
        }

        if (track.getTitle() != null) {
            addTextLine(tracksInfoBox, "Title: ", track.getTitle());
        }

        addTextLine(tracksInfoBox, "Display Title: ", track.getDisplayTitle());
    }
    private void addTextLine(VBox box, String header, String content){
        TextFlow textFlow = new TextFlow();

        Text first = new Text(header);
        first.setFill(Color.GRAY);
        first.getStyleClass().add("tiny-text");

        Text second = new Text(content);
        second.setFill(Color.WHITE);
        second.getStyleClass().add("tiny-text");
        second.getStyleClass().add("-fx-font-weight:bold;");

        textFlow.getChildren().addAll(first, second);

        box.getChildren().add(textFlow);
    }
    //endregion

    @FXML
    void save(ActionEvent event) {
        if (nameField.getText().isEmpty()){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("emptyField"));
            return;
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("sortingError"));
            return;
        }

        episodeToEdit.setName(nameField.getText());
        episodeToEdit.setOverview(overviewField.getText());

        if (!orderField.getText().isEmpty() && !orderField.isDisable())
            episodeToEdit.setOrder(Integer.parseInt(orderField.getText()));

        if (selectedImage != null)
            episodeToEdit.setImgSrc("resources/img/discCovers/" + episodeToEdit.getId() + "/" + selectedImage.getName());

        controllerParent.hideBackgroundShadow();
        controllerParent.updateDisc(episodeToEdit);

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
