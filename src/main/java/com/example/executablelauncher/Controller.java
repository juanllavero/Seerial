package com.example.executablelauncher;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
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
import java.util.stream.Stream;

import javafx.scene.image.ImageView;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
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

    @FXML
    private HBox categoriesBox;

    private Series seriesToEdit;
    private CardController seriesToEditController;
    private List<Series> collectionList;

    private MediaPlayer backgroundMusicPlayer;
    private List<Timeline> coverBorderTimelines = new ArrayList<>();

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
        closeButton.prefWidthProperty().bind(sideMenu.prefWidthProperty());

        //Remove horizontal and vertical scroll
        DesktopViewController.scrollModification(scrollPane);

        List<String> categories = Main.getCategories();

        for (String cat : categories){
            Button btn = new Button();
            btn.setText(cat);

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                showSeriesFrom(btn.getText());
            });

            categoriesBox.getChildren().add(btn);
        }

        showSeriesFrom(categories.get(0));

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void showSeriesFrom(String cat){
        cardContainer.getChildren().clear();
        collectionList = new ArrayList<>(Main.getSeriesFromCategory(cat));
        for (Series col : collectionList) {
            addCard(col);
        }
        defaultSelection();
    }

    public void defaultSelection(){
        if (!cardContainer.getChildren().isEmpty()){
            selectSeries(collectionList.get(0));
        }else{
            seriesToEdit = null;
            contextMenuLabel.setText("");
            File file = new File("src/main/resources/img/backgroundDefault.jpeg");
            Image image = new Image(file.getAbsolutePath());
            backgroundImage.setImage(image);
            BackgroundImage myBI= new BackgroundImage(new Image(file.getAbsolutePath(),
                    Screen.getPrimary().getBounds().getWidth(),Screen.getPrimary().getBounds().getHeight(),false,true),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            mainBox.setBackground(new Background(myBI));
        }
    }

    public void selectSeries(Series s){
        playInteractionSound();

        //Clear selection
        for (Node node : cardContainer.getChildren()){
            node.getStyleClass().clear();
            node.getStyleClass().add("coverNotSelected");
        }

        for (Timeline t : coverBorderTimelines){
            t.stop();
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
                    //backgroundImage.setVisible(false);

                    BackgroundImage myBI= new BackgroundImage(new Image(season.getFullScreenBlurImageSrc(),
                            Screen.getPrimary().getBounds().getWidth(),Screen.getPrimary().getBounds().getHeight(),false,true),
                            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT);
                    mainBox.setBackground(new Background(myBI));
                    GaussianBlur blur = new GaussianBlur();
                    blur.setRadius(28);
                }
            }

            Node node = cardContainer.getChildren().get(collectionList.indexOf(s));
            //node.getStyleClass().clear();
            //node.getStyleClass().add("coverSelected");
            coverBorderTimelines.get(collectionList.indexOf(s)).play();


            //Fade out effect
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), backgroundImage);
            fadeOut.setFromValue(1.5);
            fadeOut.setToValue(0.0);
            fadeOut.play();

            //Fade in effect
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1.5);
            fadeIn.play();

            contextMenuLabel.setText(s.getName());
        }
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
            cardVBox.setPrefWidth(286);
            cardVBox.setAlignment(Pos.CENTER);
            cardVBox.getChildren().add(cardBox);

            cardBox.setBackground(Background.EMPTY);

            Color[] colors = Stream.of("#d7d7d7", "#e5e5e5",  "#e0e0e0", "#d4d4d4", "#c9c9c9", "#bcbcbc", "#afafaf", "#a0a0a0", "#939393"
                            , "#797979", "#737373", "#6a6a6a", "#5f5f5f", "#545454", "#494949", "#444444", "#3a3a3a", "#343434", "#2c2c2c", "#2c2c2c"
                    , "#343434", "#3a3a3a", "#444444", "#494949", "#545454", "#5f5f5f", "#6a6a6a", "#737373", "#797979", "#939393", "#a0a0a0", "#afafaf"
                    , "#bcbcbc", "#c9c9c9", "#d4d4d4", "#e0e0e0", "#e5e5e5", "#d7d7d7")
                    .map(Color::web)
                    .toArray(Color[]::new);

            int[] mills = {-100};
            KeyFrame[] keyFrames = Stream.iterate(0, i -> i+1)
                    .limit(100)
                    .map(i -> new LinearGradient(0, 1, 0, 1, true, CycleMethod.NO_CYCLE
                            , new Stop(0, colors[i%colors.length]), new Stop(1, colors[(i+1)%colors.length])))
                    .map(lg -> new Border(new BorderStroke(lg, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3))))
                    .map(b -> new KeyFrame(Duration.millis(mills[0]+=100), new KeyValue(cardVBox.borderProperty(), b, Interpolator.EASE_IN)))
                    .toArray(KeyFrame[]::new);

            Timeline timeline = new Timeline(keyFrames);
            timeline.setCycleCount(Timeline.INDEFINITE);
            coverBorderTimelines.add(timeline);

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
    void switchToDesktop(MouseEvent event){
        playInteractionSound();
        try {
            Main.SaveData();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Desktop Mode");
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMinHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
            stage.setMinWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
            stage.initStyle(StageStyle.DECORATED);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
            stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);

            DesktopViewController desktopViewController = fxmlLoader.getController();
            desktopViewController.initValues();
            stage.show();

            Stage thisStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void editSeries(MouseEvent event){
        //Edit "sorting order" and "category"
    }
}