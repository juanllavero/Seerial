package com.example.executablelauncher;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    public static List<Series> series = new ArrayList<>();
    public static List<Season> seasons = new ArrayList<>();
    public static List<Disc> discs = new ArrayList<>();
    public static List<String> categories = new ArrayList<>();
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        LoadData();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("VideoLauncher Desktop");
        //stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        //stage.setMaximized(true);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.3);
        stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.3);
        stage.setMinWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
        stage.setMinHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
        DesktopViewController desktopViewController = fxmlLoader.getController();
        desktopViewController.initValues();
        primaryStage = stage;
        stage.show();
    }

    public void LoadData() {
        String collectionsFile = "Collections.json";
        String seasonsFile = "Seasons.json";
        String discsFile = "Discs.json";
        String catFile = "Categories.json";

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
            String[] catList = gson.fromJson(reader, String[].class);
            if (catList != null)
                categories.addAll(List.of(catList));
        } catch (FileNotFoundException e) {
            System.err.println("Json files not found");
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
            if (s != null && s.getCategory().equals(cat)){
                seriesList.add(s);
            }
        }

        seriesList.sort(new Utils.SeriesComparator());

        return seriesList;
    }

    public static List<String> getCategories(){
        return categories;
    }

    public static void addCategory(String c){
        categories.add(c);
    }

    public static boolean categoryExist(String cat){
        for (String c : categories){
            if (c.equals(cat))
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
            if (!s.getLogoSrc().equals("NO_LOGO"))
                Files.delete(FileSystems.getDefault().getPath(s.getLogoSrc()));
            if (!s.getMusicSrc().equals("NO_MUSIC"))
                Files.delete(FileSystems.getDefault().getPath(s.getMusicSrc()));
            if (!s.getVideoSrc().equals("NO_VIDEO"))
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

    public static void main(String[] args) {
        launch(args);
    }
}