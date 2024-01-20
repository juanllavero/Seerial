package com.example.executablelauncher;

import com.example.executablelauncher.entities.YoutubeVideo;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.tv.TvSeries;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
    private Button viewButton;

    @FXML
    private Label windowTitle;
    List<Pane> resultsCards = new ArrayList<>();
    List<YoutubeVideo> resultVideos = new ArrayList<>();
    DesktopViewController parentController = null;
    EditSeasonController seasonParentController = null;
    YoutubeVideo selectedVideo = null;

    public void initValues(DesktopViewController parent, EditSeasonController seasonParent, String searchText){
        windowTitle.setText(App.textBundle.getString("searchMusic"));
        downloadButton.setText(App.buttonsBundle.getString("downloadMusic"));

        searchButton.setText(App.buttonsBundle.getString("searchButton"));
        viewButton.setText(App.buttonsBundle.getString("viewVideo"));

        searchField.setText(searchText + " main theme");

        parentController = parent;
        seasonParentController = seasonParent;

        search();
    }

    @FXML
    void search() {
        resultVideos.clear();
        resultsCards.clear();
        resultsContainer.getChildren().clear();
        Task<Void> searchVideosTask = new Task<>() {
            @Override
            protected Void call() {
                resultVideos = parentController.searchYoutube(searchField.getText());

                return null;
            }
        };

        searchVideosTask.setOnSucceeded(e -> {
            if (resultVideos != null){
                for (YoutubeVideo video : resultVideos){
                    addResultsCard(video);
                }
            }
        });

        new Thread(searchVideosTask).start();
    }

    private void addResultsCard(YoutubeVideo video){
        Platform.runLater(() -> {
            BorderPane cardBox = new BorderPane();
            cardBox.setPadding(new Insets(5));
            cardBox.getStyleClass().add("downloadedImageButton");

            cardBox.setLeft(new ImageView(new Image(video.thumbnail_url, 150, 100, true, true)));

            Label videoTitle = new Label(video.title);
            videoTitle.setFont(new Font("Arial", 18));
            videoTitle.setTextFill(Color.WHITE);

            cardBox.setCenter(videoTitle);

            cardBox.setOnMouseClicked(event -> {
                selectMedia(cardBox);
            });

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

        viewButton.setDisable(false);
        downloadButton.setDisable(false);
    }

    @FXML
    void downloadMedia(){
        parentController.downloadMedia(seasonParentController.seasonToEdit.id, selectedVideo.watch_url);

        File mediaCahceDir = new File("src/main/resources/downloadedMediaCache/" + seasonParentController.seasonToEdit.id + "/");
        File[] filesInMediaCache = mediaCahceDir.listFiles();

        if (filesInMediaCache != null && filesInMediaCache.length != 0){
            File audioFile = filesInMediaCache[0];

            try{
                Files.copy(audioFile.toPath(), Paths.get("src/main/resources/music/" + seasonParentController.seasonToEdit.id + ".mp4"), StandardCopyOption.REPLACE_EXISTING);
                seasonParentController.seasonToEdit.musicSrc = "src/main/resources/music/" + seasonParentController.seasonToEdit.id + ".mp4";

                File directory = new File("src/main/resources/downloadedMediaCache/" + seasonParentController.seasonToEdit.id + "/");
                FileUtils.deleteDirectory(directory);

                seasonParentController.selectedMusic = new File("src/main/resources/music/" + seasonParentController.seasonToEdit.id + ".mp4");
            } catch (IOException e) {
                System.err.println("processEpisode: Could not copy downloaded audio file");
            }
        }

        close();
    }

    @FXML
    void viewMedia(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VideoPopup.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("YouTube Video Popup");
            stage.setScene(scene);

            VideoPopupController controller = loader.getController();
            controller.initialize(selectedVideo.watch_url.substring(selectedVideo.watch_url.lastIndexOf("=") + 1));

            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("YoutubeSearchController: viewMedia error");
        }
    }

    @FXML
    void close(){
        Stage stage = (Stage) windowTitle.getScene().getWindow();
        stage.close();
    }
}
