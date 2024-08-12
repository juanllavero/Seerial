package com.example.executablelauncher;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.tv.TvSeries;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchSeriesController {

    @FXML
    private VBox results;

    @FXML
    private Button searchButton;

    @FXML
    private TextField titleField;

    @FXML
    private Label titleText;

    @FXML
    private Label titleWindow;

    @FXML
    private TextField yearField;

    @FXML
    private Label yearText;

    TmdbApi tmdbApi = null;
    String language = "en";
    boolean isShow = true;
    List<Pane> resultsCards = new ArrayList<>();
    List<TvSeries> resultShows = new ArrayList<>();
    List<MovieDb> resultMovies = new ArrayList<>();
    DesktopViewController parentController = null;
    MovieResultsPage movieResults = null;
    TvResultsPage tvResults = null;

    public void initiValues(DesktopViewController parent, String searchText, boolean isShow, TmdbApi tmdbApi, String language){
        titleWindow.setText(App.textBundle.getString("correctIdentification"));
        searchButton.setText(App.buttonsBundle.getString("searchButton"));
        titleText.setText(App.textBundle.getString("name"));
        yearText.setText(App.textBundle.getString("year"));
        titleField.setText(searchText);
        this.language = language;
        this.isShow = isShow;

        parentController = parent;

        this.tmdbApi = tmdbApi;

        searchSeries();
    }
    @FXML
    void searchSeries() {
        resultMovies.clear();
        resultShows.clear();
        resultsCards.clear();
        results.getChildren().clear();
        Task<Void> searchShowsTask = new Task<>() {
            @Override
            protected Void call() {
                int year = 1;

                if (!yearField.getText().isEmpty())
                    year = Integer.parseInt(yearField.getText());

                tvResults = tmdbApi.getSearch().searchTv(titleField.getText(), year, language, true, 1);

                return null;
            }
        };

        searchShowsTask.setOnSucceeded(e -> {
            if (tvResults != null && tvResults.getTotalResults() > 0) {
                for (TvSeries tvSeries : tvResults.getResults()){
                    resultShows.add(tvSeries);
                    addResultsCard(tvSeries.getName(), tvSeries.getFirstAirDate(), tvSeries.getOverview(), "https://image.tmdb.org/t/p/original" + tvSeries.getPosterPath());
                }
            }
        });

        Task<Void> searchMoviesTask = new Task<>() {
            @Override
            protected Void call() {
                int year = 1;

                if (!yearField.getText().isEmpty())
                    year = Integer.parseInt(yearField.getText());

                movieResults = tmdbApi.getSearch().searchMovie(titleField.getText(), year, language, true, 1);

                return null;
            }
        };

        searchMoviesTask.setOnSucceeded(e -> {
            if (movieResults != null && movieResults.getTotalResults() > 0) {
                for (MovieDb movieDb : movieResults.getResults()){
                    resultMovies.add(movieDb);
                    addResultsCard(movieDb.getTitle(), movieDb.getReleaseDate(), movieDb.getOverview(), "https://image.tmdb.org/t/p/original" + movieDb.getPosterPath());
                }
            }
        });

        Thread thread;
        if (isShow)
            thread = new Thread(searchShowsTask);
        else
            thread = new Thread(searchMoviesTask);

        thread.setDaemon(true);
        thread.start();
    }

    private void addResultsCard(String title, String year, String resume, String posterPath){
        Platform.runLater(() -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("searchResultCard.fxml"));
                Pane cardBox = fxmlLoader.load();
                cardBox.setStyle(App.getBaseFontSize());
                SearchResultCardController controller = fxmlLoader.getController();
                controller.initValues(title, year, resume, posterPath);

                cardBox.getStyleClass().add("searchResultButton");

                cardBox.setOnMouseClicked(event -> {
                    if (isShow)
                        parentController.setCorrectIdentificationShow(resultShows.get(resultsCards.indexOf(cardBox)).getId());
                    else
                        parentController.setCorrectIdentificationMovie(resultMovies.get(resultsCards.indexOf(cardBox)).getId());
                    cancelButton();
                });

                resultsCards.add(cardBox);
                results.getChildren().add(cardBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    void cancelButton(){
        Stage stage = (Stage) titleText.getScene().getWindow();
        stage.close();
    }
}