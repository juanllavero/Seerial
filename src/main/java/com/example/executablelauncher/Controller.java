package com.example.executablelauncher;

import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Library;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.utils.Configuration;
import com.example.executablelauncher.utils.WindowDecoration;
import com.jfoenix.controls.JFXSlider;
import de.androidpit.colorthief.ColorThief;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import xss.it.fx.helpers.CornerPreference;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static com.example.executablelauncher.utils.Utils.*;

public class Controller implements Initializable {
    //region FXML ATTRIBUTES
    @FXML
    private FlowPane cardContainer;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Pane fill;

    @FXML
    private Pane shade;

    @FXML
    private StackPane mainViewBundle;

    @FXML
    private ImageView menuShadow;

    @FXML
    private ImageView currentlyWatchingImage;

    @FXML
    private ImageView mainViewShadow;

    @FXML
    private ImageView noise;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView logoImage;

    @FXML
    private HBox episodeNameBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane mainMenu;

    @FXML
    private HBox topBar;

    @FXML
    private BorderPane topBorderPane;

    @FXML
    private StackPane mainBox;

    @FXML
    private HBox librariesBox;

    @FXML
    private Button switchToDesktopButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button mainViewButton;

    @FXML
    private Pane episodeMenuParent;

    @FXML
    private VBox episodeMenuBox;

    @FXML
    private Button playEpisodeButton;

    @FXML
    private Button goToLibraryButton;

    @FXML
    private Button goToEpisodeButton;

    @FXML
    private Button markWatchedButton;

    @FXML
    private Button closeEpisodeMenuButton;

    @FXML
    private StackPane mainStack;

    @FXML
    private Label titleText;

    @FXML
    private Label info1;

    @FXML
    private Label info2;

    @FXML
    private Label info3;

    @FXML
    private HBox timeLeftBox;

    @FXML
    private Label timeLeftField;

    @FXML
    private Label overviewText;

    @FXML
    private Label continueWatchingTitle;

    @FXML
    private BorderPane mainViewPane;

    @FXML
    private HBox continueWatchingBox;

    @FXML
    private VBox menuOptions;

    @FXML
    private BorderPane settingsWindow;

    @FXML
    private Button settingsButton;

    @FXML
    private Button cardSizeButton;

    @FXML
    private VBox cardSizeOptions;

    @FXML
    private Button backButton;

    @FXML
    private Button menuButton;

    @FXML
    private Button tinyCardButton;

    @FXML
    private Button smallCardButton;

    @FXML
    private Button normalCardButton;

    @FXML
    private Button largeCardButton;

    @FXML
    private ScrollPane continueWatchingScroll;

    @FXML
    private VBox leftOptionsPane;

    @FXML
    private Label clock;

    @FXML
    private Label settingsTitle;

    @FXML
    private Label appName;

    @FXML
    private ImageView globalShadow;
    //endregion

    List<Library> libraries = null;
    Library currentLibrary = null;
    List<Series> series = new ArrayList<>();
    List<Button> seriesButtons = new ArrayList<>();
    List<Episode> continueWatching = new ArrayList<>();
    Series selectedSeries;
    Episode selectedEpisode;
    Season selectedSeason;
    String libraryType = null;
    FadeTransition fadeTransition = null;
    PauseTransition delay = null;
    int rowSize = 0;
    int rowCount = 0;
    int buttonCount = 0;
    boolean inMainView = false;

