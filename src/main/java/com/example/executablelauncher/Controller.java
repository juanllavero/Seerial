package com.example.executablelauncher;

import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private Button menuButton;

    @FXML
    private ImageView menuShadow;

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
    private HBox topBar;

    @FXML
    private BorderPane topBorderPane;

    @FXML
    private StackPane mainBox;

    @FXML
    private HBox categoriesBox;

    @FXML
    private Button switchToDesktopButton;

    @FXML
    private Button exitButton;

    @FXML
    private Label clock;

    private Series seriesToEdit;
    private List<Series> collectionList = new ArrayList<>();
    private List<Button> seriesButtons = new ArrayList<>();

    private MediaPlayer backgroundMusicPlayer;
    private List<Timeline> coverBorderTimelines = new ArrayList<>();

    @FXML
    private void close(ActionEvent event) throws IOException {
        playInteractionSound();
        App.SaveData();
        Stage stage = (Stage) mainBox.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardContainer.setPadding(new Insets(15, 15, 15, 100));
        sideMenuParent.setVisible(false);

        exitButton.setText(App.buttonsBundle.getString("exitFullscreen"));
        switchToDesktopButton.setText(App.buttonsBundle.getString("switchToDesktop"));

        playBackgroundSound();



        //Open/Close Side Menu
        sideMenu.setVisible(false);
        menuButton.setOnMouseClicked(mouseEvent -> {
            playInteractionSound();
            sideMenuParent.setVisible(true);
            sideMenu.setVisible(true);
        });

        menuButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ENTER == event.getCode()) {
                playInteractionSound();
                sideMenuParent.setVisible(true);
                sideMenu.setVisible(true);
            }
        });

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode() || KeyCode.BACK_SPACE == event.getCode()) {
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
        mainPane.prefWidthProperty().bind(mainBox.prefWidthProperty());
        mainPane.prefHeightProperty().bind(mainBox.prefHeightProperty());
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
        
        sideMenuBox.setPrefHeight(Screen.getPrimary().getBounds().getHeight());
        sideMenuBox.setPrefWidth(Screen.getPrimary().getBounds().getWidth() / 6);
        sideMenu.prefHeightProperty().bind(sideMenuBox.prefHeightProperty());
        sideMenu.prefWidthProperty().bind(sideMenuBox.prefWidthProperty());

        //Remove horizontal and vertical scroll
        DesktopViewController.scrollModification(scrollPane);

        menuShadow.setFitWidth(screenWidth);
        menuShadow.setFitHeight(screenHeight);
        menuShadow.setVisible(false);

        List<String> categories = App.getFullscreenCategories();

        for (String cat : categories){
            Button btn = new Button();
            btn.setText(cat);
            btn.getStyleClass().add("CatButton");
            HBox.setMargin(btn, new Insets(0, 5, 0, 0));

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                playCategoriesSound();
                showSeriesFrom(btn.getText());
            });

            btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
                if (KeyCode.ENTER == event.getCode()) {
                    playCategoriesSound();
                    showSeriesFrom(btn.getText());
                }
            });

            btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
                playInteractionSound();
            });

            categoriesBox.getChildren().add(btn);
        }

        //region CLOCK TIMELINE
        updateHour();
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.minutes(1),
                event -> updateHour()
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        //endregion

        showSeriesFrom(categories.get(0));
        defaultSelection();
    }

    private void updateHour() {
        // Obtener la hora actual
        LocalTime currentTime = LocalTime.now();

        DateTimeFormatter format24 = DateTimeFormatter.ofPattern("HH:mm");
        String time = currentTime.format(format24);

        clock.setText(time);
    }

    public void showSeriesFrom(String cat){
        cardContainer.getChildren().clear();
        seriesButtons.clear();
        collectionList = App.getSeriesFromCategory(cat);
        for (Series col : collectionList) {
            addCard(col);
        }

        seriesToEdit = null;

        String src;
        if (!collectionList.isEmpty() && !collectionList.get(0).getSeasons().isEmpty()){
            seriesToEdit = collectionList.get(0);
            src = "file:src/main/resources/img/backgrounds/" + (Objects.requireNonNull(App.findSeason(collectionList.get(0).getSeasons().get(0))).id) + "/fullBlur.png";
        }else{
            src = "file:src/main/resources/img/backgroundDefault.jpeg";
        }

        Image image = new Image(src);
        backgroundImage.setImage(image);
        BackgroundImage myBI= new BackgroundImage(new Image(src,
                Screen.getPrimary().getBounds().getWidth(),Screen.getPrimary().getBounds().getHeight(),false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        mainBox.setBackground(new Background(myBI));

        Background bi = mainBox.getBackground();
        Background black = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));

        mainBox.setBackground(null);
        mainBox.setBackground(black);

        //Fade in effect
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
        fadeIn.setFromValue(0.2);
        fadeIn.setToValue(1);
        fadeIn.play();

        fadeIn.setOnFinished(event -> mainBox.setBackground(bi));
    }

    public void defaultSelection(){
        if (!collectionList.isEmpty()) {
            seriesToEdit = null;
            selectSeries(collectionList.get(0));
        }
    }

    public void selectSeries(Series s){
        playInteractionSound();

        if (seriesToEdit != s){
            seriesToEdit = s;
            int i = collectionList.indexOf(seriesToEdit);
            seriesButtons.get(i).requestFocus();

            for (Timeline t : coverBorderTimelines){
                t.stop();
            }


            if (!s.getSeasons().isEmpty()){
                Season season = App.findSeason(s.getSeasons().get(0));
                if (season != null){
                    /*Image image = new Image("file:src/main/resources/img/backgrounds/" + season.id + "/fullBlur.png");
                    backgroundImage.setImage(image);
                    //backgroundImage.setPreserveRatio(true);
                    backgroundImage.setSmooth(true);
                    backgroundImage.setCache(true);*/

                    ImageView background = new ImageView(new Image("file:src/main/resources/img/backgrounds/" + season.id + "/fullBlur.png",
                            Screen.getPrimary().getBounds().getWidth(),Screen.getPrimary().getBounds().getHeight(),false,true));

                    BackgroundImage myBI= new BackgroundImage(Objects.requireNonNull(getCroppedImage(background)),
                            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT);
                    mainBox.setBackground(new Background(myBI));

                    backgroundImage.setImage(getCroppedImage(background));
                }

                //Node node = cardContainer.getChildren().get(collectionList.indexOf(s));
                //node.getStyleClass().clear();
                //node.getStyleClass().add("coverSelected");
                //coverBorderTimelines.get(collectionList.indexOf(s)).play();

                Background bi = mainBox.getBackground();
                Background black = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));

                mainBox.setBackground(null);
                mainBox.setBackground(black);

                //Fade in effect
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
                fadeIn.setFromValue(0.2);
                fadeIn.setToValue(1);
                fadeIn.play();

                fadeIn.setOnFinished(event -> mainBox.setBackground(bi));
            }
        }
    }

    private WritableImage getCroppedImage(ImageView img){
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double targetAspectRatio = screenWidth / screenHeight;

        double originalWidth = img.getImage().getWidth();
        double originalHeight = img.getImage().getHeight();
        double originalAspectRatio = originalWidth / originalHeight;

        double newWidth, newHeight;
        if (originalAspectRatio > targetAspectRatio) {
            newWidth = originalHeight * targetAspectRatio;
            newHeight = originalHeight;
        } else {
            newWidth = originalWidth;
            newHeight = originalWidth / targetAspectRatio;
        }

        double xOffset = 0;
        double yOffset = 0;

        PixelReader pixelReader = img.getImage().getPixelReader();

        if (newWidth == 0 || newHeight == 0)
            return null;

        return new WritableImage(pixelReader, 0, 0, (int) newWidth, (int) newHeight);
    }

    private void addCard(Series s){
        /*
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

        cardContainer.getChildren().add(cardVBox);*/

        Image img = new Image("file:" + s.getCoverSrc(), 273, 351, false, true);
        ImageView image = new ImageView(img);

        Button btn = new Button();
        btn.setGraphic(image);
        btn.setPrefHeight(364);
        btn.setPrefWidth(286);
        btn.setPadding(new Insets(0, 1, 2, 0));
        btn.setAlignment(Pos.CENTER);
        btn.getStyleClass().add("seriesCoverButton");
        seriesButtons.add(btn);

        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                selectSeries(collectionList.get(seriesButtons.indexOf(btn)));
                playInteractionSound();
            }
        });

        btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (event.getCode().equals(KeyCode.ENTER)){
                if (seriesToEdit != null) {
                    playCategoriesSound();
                    showSeason(seriesToEdit);
                }
            }else if (event.getCode().equals(KeyCode.X) || event.getCode().equals(KeyCode.GAME_C)){
                showSeriesMenu();
            }
        });

        btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if (event.getButton().equals(MouseButton.PRIMARY)){
                //selectSeries(collectionList.get(seriesButtons.indexOf(btn)));
                //playInteractionSound();
            }else if (event.getButton().equals(MouseButton.SECONDARY)){
                showSeriesMenu();
            }
        });

        cardContainer.getChildren().add(btn);
    }

    public void Remove() throws IOException {
        /*if (seriesToEdit != null){
            int index = collectionList.indexOf(seriesToEdit);
            if (!cardContainer.getChildren().isEmpty() && cardContainer.getChildren().size() > index)
                cardContainer.getChildren().remove(index);
            collectionList.remove(index);
            App.removeCollection(seriesToEdit);
            Files.delete(FileSystems.getDefault().getPath(seriesToEdit.getCoverSrc()));
            seriesToEdit = null;
            defaultSelection();
        }*/
        hideContextMenu();
    }

    public void showContextMenu(Series s){
        playInteractionSound();
        seriesToEdit = s;
        sideMenuParent.setVisible(true);
    }

    public void showSeriesMenu(){
        menuShadow.setVisible(true);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("seriesMenu-view.fxml"));
            Parent root = fxmlLoader.load();
            SeriesMenuController controller = fxmlLoader.getController();
            controller.setParentController(this);
            controller.setLabel(seriesToEdit.getName());
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setAlwaysOnTop(true);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Cannot load menu");
        }
    }

    @FXML
    void hideContextMenu(){
        playInteractionSound();
        seriesToEdit = null;
        sideMenuParent.setVisible(false);
        sideMenu.setVisible(false);
    }

    public void showSeason(Series s){
        if (s != null && !s.getSeasons().isEmpty()) {
            stopBackground();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("season-view.fxml"));
                Parent root = fxmlLoader.load();
                SeasonController seasonController = fxmlLoader.getController();
                seasonController.setParent(this);
                Series newSeries = App.findSeries(s.id);
                if (newSeries != null)
                    seasonController.setSeasons(newSeries.getSeasons(), newSeries.playSameMusic);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setTitle(App.textBundle.getString("season"));
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
    void switchToDesktop(ActionEvent event){
        playInteractionSound();
        try {
            App.SaveData();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("desktopMode"));
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 2);
            stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);

            DesktopViewController desktopViewController = fxmlLoader.getController();
            desktopViewController.initValues();
            FXResizeHelper rh = new FXResizeHelper(stage, 0, 5);
            stage.show();

            Stage thisStage = (Stage) mainBox.getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void editSeries(){
        //Edit "sorting order" and "category"
    }

    public void hideMenuShadow(){
        menuShadow.setVisible(false);
    }
}