package com.example.executablelauncher;

import com.example.executablelauncher.entities.Library;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.utils.Configuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {
    //region LOCALIZATION ATTRIBUTES
    public static List<Locale> languages = new ArrayList<>();
    public static List<Locale> tmdbLanguages = new ArrayList<>();
    public static Locale globalLanguage;
    public static ResourceBundle buttonsBundle;
    public static ResourceBundle textBundle;
    //endregion
    public static Library currentLibrary = null;
    public static Series selectedSeries = null;
    public static Stage primaryStage;
    public static String lastDirectory = null;
    public static String lastVideoDirectory = null;
    public static String lastMusicDirectory = null;

    @Override
    public void start(Stage stage) throws IOException {
        //Data Initialization
        DataManager.INSTANCE.loadData();

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
        stage.setTitle(App.textBundle.getString("desktopMode"));
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image("file:resources/img/icons/AppIcon.png"));
        Scene scene = new Scene(root);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
        stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.25);
        DesktopViewController desktopViewController = fxmlLoader.getController();
        desktopViewController.initValues();
        primaryStage = stage;
        //FXResizeHelper rh = new FXResizeHelper(stage, 0, 5);
        stage.show();

        primaryStage.setOnCloseRequest(event -> close());
    }

    public static boolean pressedUp(KeyEvent event){
        return event.getCode().equals(KeyCode.UP);
    }
    public static boolean pressedDown(KeyEvent event){
        return event.getCode().equals(KeyCode.DOWN);
    }
    public static boolean pressedLeft(KeyEvent event){
        return event.getCode().equals(KeyCode.LEFT);
    }
    public static boolean pressedRight(KeyEvent event){
        return event.getCode().equals(KeyCode.RIGHT);
    }
    public static boolean pressedSelect(KeyEvent event){
        return event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.SPACE);
    }
    public static boolean pressedBack(KeyEvent event){
        return event.getCode().equals(KeyCode.ESCAPE) || event.getCode().equals(KeyCode.BACK_SPACE);
    }

    public static boolean pressedRB(KeyEvent event){
        return event.getCode().equals(KeyCode.ADD) || event.getCode().equals(KeyCode.PLUS);
    }

    public static boolean pressedLB(KeyEvent event){
        return event.getCode().equals(KeyCode.SUBTRACT) || event.getCode().equals(KeyCode.MINUS);
    }

    public static void close(){
        DataManager.INSTANCE.saveData();
        Platform.exit();
    }

    //Create a simple file in the drive in order to avoid playing video with the drive suspended
    public static void wakeUpDrive(String rootDir){
        Task<Void> wakeDirTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    File tempFile = new File(rootDir + "/temp.txt");
                    if (tempFile.createNewFile()) {
                        tempFile.delete();
                    }
                } catch (IOException e) {
                    System.err.println("wakeUpDrive: Cannot access drive");
                }
                return null;
            }
        };

        new Thread(wakeDirTask).start();
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

    public static List<Library> getCategories(boolean fullscreen){
        return DataManager.INSTANCE.getCategories(fullscreen);
    }

    public static List<String> getCategoriesNames(){
        List<Library> catList = DataManager.INSTANCE.getCategories(false);
        List<String> categoriesNames = new ArrayList<>();

        for (Library cat : catList)
            categoriesNames.add(cat.name);

        return categoriesNames;
    }

    public static void setCurrentLibrary(Library cat){ currentLibrary = cat; }

    public static Library getCurrentLibrary(){
        return currentLibrary;
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