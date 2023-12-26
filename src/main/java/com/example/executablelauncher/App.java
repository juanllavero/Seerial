package com.example.executablelauncher;

import com.example.executablelauncher.entities.*;
import com.github.m0nk3y2k4.thetvdb.api.QueryParameters;
import com.github.m0nk3y2k4.thetvdb.api.TheTVDBApi;
import com.github.m0nk3y2k4.thetvdb.api.constants.Query;
import com.github.m0nk3y2k4.thetvdb.api.exception.APIException;
import com.github.m0nk3y2k4.thetvdb.api.model.data.Episode;
import com.github.m0nk3y2k4.thetvdb.api.model.data.Language;
import com.github.m0nk3y2k4.thetvdb.api.model.data.SeriesSearchResult;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.google.gson.Gson;
import javafx.stage.StageStyle;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;

import com.github.m0nk3y2k4.thetvdb.TheTVDBApiFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App extends Application {
    public static List<EpisodeMetadata> episodeMetadata = new ArrayList<>();
    public static List<Series> series = new ArrayList<>();
    public static List<Season> seasons = new ArrayList<>();
    public static List<Disc> discs = new ArrayList<>();
    public static List<Category> categories = new ArrayList<>();
    public static List<Locale> languages = new ArrayList<>();
    public static Locale globalLanguage;
    public static ResourceBundle buttonsBundle;
    public static ResourceBundle textBundle;
    public static Stage primaryStage;
    public static String lastDirectory = null;
    public static String lastVideoDirectory = null;
    public static String lastMusicDirectory = null;

    @Override
    public void start(Stage stage) throws IOException {
        languages.add(Locale.forLanguageTag("en"));
        languages.add(Locale.forLanguageTag("es"));
        globalLanguage = Locale.forLanguageTag("es");
        buttonsBundle = ResourceBundle.getBundle("buttons", globalLanguage);
        textBundle = ResourceBundle.getBundle("text", globalLanguage);
        LoadData();

        /*TheTVDBApi api = TheTVDBApiFactory.createApi("f46a28ea-ef53--9e7b-31e32b7743ab");

        try{


            api.setLanguage("es");

            List<SeriesSearchResult> seriesSearch = api.searchSeriesByName("Attack on Titan");

            for (SeriesSearchResult s : seriesSearch) {
                System.out.println(s.getSeriesName() + " - " + s.getId());
            }


            //TheTVDB
            String tvdbSearch = "https://www.thetvdb.com/api/GetSeries.php?seriesname=";
            String searchTitle = "naruto";
            String endSearch = "&language=all";

            Document doc = Jsoup.connect(tvdbSearch + searchTitle + endSearch).timeout(6000).get();
            Elements seriesList = doc.getAllElements();


            long seriesID = 267440;
            QueryParameters query = TheTVDBApiFactory.createQueryParameters();
            query.addParameter(Query.Series.AIREDSEASON, "3");

            List<Episode> seasonThree = api.queryEpisodesByAiredSeason(seriesID, 3);

            System.out.println("And again, all the episodes of season 3:");
            seasonThree.stream().forEach(e -> System.out.println(
                        e.getAiredSeason() + "." + e.getAiredEpisodeNumber() + ": " + e.getEpisodeName() + e.getImdbId()));
        } catch (APIException e) {
            System.err.println("App: Couldn't find episodes");
        }*/

        /************************************/
        //IMDB
        String imdbBase = "https://www.imdb.com/title/";
        String imdbPoster = "/mediaviewer";
        String posterSrc = "";

        Document doc = Jsoup.connect(imdbBase + "tt8733180" + imdbPoster).timeout(6000).get();
        Elements body = doc.select("div.media-viewer");
        for (Element element : body){
            posterSrc = element.select("img").attr("src");
        }

        /************************************/

        /*Document doc= Jsoup.connect("https://www.imdb.com/find/?s=tt&q=the%20eminence%20in%20shadow&ref_=nv_sr_sm").timeout(6000).get();
        Elements body = doc.select("ul.ipc-metadata-list");
        System.out.println(body.select("div.ipc-metadata-list-summary-item__tc").size() + " search results");
        int counter=1;
        for (Element e : body.select("div.ipc-metadata-list-summary-item__tc"))
        {
            System.out.println(counter);
            String title = e.select("a.ipc-metadata-list-summary-item__t").text();
            String url = e.select("a.ipc-metadata-list-summary-item__t").attr("href");
            Elements elements = e.select("ul span");
            String year = "", type = "";
            if (elements.size() > 1){
                year = elements.get(0).text();
                type = elements.get(1).text();
            }else if (elements.size() == 1){
                year = elements.get(0).text();
                type = "";
            }

            System.out.println("Title: " + title);
            System.out.println("Year: " + year);
            System.out.println("Type: " + type);
            System.out.println("URL: " + url + "\n");
            counter++;
        }

        doc= Jsoup.connect("https://www.imdb.com" + "/title/tt0988824/episodes/?season=1").timeout(6000).get();
        body = doc.select("section.sc-58f3e8aa-0");
        System.out.println(body.select("div.sc-9115db22-1").size() + " search results");
        counter=1;
        for (Element e : body.select("div.sc-9115db22-1"))
        {
            System.out.println(counter);
            String title = e.select("div.ipc-title__text").text();
            String img = e.select("img.ipc-image").attr("src");

            System.out.println("Year: " + title);
            System.out.println("Img: " + img+ "\n");
            counter++;
        }*/

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle(textBundle.getString("desktopMode"));
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 2);
        stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
        DesktopViewController desktopViewController = fxmlLoader.getController();
        desktopViewController.initValues();
        primaryStage = stage;
        FXResizeHelper rh = new FXResizeHelper(stage, 0, 5);
        stage.show();
    }

    public static void setPopUpProperties(Stage stage){
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(App.primaryStage);
    }

    public static List<String> getLanguages(){
        List<String> langs = new ArrayList<>();
        for (Locale locale : languages){
            langs.add(locale.getLanguage());
        }
        return langs;
    }

    public void LoadData() {
        String collectionsFile = "Collections.json";
        String seasonsFile = "Seasons.json";
        String discsFile = "Discs.json";
        String catFile = "Categories.json";
        String episodeMeta = "EpisodeMetadata.json";

        Gson gson = new Gson();

        try{
            JsonReader reader = new JsonReader(new FileReader(collectionsFile));
            Series[] sList = gson.fromJson(reader,Series[].class);
            if (sList != null)
                series.addAll(List.of(sList));

            reader = new JsonReader(new FileReader(seasonsFile));
            Season[] seasonList = gson.fromJson(reader, Season[].class);
            if (seasonList != null)
                seasons.addAll(List.of(seasonList));

            reader = new JsonReader(new FileReader(discsFile));
            Disc[] dList = gson.fromJson(reader, Disc[].class);
            if (dList != null)
                discs.addAll(List.of(dList));

            reader = new JsonReader(new FileReader(catFile));
            Category[] catList = gson.fromJson(reader, Category[].class);
            if (catList != null)
                categories.addAll(List.of(catList));

            reader = new JsonReader(new FileReader(episodeMeta));
            EpisodeMetadata[] episodeList = gson.fromJson(reader, EpisodeMetadata[].class);
            if (episodeList != null)
                episodeMetadata.addAll(List.of(episodeList));
        } catch (FileNotFoundException e) {
            System.err.println("Json files not found");
        }

        for (Category cat : categories){
            cat.name = cat.name.toUpperCase();
        }
    }

    public static void SaveData() throws IOException {
        String collectionsFile = "Collections.json";
        String seasonsFile = "Seasons.json";
        String discsFile = "Discs.json";
        String catFile = "Categories.json";
        String episodeMeta = "EpisodeMetadata.json";

        try (Writer writer = new FileWriter(collectionsFile)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(series, writer);
        }

        try (Writer writer = new FileWriter(seasonsFile)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(seasons, writer);
        }

        try (Writer writer = new FileWriter(discsFile)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(discs, writer);
        }

        try (Writer writer = new FileWriter(catFile)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(categories, writer);
        }

        try (Writer writer = new FileWriter(episodeMeta)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(episodeMetadata, writer);
        }
    }

    public static void changeLanguage(String lang){
        globalLanguage = Locale.forLanguageTag(lang);
        buttonsBundle = ResourceBundle.getBundle("buttons", globalLanguage);
        textBundle = ResourceBundle.getBundle("text", globalLanguage);
    }

    public static Series findSeriesByName(String sName){
        for (Series s: series){
            if (s.getName().equals(sName))
                return s;
        }

        return null;
    }

    public static int getSeasonID(String seasonName, String seriesName){
        for (Series s : series){
            if (s.getName().equals(seriesName)){
                for (Season season : seasons){
                    if (season.getName().equals(seasonName)){
                        return season.getId();
                    }
                }
            }
        }

        return -1;
    }

    public static List<Series> getSeriesFromCategory(String cat){
        List<Series> seriesList = new ArrayList<>();
        for (Series s : series){
            if (s != null && s.getCategory().toUpperCase().equals(cat)){
                seriesList.add(s);
            }
        }

        seriesList.sort(new Utils.SeriesComparator());

        return seriesList;
    }

    public static List<String> getCategories(){
        List<String> catList = new ArrayList<>();
        for (Category cat : categories){
            catList.add(cat.name);
        }
        return catList;
    }

    public static List<String> getFullscreenCategories(){
        List<String> catList = new ArrayList<>();
        for (Category cat : categories){
            if (!cat.name.equals("NO CATEGORY") && cat.showOnFullscreen)
                catList.add(cat.name);
        }
        return catList;
    }

    public static void addCategory(String c, boolean showOnFullscreen){
        categories.add(new Category(c, showOnFullscreen));
    }

    public static void editCategory(String c, boolean showOnFullscreen){
        for (Category cat : categories){
            if (cat.name.equals(c)){
                cat.name = c;
                cat.showOnFullscreen = showOnFullscreen;
            }
        }
    }

    public static void removeCategory(String name){
        if (!name.equals("NO CATEGORY")){
            categories.removeIf(cat -> cat.name.equals(name));

            for (Series s : series){
                if (s.getCategory().toUpperCase().equals(name)){
                    s.setCategory("NO CATEGORY");
                }
            }
        }
    }

    public static Category findCategory(String name){
        for (Category c : categories){
            if (c.name.equals(name))
                return c;
        }
        return null;
    }

    public static boolean categoryExist(String cat){
        for (Category c : categories){
            if (c.name.equals(cat))
                return true;
        }
        return false;
    }

    public static void addCollection(Series col){
        series.add(col);
    }

    public static void addSeason(Season season, String seriesName){
        seasons.add(season);

        for (Series s : series){
            if (s.getName().equals(seriesName))
                s.addSeason(season);
        }
    }

    public static void addDisc(Disc d){
        discs.add(d);

        Objects.requireNonNull(findSeason(d.getSeasonID())).setDisc(d);
    }

    public static Series findSeries(Series s){
        for (Series i : series){
            if (i.getName().equals(s.getName()))
                return i;
        }
        return null;
    }

    public static Season findSeason(int i){
        for (Season s: seasons){
            if (s.getId() == i)
                return s;
        }
        return null;
    }

    public static Disc findDisc(int id){
        for (Disc d : discs){
            if (d.getId() == id)
                return d;
        }
        return null;
    }

    public static void removeCollection(Series col) throws IOException {
        Series s = findSeries(col);
        assert s != null;

        Files.delete(FileSystems.getDefault().getPath(s.getCoverSrc()));
        s.clearSeasons();
        series.remove(s);
        SaveData();
    }

    public static void removeSeason(int id){
        Season s = findSeason(id);
        assert s != null;

        try{
            Files.delete(FileSystems.getDefault().getPath(s.getBackgroundSrc()));
            if (!s.getLogoSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(s.getLogoSrc()));
            if (!s.getMusicSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(s.getMusicSrc()));
            if (!s.getVideoSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(s.getVideoSrc()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Integer> dList = s.getDiscs();
        for (int i : dList){
            discs.remove(findDisc(i));
        }

        for (Series serie: series){
            if (serie.getSeasons().contains(s.getId())) {
                serie.removeSeason(s.getId());
                break;
            }
        }

        seasons.remove(s);
    }

    public static void removeDisc(Disc d){
        Season s = findSeason(d.getSeasonID());
        if (s != null)
            s.removeDisc(d);
        discs.remove(d);
    }

    public static boolean nameExist(String name){
        for (Series s : series){
            if (s.getName().equals(name))
                return true;
        }
        return false;
    }

    public static List<Series> getCollection(){
        series.sort(new Utils.SeriesComparator());
        return series;
    }

    public static void showErrorMessage(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}