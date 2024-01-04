package com.example.executablelauncher;

import com.example.executablelauncher.entities.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;

public class App extends Application {
    public static Map<String, Series> series = new HashMap<>();
    public static Map<String, Season> seasons = new HashMap<>();
    public static Map<String, Disc> discs = new HashMap<>();
    public static List<Category> categories = new ArrayList<>();
    public static List<Locale> languages = new ArrayList<>();
    public static List<Locale> tmdbLanguages = new ArrayList<>();
    public static Locale globalLanguage;
    public static ResourceBundle buttonsBundle;
    public static ResourceBundle textBundle;
    public static Stage primaryStage;
    public static String lastDirectory = null;
    public static String lastVideoDirectory = null;
    public static String lastMusicDirectory = null;

    @Override
    public void start(Stage stage) throws IOException {
        languages.add(Locale.forLanguageTag("en-US"));
        languages.add(Locale.forLanguageTag("es-ES"));
        globalLanguage = Locale.forLanguageTag("en-US");
        buttonsBundle = ResourceBundle.getBundle("buttons", globalLanguage);
        textBundle = ResourceBundle.getBundle("text", globalLanguage);
        LoadData();

        //Set a few of TheMovieDB translations for metadata
        tmdbLanguages = List.of(new Locale[]{
                Locale.forLanguageTag("es-ES"),
                Locale.forLanguageTag("es-MX"),
                Locale.forLanguageTag("en-US"),
                Locale.forLanguageTag("de-DE"),
                Locale.forLanguageTag("it-IT"),
                Locale.forLanguageTag("fr-FR")
        });

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle(textBundle.getString("desktopMode"));
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
        stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
        DesktopViewController desktopViewController = fxmlLoader.getController();
        desktopViewController.initValues();
        primaryStage = stage;
        FXResizeHelper rh = new FXResizeHelper(stage, 0, 5);
        stage.show();
    }

    public static boolean isRepeatedID(String uuid){
        return discs.containsKey(uuid);
    }
    public static boolean isRepeatedSeriesID(String uuid){
        return series.containsKey(uuid);
    }

    public static boolean isRepeatedSeasonID(String uuid){
        return seasons.containsKey(uuid);
    }

    public static void setPopUpProperties(Stage stage){
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(App.primaryStage);
    }

    public static List<String> getLanguages(){
        List<String> langs = new ArrayList<>();
        for (Locale locale : languages){
            langs.add(locale.getDisplayName());
        }
        return langs;
    }

    public void LoadData() {
        String collectionsFile = "Collections.json";
        String seasonsFile = "Seasons.json";
        String discsFile = "Discs.json";
        String catFile = "Categories.json";

        Gson gson = new Gson();

        try{
            JsonReader reader = new JsonReader(new FileReader(collectionsFile));
            Type type = new TypeToken<Map<String, Series>>() {}.getType();
            series = new Gson().fromJson(reader, type);

            reader = new JsonReader(new FileReader(seasonsFile));
            type = new TypeToken<Map<String, Season>>() {}.getType();
            seasons = new Gson().fromJson(reader, type);

            reader = new JsonReader(new FileReader(discsFile));
            type = new TypeToken<Map<String, Disc>>() {}.getType();
            discs = new Gson().fromJson(reader, type);

            reader = new JsonReader(new FileReader(catFile));
            Category[] catList = gson.fromJson(reader, Category[].class);
            if (catList != null)
                categories.addAll(List.of(catList));
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
    }

    public static void changeLanguage(String lang){

        for (Locale language : languages){
            if (language.getDisplayName().equals(lang)){
                globalLanguage = language;
                break;
            }
        }

        buttonsBundle = ResourceBundle.getBundle("buttons", globalLanguage);
        textBundle = ResourceBundle.getBundle("text", globalLanguage);
    }

    public static List<Series> getSeriesFromCategory(String cat){
        Category category = findCategory(cat);
        if (category == null)
            return null;

        List<Series> seriesList = new ArrayList<>();
        for(String seriesID : category.series){
            seriesList.add(findSeries(seriesID));
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

    public static void addCategory(String n, String lang, String t, List<String> f, boolean s){
        categories.add(new Category(n, lang, t, f, s));
    }

    public static void editCategory(String c, String lang, String type, List<String> folders, boolean showOnFullscreen){
        for (Category cat : categories){
            if (cat.name.equals(c)){
                cat.name = c;
                cat.language = lang;
                cat.type = type;
                cat.folders = folders;
                cat.showOnFullscreen = showOnFullscreen;
            }
        }
    }

    public static void removeCategory(String name){
        Category category = null;

        for (Category cat : categories){
            if (cat.name.equals(name)){
                category = cat;
                break;
            }
        }

        if (category == null)
            return;

        for (String seriesID : category.series){
            try{
                removeCollection(App.findSeries(seriesID));
            } catch (IOException e) {
                System.err.println("removeCategory error");
            }
        }

        categories.remove(category);
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
        series.put(col.id, col);
    }

    public static void addSeason(Season season, String seriesId){
        seasons.put(season.id, season);

        Series s = series.get(seriesId);
        if (s != null)
            s.addSeason(season);
    }

    public static void addDisc(Disc d){
        discs.put(d.id, d);

        Objects.requireNonNull(findSeason(d.getSeasonID())).setDisc(d);
    }

    public static Series findSeries(String id){
        return series.get(id);
    }

    public static Season findSeason(String id){
        return seasons.get(id);
    }

    public static Disc findDisc(String id){
        return discs.get(id);
    }

    public static void removeCollection(Series col) throws IOException {
        Series s = findSeries(col.id);
        assert s != null;

        for (String seasonID : col.seasons){
            removeSeason(seasonID);
        }

        Files.delete(FileSystems.getDefault().getPath(s.getCoverSrc()));
        s.clearSeasons();
        series.remove(s.id);
        SaveData();
    }

    public static void removeSeason(String id){
        Season s = seasons.get(id);
        assert s != null;

        try{
            FileUtils.deleteDirectory(new File("src/main/resources/img/backgrounds/" + id + "/"));
            if (!s.getLogoSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(s.getLogoSrc()));
            if (!s.getMusicSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(s.getMusicSrc()));
            if (!s.getVideoSrc().isEmpty())
                Files.delete(FileSystems.getDefault().getPath(s.getVideoSrc()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> dList = s.getDiscs();
        if (dList != null){
            for (String i : dList){
                removeDisc(App.findDisc(i));
            }
        }

        for (Series serie: series.values()){
            if (serie.getSeasons().contains(s.getId())) {
                serie.removeSeason(s.getId());
                break;
            }
        }

        seasons.remove(s.id);
    }

    public static void removeDisc(Disc d){
        Season s = findSeason(d.getSeasonID());
        if (s != null)
            s.removeDisc(d.id);

        try{
            FileUtils.deleteDirectory(new File("src/main/resources/img/discCovers/" + d.id + "/"));
        } catch (IOException e) {
            System.err.println("App: Error deleting directory: src/main/resources/img/discCovers/" + d.id + "/");
        }

        discs.remove(d.id);
    }

    public static boolean nameExist(String name){
        for (Series s : series.values()){
            if (s.getName().equals(name))
                return true;
        }
        return false;
    }

    public static List<Series> getCollection(){
        List<Series> seriesList = new ArrayList<>(series.values().stream().toList());
        seriesList.sort(new Utils.SeriesComparator());
        return seriesList;
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