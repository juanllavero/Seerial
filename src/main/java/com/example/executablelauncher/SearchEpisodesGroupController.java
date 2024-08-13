package com.example.executablelauncher;

import com.example.executablelauncher.tmdbMetadata.groups.SeasonsGroup;
import com.example.executablelauncher.tmdbMetadata.groups.SeasonsGroupRoot;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchEpisodesGroupController {
    @FXML
    private VBox results;

    @FXML
    private Label titleWindow;
    List<Pane> resultsCards = new ArrayList<>();
    List<SeasonsGroup> resultGroups = new ArrayList<>();
    DesktopViewController parentController = null;
    int tmdbID;

    public void initiValues(DesktopViewController parent, int tmdbID){
        titleWindow.setText(App.buttonsBundle.getString("changeEpisodesGroup"));
        this.tmdbID = tmdbID;
        parentController = parent;

        searchGroups();
    }

    private void searchGroups() {
        resultGroups.clear();
        resultsCards.clear();
        results.getChildren().clear();

        if (tmdbID <= 0)
            return;

        try{
            OkHttpClient client = new OkHttpClient();
            Request requestGroups = new Request.Builder()
                    .url("https://api.themoviedb.org/3/tv/" + tmdbID + "/episode_groups")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer " + App.themoviedbAPIKey)
                    .build();

            Response response = client.newCall(requestGroups).execute();

            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                assert response.body() != null;
                SeasonsGroupRoot groups = objectMapper.readValue(response.body().string(), SeasonsGroupRoot.class);

                SeasonsGroup defaultGroup = new SeasonsGroup();
                defaultGroup.name = App.textBundle.getString("defaultGroup");
                resultGroups.add(defaultGroup);
                addResultsCard(defaultGroup, true);

                resultGroups.addAll(groups.results);

                for (SeasonsGroup group : groups.results)
                    addResultsCard(group, false);
            } else {
                System.out.println("getEpisodeGroup: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            //App.showErrorMessage("Search Error", "", "No episode group found");
            System.err.println("searchGroups: Could not find any episode group");
        }
    }

    private void addResultsCard(SeasonsGroup group, boolean defaultOption){
        Platform.runLater(() -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("searchResultEpGroupCard.fxml"));
                Pane cardBox = fxmlLoader.load();
                cardBox.setStyle(App.getBaseFontSize());
                SearchResultEpGroupCardController controller = fxmlLoader.getController();
                controller.initValues(group);

                cardBox.getStyleClass().add("searchResultButton");

                if (defaultOption){
                    cardBox.setOnMouseClicked(event -> {
                        parentController.changeEpisodesGroup("");
                        cancelButton();
                    });
                }else{
                    cardBox.setOnMouseClicked(event -> {
                        parentController.changeEpisodesGroup(resultGroups.get(resultsCards.indexOf(cardBox)).id);
                        cancelButton();
                    });
                }

                resultsCards.add(cardBox);
                results.getChildren().add(cardBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    void cancelButton(){
        Stage stage = (Stage) titleWindow.getScene().getWindow();
        stage.close();
    }
}
