package com.example.executablelauncher;

import com.example.executablelauncher.entities.Library;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.utils.Configuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
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
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    //region LOCALIZATION ATTRIBUTES
    public static List<Locale> languages = new ArrayList<>();
    public static List<Locale> tmdbLanguages = new ArrayList<>();
    public static Locale globalLanguage;
    public static ResourceBundle buttonsBundle;
    public static ResourceBundle textBundle;
    //endregion
    public static Series selectedSeries = null;
    public static Stage primaryStage;
    public static String lastDirectory = null;
    public static String lastVideoDirectory = null;
    public static String lastMusicDirectory = null;
    public static boolean isConnectedToInternet = false;
    static ScheduledExecutorService executorService;
    public static List<Task<Void>> tasks = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        //Data Initialization
        DataManager.INSTANCE.loadData();

        //Set global language
        globalLanguage = Locale.forLanguageTag(Configuration.loadConfig("currentLanguageTag", "en-US"));

        //Add languages
        String languageDir = "resources";
        File[] filesInDir = new File(languageDir).listFiles();

        if (filesInDir != null){
            for (File file : filesInDir){
                if (file.getName().matches("^buttons.*")){
                    String language = "";

                    language = file.getName().substring(file.getName().indexOf("_") + 1, file.getName().lastIndexOf("."));

                    String tag = language.substring(0, language.indexOf("_"));
                    String country = language.substring(language.indexOf("_") + 1);

                    languages.add(Locale.of(tag, country));
                }
            }
        }

        isConnectedToInternet = isInternetAvailable();

        //Check every minute if there is Internet connection
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(App::isInternetAvailable, 0, 5, TimeUnit.MINUTES);

        //Set resource bundles
        File file = new File("resources/");
        URL[] urls = {file.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        buttonsBundle = ResourceBundle.getBundle("buttons", globalLanguage, loader);
        textBundle = ResourceBundle.getBundle("text", globalLanguage, loader);

        //Set a few of TheMovieDB translations for metadata
        tmdbLanguages = List.of(new Locale[]{
            Locale.forLanguageTag("es-ES"),
            Locale.forLanguageTag("en-US"),
            Locale.forLanguageTag("de-DE"),
            Locale.forLanguageTag("it-IT"),
            Locale.forLanguageTag("fr-FR")
        });

        String mode = Configuration.loadConfig("fullscreen", "off");

        primaryStage = stage;

        if (mode.equals("off"))
            loadDesktopMode();
        else
            loadFullscreenMode();

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> close());
    }
    private void loadDesktopMode() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle(App.textBundle.getString("desktopMode"));
        primaryStage.getIcons().add(new Image("file:resources/img/icons/AppIcon.png"));
        Scene scene = new Scene(root);
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
        primaryStage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.25);
        DesktopViewController desktopViewController = fxmlLoader.getController();
        desktopViewController.initValues();
    }
    private void loadFullscreenMode() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();
        //Controller controller = fxmlLoader.getController();
        //controller.playIntroVideo();
        primaryStage = new Stage();
        primaryStage.setTitle(App.textBundle.getString("fullscreenMode"));
        primaryStage.getIcons().add(new Image("file:resources/img/icons/AppIcon.png"));
        Scene scene = new Scene(root);
        scene.setCursor(Cursor.NONE);
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setWidth(Screen.getPrimary().getBounds().getWidth());
        primaryStage.setHeight(Screen.getPrimary().getBounds().getHeight());
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
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Task<Void> task : tasks)
            if (task.isRunning())
                task.cancel();

        DataManager.INSTANCE.saveData();
        Platform.exit();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return address.isReachable(2000);
        } catch (IOException e) {
            return false;
        }
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

    public static boolean checkIfDriveIsConnected(String path){
        //Get connected drives
        File[] roots = File.listRoots();

        for (File root : roots) {
            if (path.startsWith(root.getPath())) {
                return true;
            }
        }

        return false;
    }

    public static void setPopUpProperties(Stage stage, Stage primaryStage){
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
    }

    public static void changeLanguage(Locale language){
        globalLanguage = language;
        Configuration.saveConfig("currentLanguageTag", globalLanguage.toLanguageTag());

        try{
            File file = new File("resources/");
            URL[] urls = {file.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            buttonsBundle = ResourceBundle.getBundle("buttons", globalLanguage, loader);
            textBundle = ResourceBundle.getBundle("text", globalLanguage, loader);
        } catch (MalformedURLException e) {
            System.err.println("changeLanguage: ResourceBundle files could not be loaded");
        }
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
        alert.setGraphic(null);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.class.getResource("styles.css")).toExternalForm());

        alert.getDialogPane().getButtonTypes().stream()
                .map(alert.getDialogPane()::lookupButton)
                .forEach(button -> button.getStyleClass().add("desktopButton"));

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}