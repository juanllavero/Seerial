package com.example.executablelauncher;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    public static List<Series> series = new ArrayList<>();
    public static List<Season> seasons = new ArrayList<>();
    public static List<Disc> discs = new ArrayList<>();
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        LoadData();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
        stage.setTitle("VideoLauncher");
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getBounds().getHeight());
        primaryStage = stage;
        stage.show();
    }

    public void LoadData() {
        String collectionsFile = "Collections.json";
        String seasonsFile = "Seasons.json";
        String discsFile = "Discs.json";

        Gson gson = new Gson();

        try{
            JsonReader reader = new JsonReader(new FileReader(collectionsFile));
            Series[] sList = gson.fromJson(reader,Series[].class);
            series.addAll(List.of(sList));

            reader = new JsonReader(new FileReader(seasonsFile));
            Season[] seasonList = gson.fromJson(reader, Season[].class);
            seasons.addAll(List.of(seasonList));

            reader = new JsonReader(new FileReader(discsFile));
            Disc[] dList = gson.fromJson(reader, Disc[].class);
            discs.addAll(List.of(dList));
        } catch (FileNotFoundException e) {
            System.err.println("Json files not found");
        }
    }

    public static void SaveData() throws IOException {
        String collectionsFile = "Collections.json";
        String seasonsFile = "Seasons.json";
        String discsFile = "Discs.json";

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
        List<Integer> sList = col.getSeasons();
        for (int i : sList){
            Season s = findSeason(i);
            List<Integer> dList = s.getDiscs();
            for (int j : dList){
                discs.remove(discs.get(j));
            }
            int index = -1;
            for (Season season : seasons){
                if (season.getId() == i) {
                    index = seasons.indexOf(season);
                    break;
                }
            }
            if (index != -1)
                seasons.remove(index);
        }
        series.remove(col);

        SaveData();
    }

    public static void removeSeason(int id){
        Season s = findSeason(id);
        List<Integer> dList = s.getDiscs();
        for (int i : dList){
            discs.remove(findDisc(i));
        }

        for (Series serie: series){
            if (serie.getSeasons().contains(s.getId())) {
                serie.getSeasons().remove(s.getId());
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
        return series;
    }

    public static void main(String[] args) {
        launch(args);
    }
}