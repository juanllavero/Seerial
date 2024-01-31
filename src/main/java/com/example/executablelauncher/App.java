package com.example.executablelauncher;

import com.example.executablelauncher.entities.Category;
import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.utils.Configuration;
import com.example.executablelauncher.utils.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;

public class App extends Application {
    //region LOCALIZATION ATTRIBUTES
    public static List<Locale> languages = new ArrayList<>();
    public static List<Locale> tmdbLanguages = new ArrayList<>();
    public static Locale globalLanguage;
    public static ResourceBundle buttonsBundle;
    public static ResourceBundle textBundle;
    //endregion
    public static Category currentCategory = null;
    public static Series selectedSeries = null;
    public static Stage primaryStage;
    public static String lastDirectory = null;
    public static String lastVideoDirectory = null;
    public static String lastMusicDirectory = null;

    @Override
    public void start(Stage stage) throws IOException {
        //DB Initialization
        DBManager.INSTANCE.openDB();

        //Set global language
        globalLanguage = Locale.forLanguageTag(Configuration.loadConfig("currentLanguageTag", "en-US"));

        //Add languages
        languages.add(Locale.forLanguageTag("en-US"));
        languages.add(Locale.forLanguageTag("es-ES"));

        //Set resource bundles
        buttonsBundle = ResourceBundle.getBundle("buttons", globalLanguage);
        textBundle = ResourceBundle.getBundle("text", globalLanguage);

        //Set a few of TheMovieDB translations for metadata
        tmdbLanguages = List.of(new Locale[]{
                Locale.forLanguageTag("es-ES"),
                Locale.forLanguageTag("en-US"),
                Locale.forLanguageTag("de-DE"),
                Locale.forLanguageTag("it-IT"),
                Locale.forLanguageTag("fr-FR")
        });

        //Start the stage
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle(textBundle.getString("desktopMode"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image("file:src/main/resources/img/icons/AppIcon.png"));
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

        primaryStage.setOnCloseRequest(event -> close());
    }

    public static void close(){
        DBManager.INSTANCE.closeDB();
        Platform.exit();
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

    public static void changeLanguage(String lang){

        for (Locale language : languages){
            if (language.getDisplayName().equals(lang)){
                globalLanguage = language;
                Configuration.saveConfig("currentLanguageTag", globalLanguage.toLanguageTag());
                break;
            }
        }

        buttonsBundle = ResourceBundle.getBundle("buttons", globalLanguage);
        textBundle = ResourceBundle.getBundle("text", globalLanguage);
    }

    public static List<Category> getCategories(boolean fullscreen){
        return DBManager.INSTANCE.getCategories(fullscreen);
    }

    public static List<String> getCategoriesNames(){
        List<Category> catList = DBManager.INSTANCE.getCategories(false);
        List<String> categoriesNames = new ArrayList<>();

        for (Category cat : catList)
            categoriesNames.add(cat.name);

        return categoriesNames;
    }
    //endregion

    public static void setCurrentCategory(Category cat){ currentCategory = cat; }

    public static Category getCurrentCategory(){
        return currentCategory;
    }

    public static void setSelectedSeries(Series series){ selectedSeries = series; }

    public static Series getSelectedSeries(){
        return selectedSeries;
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