    @FXML
    void close() {
        playInteractionSound();
        App.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        settingsButton.setText(App.buttonsBundle.getString("settings"));
        exitButton.setText(App.buttonsBundle.getString("exitFullscreen"));
        switchToDesktopButton.setText(App.buttonsBundle.getString("switchToDesktop"));
        backButton.setText(App.buttonsBundle.getString("backButton"));

        settingsTitle.setText(App.buttonsBundle.getString("settings"));

        addInteractionSound(exitButton);
        addInteractionSound(switchToDesktopButton);

        //Open/Close Menu
        mainMenu.setVisible(false);

        mainMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            playCategoriesSound();
            hideContextMenu();
        });

        librariesBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                int index = libraries.indexOf(currentLibrary);
                librariesBox.getChildren().get(index).requestFocus();
            }
        });

        cardSizeButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                cardSizeOptions.setVisible(true);
        });

        int columnCount = Integer.parseInt(Configuration.loadConfig("cardSize", "5"));
        switch (columnCount){
            case 5:
                largeCardButton.getStyleClass().clear();
                largeCardButton.getStyleClass().add("playerOptionsSelected");
                break;
            case 7:
                normalCardButton.getStyleClass().clear();
                normalCardButton.getStyleClass().add("playerOptionsSelected");
                break;
            case 9:
                smallCardButton.getStyleClass().clear();
                smallCardButton.getStyleClass().add("playerOptionsSelected");
                break;
            case 11:
                tinyCardButton.getStyleClass().clear();
                tinyCardButton.getStyleClass().add("playerOptionsSelected");
                break;
        }

        updateRowSize(columnCount);

        Platform.runLater(() -> buttonCount = getVisibleButtonCountGlobal(continueWatchingScroll, continueWatchingBox));

        tinyCardButton.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                for (Node node : cardSizeOptions.getChildren()){
                    node.getStyleClass().clear();
                    node.getStyleClass().add("playerOptionsButton");
                }

                tinyCardButton.getStyleClass().add("playerOptionsSelected");

                Configuration.saveConfig("cardSize", "11");
                rowSize = 0;
                updateRowSize(11);
                showSeriesFrom(currentLibrary, false, true);
                tinyCardButton.requestFocus();
            }
        });

        smallCardButton.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                for (Node node : cardSizeOptions.getChildren()){
                    node.getStyleClass().clear();
                    node.getStyleClass().add("playerOptionsButton");
                }

                smallCardButton.getStyleClass().add("playerOptionsSelected");

                Configuration.saveConfig("cardSize", "9");
                rowSize = 0;
                updateRowSize(9);
                showSeriesFrom(currentLibrary, false, true);
                smallCardButton.requestFocus();
            }
        });

        normalCardButton.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                for (Node node : cardSizeOptions.getChildren()){
                    node.getStyleClass().clear();
                    node.getStyleClass().add("playerOptionsButton");
                }

                normalCardButton.getStyleClass().add("playerOptionsSelected");

                Configuration.saveConfig("cardSize", "7");
                rowSize = 0;
                updateRowSize(7);
                showSeriesFrom(currentLibrary, false, true);
                normalCardButton.requestFocus();
            }
        });

        largeCardButton.setOnKeyPressed(e -> {
            if (App.pressedSelect(e)){
                for (Node node : cardSizeOptions.getChildren()){
                    node.getStyleClass().clear();
                    node.getStyleClass().add("playerOptionsButton");
                }

                largeCardButton.getStyleClass().add("playerOptionsSelected");

                Configuration.saveConfig("cardSize", "5");
                rowSize = 0;
                updateRowSize(5);
                showSeriesFrom(currentLibrary, false, true);
                largeCardButton.requestFocus();
            }
        });

        //mainViewPane.setVisible(false);

        mainViewButton.setOnMouseClicked(e -> showContinueWatchingView());
        mainViewButton.setOnKeyPressed(e-> {
            if (App.pressedSelect(e))
                showContinueWatchingView();
        });

        //Fit width and height of components to window size
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        topBorderPane.prefWidthProperty().bind(topBar.widthProperty());

        backgroundImage.setFitHeight(screenHeight);
        backgroundImage.setFitWidth(screenWidth);
        backgroundImage.setPreserveRatio(true);

        currentlyWatchingImage.setFitHeight(screenHeight);
        currentlyWatchingImage.setFitWidth(screenWidth);
        currentlyWatchingImage.setPreserveRatio(true);

        noise.setFitHeight(screenHeight);
        noise.setFitWidth(screenWidth);
        noise.setPreserveRatio(false);

        leftOptionsPane.setPrefWidth(screenWidth * 0.5);
        cardSizeOptions.setPrefWidth(450);

        //Set padding for the series container
        cardContainer.setPrefWidth(screenWidth);
        int padding = 50;
        if (screenWidth / screenHeight > 1.8)
            padding = 100;
        cardContainer.setPadding(new Insets(100, padding, 50, padding + ((double) padding / 2)));

        //Remove horizontal and vertical scroll
        DesktopViewController.scrollModification(scrollPane);

        menuShadow.setFitWidth(screenWidth);
        menuShadow.setFitHeight(screenHeight);
        menuShadow.setVisible(false);

        globalShadow.setFitWidth(screenWidth);
        globalShadow.setFitHeight(screenHeight);
        globalShadow.setVisible(false);

        mainViewShadow.setFitWidth(screenWidth);
        mainViewShadow.setFitHeight(screenHeight);
        mainViewShadow.setVisible(true);

        mainViewBundle.setPrefWidth(screenWidth);
        mainViewBundle.setPrefHeight(screenHeight);

        double aspectRatio = screenWidth / screenHeight;

        if (aspectRatio > (double) 16/9){
            shade.setPrefWidth(((double) 16/9) * screenHeight);
            shade.setPrefHeight(screenHeight);

            fill.setPrefWidth(screenWidth - shade.getPrefWidth());
            fill.setPrefHeight(screenHeight);
        }else{
            shade.setPrefWidth(screenWidth);
            shade.setPrefHeight(screenHeight);

            fill.setPrefWidth(0);
        }

        episodeMenuParent.setOnMouseClicked(e -> hideEpisodeMenu());
        episodeMenuParent.setVisible(false);

        playEpisodeButton.setText(App.buttonsBundle.getString("playButton"));
        goToLibraryButton.setText(App.buttonsBundle.getString("goToLibraryButton"));
        goToEpisodeButton.setText(App.buttonsBundle.getString("goToEpisodeButton"));
        markWatchedButton.setText(App.buttonsBundle.getString("markWatched"));
        closeEpisodeMenuButton.setText(App.buttonsBundle.getString("backButton"));

        playEpisodeButton.setOnKeyPressed(e -> {
            playInteractionSound();
            if (App.pressedSelect(e))
                playEpisode();
            else if (App.pressedDown(e))
                goToLibraryButton.requestFocus();
            else if (App.pressedUp(e))
                closeEpisodeMenuButton.requestFocus();
        });

        goToLibraryButton.setOnKeyPressed(e -> {
            playInteractionSound();
            if (App.pressedSelect(e))
                goToLibrary();
            else if (App.pressedUp(e))
                playEpisodeButton.requestFocus();
            else if (App.pressedDown(e))
                goToEpisodeButton.requestFocus();
        });

        goToEpisodeButton.setOnKeyPressed(e -> {
            playInteractionSound();
            if (App.pressedSelect(e))
                goToEpisode();
            else if (App.pressedUp(e))
                goToLibraryButton.requestFocus();
            else if (App.pressedDown(e))
                markWatchedButton.requestFocus();
        });

        markWatchedButton.setOnKeyPressed(e -> {
            playInteractionSound();
            if (App.pressedSelect(e))
                markAsWatched(selectedEpisode);
            else if (App.pressedUp(e))
                goToEpisodeButton.requestFocus();
            else if (App.pressedDown(e))
                closeEpisodeMenuButton.requestFocus();
        });

        closeEpisodeMenuButton.setOnKeyPressed(e -> {
            playInteractionSound();
            if (App.pressedUp(e))
                markWatchedButton.requestFocus();
            else if (App.pressedDown(e))
                playEpisodeButton.requestFocus();
        });

        menuButton.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            playInteractionSound();

            if (App.pressedLeft(event))
                librariesBox.getChildren().getLast().requestFocus();
            else if (App.pressedSelect(event))
                showMenu();
        });

        menuButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            playInteractionSound();
        });

        libraries = DataManager.INSTANCE.getLibraries(true);

        for (Library cat : libraries){
            Button btn = new Button();
            btn.setText(cat.getName());
            btn.getStyleClass().add("CatButton");
            btn.setPadding(new Insets(12));
            HBox.setMargin(btn, new Insets(0, 5, 0, 0));

            btn.setFocusTraversable(false);

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                playCategoriesSound();
                selectLibraryButton(btn);
            });

            btn.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (App.pressedSelect(event)) {
                    selectLibraryButton(btn);
                }else if (App.pressedLeft(event)){
                    if (librariesBox.getChildren().indexOf(btn) > 0)
                        librariesBox.getChildren().get(librariesBox.getChildren().indexOf(btn) - 1).requestFocus();
                    else
                        mainViewButton.requestFocus();
                }else if (App.pressedRight(event)){
                    if (btn == librariesBox.getChildren().getLast()){
                        menuButton.requestFocus();
                    }else{
                        librariesBox.getChildren().get(Math.min(librariesBox.getChildren().indexOf(btn) + 1, librariesBox.getChildren().size() - 1)).requestFocus();
                    }
                }else if (App.pressedDown(event)){
                    if (!inMainView && selectedSeries != null)
                        seriesButtons.get(series.indexOf(selectedSeries)).requestFocus();
                    else if (inMainView)
                        continueWatchingBox.getChildren().get(continueWatching.indexOf(selectedEpisode)).requestFocus();
                }
            });

            btn.focusedProperty().addListener((obs, oldVal, newVal) -> playInteractionSound());

            librariesBox.getChildren().add(btn);
        }

        mainViewButton.setOnKeyPressed(e -> {
            if (App.pressedDown(e) && !continueWatching.isEmpty())
                continueWatchingBox.getChildren().getFirst().requestFocus();
            else if (App.pressedSelect(e))
                showContinueWatchingView();
            else if (App.pressedRight(e))
                librariesBox.getChildren().getFirst().requestFocus();

        });

        //region CLOCK TIMELINE
        if (Boolean.parseBoolean(Configuration.loadConfig("showClock", "true"))){
            clock.setVisible(true);
            updateHour();
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.minutes(1),
                    event -> updateHour()
            ));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }else{
            clock.setVisible(false);
        }
        //endregion

        currentLibrary = DataManager.INSTANCE.currentLibrary;

        if (currentLibrary == null && !libraries.isEmpty())
            currentLibrary = libraries.get(0);

        if (currentLibrary != null){
            mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
                if (App.pressedBack(event)) {
                    if (mainMenu.isVisible()){
                        hideContextMenu();
                    }else if (!episodeMenuParent.isVisible()){
                        playInteractionSound();
                        showMenu();
                    }
                }else if (App.pressedLB(event)){
                    if (libraries.indexOf(currentLibrary) > 0)
                        selectLibraryButton((Button) librariesBox.getChildren().get(libraries.indexOf(currentLibrary) - 1));
                    else
                        showContinueWatchingView();
                }else if (App.pressedRB(event) && libraries.indexOf(currentLibrary) < libraries.size() - 1){
                    selectLibraryButton((Button) librariesBox.getChildren().get(libraries.indexOf(currentLibrary) + 1));
                }
            });

            showContinueWatchingView();
        }else{
            showMenu();
            settingsButton.setDisable(true);
            appName.setText(App.textBundle.getString("noLibraries"));

            switchToDesktopButton.requestFocus();
        }
    }


    private void updateRowSize(int newSize){
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        if (newSize != rowSize){
            rowSize = newSize;

            if (screenWidth / screenHeight > 1.8)
                rowSize += 2;
        }

        Platform.runLater(() -> {
            rowCount = (int) Math.ceil((double) seriesButtons.size() / rowSize);
            scrollPane.setVmax(rowCount - 1);
        });
    }

    private void selectLibraryButton(Button btn){
        for (Node node : librariesBox.getChildren()) {
            Button catButton = (Button) node;
            catButton.getStyleClass().clear();
            catButton.getStyleClass().add("CatButton");
        }

        btn.getStyleClass().clear();
        btn.getStyleClass().add("CatButtonSelected");

        playCategoriesSound();
        showSeriesFrom(libraries.get(librariesBox.getChildren().indexOf(btn)), true, !inMainView);
    }

    private void addInteractionSound(Button btn){
        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                playInteractionSound();
        });
    }

    private void updateHour() {
        // Obtener la hora actual
        LocalTime currentTime = LocalTime.now();

        DateTimeFormatter format24 = DateTimeFormatter.ofPattern("HH:mm");
        String time = currentTime.format(format24);

        clock.setText(time);
    }

    public void showSeriesFrom(Library library, boolean selectSeries, boolean selectCurrentSeries){
        if (library != currentLibrary)
            DataManager.INSTANCE.currentLibrary = library;

        if (inMainView) {
            showLibraryView();
            inMainView = false;
        }

        currentLibrary = library;

        //Create temp files to keep drives on
        for (String folder : currentLibrary.getFolders()){
            App.wakeUpDrive(folder);
        }

        libraryType = currentLibrary.getType();
        cardContainer.getChildren().clear();
        seriesButtons.clear();

        series = currentLibrary.getSeries();

        for (Series col : series)
            addCard(col);

        if (selectSeries)
            selectedSeries = null;

        Platform.runLater(() -> {
            updateRowSize(rowSize);

            if (selectSeries)
                if (selectCurrentSeries)
                    restoreSelection();
                else
                    seriesButtons.getFirst().requestFocus();
        });
    }

    private void restoreSelection(){
        if (inMainView){
            if (selectedEpisode != null)
                continueWatchingBox.getChildren().get(continueWatching.indexOf(selectedEpisode)).requestFocus();
            else
                continueWatchingBox.getChildren().getFirst().requestFocus();
        }else{
            Series seriesToSelect = App.getSelectedSeries();

            if (seriesToSelect == null)
                seriesToSelect = series.get(0);

            int index = series.indexOf(seriesToSelect);
            if (index == -1)
                index = 0;

            selectedSeries = null;
            seriesButtons.get(index).requestFocus();
        }
    }

    public void selectSeries(Series s){
        if (selectedSeries != s){
            selectedSeries = s;

            App.setSelectedSeries(selectedSeries);

            delay = new PauseTransition(Duration.millis(150));
            delay.setOnFinished(event -> Platform.runLater(() -> {
                if (!s.getSeasons().isEmpty() && seriesButtons.get(series.indexOf(s)).isFocused()) {
                    Season season = s.getSeasons().get(0);
                    if (season != null) {
                        String imagePath = "resources/img/backgrounds/" + season.getId();
                        File fullBlur = new File(imagePath + "/fullBlur.jpg");
                        String backgroundPath = fullBlur.exists() ? "fullBlur.jpg" : "background.jpg";

                        File imageFile = new File(imagePath + "/" + backgroundPath);
                        if (!imageFile.exists()) {
                            imagePath = "resources/img";
                            backgroundPath = "backgroundDefault.png";
                        }

                        Image currentImage = backgroundImage.getImage();

                        ImageView background = new ImageView(new Image("file:" + imagePath + "/" + backgroundPath,
                                Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight(), false, true));

                        WritableImage image = getCroppedImage(background);

                        if (image != null){
                            BackgroundImage myBI = new BackgroundImage(
                                    image,
                                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    BackgroundSize.DEFAULT);

                            mainBox.setBackground(new Background(myBI));
                            fadeEffectComplete(backgroundImage, currentImage, image);
                        }
                    }
                }
            }));

            delay.play();
        }
    }

    public void fadeEffectComplete(ImageView imgView, Image first, Image second){
        imgView.setImage(first);
        fadeTransition = new FadeTransition(Duration.seconds(0.3), imgView);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0.1);
        fadeTransition.play();

        fadeTransition.setOnFinished(e -> {
            imgView.setImage(second);
            fadeTransition = new FadeTransition(Duration.seconds(0.2), imgView);
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

    private Button addBaseCard(Series s, Episode episode){
        double screenAspectRatio = Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double originalWidth, originalHeight;

        Button btn = new Button();

        String coverSrc = "resources/img/DefaultPoster.png";

        if (!s.getCoverSrc().isEmpty())
            coverSrc = s.getCoverSrc();

        File imgFile = new File(coverSrc);

        if (!imgFile.isFile())
            coverSrc = "resources/img/DefaultPoster.png";

        int innerSpace;
        double aspectRatio = 1.28125;

        //If screen aspect ratio is greater than 16:90
        if (screenAspectRatio > 1.8){
            innerSpace = 250;
        }else{
            innerSpace = 125;
        }

        //Calculate the size of the card
        int numCards = 6;
        if (episode != null){
            if (screenWidth / screenHeight > 1.8)
                numCards += 2;
        }else{
            numCards = rowSize;
        }

        originalWidth = (Screen.getPrimary().getBounds().getWidth() - innerSpace - ((30 * (numCards - 1)) * 2)) / numCards;
        originalHeight = originalWidth * aspectRatio;

        Rectangle img = setRoundedBorders(coverSrc, originalWidth, originalHeight);

        StackPane main = null;
        if (episode != null){
            main = new StackPane(img);
            BorderPane details = new BorderPane();

            if (episode.getTimeWatched() != 0){
                JFXSlider slider = new JFXSlider(0, episode.getRuntimeInSeconds()
                        , episode.getTimeWatched());
                slider.setFocusTraversable(false);
                details.setBottom(slider);
            }

            main.getChildren().add(details);
        }

        btn.setGraphic(Objects.requireNonNullElse(main, img));
        btn.setAlignment(Pos.CENTER);
        btn.setContentDisplay(ContentDisplay.CENTER);

        btn.setPadding(new Insets(0));
        btn.getStyleClass().add("seriesCoverButton");

        return btn;
    }
    private void addCard(Series s){
        Button btn = addBaseCard(s, null);
        seriesButtons.add(btn);

        btn.setFocusTraversable(false);

        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                playInteractionSound();

                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.15), btn);
                scaleTransition.setToX(1.1);
                scaleTransition.setToY(1.1);

                scaleTransition.setOnFinished(e -> {
                    CompletableFuture.runAsync(() -> selectSeries(series.get(seriesButtons.indexOf(btn))));
                });

                scaleTransition.play();

                //Update scroll vertical value
                int index = (seriesButtons.indexOf(btn) / rowSize);

                if (index != scrollPane.getVvalue()) {
                    Bounds cardBounds = btn.localToScene(btn.getBoundsInLocal());
                    Bounds scrollBounds = scrollPane.localToScene(scrollPane.getBoundsInLocal());

                    //Check if the button is outside the view
                    if (cardBounds.getMinY() < scrollBounds.getMinY() || cardBounds.getMaxY() > scrollBounds.getMaxY()){
                        //Animate transition
                        Timeline timeline = new Timeline();
                        KeyValue keyValue = new KeyValue(scrollPane.vvalueProperty(), index);
                        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), keyValue);
                        timeline.getKeyFrames().add(keyFrame);
                        timeline.play();
                    }
                }
            }else{
                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.15), btn);
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);

                scaleTransition.play();
            }
        });

        btn.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                if (selectedSeries != null) {
                    showSeason(selectedSeries);
                }
            }

            int index = seriesButtons.indexOf(btn);

            if (App.pressedUp(event)){
                if (index < rowSize)
                    librariesBox.getChildren().get(libraries.indexOf(currentLibrary)).requestFocus();
                else
                    seriesButtons.get(seriesButtons.indexOf(btn) - rowSize).requestFocus();
            }else if (App.pressedDown(event)){
                if (index + rowSize < seriesButtons.size())
                    seriesButtons.get(seriesButtons.indexOf(btn) + rowSize).requestFocus();
                else
                    seriesButtons.getLast().requestFocus();
            }else if (App.pressedLeft(event)){
                if (index > 0)
                    seriesButtons.get(seriesButtons.indexOf(btn) - 1).requestFocus();
            }else if (App.pressedRight(event)){
                if (index < seriesButtons.size() - 1)
                    seriesButtons.get(seriesButtons.indexOf(btn) + 1).requestFocus();
            }
        });

        btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if (event.getButton().equals(MouseButton.PRIMARY)){
                if (btn == seriesButtons.get(series.indexOf(selectedSeries)))
                    showSeason(selectedSeries);
                else
                    seriesButtons.get(seriesButtons.indexOf(btn)).requestFocus();
            }
        });

        cardContainer.getChildren().add(btn);
    }


    private Rectangle setRoundedBorders(String imageSrc, double width, double height){
        Rectangle rectangle = new Rectangle(0, 0, width, height);
        rectangle.setArcWidth(20.0);
        rectangle.setArcHeight(20.0);

        ImagePattern pattern = new ImagePattern(
                new Image("file:" + imageSrc, width, height, false, false)
        );

        rectangle.setFill(pattern);
        rectangle.setEffect(new DropShadow(15, Color.BLACK));

        return rectangle;
    }
    private void showLibraryView(){
        Platform.runLater(() -> {
            backgroundImage.setVisible(true);
            mainViewBundle.setVisible(false);
            mainViewPane.setVisible(false);
            scrollPane.setVisible(true);
            ParallelTransition parallelTransition = createParallelTransition(mainViewBundle, mainViewPane,
                    scrollPane, backgroundImage, 0.2f);
            parallelTransition.play();
        });
    }
    private void showMainView(){
        Platform.runLater(() -> {
            backgroundImage.setVisible(false);
            mainViewBundle.setVisible(true);
            mainViewPane.setVisible(true);
            scrollPane.setVisible(false);
            ParallelTransition parallelTransition = createParallelTransition(backgroundImage, scrollPane,
                    mainViewBundle, mainViewPane, 0.2f);
            parallelTransition.play();
        });
    }

    //region MAIN VIEW
    private void showContinueWatchingView(){
        if (inMainView)
            return;

        for (Node node : librariesBox.getChildren()){
            Button btn = (Button) node;
            btn.getStyleClass().clear();
            btn.getStyleClass().add("CatButton");
        }

        inMainView = true;
        showMainView();

        continueWatching.clear();
        continueWatchingBox.getChildren().clear();

        for (Library library : libraries){
            for (Series series : library.getSeries()){
                if (!series.isBeingWatched())
                    continue;

                Season season = series.getCurrentlyWatchingSeason();
                if (season == null)
                    continue;

                if (library.getType().equals("Shows")){
                    Episode episode = season.getCurrentlyWatchingEpisode();
                    if (episode != null) {
                        continueWatching.add(episode);
                        addEpisodeCard(series, season, episode);
                    }
                }else{
                    for (Season s : series.getSeasons()){
                        if (!s.isBeingWatched())
                            continue;

                        Episode episode = s.getCurrentlyWatchingEpisode();
                        if (episode != null) {
                            continueWatching.add(episode);
                            addEpisodeCard(series, s, episode);
                        }
                    }
                }
            }
        }

        Platform.runLater(() -> {
            mainViewPane.requestFocus();
            if (!continueWatchingBox.getChildren().isEmpty()){
                continueWatchingBox.getChildren().getFirst().requestFocus();
            }else{
                selectLibraryButton((Button) librariesBox.getChildren().getFirst());
            }
        });

    }
    private void addEpisodeCard(Series series, Season season, Episode episode){
        Button btn = addBaseCard(series, episode);
        btn.setFocusTraversable(false);

        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                playInteractionSound();

                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.15), btn);
                scaleTransition.setToX(1.1);
                scaleTransition.setToY(1.1);

                scaleTransition.setOnFinished(e -> CompletableFuture.runAsync(() -> selectEpisode(series, season, episode)));

                scaleTransition.play();

                //Move ScrollPane
                handleButtonFocus(btn);
            }else{
                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.15), btn);
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);
                scaleTransition.play();
            }
        });

        btn.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) ->{
            if (App.pressedSelect(event)){
                if (selectedEpisode != null)
                    showEpisodeMenu(series, season, episode);
            }

            int index = continueWatchingBox.getChildren().indexOf(btn);

            if (App.pressedUp(event)){
                librariesBox.getChildren().get(0).requestFocus();
            }else if (App.pressedLeft(event)){
                if (index > 0)
                    continueWatchingBox.getChildren().get(continueWatchingBox.getChildren().indexOf(btn) - 1).requestFocus();
            }else if (App.pressedRight(event)){
                if (index < continueWatchingBox.getChildren().size() - 1)
                    continueWatchingBox.getChildren().get(continueWatchingBox.getChildren().indexOf(btn) + 1).requestFocus();
            }
        });

        btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if (event.getButton().equals(MouseButton.PRIMARY)){
                if (btn == continueWatchingBox.getChildren().get(continueWatching.indexOf(selectedEpisode))
                        && btn.isFocused())
                    showEpisodeMenu(series, season, episode);
                else
                    btn.requestFocus();
            }
        });

        continueWatchingBox.getChildren().add(btn);
    }
    private void selectEpisode(Series series, Season season, Episode episode){
        Platform.runLater(() -> {
            if (selectedEpisode != episode){
                selectedEpisode = episode;

                String logoSrc;
                if (DataManager.INSTANCE.getLibrary(series).getType().equals("Shows")) {
                    logoSrc = series.getLogoSrc();
                    titleText.setText(series.getName());
                    info1.setText(App.textBundle.getString("seasonLetter") + season.getSeasonNumber() + " " + App.textBundle.getString("episodeLetter") + episode.getEpisodeNumber());
                    info2.setText(season.getYear());
                    info3.setText(setRuntime(episode.getRuntime()));
                }else {
                    logoSrc = season.getLogoSrc();
                    titleText.setText(season.getName());
                    info1.setText(season.getYear());
                    info2.setText(setRuntime(episode.getRuntime()));
                    info3.setText(String.valueOf(season.getScore()));
                }

                setTimeLeft(timeLeftBox, timeLeftField, episode);

                logoImage.setVisible(!logoSrc.isEmpty());
                titleText.setVisible(logoSrc.isEmpty());

                if (!logoSrc.isEmpty()){
                    Image img;
                    img = new Image("file:" + logoSrc, mainPane.getScene().getWidth() * 0.2, mainPane.getScene().getHeight() * 0.2, true, true);

                    logoImage.setImage(img);
                    logoImage.setFitWidth(mainPane.getScene().getWidth() * 0.2);
                    logoImage.setFitHeight(mainPane.getScene().getHeight() * 0.2);
                }

                episodeNameBox.getChildren().clear();
                if (DataManager.INSTANCE.getLibrary(series).getType().equals("Shows")){
                    Label episodeName = new Label(episode.getName());
                    episodeName.setFont(new Font("Roboto", 42));
                    episodeName.setStyle("-fx-font-weight: bold");
                    episodeName.setTextFill(Color.color(1, 1, 1));
                    episodeName.setEffect(new DropShadow());
                    episodeNameBox.getChildren().add(episodeName);
                }

                if (!episode.getOverview().isEmpty())
                    overviewText.setText(episode.getOverview());
                else
                    overviewText.setText(App.textBundle.getString("defaultOverview"));

                delay = new PauseTransition(Duration.millis(150));
                delay.setOnFinished(event -> Platform.runLater(() -> {
                    if (!series.getSeasons().isEmpty() &&
                            continueWatchingBox.getChildren().get(continueWatching.indexOf(episode)).isFocused()) {
                        String imagePath = "resources/img/backgrounds/" + season.getId();
                        String backgroundPath = "background.jpg";

                        File imageFile = new File(imagePath + "/" + backgroundPath);
                        if (!imageFile.exists()){
                            imagePath = "resources/img";
                            backgroundPath = "backgroundDefault.png";
                        }

                        BufferedImage bufferedImage;

                        ImageView background = new ImageView(new Image("file:" + imagePath + "/" + backgroundPath,
                                Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight(), true, true));

                        double aspectRatio = background.getImage().getWidth() / background.getImage().getHeight();

                        WritableImage croppedImage;
                        if (aspectRatio < (double) 16/9){
                            double originalWidth = background.getImage().getWidth();
                            double originalHeight = background.getImage().getHeight();

                            double targetAspectRatio = (double) 16/9;
                            double newHeight = originalWidth / targetAspectRatio;

                            int xOffset = 0;
                            int yOffset = (int) ((background.getImage().getHeight() - originalHeight) / 2);

                            PixelReader pixelReader = background.getImage().getPixelReader();
                            croppedImage = new WritableImage(pixelReader, xOffset, 0, (int) originalWidth, (int) newHeight);

                            background.setImage(croppedImage);
                        }

                        Color dominantColor = Color.BLACK;
                        try {
                            bufferedImage = ImageIO.read(new File(imagePath + "/" + backgroundPath));

                            int[] colorValues = ColorThief.getColor(bufferedImage, 10, true);


                            if (colorValues != null)
                                dominantColor = Color.rgb(colorValues[0], colorValues[1], colorValues[2]);

                            bufferedImage.flush();
                        } catch (IOException ex) {
                            System.err.println("selectEpisode: background image could not be loaded");
                        }

                        applyGradient(dominantColor, background.getImage());
                    }
                }));

                delay.play();
            }
        });
    }
    private void applyGradient(Color dominantColor, Image postImage){
        RadialGradient shadePaint = new RadialGradient(
                0, 1, 0.5, 0.4, 0.8, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.5, Color.TRANSPARENT),
                new Stop(0.8, dominantColor)
        );

        mainBox.setBackground(
                new Background(
                        new BackgroundFill(
                                dominantColor, null, new Insets(-10)
                        )
                )
        );

        FadeTransition fadeOut = fadeOutEffect(mainViewBundle, 0.4f, 0.1f);
        fadeOut.setOnFinished(e -> {
            fill.setBackground(
                    new Background(
                            new BackgroundFill(
                                    dominantColor, null, new Insets(-10)
                            )
                    )
            );

            shade.setBackground(
                    new Background(
                            new BackgroundFill(
                                    shadePaint, null, new Insets(-10)
                            )
                    )
            );

            shade.setEffect(new BoxBlur(10, 10, 4));

            currentlyWatchingImage.setImage(postImage);

            fadeInEffect(mainViewBundle, 0.4f, 0.1f).play();
        });

        fadeOut.play();
    }
    private void handleButtonFocus(Button focusedButton) {
        double screenCenter, buttonCenterX, offset, finalPos;

        if (continueWatchingBox.getChildren().indexOf(focusedButton) <= (buttonCount / 2) + 1 ||
                continueWatchingBox.getChildren().size() <= 6 || continueWatchingBox.getChildren().size() == buttonCount){
            finalPos = 0;
        }else{
            //Get center of screen
            screenCenter = Screen.getPrimary().getBounds().getWidth() / 2;

            //Check aspect ratio to limit the right end of the button list
            double aspectRatio = Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight();
            int maxButtons = (continueWatchingBox.getChildren().size() - (buttonCount / 2));
            if (aspectRatio > 1.8)
                maxButtons--;

            if (continueWatchingBox.getChildren().indexOf(focusedButton) >= (continueWatchingBox.getChildren().size() - (buttonCount / 2)))
                focusedButton = (Button) continueWatchingBox.getChildren().get(Math.max(2, maxButtons));

            //Get center of button in the screen
            Bounds buttonBounds = focusedButton.localToScene(focusedButton.getBoundsInLocal());
            buttonCenterX = buttonBounds.getMinX() + buttonBounds.getWidth() / 2;

            //Calculate offset
            offset = screenCenter - buttonCenterX;

            //Calculate position
            finalPos = continueWatchingBox.getTranslateX() + offset;
        }

        //Translation with animation
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.2), continueWatchingBox);
        transition.setToX(finalPos);
        transition.play();
    }
    private void showEpisodeMenu(Series series, Season season, Episode episode){
        playInteractionSound();

        Button btn = (Button) continueWatchingBox.getChildren().get(continueWatching.indexOf(episode));
        if (btn == null)
            return;

        App.setSelectedSeries(series);
        selectedSeries = series;
        selectedSeason = season;
        selectedEpisode = episode;

        Bounds bounds = btn.getBoundsInLocal();
        Bounds buttonBounds = btn.localToScreen(bounds);

        episodeMenuParent.setVisible(true);
        episodeMenuBox.setLayoutX(buttonBounds.getCenterX());
        episodeMenuBox.setLayoutY(buttonBounds.getMinY() - 200);

        libraryType = DataManager.INSTANCE.getLibrary(series).getType();

        playEpisodeButton.requestFocus();
    }
    private void goToLibrary(){
        hideEpisodeMenu();

        Library library = DataManager.INSTANCE.getLibrary(selectedSeries);
        Button btn = (Button) librariesBox.getChildren().get(libraries.indexOf(library));

        if (btn == null)
            return;

        selectLibraryButton(btn);
        selectSeries(selectedSeries);
    }
    private void goToEpisode(){
        hideEpisodeMenu();
        playInteractionSound();
        showSeason(selectedSeries);
    }
    private void playEpisode(){
        hideEpisodeMenu();
        playInteractionSound();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("season-view.fxml"));
            Parent root = fxmlLoader.load();
            SeasonController seasonController = fxmlLoader.getController();
            seasonController.setParent(this);
            seasonController.setSeasons(selectedSeries, selectedSeries.isPlaySameMusic(), libraryType.equals("Shows"));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setTitle(App.textBundle.getString("season"));
            Scene scene = new Scene(root);
            //scene.setCursor(Cursor.NONE);
            scene.setFill(Color.BLACK);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth());
            stage.setHeight(Screen.getPrimary().getBounds().getHeight());
            stage.show();

            seasonController.playEpisode(selectedEpisode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void markAsWatched(Episode episode){
        hideEpisodeMenu();

        playInteractionSound();

        episode.setWatched();
        processCurrentlyWatching(selectedSeries, selectedSeason, episode);

        int index = continueWatching.indexOf(episode);
        continueWatchingBox.getChildren().remove(index);
        continueWatching.remove(episode);

        if (selectedSeries.isBeingWatched() && selectedSeries.getCurrentlyWatchingSeason().isBeingWatched()){
            Season season = selectedSeries.getCurrentlyWatchingSeason();
            Episode ep = season.getCurrentlyWatchingEpisode();

            continueWatching.add(episode);
            addEpisodeCard(selectedSeries, season, ep);
        }

        if (!continueWatchingBox.getChildren().isEmpty())
            continueWatchingBox.getChildren().getFirst().requestFocus();
        else
            selectLibraryButton((Button) librariesBox.getChildren().getFirst());
    }
    @FXML
    void hideEpisodeMenu(){
        playInteractionSound();
        episodeMenuParent.setVisible(false);
        continueWatchingBox.getChildren().get(continueWatching.indexOf(selectedEpisode)).requestFocus();
    }
    //endregion
    @FXML
    void hideContextMenu(){
        selectedSeries = null;
        mainMenu.setVisible(false);
        globalShadow.setVisible(false);
        mainPane.setDisable(false);
        restoreSelection();
    }

    @FXML
    void showMenu(){
        globalShadow.setVisible(true);
        mainMenu.setVisible(true);
        menuOptions.setVisible(true);
        settingsWindow.setVisible(false);
        settingsButton.requestFocus();
    }

    @FXML
    void openSettings(){
        globalShadow.setVisible(false);
        menuOptions.setVisible(false);
        settingsWindow.setVisible(true);
        cardSizeButton.requestFocus();
    }

    public void showSeason(Series s){
        playCategoriesSound();
        if (s != null && !s.getSeasons().isEmpty()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("season-view.fxml"));
                Parent root = fxmlLoader.load();
                SeasonController seasonController = fxmlLoader.getController();
                seasonController.setParent(this);
                seasonController.setSeasons(s, s.isPlaySameMusic(), libraryType.equals("Shows"));
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setTitle(App.textBundle.getString("season"));
                Scene scene = new Scene(root);
                //scene.setCursor(Cursor.NONE);
                scene.setFill(Color.BLACK);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.setWidth(Screen.getPrimary().getBounds().getWidth());
                stage.setHeight(Screen.getPrimary().getBounds().getHeight());
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void switchToDesktop(){
        playInteractionSound();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("desktop-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();

            WindowDecoration windowDecoration = null;
            if (System.getProperty("os.name").toLowerCase().contains("win")){
                windowDecoration = new WindowDecoration(stage, true);
                windowDecoration.setCornerPreference(CornerPreference.ROUND);
            }

            stage.setTitle(App.textBundle.getString("desktopMode"));
            stage.getIcons().add(new Image(getFileAsIOStream("img/icons/AppIcon.png")));
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
            stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.25);

            DesktopViewController desktopViewController = fxmlLoader.getController();
            desktopViewController.initValues(windowDecoration);
            stage.show();

            Stage thisStage = (Stage) mainBox.getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}