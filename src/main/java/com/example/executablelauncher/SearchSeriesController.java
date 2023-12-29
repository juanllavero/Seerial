package com.example.executablelauncher;

import com.example.executablelauncher.entities.EpisodeMetadata;
import com.example.executablelauncher.entities.SeriesMetadata;
import com.github.m0nk3y2k4.thetvdb.TheTVDBApiFactory;
import com.github.m0nk3y2k4.thetvdb.api.TheTVDBApi;
import com.github.m0nk3y2k4.thetvdb.api.exception.APIException;
import com.github.m0nk3y2k4.thetvdb.api.model.data.Episode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchSeriesController {

    @FXML
    private Button cancelButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchText;

    @FXML
    private Button selectButton;

    @FXML
    private VBox seriesContainer;

    @FXML
    private Label title;

    private AddCollectionController parent = null;
    private List<SeriesMetadata> results = new ArrayList<>();
    private SeriesMetadata selectedSeries = null;

    public void setParent(AddCollectionController c){
        parent = c;
    }

    public void initValues(String toSearch){
        if (!toSearch.isEmpty())
            search(toSearch);
    }

    private void search(String text){
        String tvdbSearch = "https://www.thetvdb.com/api/GetSeries.php?seriesname=";
        String endSearch = "&language=all";

        results.clear();
        seriesContainer.getChildren().clear();

        if (!text.isEmpty()){
            try{
                Document doc = Jsoup.connect(tvdbSearch + text + endSearch).timeout(6000).get();
                Element body = doc.selectFirst("Data");
                assert body != null;
                Elements seriesList = body.select("Series");
                for (Element seriesElement : seriesList) {
                    String seriesId = Objects.requireNonNull(seriesElement.selectFirst("seriesid")).text();
                    String seriesName = Objects.requireNonNull(seriesElement.selectFirst("SeriesName")).text();
                    String firstAired = Objects.requireNonNull(seriesElement.selectFirst("FirstAired")).text();
                    if (!firstAired.isEmpty())
                        firstAired = firstAired.substring(0, 4);

                    if (!seriesId.isEmpty() && !seriesName.isEmpty() && !firstAired.isEmpty())
                        results.add(new SeriesMetadata(seriesName, firstAired, seriesId));
                }
            } catch (IOException e) {
                System.err.println("SearchSeriesController: Error in series search");
            }

            for (SeriesMetadata s : results){
                Button btn = new Button();
                btn.setText(s.name + "\n" + s.year);
                btn.getStyleClass().add("seriesSearchButton");
                btn.setMaxWidth(Integer.MAX_VALUE);
                btn.setAlignment(Pos.CENTER_LEFT);
                btn.setPadding(new Insets(1));

                btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                    selectButton(btn);
                });

                seriesContainer.getChildren().add(btn);
            }
        }
    }

    private void selectButton(Button btn){
        int index = 0;
        int i = 0;
        for (Node n : seriesContainer.getChildren()){
            Button b = (Button)n;
            b.getStyleClass().clear();
            b.getStyleClass().add("seriesSearchButton");
            if (b == btn)
                index = i;
            i++;
        }

        btn.getStyleClass().clear();
        btn.getStyleClass().add("seriesSearchButtonActive");

        selectedSeries = results.get(index);
        selectButton.setDisable(false);
    }

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) title.getScene().getWindow();
        stage.close();
    }

    @FXML
    void searchSeries(ActionEvent event) {
        search(searchText.getText());
    }

    @FXML
    void selectSeries(ActionEvent event) {
        String posterUrl = "";

        TheTVDBApi api = TheTVDBApiFactory.createApi("f46a28ea-ef53--9e7b-31e32b7743ab");

        api.setLanguage(App.globalLanguage.getLanguage());

        List<EpisodeMetadata> episodeMetadata = new ArrayList<>();

        try{
            com.github.m0nk3y2k4.thetvdb.api.model.data.Series series = api.getSeries(Long.parseLong(selectedSeries.id));
            selectedSeries.name = series.getSeriesName();
            String imdbID = series.getImdbId();
            if (imdbID != null){
                try{
                    Document doc= Jsoup.connect("https://www.imdb.com/title/" + imdbID).timeout(6000).get();
                    Elements body = doc.select("div.ipc-poster");
                    for (Element e : body.select("a.ipc-lockup-overlay"))
                    {
                        String img = e.attr("href");

                        posterUrl = "https://www.imdb.com" + img;
                        break;
                    }

                    doc = Jsoup.connect(posterUrl).timeout(6000).get();
                    body = doc.select("div.sc-7c0a9e7c-2");
                    for (Element e : body.select("img.sc-7c0a9e7c-0"))
                    {
                        posterUrl = e.attr("src");
                        break;
                    }
                } catch (IOException e) {
                    System.err.println("SearchSeries: IMDB connection lost");
                }

            }

            if (!App.seriesMetadataExists(selectedSeries.id)){
                List<Episode> episodeList;
                int i = 0;
                while(true){
                    episodeList = api.queryEpisodesByAiredSeason(Long.parseLong(selectedSeries.id), i);
                    for (Episode episode : episodeList){
                        long absoluteEpisode, episodeNumber, seasonNumber;

                        try{
                            absoluteEpisode = episode.getAbsoluteNumber();
                        }catch (NullPointerException exception){
                            absoluteEpisode = -1;
                        }

                        try{
                            episodeNumber = episode.getAiredEpisodeNumber();
                        }catch (NullPointerException exception){
                            episodeNumber = -1;
                        }

                        try{
                            seasonNumber = episode.getAiredSeason();
                        }catch (NullPointerException exception){
                            seasonNumber = -1;
                        }

                        System.out.println(absoluteEpisode + " - " + episodeNumber + " - " + seasonNumber);

                        episodeMetadata.add(new EpisodeMetadata(Objects.requireNonNull(episode.getId()).toString(), episode.getImdbId(), episode.getSeriesId(), episode.getEpisodeName(), absoluteEpisode, episodeNumber, seasonNumber));
                    }
                    i++;
                }
            }
        } catch (APIException | NullPointerException e) {
            if (!episodeMetadata.isEmpty()){
                App.episodesMetadata.remove(episodeMetadata.get(0).seriesID);
                App.episodesMetadata.put(episodeMetadata.get(0).seriesID, episodeMetadata);
            }
            System.err.println("Error: there are no episodes for this season");
        }

        parent.setMetadata(selectedSeries.name, posterUrl, Long.parseLong(selectedSeries.id));
        close(event);
    }
}
