package com.example.executablelauncher;

import com.example.executablelauncher.entities.Category;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Controller implements Initializable {
    @FXML
    private FlowPane cardContainer;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button menuButton;

    @FXML
    private ImageView menuShadow;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView backgroundShadow;

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

    @FXML
    private ImageView globalShadow;

    private Series seriesToEdit;
    private List<Series> collectionList = new ArrayList<>();
    private List<Button> seriesButtons = new ArrayList<>();

    private MediaPlayer backgroundMusicPlayer;
    private String categoryType = null;
    private FadeTransition fadeTransition = null;
    private PauseTransition delay = null;

    @FXML
    private void close(ActionEvent event) throws IOException {
        playInteractionSound();
        App.SaveData();
        Stage stage = (Stage) mainBox.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sideMenuParent.setVisible(false);

        exitButton.setText(App.buttonsBundle.getString("exitFullscreen"));
        switchToDesktopButton.setText(App.buttonsBundle.getString("switchToDesktop"));

        playBackgroundSound();

        //Open/Close Side Menu
        sideMenu.setVisible(false);
        menuButton.setOnMouseClicked(mouseEvent -> {
            playInteractionSound();
            globalShadow.setVisible(true);
            sideMenuParent.setVisible(true);
            sideMenu.setVisible(true);
            switchToDesktopButton.requestFocus();
        });

        menuButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ENTER == event.getCode()) {
                playInteractionSound();
                globalShadow.setVisible(true);
                sideMenuParent.setVisible(true);
                sideMenu.setVisible(true);
                switchToDesktopButton.requestFocus();
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
        //mainBox.prefWidth(screenWidth);
        //mainBox.prefHeight(screenHeight);
        //mainPane.prefWidthProperty().bind(mainBox.prefWidthProperty());
        //mainPane.prefHeightProperty().bind(mainBox.prefHeightProperty());
        topBorderPane.prefWidthProperty().bind(topBar.widthProperty());
        backgroundImage.setFitHeight(screenHeight);
        backgroundImage.setFitWidth(screenWidth);
        backgroundImage.setPreserveRatio(false);

        backgroundShadow.setFitHeight(screenHeight);
        backgroundShadow.setFitWidth(screenWidth);
        backgroundShadow.setPreserveRatio(false);
        
        sideMenuBox.setPrefHeight(Screen.getPrimary().getBounds().getHeight());
        sideMenuBox.setPrefWidth(Screen.getPrimary().getBounds().getWidth() / 6);
        sideMenu.prefHeightProperty().bind(sideMenuBox.prefHeightProperty());
        sideMenu.prefWidthProperty().bind(sideMenuBox.prefWidthProperty());

        //Remove horizontal and vertical scroll
        DesktopViewController.scrollModification(scrollPane);

        menuShadow.setFitWidth(screenWidth);
        menuShadow.setFitHeight(screenHeight);
        menuShadow.setVisible(false);

        globalShadow.setFitWidth(screenWidth);
        globalShadow.setFitHeight(screenHeight);
        globalShadow.setVisible(false);

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
        Category category = App.findCategory(cat);
        categoryType = category.type;
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

        mainBox.setBackground(bi);

        //Fade in effect
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    public void defaultSelection(){
        if (!collectionList.isEmpty()) {
            seriesToEdit = null;
            mainPane.requestFocus();
            cardContainer.requestFocus();
            seriesButtons.get(0).requestFocus();
        }
    }

    public void selectSeries(Series s){
        if (seriesToEdit != s){
            seriesToEdit = s;

            delay = new PauseTransition(Duration.millis(400));
            delay.setOnFinished(event -> Platform.runLater(() -> {
                if (!s.getSeasons().isEmpty()) {
                    Season season = App.findSeason(s.getSeasons().get(0));
                    if (season != null) {
                        String imagePath = "src/main/resources/img/backgrounds/" + season.id;
                        File fullBlur = new File(imagePath + "/fullBlur.png");
                        String backgroundPath = fullBlur.exists() ? "fullBlur.png" : "background.png";

                        Image currentImage = backgroundImage.getImage();

                        ImageView background = new ImageView(new Image("file:" + imagePath + "/" + backgroundPath,
                                Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight(), false, true));

                        BackgroundImage myBI = new BackgroundImage(
                                Objects.requireNonNull(getCroppedImage(background)),
                                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                BackgroundSize.DEFAULT);

                        Platform.runLater(() -> {
                            mainBox.setBackground(new Background(myBI));
                            fadeEffectComplete(currentImage, getCroppedImage(background));
                        });
                    }
                }
            }));

            delay.play();
        }
    }

    public void fadeEffectComplete(Image first, Image second){
        backgroundImage.setImage(first);
        fadeTransition = new FadeTransition(Duration.seconds(0.5), backgroundImage);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0.1);
        fadeTransition.play();

        fadeTransition.setOnFinished(e -> {
            backgroundImage.setImage(second);
            fadeTransition = new FadeTransition(Duration.seconds(0.5), backgroundImage);
            fadeTransition.setFromValue(0.1);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        });
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
        String coverSrc = "src/main/resources/img/DefaultPoster.png";

        if (!s.coverSrc.isEmpty())
            coverSrc = s.getCoverSrc();
        Image img = new Image("file:" + coverSrc, 260, 350, false, true);
        ImageView image = new ImageView(img);

        Button btn = new Button();
        btn.setGraphic(image);
        btn.setAlignment(Pos.CENTER);
        btn.setContentDisplay(ContentDisplay.CENTER);

        btn.setPadding(new Insets(0));
        btn.getStyleClass().add("seriesCoverButton");
        seriesButtons.add(btn);

        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                btn.setScaleX(1.1);
                btn.setScaleY(1.1);
                playInteractionSound();

                CompletableFuture.runAsync(() -> selectSeries(collectionList.get(seriesButtons.indexOf(btn))));
            }else{
                btn.setScaleX(1);
                btn.setScaleY(1);
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
                selectSeries(collectionList.get(seriesButtons.indexOf(btn)));
                playInteractionSound();
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
        globalShadow.setVisible(false);
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
                    seasonController.setSeasons(seriesToEdit, newSeries.getSeasons(), newSeries.playSameMusic, categoryType.equals("Shows"));
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
        player.setVolume(1);
        player.seek(player.getStartTime());
        player.play();
    }

    public void playCategoriesSound() {
        File file = new File("src/main/resources/audio/categories.wav");
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(1);
        player.seek(player.getStartTime());
        player.play();
    }

    public void playBackgroundSound() {
        File file = new File("src/main/resources/audio/background.mp3");
        Media media = new Media(file.toURI().toString());
        backgroundMusicPlayer = new MediaPlayer(media);
        backgroundMusicPlayer.setVolume(0.05);
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
            stage.getIcons().add(new Image("file:src/main/resources/img/icons/AppIcon.png"));
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
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