package com.example.executablelauncher;

import com.example.executablelauncher.entities.YoutubeVideo;
import com.example.executablelauncher.utils.Utils;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.tv.TvSeries;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class YoutubeSearchController {

    @FXML
    private Button downloadButton;

    @FXML
    private VBox resultsContainer;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label windowTitle;
    List<Pane> resultsCards = new ArrayList<>();
    List<YoutubeVideo> resultVideos = new ArrayList<>();
    DesktopViewController parentController = null;
    EditSeasonController seasonParentController = null;
    YoutubeVideo selectedVideo = null;
    MediaPlayer mediaPlayer = null;
    boolean searching = false;

    public void initValues(DesktopViewController parent, EditSeasonController seasonParent, String searchText){
        windowTitle.setText(App.textBundle.getString("searchMusic"));
        downloadButton.setText(App.buttonsBundle.getString("downloadMusic"));

        searchButton.setText(App.buttonsBundle.getString("searchButton"));

        searchField.setText(searchText + " main theme");

        parentController = parent;
        seasonParentController = seasonParent;

        search();
    }

    @FXML
    void search() {
        if (searching)
            return;

        searching = true;

        resultVideos = null;
        resultsCards.clear();
        resultsContainer.getChildren().clear();

        MFXProgressSpinner spinner = Utils.getCircularProgress(50);
        resultsContainer.getChildren().add(spinner);
        VBox.setMargin(spinner, new Insets(100, 10, 10, 10));

        Task<Void> searchVideosTask = new Task<>() {
            @Override
            protected Void call() {
                resultVideos = parentController.searchYoutube(searchField.getText());
                return null;
            }
        };

        searchVideosTask.setOnSucceeded(e -> {
            resultsContainer.getChildren().clear();
            if (resultVideos != null){
                for (YoutubeVideo video : resultVideos){
                    addResultsCard(video);
                }
            }

            searching = false;
        });

        App.executor.submit(searchVideosTask);
    }

    private void addResultsCard(YoutubeVideo video){
        Platform.runLater(() -> {
            BorderPane cardBox = new BorderPane();
            cardBox.setPadding(new Insets(5));
            cardBox.getStyleClass().add("downloadedImageButton");

            cardBox.setLeft(new ImageView(new Image(video.thumbnail_url, 150, 100, true, true)));

            Label videoTitle = new Label(video.title);
            videoTitle.getStyleClass().add("small-text");
            videoTitle.setTextFill(Color.WHITE);
            videoTitle.setWrapText(true);

            Label videoUrl = new Label(video.watch_url);
            videoUrl.getStyleClass().addAll("folderLink", "tiny-text");
            videoUrl.setTextFill(Color.WHITE);

            videoUrl.setOnMouseClicked(e -> {
                try {
                    Desktop.getDesktop().browse(new URI(video.watch_url));
                } catch (IOException | URISyntaxException ex) {
                    App.showErrorMessage("URL ERROR", "", "Browser could not be opened");
                }
            });

            VBox videoInfo = new VBox(videoTitle, videoUrl);
            videoInfo.setPadding(new Insets(5, 5, 5, 10));

            cardBox.setCenter(videoInfo);

            cardBox.setOnMouseClicked(event -> selectMedia(cardBox));

            resultsCards.add(cardBox);
            resultsContainer.getChildren().add(cardBox);
        });
    }

    public void selectMedia(Pane pane){
        int index = 0;
        int i = 0;
        for (Node n : resultsContainer.getChildren()){
            n.getStyleClass().clear();
            n.getStyleClass().add("downloadedImageButton");
            if (n == pane)
                index = i;
            i++;
        }

        pane.getStyleClass().clear();
        pane.getStyleClass().add("downloadedImageButtonSelected");

        selectedVideo = resultVideos.get(index);

        downloadButton.setDisable(false);
    }

    @FXML
    void downloadMedia(){
        boolean downloaded = parentController.downloadMedia(seasonParentController.seasonToEdit, selectedVideo.watch_url);

        if (downloaded)
            seasonParentController.setMusic("resources/music/" + seasonParentController.seasonToEdit.getId() + ".mp4");

        close();
    }

    @FXML
    void close(){
        if (mediaPlayer != null)
            mediaPlayer.stop();

        Stage stage = (Stage) windowTitle.getScene().getWindow();
        stage.close();
    }
}
