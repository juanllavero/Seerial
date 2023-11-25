package com.example.executablelauncher;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Controller implements Initializable {
    @FXML
    private FlowPane cardContainer;

    @FXML
    private HBox hBox;

    @FXML
    private BorderPane mainPane;

    @FXML
    private ImageView menuButton;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox sideMenu;

    @FXML
    private AnchorPane sideMenuBox;

    @FXML
    private StackPane sideMenuParent;

    @FXML
    private FlowPane contextMenuParent;

    @FXML
    private VBox contextMenu;

    @FXML
    private Button addColButton;

    @FXML
    private Button addCatButton;

    @FXML
    private Button closeButton;

    @FXML
    private HBox topBar;

    @FXML
    private BorderPane topBorderPane;

    @FXML
    private StackPane firstPane;

    @FXML
    private Label contextMenuLabel;

    @FXML
    private HBox mainBox;

    private Series seriesToEdit;
    private CardController seriesToEditController;
    private List<Series> collectionList;

    private MediaPlayer backgroundMusicPlayer;

    @FXML
    private void close(MouseEvent event) throws IOException {
        playInteractionSound();
        Main.SaveData();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardContainer.setPadding(new Insets(15, 15, 15, 100));
        sideMenuParent.setVisible(false);

        playBackgroundSound();

        //Open/Close Side Menu
        sideMenu.setVisible(false);
        contextMenu.setVisible(false);
        menuButton.setOnMouseClicked(mouseEvent -> {
            playInteractionSound();
            sideMenuParent.setVisible(true);
            sideMenu.setVisible(true);
        });

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                hideContextMenu();
            }
        });

        //Load collections
        collectionList = new ArrayList<>(Main.getCollection());
        for (Series col : collectionList) {
            addCard(col);
        }

        //Add css to scrollPane to make it look modern
        scrollPane.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("styles.css")).toExternalForm());

        //Fit width and height of components to window size
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        mainBox.prefWidth(screenWidth);
        mainBox.prefHeight(screenHeight);
        firstPane.prefHeightProperty().bind(mainBox.heightProperty());
        firstPane.prefWidthProperty().bind(mainBox.widthProperty());
        mainPane.prefWidthProperty().bind(firstPane.prefWidthProperty());
        mainPane.prefHeightProperty().bind(firstPane.prefHeightProperty());
        hBox.prefWidthProperty().bind(mainPane.widthProperty());
        hBox.prefHeightProperty().bind(mainPane.heightProperty());
        scrollPane.prefWidthProperty().bind(hBox.widthProperty());
        cardContainer.prefWidthProperty().bind(hBox.widthProperty());
        scrollPane.prefHeightProperty().bind(hBox.heightProperty());
        cardContainer.prefHeightProperty().bind(hBox.heightProperty());
        topBorderPane.prefWidthProperty().bind(topBar.widthProperty());
        backgroundImage.setFitHeight(screenHeight);
        backgroundImage.setFitWidth(screenWidth);
        backgroundImage.setPreserveRatio(false);
        contextMenuParent.prefHeightProperty().bind(mainBox.heightProperty());
        contextMenuParent.prefWidthProperty().bind(mainBox.widthProperty());
        
        sideMenuBox.setPrefHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
        sideMenuBox.setPrefWidth(Screen.getPrimary().getBounds().getWidth() / 4.5);
        sideMenu.prefHeightProperty().bind(sideMenuBox.prefHeightProperty());
        sideMenu.prefWidthProperty().bind(sideMenuBox.prefWidthProperty());
        addColButton.prefWidthProperty().bind(sideMenu.prefWidthProperty());
        addCatButton.prefWidthProperty().bind(sideMenu.prefWidthProperty());
        closeButton.prefWidthProperty().bind(sideMenu.prefWidthProperty());

        //Remove horizontal and vertical scroll
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //Changes the scrolling speed
        final double SPEED = 0.0025;
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });

        defaultSelection();

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void defaultSelection(){
        if (!cardContainer.getChildren().isEmpty()){
            selectSeries(collectionList.get(0));
        }
    }

    public void selectSeries(Series s){
        playInteractionSound();

        //Clear selection
        for (Node node : cardContainer.getChildren()){
            node.getStyleClass().clear();
            node.getStyleClass().add("coverNotSelected");
        }

        if (s.getName().equals(contextMenuLabel.getText())){
            contextMenuLabel.setText("");
            showSeason(s);
        }else{
            if (!s.getSeasons().isEmpty()){
                Season season = Main.findSeason(s.getSeasons().get(0));
                if (season != null){
                    Image image = new Image(season.getBackgroundSrc());
                    backgroundImage.setImage(image);
                }
            }

            Node node = cardContainer.getChildren().get(collectionList.indexOf(s));
            node.getStyleClass().clear();
            node.getStyleClass().add("coverSelected");

            //Fade out effect
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), backgroundImage);
            fadeOut.setFromValue(3.5);
            fadeOut.setToValue(0.0);
            fadeOut.play();

            //Fade in effect
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(3.5);
            fadeIn.play();

            contextMenuLabel.setText(s.getName());
        }
    }

    public void addSeries(Series s){
        collectionList.add(s);
        addCard(s);
    }

    private void addCard(Series s){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("card.fxml"));
            Pane cardBox = fxmlLoader.load();
            CardController cardController = fxmlLoader.getController();
            cardController.setParent(this);
            cardController.setData(s);

            VBox cardVBox = new VBox();
            cardVBox.setPrefHeight(364);
            cardVBox.setPrefWidth(247);
            cardVBox.setAlignment(Pos.CENTER);
            cardVBox.getChildren().add(cardBox);

            cardContainer.getChildren().add(cardVBox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void Remove(MouseEvent event) throws IOException {
        if (seriesToEdit != null){
            int index = collectionList.indexOf(seriesToEdit);
            if (!cardContainer.getChildren().isEmpty() && cardContainer.getChildren().size() > index)
                cardContainer.getChildren().remove(index);
            collectionList.remove(index);
            Main.removeCollection(seriesToEdit);
            Files.delete(FileSystems.getDefault().getPath(seriesToEdit.getCoverSrc()));
            seriesToEdit = null;
            defaultSelection();
        }
        hideContextMenu();
    }

    @FXML
    void addCollection(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCollection-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddCollectionController addColController = fxmlLoader.getController();
            addColController.setParentController(this);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Add Series");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideContextMenu();
    }

    @FXML
    void addSeason(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addSeason-view.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            AddSeasonController addSeasonController = fxmlLoader.getController();
            addSeasonController.setCollection(seriesToEdit);
            Stage stage = new Stage();
            stage.setTitle("Add Season");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideContextMenu();
    }

    @FXML
    void editSeries(){
        if (seriesToEdit != null && seriesToEditController != null){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCollection-view.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                AddCollectionController addColController = fxmlLoader.getController();
                addColController.setParentController(this);
                addColController.setParentCardController(seriesToEditController);
                addColController.setSeries(seriesToEdit);
                Stage stage = new Stage();
                stage.setTitle("Edit Series");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hideContextMenu();
        }
    }

    public void showContextMenu(Series s, CardController seriesController){
        playInteractionSound();
        seriesToEdit = s;
        seriesToEditController = seriesController;
        contextMenu.setVisible(true);
        sideMenuParent.setVisible(true);
    }

    @FXML
    void hideContextMenu(){
        playInteractionSound();
        seriesToEdit = null;
        contextMenu.setVisible(false);
        sideMenuParent.setVisible(false);
        sideMenu.setVisible(false);
    }

    public void showSeason(Series s){
        if (s != null && !s.getSeasons().isEmpty()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("season-view.fxml"));
                Parent root = fxmlLoader.load();
                SeasonController seasonController = fxmlLoader.getController();
                seasonController.setParent(this);
                Series newSeries = Main.findSeries(s);
                if (newSeries != null)
                    seasonController.setSeasons(newSeries.getSeasons());
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setTitle("Season");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.setWidth(Screen.getPrimary().getBounds().getWidth());
                stage.setHeight(Screen.getPrimary().getBounds().getHeight());
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void playInteractionSound() {
        File file = new File("src/main/resources/audio/interaction.wav");
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(0.15);
        player.seek(player.getStartTime());
        player.play();
    }

    public void playCategoriesSound() {
        File file = new File("src/main/resources/audio/categories.wav");
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(0.15);
        player.seek(player.getStartTime());
        player.play();
    }

    public void playBackgroundSound() {
        File file = new File("src/main/resources/audio/background.mp3");
        Media media = new Media(file.toURI().toString());
        backgroundMusicPlayer = new MediaPlayer(media);
        backgroundMusicPlayer.setVolume(0.15);
        backgroundMusicPlayer.seek(backgroundMusicPlayer.getStartTime());
        backgroundMusicPlayer.play();
    }

    public void stopBackground(){
        backgroundMusicPlayer.stop();
    }

    @FXML
    void addCategory(MouseEvent event){

    }

    @FXML
    void switchToDesktop(MouseEvent event){
        playInteractionSound();
        try {
            Main.SaveData();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
            Parent root = fxmlLoader.load();
            DesktopViewController desktopViewController = fxmlLoader.getController();
            desktopViewController.initValues();
            Stage stage = new Stage();
            stage.setTitle("Desktop Mode");
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMinHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
            stage.setMinWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
            stage.initStyle(StageStyle.DECORATED);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
            stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
            stage.show();

            Stage thisStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}