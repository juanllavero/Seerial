package com.example.executablelauncher;

import com.example.executablelauncher.entities.*;
import info.movito.themoviedbapi.*;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import info.movito.themoviedbapi.model.tv.TvSeason;
import info.movito.themoviedbapi.model.tv.TvSeries;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesktopViewController {
    //region FXML ATTRIBUTES
    @FXML
    private Button addCollectionButton;

    @FXML
    private Label elementsSelectedText;

    @FXML
    private Button deleteSelectedButton;

    @FXML
    private Button deselectAllButton;

    @FXML
    private Button selectAllButton;

    @FXML
    private BorderPane selectionOptions;

    @FXML
    private Button addDiscButton;

    @FXML
    private Button addSeasonButton;

    @FXML
    private ImageView backgroundShadow;

    @FXML
    private ChoiceBox<String> categorySelector;

    @FXML
    private Button closeButton;

    @FXML
    private VBox seasonsEpisodesBox;

    @FXML
    private FlowPane discContainer;

    @FXML
    private VBox discMenu;

    @FXML
    private Button editColButton;

    @FXML
    private Button editDiscButton;

    @FXML
    private Button editSeasonButton;

    @FXML
    private Button exitButton;

    @FXML
    private ImageView globalBackground;

    @FXML
    private ImageView globalBackgroundShadow;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private StackPane mainBox;

    @FXML
    private VBox mainMenu;

    @FXML
    private ImageView maximizeRestoreImage;

    @FXML
    private Pane menuParentPane;

    @FXML
    private Button removeColButton;

    @FXML
    private Button removeDiscButton;

    @FXML
    private Button removeSeasonButton;

    @FXML
    private HBox seasonContainer;

    @FXML
    private HBox seasonLogoBox;

    @FXML
    private StackPane seasonInfoPane;

    @FXML
    private ImageView seasonLogo;

    @FXML
    private VBox seasonMenu;

    @FXML
    private Label seasonName;

    @FXML
    private ScrollPane seasonScroll;

    @FXML
    private VBox seriesContainer;

    @FXML
    private ImageView seriesCover;

    @FXML
    private VBox seriesMenu;

    @FXML
    private ScrollPane seriesScrollPane;

    @FXML
    private Button settingsButton;

    @FXML
    private Button switchFSButton;

    @FXML
    private BorderPane topBar;

    @FXML
    private HBox topRightBar;

    @FXML
    private ImageView noiseImage;

    @FXML
    private StackPane seriesStack;

    @FXML
    private Label detailsText;

    @FXML
    private Label yearText;

    @FXML
    private Label orderText;

    @FXML
    private Label episodesText;

    @FXML
    private Label yearField;

    @FXML
    private Label orderField;

    @FXML
    private Label episodesField;

    @FXML
    private HBox downloadingContentWindow;

    @FXML
    private Label downloadingContentText;

    @FXML
    private BorderPane seasonBorderPane;
    //endregion

    //region ATTRIBUTES
    private final ImageViewPane seasonBackground = new ImageViewPane();
    private final ImageViewPane seasonBackgroundNoise = new ImageViewPane();
    private final ImageViewPane seriesBackgroundNoise = new ImageViewPane();

    private List<Series> seriesList = new ArrayList<>();
    private List<Season> seasonList = new ArrayList<>();
    private List<Disc> discList = new ArrayList<>();
    private List<Button> seriesButtons = new ArrayList<>();
    private List<Button> seasonsButtons = new ArrayList<>();
    public Disc selectedDisc = null;
    private Series selectedSeries = null;
    private Season selectedSeason = null;
    private List<Disc> selectedDiscs = new ArrayList<>();
    private List<DiscController> discControllers = new ArrayList<>();
    private Category currentCategory;
    private double xOffset = 0;
    private double yOffset = 0;
    private double ASPECT_RATIO = 16.0 / 9.0;
    private int downloadedMetadataCount = 0;
    private int downloadedMetadataTotal = 0;
    //endregion

    //region THEMOVIEDB ATTRIBUTES
    TmdbApi tmdbApi = new TmdbApi("4b46560aff5facd1d9ede196ce7d675f");
    TmdbTV seriesMetadata;                                                              //Saves all series from TheMovieDB
    TmdbMovies moviesMetadata;                                                          //Saves all movies from TheMovieDB
    //endregion

    public void initValues(){
        Stage stage = (Stage) mainBox.getScene().getWindow();
        updateLanguage();

        categorySelector.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> selectCategory(newValue) );

        setDragWindow(topBar);

        selectionOptions.setVisible(false);

        downloadingContentWindow.setVisible(false);

        menuParentPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            hideMenu();
        });

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.F11 == event.getCode()) {
                fullScreen();
            }
        });

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                hideMenu();
            }
        });

        ChangeListener<Number> widthListener = (obs, oldWidth, newWidth) -> {
            double scaleFactor;
            double currentAspectRatio = newWidth.doubleValue() / stage.heightProperty().doubleValue();

            if (currentAspectRatio >= ASPECT_RATIO) {
                scaleFactor = newWidth.doubleValue() / globalBackground.getImage().getWidth();
                globalBackground.setFitWidth(newWidth.doubleValue());
                globalBackground.setFitHeight(globalBackground.getImage().getHeight() * scaleFactor);
            }
        };

        ChangeListener<Number> heightListener = (obs, oldHeight, newHeight) -> {
            double scaleFactor;
            double currentAspectRatio = stage.widthProperty().doubleValue() / newHeight.doubleValue();
            double ratioHeight = stage.widthProperty().doubleValue() / ASPECT_RATIO;
            if (ratioHeight < newHeight.doubleValue()){
                scaleFactor = newHeight.doubleValue() / globalBackground.getImage().getHeight();
                globalBackground.setFitHeight(newHeight.doubleValue());
                globalBackground.setFitWidth(globalBackground.getImage().getWidth() * scaleFactor);
            }
        };

        stage.widthProperty().addListener(widthListener);
        stage.heightProperty().addListener(heightListener);

        mainBox.getScene().getWindow().setOnCloseRequest(e -> closeWindow());

        //Remove horizontal and vertical scroll
        scrollModification(seasonScroll);
        scrollModification(seriesScrollPane);

        menuParentPane.setVisible(false);
        mainMenu.setVisible(false);
        seriesMenu.setVisible(false);
        seasonMenu.setVisible(false);
        discMenu.setVisible(false);

        //Elements size
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        seriesScrollPane.setPrefHeight(screenHeight);
        seriesContainer.setPrefHeight(screenHeight);

        /*
        seasonsEpisodesBox.setMinHeight(discContainer.getMinHeight());
        seasonsEpisodesBox.prefHeightProperty().bind(discContainer.heightProperty());
        seasonsEpisodesBox.maxHeightProperty().bind(discContainer.heightProperty());
        seasonBorderPane.prefHeightProperty().bind(seasonsEpisodesBox.heightProperty());
        //seasonBorderPane.maxHeightProperty().bind(seasonsEpisodesBox.heightProperty());
        //seasonInfoPane.minHeightProperty().bind(seasonBorderPane.heightProperty());
        seasonInfoPane.prefHeightProperty().bind(seasonBorderPane.heightProperty());
        //seasonInfoPane.maxHeightProperty().bind(seasonBorderPane.heightProperty());
        */

        discContainer.prefHeightProperty().bind(seasonsEpisodesBox.heightProperty());
        discContainer.prefWidthProperty().bind(seasonsEpisodesBox.widthProperty());


        seasonInfoPane.getChildren().add(0, seasonBackground);
        seasonInfoPane.getChildren().add(1, seasonBackgroundNoise);

        ImageView seasonNoise = new ImageView(new Image("file:src/main/resources/img/noise.png"));
        seasonNoise.setPreserveRatio(false);
        seasonNoise.setOpacity(0.03);
        seasonBackgroundNoise.setImageView(seasonNoise);

        seriesStack.prefHeightProperty().bind(seriesScrollPane.heightProperty());
        seriesStack.getChildren().add(0, seriesBackgroundNoise);

        ImageView seriesNoise = new ImageView(new Image("file:src/main/resources/img/noise.png"));
        seriesNoise.setPreserveRatio(false);
        seriesNoise.setOpacity(0.03);
        seriesBackgroundNoise.setImageView(seriesNoise);

        globalBackground.setPreserveRatio(true);

        globalBackgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackgroundShadow.setPreserveRatio(false);

        noiseImage.setFitWidth(screenWidth);
        noiseImage.setFitHeight(screenHeight);
        noiseImage.setPreserveRatio(false);

        backgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        backgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        backgroundShadow.setPreserveRatio(false);

        updateCategories();
    }

    public void showSeries(){
        seriesButtons.clear();

        if (seriesList == null)
            return;

        for (Series s : seriesList){
            Button seriesButton = new Button();
            seriesButton.setText(s.getName());
            seriesButton.setBackground(null);
            seriesButton.getStyleClass().add("desktopTextButton");
            seriesButton.setMaxWidth(Integer.MAX_VALUE);
            seriesButton.setAlignment(Pos.BASELINE_LEFT);
            seriesButton.setWrapText(true);

            seriesButton.setPadding(new Insets(5, 5, 5, 5));

            seriesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                selectSeriesButton(seriesButton);
                if (event.getButton() == MouseButton.SECONDARY) {
                    openSeriesMenu(event);
                }
            });

            seriesContainer.getChildren().add(seriesButton);
            seriesButtons.add(seriesButton);
        }

        if (seriesList.isEmpty())
            blankSelection();
        else{
            selectSeriesButton(seriesButtons.get(0));
            selectSeries(seriesList.get(0));
        }
    }

    public void selectSeries(Series s) {
        if (selectedSeries != s){
            selectedDiscs.clear();
            seasonScroll.setVisible(true);
            selectionOptions.setVisible(false);
            selectedSeries = s;
            selectSeriesButton(seriesButtons.get(getSeriesIndex(s)));

            if (!s.getSeasons().isEmpty()){
                seasonList.clear();
                for (String id : s.getSeasons()){
                    Season season = App.findSeason(id);
                    if (season != null)
                        seasonList.add(season);
                }

                seasonList.sort(new Utils.SeasonComparator());
                showSeasons();
                selectedSeason = seasonList.get(0);
                selectSeasonButton(seasonsButtons.get(0));

                fillSeasonInfo();

                if (!selectedSeason.getDiscs().isEmpty()){
                    showDiscs(selectedSeason);
                }else{
                    discContainer.getChildren().clear();
                }
            }else{
                seasonScroll.setVisible(false);
            }
        }
    }

    private void fadeInTransition(ImageView imageV){
        //Fade In Transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), imageV);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void fillSeasonInfo() {
        selectedDiscs.clear();
        selectionOptions.setVisible(false);
        Image i = new Image("file:" + "src/main/resources/img/backgrounds/" + selectedSeason.getId() + "/" + "desktopBlur.png");
        ASPECT_RATIO = i.getWidth() / i.getHeight();
        globalBackground.setImage(i);
        ImageView img = new ImageView(new Image("file:" + "src/main/resources/img/backgrounds/" + selectedSeason.getId() + "/" + "transparencyEffect.png"));
        img.setPreserveRatio(true);
        seasonBackground.setImageView(img);
        fadeInTransition(globalBackground);
        fadeInTransition(seasonBackground.getImageView());

        //Fill info
        detailsText.setText(App.textBundle.getString("details"));
        yearText.setText(App.textBundle.getString("year"));
        orderText.setText(App.textBundle.getString("order"));
        episodesText.setText(App.textBundle.getString("episodes"));
        yearField.setText(selectedSeason.getYear());
        orderField.setText(Integer.toString(selectedSeason.getOrder()));
        episodesField.setText(Integer.toString(selectedSeason.getDiscs().size()));

        if (selectedSeason.getLogoSrc().isEmpty()){
            seasonLogoBox.getChildren().remove(0);
            Label seasonLogoText = new Label(selectedSeries.getName());
            seasonLogoText.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 42));
            seasonLogoText.setTextFill(Color.color(1, 1, 1));
            seasonLogoText.setEffect(new DropShadow());
            seasonLogoText.setPadding(new Insets(0, 0, 0, 15));
            seasonLogoBox.getChildren().add(seasonLogoText);
        }else{
            seasonLogoBox.getChildren().remove(0);
            seasonLogo = new ImageView();
            File file = new File(selectedSeason.getLogoSrc());
            try{
                seasonLogo.setImage(new Image(file.toURI().toURL().toExternalForm()));
                seasonLogo.setFitWidth(500);
                seasonLogo.setPreserveRatio(true);
                seasonLogoBox.getChildren().add(seasonLogo);
            } catch (MalformedURLException e) {
                System.err.println("DesktopViewController: Logo not loaded");
            }
        }

        try{
            if (!selectedSeries.getCoverSrc().isEmpty()){
                File file = new File(selectedSeries.getCoverSrc());
                Image image = new Image(file.toURI().toURL().toExternalForm(), 200, 251, true, true);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

                //Compress image
                BufferedImage resizedImage = Thumbnails.of(bufferedImage)
                        .size(200, 251)
                        .outputFormat("jpg")
                        .asBufferedImage();

                Image compressedImage = SwingFXUtils.toFXImage(resizedImage, null);
                bufferedImage.flush();
                resizedImage.flush();
                seriesCover.setImage(compressedImage);
            }
        } catch (MalformedURLException e) {
            System.err.println("Series cover not found");
        } catch (IOException e) {
            System.err.println("DesktopView: Series Cover not properly compressed");
        }

        seasonName.setText(selectedSeason.getName());
    }

    public void updateDiscView(){
        if (selectedSeason != null){
            selectedSeason = App.findSeason(selectedSeason.getId());
            selectSeason(selectedSeason);
        }else{
            selectedSeries = App.findSeries(selectedSeries.id);
            selectSeries(selectedSeries);
        }
    }

    private void showSeasons() {
        seasonContainer.getChildren().clear();
        seasonsButtons.clear();

        for (Season s : seasonList){
            Button seasonButton = new Button();
            seasonButton.setText(s.getName());
            seasonButton.setBackground(null);
            seasonButton.getStyleClass().add("seasonButton");
            seasonButton.setMaxWidth(Integer.MAX_VALUE);
            seasonButton.setAlignment(Pos.BASELINE_LEFT);
            seasonButton.setWrapText(true);

            seasonButton.setPadding(new Insets(5, 5, 5, 5));

            seasonButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                selectSeasonButton(seasonButton);
                if (event.getButton() == MouseButton.SECONDARY) {
                    openSeasonMenu(event);
                }
            });

            seasonContainer.getChildren().add(seasonButton);
            seasonsButtons.add(seasonButton);
        }
    }

    public void updateSeasons(){
        selectedSeries = App.findSeries(selectedSeries.id);
        selectSeries(selectedSeries);
    }

    private void showDiscs(Season s) {
        discList.clear();
        discContainer.getChildren().clear();
        discControllers.clear();
        for (String i : s.getDiscs()){
            Disc d = App.findDisc(i);
            if (d != null){
                discList.add(d);
            }
        }

        if (!discList.isEmpty()){
            discList.sort(new Utils.DiscComparator());
            for (Disc d : discList){
                addEpisodeCard(d);
            }
        }
    }

    public void showDownloadWindow(int total){
        downloadedMetadataCount = 0;
        downloadedMetadataTotal = total;
        downloadingContentText.setText("Downloading images " + downloadedMetadataCount + "/" + downloadedMetadataTotal);
        downloadingContentWindow.setVisible(true);
    }

    public void hideDownloadWindow(){
        downloadingContentWindow.setVisible(false);
    }

    public Season getCurrentSeason(){
        return selectedSeason;
    }

    public void blankSelection(){
        seasonScroll.setVisible(false);
    }

    private int getSeriesIndex(Series s){
        for (int i = 0; i < seriesList.size(); i++){
            if (seriesList.get(i).getId() == s.getId())
                return i;
        }
        return 0;
    }

    public static void scrollModification(ScrollPane scroll) {
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final double SPEED = 0.0025;
        scroll.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scroll.setVvalue(scroll.getVvalue() - deltaY);
        });
    }

    public void refreshSeason(Season s){
        selectedSeason = null;
        selectSeason(s);
    }

    //region UPDATE VALUES
    public void updateCategories(){
        List<String> categories = App.getCategories();
        categorySelector.getItems().clear();
        categorySelector.getItems().addAll(categories);
        if (!categories.isEmpty()){
            categorySelector.setValue(categories.get(0));
            selectCategory(categories.get(0));
        }
    }
    public void updateLanguage(){
        categorySelector.getItems().clear();
        categorySelector.getItems().addAll(App.getCategories());
        if (categorySelector.getItems().size() > 1) {
            categorySelector.setValue(categorySelector.getItems().get(1));
            seriesList = App.getSeriesFromCategory(categorySelector.getValue());
            selectCategory(categorySelector.getValue());
        }else{
            seriesList = App.getCollection();
        }

        addCollectionButton.setText(App.buttonsBundle.getString("addCollection"));
        addSeasonButton.setText(App.buttonsBundle.getString("addSeason"));
        addDiscButton.setText(App.buttonsBundle.getString("addEpisodes"));
        settingsButton.setText(App.buttonsBundle.getString("settings"));
        exitButton.setText(App.buttonsBundle.getString("eixtButton"));
        switchFSButton.setText(App.buttonsBundle.getString("switchToFullscreen"));
        removeColButton.setText(App.buttonsBundle.getString("removeButton"));
        removeSeasonButton.setText(App.buttonsBundle.getString("removeButton"));
        removeDiscButton.setText(App.buttonsBundle.getString("removeButton"));
        editColButton.setText(App.buttonsBundle.getString("editButton"));
        editSeasonButton.setText(App.buttonsBundle.getString("editButton"));
        editDiscButton.setText(App.buttonsBundle.getString("editButton"));
    }
    //endregion

    //region SELECTION
    public void selectCategory(String category){
        seriesContainer.getChildren().clear();
        currentCategory = App.findCategory(category);
        seriesList = App.getSeriesFromCategory(category);
        showSeries();
    }
    private void selectSeasonButton(Button btn) {
        //Clear Selected Button
        for (Button b : seasonsButtons){
            b.getStyleClass().clear();
            b.getStyleClass().add("seasonButton");
        }
        //Select current button
        btn.getStyleClass().clear();
        btn.getStyleClass().add("seasonButtonActive");
        String seasonName = btn.getText();
        Season season = null;

        for (Season s : seasonList){
            if (s.getName().equals(seasonName)){
                season = s;
            }
        }

        if (season != null){
            selectSeason(season);
        }
    }
    public void selectSeason(Season s) {
        if (selectedSeason != s){
            selectedSeason = s;

            fillSeasonInfo();

            if (!selectedSeason.getDiscs().isEmpty()){
                showDiscs(selectedSeason);
            }else{
                discContainer.getChildren().clear();
            }
        }
    }
    void selectSeriesButton(Button btn){
        //Clear Selected Button
        for (Button b : seriesButtons){
            b.getStyleClass().clear();
            b.getStyleClass().add("desktopTextButton");
        }
        //Select current button
        btn.getStyleClass().clear();
        btn.getStyleClass().add("desktopButtonActive");
        String seriesName = btn.getText();

        Series series = null;

        for (Series s : seriesList){
            if (s.getName().equals(seriesName)){
                series = s;
            }
        }

        if (series != null){
            selectSeries(series);
        }
    }
    public boolean isDiscSelected(){
        return !selectedDiscs.isEmpty();
    }
    //endregion

    //region IMAGE EFFECTS
    public void saveBackground(Season s, boolean edit, String imageToCopy, boolean croppedImage){
        try{
            Files.createDirectories(Paths.get("src/main/resources/img/backgrounds/" + s.id + "/"));
        } catch (IOException e) {
            System.err.println("saveBackground: Directory could not be created");
        }

        //Clear old images
        try{
            File f;
            if (!croppedImage){
                f = new File(s.getBackgroundSrc());
                if (f.exists())
                    Files.delete(f.toPath());
            }
            f = new File("src/main/resources/img/backgrounds/" + s.id + "/" + "fullBlur.png");
            if (f.exists())
                Files.delete(f.toPath());
            f = new File("src/main/resources/img/backgrounds/" + s.id + "/" + "transparencyEffect.png");
            if (f.exists())
                Files.delete(f.toPath());
            f = new File("src/main/resources/img/backgrounds/" + s.id + "/" + "desktopBlur.png");
            if (f.exists())
                Files.delete(f.toPath());
        } catch (IOException e) {
            System.err.println("AddSeasonController: Error removing old images");
        }

        File newBackground;
        if (!croppedImage || !edit){
            newBackground = new File("src/main/resources/img/backgrounds/" + s.id + "/background.png");

            try{
                File file = new File(imageToCopy);
                Files.copy(file.toPath(), newBackground.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                System.err.println("Background not copied");
            }

            s.setBackgroundSrc("src/main/resources/img/backgrounds/" + s.id + "/background.png");
        }else{
            newBackground = new File(s.getBackgroundSrc());
        }

        Image image = null;
        try{
            image = new Image(newBackground.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            System.err.println("Background image not created");
        }

        ImageView backgroundBlur = new ImageView(image);
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(27);
        backgroundBlur.setEffect(blur);

        File backgroundFullscreenBlur = new File("src/main/resources/img/backgrounds/" + s.id + "/fullBlur.png");
        BufferedImage bImageFull = SwingFXUtils.fromFXImage(backgroundBlur.snapshot(null, null), null);

        try {
            ImageIO.write(bImageFull, "png", backgroundFullscreenBlur);
        } catch (IOException e) {
            System.err.println("Blur image fullscreen error");
        }

        bImageFull.flush();

        File file = new File(backgroundFullscreenBlur.getAbsolutePath());
        try{
            image = new Image(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            System.err.println("Background image creation error");
        }

        if (image == null)
            return;

        PixelReader reader = image.getPixelReader();
        WritableImage newImage = new WritableImage(reader
                , (int) (image.getWidth() * 0.03), (int) (image.getHeight() * 0.05)
                , (int) (image.getWidth() * 0.93), (int) (image.getHeight() * 0.9));

        try{
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(newImage, null);
            ImageIO.write(renderedImage,"png", file);
        } catch (IOException e) {
            System.err.println("Background image copy error");
        }

        ImageView backgroundBlurDesktop = new ImageView(image);
        blur.setRadius(80);
        backgroundBlurDesktop.setEffect(blur);

        File backgroundDesktopBlur = new File("src/main/resources/img/backgrounds/" + s.id + "/" + "desktopBlur.png");
        BufferedImage bImageDesktop = SwingFXUtils.fromFXImage(backgroundBlurDesktop.snapshot(null, null), null);

        try {
            ImageIO.write(bImageDesktop, "png", backgroundDesktopBlur);
        } catch (IOException e) {
            System.err.println("Blur image desktop error");
        }

        bImageDesktop.flush();

        file = new File(backgroundDesktopBlur.getAbsolutePath());
        try{
            image = new Image(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            System.err.println("Background image creation error");
        }

        reader = image.getPixelReader();
        newImage = new WritableImage(reader
                , (int) (image.getWidth() * 0.08), (int) (image.getHeight() * 0.1)
                , (int) (image.getWidth() * 0.86), (int) (image.getHeight() * 0.8));

        try{
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(newImage, null);
            ImageIO.write(renderedImage,"png", file);
        } catch (IOException e) {
            System.err.println("Background image copy error");
        }

        setTransparencyEffect(s.getBackgroundSrc(), s.id);
        setDesktopBackgroundBlur(s.id);
    }
    private void setTransparencyEffect(String src, String seasonId){
        try {
            //Load image
            BufferedImage originalImage = ImageIO.read(new File(src));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            //Create copy
            BufferedImage blendedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            //Apply gradual opacity
            for (int y = 0; y < height; y++) {
                float opacity = 1.0f - ((float) y / (height / 1.15f));

                //Make sure the opacity value is valid
                opacity = Math.min(1.0f, Math.max(0.0f, opacity));

                for (int x = 0; x < width; x++) {
                    //Obtain original pixel's color
                    java.awt.Color originalColor = new java.awt.Color(originalImage.getRGB(x, y), true);

                    //Apply opacity
                    int blendedAlpha = (int) (originalColor.getAlpha() * opacity);

                    //Create new color with opacity
                    java.awt.Color blendedColor = new java.awt.Color(originalColor.getRed(), originalColor.getGreen(),
                            originalColor.getBlue(), blendedAlpha);

                    //Apply color to the new image
                    blendedImage.setRGB(x, y, blendedColor.getRGB());
                }
            }

            //Save the image with the progressive transparency effect
            ImageIO.write(blendedImage, "png"
                    , new File("src/main/resources/img/backgrounds/" + seasonId + "/" + "transparencyEffect.png"));
            originalImage.flush();
            blendedImage.flush();
        } catch (IOException e) {
            System.err.println("AddSeasonController: error applying transparency effect to background");
        }
    }

    private void setDesktopBackgroundBlur(String seasonId){
        try {
            BufferedImage backgroundEffect = ImageIO.read(new File("src/main/resources/img/Background.png"));
            BufferedImage originalImage = ImageIO.read(new File("src/main/resources/img/backgrounds/" + seasonId + "/" + "desktopBlur.png"));

            float contrastFactor = 0.1f;
            BufferedImage highContrastImage = applyContrast(backgroundEffect, contrastFactor);

            BufferedImage resultImage = applyNoiseEffect(highContrastImage, originalImage);
            resultImage = applyNoiseEffect(resultImage, originalImage);

            ImageIO.write(resultImage, "jpg", new File("src/main/resources/img/backgrounds/" + seasonId + "/" + "desktopBlur.png"));
            backgroundEffect.flush();
            originalImage.flush();
            highContrastImage.flush();
            resultImage.flush();
        } catch (IOException e) {
            System.err.println("AddSeasonController: Error creating Desktop Blur and Noise Effects");
        }
    }

    private static BufferedImage scaleImageTo(BufferedImage originalImage, int targetWidth, int targetHeight) {
        java.awt.Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH);

        BufferedImage scaledBufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledBufferedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return scaledBufferedImage;
    }

    private static BufferedImage applyNoiseEffect(BufferedImage originalImage, BufferedImage noiseImage) {
        BufferedImage scaledNoiseImage = scaleImageTo(noiseImage, originalImage.getWidth(), originalImage.getHeight());

        int width = Math.min(originalImage.getWidth(), scaledNoiseImage.getWidth());
        int height = Math.min(originalImage.getHeight(), scaledNoiseImage.getHeight());

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        float blendFactor = 0.2f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                java.awt.Color originalColor = new java.awt.Color(originalImage.getRGB(x, y), true);

                java.awt.Color noiseColor = new java.awt.Color(scaledNoiseImage.getRGB(x, y));

                int blendedRed = (int) (originalColor.getRed() * (1 - blendFactor) + noiseColor.getRed() * blendFactor);
                int blendedGreen = (int) (originalColor.getGreen() * (1 - blendFactor) + noiseColor.getGreen() * blendFactor);
                int blendedBlue = (int) (originalColor.getBlue() * (1 - blendFactor) + noiseColor.getBlue() * blendFactor);
                int blendedAlpha = originalColor.getAlpha();

                java.awt.Color blendedColor = new java.awt.Color(blendedRed, blendedGreen, blendedBlue, blendedAlpha);
                resultImage.setRGB(x, y, blendedColor.getRGB());
            }
        }

        return resultImage;
    }

    private static BufferedImage applyContrast(BufferedImage image, float contrastFactor) {
        RescaleOp rescaleOp = new RescaleOp(contrastFactor, 0, null);
        return rescaleOp.filter(image, null);
    }
    //endregion

    //region PLAY EPISODE
    public void playEpisode(Disc disc) {
        //Run file in vlc
        String command = null;
        String extension = disc.getExecutableSrc().substring(disc.getExecutableSrc().length() - 3);

        if (disc.getType().equals("Folder") || extension.equals("iso") || extension.equals("ISO"))
            command = "bluray:///" + disc.getExecutableSrc();
        else
            command = disc.getExecutableSrc();

        try {
            ProcessBuilder pBuilder = new ProcessBuilder("C:\\Program Files\\VideoLAN\\VLC\\vlc.exe", command);

            // don't forget to handle the error stream, and so
            // either combine error stream with input stream, as shown here
            // or gobble it separately
            pBuilder.redirectErrorStream(true);
            final Process process = pBuilder.start();

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error playing episode in DesktopViewController");
        }
    }
    //endregion

    //region EPISODE SELECTION
    public void selectDisc(Disc disc){
        if (selectedDiscs.contains(disc)) {
            selectedDiscs.remove(disc);
        }else{
            selectedDiscs.add(disc);
        }

        selectionOptions.setVisible(!selectedDiscs.isEmpty());

        if (selectedDiscs.size() == 1)
            elementsSelectedText.setText(App.textBundle.getString("oneSelected"));
        else
            elementsSelectedText.setText(selectedDiscs.size() + " " + App.textBundle.getString("selectedDiscs"));
    }
    @FXML
    void deleteSelected(ActionEvent event) {
        for (Disc disc : selectedDiscs){
            removeDisc(disc);
        }
        selectedDiscs.clear();
        selectedDisc = null;
        selectionOptions.setVisible(false);
    }

    @FXML
    void deselectAll(ActionEvent event) {
        for (DiscController d : discControllers){
            d.clearSelection();
        }
        selectionOptions.setVisible(false);
        selectedDiscs.clear();
    }
    @FXML
    void selectAll(ActionEvent event) {
        for (DiscController d : discControllers){
            if (!d.discSelected)
                d.selectDiscDesktop();
        }
    }
    //endregion

    //region WINDOW
    private void closeWindow(){
        try{
            App.SaveData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
    @FXML
    void close(MouseEvent event) {
        closeWindow();
    }
    @FXML
    void maximizeWindow(MouseEvent event) {
        Stage stage = (Stage)((Button) event.getSource()).getScene().getWindow();

        if (stage.isMaximized())
            maximizeRestoreImage.setImage(new Image("file:src/main/resources/img/icons/windowMaximize.png"));
        else
            maximizeRestoreImage.setImage(new Image("file:src/main/resources/img/icons/windowRestore.png"));

        stage.setMaximized(!stage.isMaximized());
    }
    @FXML
    void minimizeWindow(MouseEvent event) {
        ((Stage)((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }
    private void setDragWindow(BorderPane topBar) {
        topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) mainBox.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }
    //endregion

    //region AUTOMATED FILE SEARCH
    public void loadCategory(String name){
        categorySelector.getItems().add(name);
        categorySelector.setValue(name);

        currentCategory = App.findCategory(name);

        if (currentCategory == null)
            return;

        if (currentCategory.type.equals("Shows"))
            seriesMetadata = tmdbApi.getTvSeries();
        else
            moviesMetadata = tmdbApi.getMovies();

        List<String> folders = currentCategory.folders;
        for (String folderSrc : folders){
            File folder = new File(folderSrc);

            if (!folder.exists())
                continue;

            File[] files = folder.listFiles();

            if (files == null)
                continue;

            for (File f : files){
                if (currentCategory.type.equals("Shows"))
                    addTVShow(f);
                else
                    addMovieOrConcert(f);
            }
        }

        updateCategories();
    }
    private void addTVShow(File directory){
        if (directory.isFile())
            return;

        File[] filesInDir = directory.listFiles();
        if (filesInDir == null)
            return;

        //CREATE SERIES
        Series newSeries = searchFirstSeries(directory.getName());
        App.addCollection(newSeries);
        currentCategory.series.add(newSeries.id);

        boolean seriesNotFound = false;
        List<TvSeason> seasonsList = new ArrayList<>();

        //region CREATE SEASONS
        if (newSeries.themdbID == -1){
            seriesNotFound = true;
            Season newSeason = new Season();
            newSeason.name = directory.getName();
            newSeason.collectionName = newSeries.name;
            App.addSeason(newSeason, newSeries.id);
        }else{
            for (int i = 0; i <= newSeries.seasonsNumber; i++){
                TmdbTvSeasons tvSeasons = tmdbApi.getTvSeasons();
                TvSeason season = tvSeasons.getSeason(newSeries.themdbID, i, currentCategory.language, TmdbTvSeasons.SeasonMethod.values());

                if (season == null)
                    continue;

                seasonsList.add(season);
                Season newSeason = new Season();
                newSeason.name = season.getName();
                newSeason.collectionName = newSeries.name;

                newSeason.order = i;

                if (i == 0)
                    newSeason.order = newSeries.seasonsNumber + 1;

                App.addSeason(newSeason, newSeries.id);
            }
        }
        //endregion

        System.out.println("Seasons created");

        //Process background images
        if (!seriesNotFound){
            /*Task<Void> imageProcessing = new Task<>() {
                @Override
                protected Void call() {


                    return null;
                }
            };

            new Thread(imageProcessing).start();*/
            for (String seasonID : newSeries.getSeasons()) {
                Season season = App.findSeason(seasonID);
                saveBackground(season, false, "src/main/resources/img/DownloadCache/" + newSeries.id + ".png", false);
            }
        }

        List<File> folders = new ArrayList<>();

        //Files in root directory of the Show
        List<File> filesInRoot = new ArrayList<>();
        for (File file : filesInDir){
            if (file.isDirectory())
                folders.add(file);
            else
                filesInRoot.add(file);
        }

        final String regexSeason = "(?i)(?<season>S[0-9]{1,3})";
        final Pattern pattern = Pattern.compile(regexSeason, Pattern.MULTILINE);

        for (File file : folders){
            final Matcher matcher = pattern.matcher(file.getName());

            if (!matcher.find())
                continue;

            File[] episodeFiles = file.listFiles();

            if (episodeFiles == null)
                continue;

            for (File episodeFile : episodeFiles){
                if (episodeFile.isFile() && validVideoFile(episodeFile))
                    processEpisode(newSeries, episodeFile, seasonsList);
            }
        }

        for (File file : filesInRoot){
            if (validVideoFile(file))
                processEpisode(newSeries, file, seasonsList);
        }
    }
    private boolean validVideoFile(File file){
        String videoExtension = file.getName().substring(file.getName().length() - 4);
        videoExtension = videoExtension.toLowerCase();

        return videoExtension.equals(".mkv") || videoExtension.equals(".mp4") || videoExtension.equals(".avi") || videoExtension.equals(".mov")
                || videoExtension.equals(".wmv") || videoExtension.equals("mpeg") || videoExtension.equals("m2ts") || videoExtension.equals(".iso");
    }
    private void processEpisode(Series series, File file, List<TvSeason> seasonsMetadata){
        Disc newDisc = new Disc();

        String fullName = file.getName().substring(0, file.getName().lastIndexOf("."));
        String extension = file.getName().substring(file.getName().length() - 3);

        if (extension.equals("iso")){
            newDisc.name = fullName;
            newDisc.seasonID = series.getSeasons().get(0);
            newDisc.executableSrc = file.getAbsolutePath();
            newDisc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
            return;
        }

        final String regexSeasonEpisode = "(?i)(?<season>S[0-9]{1,3}+)(?<episode>E[0-9]{1,4})";
        final String regexOnlyEpisode = "(?i)(?<episode>[0-9]{1,4})";

        final Pattern pattern = Pattern.compile(regexSeasonEpisode, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(fullName);

        if (!matcher.find()){
            Pattern newPattern = Pattern.compile(regexOnlyEpisode, Pattern.MULTILINE);
            Matcher newMatch = newPattern.matcher(fullName);

            if (newMatch.find()){

                int absoluteNumber = Integer.parseInt(newMatch.group("episode"));
                int j = 1;
                int k = 1;
                for (int i = 1; i < absoluteNumber; i++){
                    if (k == seasonsMetadata.get(j).getEpisodes().size()){
                        j++;
                        k = 1;
                    }else{
                        k++;
                    }
                }

                TvEpisode episode = seasonsMetadata.get(j).getEpisodes().get(k - 1);

                newDisc.setEpisodeNumber(newMatch.group("episode"));
                newDisc.seasonID = series.getSeasons().get(j);
                setEpisodeData(newDisc, episode, series);
            }else{
                newDisc.setName(fullName);
                newDisc.setEpisodeNumber("");
            }
        }else{
            int seasonNumber = Integer.parseInt(matcher.group("season").substring(1));
            if (Integer.parseInt(matcher.group("season").substring(1)) >= seasonsMetadata.size())
                seasonNumber = seasonsMetadata.size() - 1;

            TvEpisode episode = seasonsMetadata.get(seasonNumber).getEpisodes().get(Integer.parseInt(matcher.group("episode").substring(1)));
            newDisc.setEpisodeNumber(matcher.group("episode").substring(1));
            newDisc.seasonID = series.getSeasons().get(seasonNumber);
            setEpisodeData(newDisc, episode, series);
        }

        newDisc.setExecutableSrc(file.getAbsolutePath());
        App.addDisc(newDisc);
    }
    private void setEpisodeData(Disc disc, TvEpisode episodeMetadata, Series show){
        disc.name = episodeMetadata.getName();
        disc.resume = episodeMetadata.getOverview();

        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        try{
            Files.createDirectories(Paths.get("src/main/resources/img/discCovers/" + disc.id + "/"));
        } catch (IOException e) {
            System.err.println("setEpisodeData: Directory could not be created");
        }

        MovieImages images = tmdbApi.getTvEpisodes().getEpisode(show.themdbID, episodeMetadata.getSeasonNumber(), episodeMetadata.getEpisodeNumber(), null, TmdbTvEpisodes.EpisodeMethod.images).getImages();
        List<Artwork> thumbnails = images.getStills();

        List<String> thumbnailsUrls = new ArrayList<>();
        if (thumbnails != null){
            for (Artwork artwork : thumbnails){
                thumbnailsUrls.add(imageBaseURL + artwork.getFilePath());
            }
        }

        //region THUMBNAIL DOWNLOADER
        for (int i = 0; i < thumbnailsUrls.size(); i++){
            try{
                Image originalImage = new Image(thumbnailsUrls.get(i), 480, 270, true, true);

                double maxWidth = 480;
                double maxHeight = 270;
                double originalWidth = originalImage.getWidth();
                double originalHeight = originalImage.getHeight();

                Image compressedImage;
                if (originalWidth > maxWidth || originalHeight > maxHeight) {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);

                    BufferedImage resizedImage = Thumbnails.of(bufferedImage)
                            .size((int) maxWidth, (int) maxHeight)
                            .outputFormat("jpg")
                            .asBufferedImage();

                    compressedImage = SwingFXUtils.toFXImage(resizedImage, null);
                    bufferedImage.flush();
                    resizedImage.flush();
                }else{
                    compressedImage = originalImage;
                }

                if (!originalImage.isError()){
                    File file = new File("src/main/resources/img/discCovers/" + disc.id + "/" + i + ".png");
                    try{
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(compressedImage, null);
                        ImageIO.write(renderedImage,"jpg", file);
                    } catch (IOException e) {
                        System.err.println("DesktopViewController: Disc downloaded thumbnail not saved");
                    }
                }
            } catch (IOException e) {
                System.err.println("DesktopViewController: Error compressing image");
            }
        }
        //endregion

        File img = new File("src/main/resources/img/discCovers/" + disc.id + "/0.png");
        if (!img.exists()){
            disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
        }else{
            disc.imgSrc = "src/main/resources/img/discCovers/" + disc.id + "/0.png";
        }
    }
    private Series searchFirstSeries(String seriesName){
        Series newSeries = new Series();

        TvResultsPage tvResults = tmdbApi.getSearch().searchTv(seriesName, 1, currentCategory.language, true, 1);

        if (tvResults.getTotalResults() > 0) {
            TvSeries tvSeries = tvResults.getResults().get(0);
            newSeries.name = tvSeries.getName();
            newSeries.resume = tvSeries.getOverview();
            newSeries.themdbID = tvSeries.getId();

            TvSeries show = seriesMetadata.getSeries(tvSeries.getId(), currentCategory.language);
            newSeries.seasonsNumber = show.getNumberOfSeasons();

            downloadImages(newSeries, tvSeries.getId(), seriesMetadata.getImages(tvSeries.getId(), null));

            return newSeries;
        }

        newSeries.name = seriesName;
        return newSeries;
    }
    private void saveCover(Series newSeries, int i, Image originalImage) {
        try{
            Files.createDirectories(Paths.get("src/main/resources/img/seriesCovers/" + newSeries.id + "/"));
        } catch (IOException e) {
            System.err.println("Directory could not be created");
        }

        if (!originalImage.isError()){
            File file = new File("src/main/resources/img/seriesCovers/" + newSeries.id + "/" + i + ".png");
            try{
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(originalImage, null);
                ImageIO.write(renderedImage,"png", file);
            } catch (IOException e) {
                System.err.println("DesktopViewController: Downloaded cover not saved");
            }
        }
    }
    private void addMovieOrConcert(File f){
        if (!f.isDirectory()){
            //region MOVIE FILE ONLY
            if (!validVideoFile(f)){
                return;
            }

            //Create Series
            Series newSeries = searchFirstMovie(f.getName().substring(0, f.getName().lastIndexOf(".")));
            Season newSeason = new Season();
            if (newSeries.themdbID == -1){
                newSeason.name = f.getName().substring(0, f.getName().lastIndexOf("."));
                newSeason.collectionName = newSeries.name;

                App.addCollection(newSeries);
                App.addSeason(newSeason, newSeries.id);
                currentCategory.series.add(newSeries.id);

                if (!f.isDirectory() && validVideoFile(f))
                    saveDiscWithoutMetadata(f, newSeason);
            }else{
                newSeason.name = newSeries.name;
                newSeason.collectionName = newSeries.name;
                newSeason.themdbID = newSeries.themdbID;

                downloadImages(newSeries, newSeries.themdbID, moviesMetadata.getImages(newSeries.themdbID, null));
                saveBackground(newSeason, false, "src/main/resources/img/DownloadCache/" + newSeries.themdbID + ".png", false);

                App.addCollection(newSeries);
                App.addSeason(newSeason, newSeries.id);
                currentCategory.series.add(newSeries.id);

                if (!f.isDirectory())
                    processMovieOrConcert(f, newSeason);
            }
            //endregion
        }else {
            File[] filesInDir = f.listFiles();
            if (filesInDir == null)
                return;

            List<File> folders = new ArrayList<>();
            List<File> filesInRoot = new ArrayList<>();
            for (File file : filesInDir){
                if (file.isDirectory())
                    folders.add(file);
                else
                    filesInRoot.add(file);
            }

            if (!folders.isEmpty()){
                //region FOLDERS CORRESPONDING DIFFERENT MOVIES FROM A COLLECTION
                //Create new collection for the movies inside
                Series newSeries = new Series();
                newSeries.name = f.getName();
                currentCategory.series.add(newSeries.id);
                App.addCollection(newSeries);

                for (File folder : folders){
                    File[] filesInFolder = folder.listFiles();
                    if (filesInFolder == null)
                        continue;

                    MovieResultsPage movieResults = tmdbApi.getSearch().searchMovie(folder.getName(), 1, currentCategory.language, true, 1);

                    if (movieResults.getTotalResults() > 0) {
                        //Load Movie metadata as Season data
                        MovieDb movie = movieResults.getResults().get(0);
                        Season newSeason = new Season();
                        newSeason.name = movie.getTitle();
                        newSeason.resume = movie.getOverview();
                        newSeason.themdbID = movie.getId();
                        newSeason.collectionName = newSeries.name;

                        downloadImages(newSeries, newSeason.themdbID, moviesMetadata.getImages(movie.getId(), null));
                        saveBackground(newSeason, false, "src/main/resources/img/DownloadCache/" + newSeason.themdbID + ".png", false);

                        App.addSeason(newSeason, newSeries.id);

                        for (File file : filesInFolder){
                            if (!file.isDirectory() && validVideoFile(file))
                                processMovieOrConcert(file, newSeason);
                        }
                    }else{
                        Season newSeason = new Season();
                        newSeason.name = folder.getName();
                        newSeason.collectionName = newSeries.name;

                        App.addSeason(newSeason, newSeries.id);

                        for (File file : filesInFolder){
                            if (!file.isDirectory() && validVideoFile(file))
                                saveDiscWithoutMetadata(file, newSeason);
                        }
                    }
                }
                //endregion
            }else if (!filesInRoot.isEmpty()){
                //region MOVIE FILE/CONCERT FILES INSIDE FOLDER
                //Create Series
                Series newSeries = searchFirstMovie(f.getName());
                Season newSeason = new Season();
                if (newSeries.themdbID == -1){
                    newSeason.name = f.getName();
                    newSeason.collectionName = newSeries.name;

                    App.addCollection(newSeries);
                    App.addSeason(newSeason, newSeries.id);
                    currentCategory.series.add(newSeries.id);

                    for (File file : filesInRoot){
                        if (file.isFile() && validVideoFile(file))
                            saveDiscWithoutMetadata(file, newSeason);
                    }
                }else{
                    newSeason.name = newSeries.name;
                    newSeason.collectionName = newSeries.name;
                    newSeason.themdbID = newSeries.themdbID;

                    downloadImages(newSeries, newSeason.themdbID, moviesMetadata.getImages(newSeason.themdbID, null));
                    saveBackground(newSeason, false, "src/main/resources/img/DownloadCache/" + newSeason.themdbID + ".png", false);

                    App.addCollection(newSeries);
                    App.addSeason(newSeason, newSeries.id);
                    currentCategory.series.add(newSeries.id);

                    for (File file : filesInRoot){
                        if (file.isFile() && validVideoFile(file))
                            processMovieOrConcert(file, newSeason);
                    }
                }
                //endregion
            }
        }
    }
    private void saveDiscWithoutMetadata(File f, Season newSeason) {
        Disc newDisc = new Disc();
        newDisc.name = f.getName().substring(0, f.getName().lastIndexOf("."));
        newDisc.executableSrc = f.getAbsolutePath();
        newDisc.seasonID = newSeason.id;
        newDisc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
        App.addDisc(newDisc);
    }
    private void processMovieOrConcert(File file, Season season){
        Disc newDisc = new Disc();

        newDisc.name = file.getName().substring(0, file.getName().lastIndexOf("."));
        newDisc.episodeNumber = String.valueOf(season.getDiscs().size());
        newDisc.seasonID = season.id;
        newDisc.executableSrc = file.getAbsolutePath();

        setMovieThumbnail(newDisc, season.themdbID);
        App.addDisc(newDisc);
    }
    private void setMovieThumbnail(Disc disc, int themdbID){
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        try{
            Files.createDirectories(Paths.get("src/main/resources/img/discCovers/" + disc.id + "/"));
        } catch (IOException e) {
            System.err.println("setEpisodeData: Directory could not be created");
        }

        MovieImages images =tmdbApi.getMovies().getImages(themdbID, null);
        List<Artwork> thumbnails = images.getBackdrops();

        List<String> thumbnailsUrls = new ArrayList<>();
        if (thumbnails != null){
            for (Artwork artwork : thumbnails){
                thumbnailsUrls.add(imageBaseURL + artwork.getFilePath());
            }
        }

        //region THUMBNAIL DOWNLOADER
        for (int i = 0; i < thumbnailsUrls.size(); i++){
            try{
                Image originalImage = new Image(thumbnailsUrls.get(i), 480, 270, true, true);

                double maxWidth = 480;
                double maxHeight = 270;
                double originalWidth = originalImage.getWidth();
                double originalHeight = originalImage.getHeight();

                Image compressedImage;
                if (originalWidth > maxWidth || originalHeight > maxHeight) {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);

                    BufferedImage resizedImage = Thumbnails.of(bufferedImage)
                            .size((int) maxWidth, (int) maxHeight)
                            .outputFormat("jpg")
                            .asBufferedImage();

                    compressedImage = SwingFXUtils.toFXImage(resizedImage, null);
                    bufferedImage.flush();
                    resizedImage.flush();
                }else{
                    compressedImage = originalImage;
                }

                if (!originalImage.isError()){
                    File file = new File("src/main/resources/img/discCovers/" + disc.id + "/" + i + ".png");
                    try{
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(compressedImage, null);
                        ImageIO.write(renderedImage,"jpg", file);
                    } catch (IOException e) {
                        System.err.println("DesktopViewController: Disc downloaded thumbnail not saved");
                    }
                }
            } catch (IOException e) {
                System.err.println("DesktopViewController: Error compressing image");
            }
        }
        //endregion

        File img = new File("src/main/resources/img/discCovers/" + disc.id + "/0.png");
        if (!img.exists()){
            disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
        }else{
            disc.imgSrc = "src/main/resources/img/discCovers/" + disc.id + "/0.png";
        }
    }
    private Series searchFirstMovie(String seriesName){
        Series newSeries = new Series();

        int index = seriesName.lastIndexOf("(");
        if (index != -1){
            seriesName = seriesName.substring(0, index - 1);
        }

        MovieResultsPage movieResults = tmdbApi.getSearch().searchMovie(seriesName, 1, currentCategory.language, true, 1);

        if (movieResults.getTotalResults() > 0) {
            MovieDb movie = movieResults.getResults().get(0);
            newSeries.name = movie.getTitle();
            newSeries.resume = movie.getOverview();
            newSeries.themdbID = movie.getId();

            return newSeries;
        }

        newSeries.name = seriesName;
        return newSeries;
    }
    private void downloadImages(Series series, int tmdbID, MovieImages images){
        //region SAVE IMAGES
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        //Save posters and background images
        if (images != null){
            List<Artwork> covers = images.getPosters();
            int j = 0;
            for (int i = 0; i < covers.size(); i++){
                if (j == 10)
                    break;

                Image originalImage = new Image(imageBaseURL + covers.get(i).getFilePath());
                saveCover(series, i, originalImage);
                j++;
            }

            List<Artwork> backgrounds = images.getBackdrops();
            if (!backgrounds.isEmpty()){
                Artwork background;
                if (currentCategory.type.equals("Shows"))
                    background = backgrounds.get(0);
                else
                    background = backgrounds.get(backgrounds.size() - 1);
                Image originalImage = new Image(imageBaseURL + background.getFilePath());

                if (!originalImage.isError()){
                    File file;
                    if (currentCategory.type.equals("Shows"))
                        file = new File("src/main/resources/img/DownloadCache/" + series.id + ".png");
                    else
                        file = new File("src/main/resources/img/DownloadCache/" + tmdbID + ".png");
                    try{
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(originalImage, null);
                        ImageIO.write(renderedImage,"png", file);
                    } catch (IOException e) {
                        System.err.println("DesktopViewController: Downloaded background not saved");
                    }
                }
            }
        }

        File posterDir = new File("src/main/resources/img/seriesCovers/" + series.id + "/");
        File[] coverFiles = posterDir.listFiles();
        if (coverFiles != null){
            for (File file : coverFiles){
                if (file.exists()){
                    series.coverSrc = "src/main/resources/img/seriesCovers/" + series.id + "/" + file.getName();
                    break;
                }
            }
        }

        //Save english posters
        if (currentCategory.type.equals("Shows"))
            images = seriesMetadata.getImages(tmdbID, "en");
        else
            images = moviesMetadata.getImages(tmdbID, "en");
        if (images != null){
            List<Artwork> covers = images.getPosters();
            File coversDir = new File("src/main/resources/img/seriesCovers/");
            if (coversDir.listFiles() != null){
                int i = Objects.requireNonNull(coversDir.listFiles()).length;
                int j = 0;
                for (Artwork cover : covers){
                    if (j == 10)
                        break;

                    Image originalImage = new Image(imageBaseURL + cover.getFilePath());
                    saveCover(series, i, originalImage);
                    i++;
                    j++;
                }
            }
        }

        //Save current language posters
        if (!currentCategory.type.equals("Shows")){
            //Save english posters
            images = moviesMetadata.getImages(tmdbID, currentCategory.language);
            if (images != null){
                List<Artwork> covers = images.getPosters();
                File coversDir = new File("src/main/resources/img/seriesCovers/");
                if (coversDir.listFiles() != null){
                    int i = Objects.requireNonNull(coversDir.listFiles()).length;
                    int j = 0;
                    for (Artwork cover : covers){
                        if (j == 10)
                            break;

                        Image originalImage = new Image(imageBaseURL + cover.getFilePath());
                        saveCover(series, i, originalImage);
                        i++;
                        j++;
                    }
                }
            }
        }
        //endregion
    }
    //endregion

    //region ADD SECTION
    public void addSeries(Series s){
        seriesList.add(s);
        selectCategory(currentCategory.name);
    }
    @FXML
    void addCategory(MouseEvent event) {
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCategory-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddCategoryController addCategoryController = fxmlLoader.getController();
            addCategoryController.setParent(this);
            addCategoryController.initValues();
            Stage stage = new Stage();
            stage.setTitle("Add Category");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        updateCategories();
    }
    @FXML
    void addCollection(MouseEvent event) {
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCollection-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddCollectionController addColController = fxmlLoader.getController();
            addColController.setParentController(this);
            addColController.initializeCategories();
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("collectionWindowTitle"));
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }
    @FXML
    void addSeason(){
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addSeason-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddSeasonController addSeasonController = fxmlLoader.getController();
            addSeasonController.setParentController(this);
            addSeasonController.setCollection(selectedSeries);
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("seasonWindowTitle"));
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            scene.setFill(Color.BLACK);
            stage.setScene(scene);
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }
    @FXML
    void addDisc(MouseEvent event){
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addDisc-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddDiscController addDiscController = fxmlLoader.getController();
            addDiscController.setParentController(this);
            addDiscController.InitValues();
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("episodeWindowTitle"));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage);
            stage.showAndWait();

            backgroundShadow.setVisible(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }
    public void addDisc(Disc newDisc){
        addEpisodeCard(newDisc);
    }
    private void addEpisodeCard(Disc disc){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("discCard.fxml"));
            Pane cardBox = fxmlLoader.load();
            DiscController discController = fxmlLoader.getController();
            discController.setDesktopParentParent(this);
            discController.setData(disc);

            discContainer.getChildren().add(cardBox);
            discControllers.add(discController);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateDisc(Disc d){
        for (DiscController discController : discControllers) {
            if (discController.disc.id.equals(d.id)){
                discController.setData(d);
                return;
            }
        }
    }
    //endregion

    //region EDIT SECTION
    @FXML
    void editCategory(MouseEvent event) {
        if (currentCategory != null){
            showBackgroundShadow();
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCategory-view.fxml"));
                Parent root1 = fxmlLoader.load();
                AddCategoryController addCategoryController = fxmlLoader.getController();
                addCategoryController.setParent(this);
                addCategoryController.setValues(currentCategory.name, currentCategory.language, currentCategory.type, currentCategory.folders, currentCategory.showOnFullscreen);
                Stage stage = new Stage();
                stage.setTitle("Edit Category");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(new Scene(root1));
                App.setPopUpProperties(stage);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            updateCategories();
        }
    }
    @FXML
    void editSeries(){
        showBackgroundShadow();
        if (selectedSeries != null){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCollection-view.fxml"));
                Parent root1 = fxmlLoader.load();
                AddCollectionController addColController = fxmlLoader.getController();
                addColController.setParentController(this);
                addColController.setSeries(selectedSeries);
                Stage stage = new Stage();
                stage.setTitle(App.textBundle.getString("collectionWindowTitleEdit"));
                stage.initStyle(StageStyle.UNDECORATED);
                Scene scene = new Scene(root1);
                scene.setFill(Color.BLACK);
                stage.setScene(scene);
                App.setPopUpProperties(stage);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hideMenu();
        }
    }
    @FXML
    void editSeason(MouseEvent event){
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addSeason-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddSeasonController addSeasonController = fxmlLoader.getController();
            addSeasonController.setParentController(this);
            addSeasonController.setSeason(selectedSeason);
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("seasonWindowTitle"));
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            scene.setFill(Color.BLACK);
            stage.setScene(scene);
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        hideMenu();
    }
    @FXML
    void editDisc(MouseEvent event){
        editDisc(selectedDisc);
    }
    void editDisc(Disc d){
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editDisc-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddDiscController addDiscController = fxmlLoader.getController();
            addDiscController.setParentController(this);
            addDiscController.setDisc(d);
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("episodeWindowTitleEdit"));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }
    //endregion

    //region REMOVE SECTION
    @FXML
    void removeCategory(MouseEvent event) {
        App.removeCategory(currentCategory.name);
        updateCategories();
    }
    @FXML
    void removeCollection(MouseEvent event) {
        try {
            if (selectedSeries != null){
                seriesList.remove(selectedSeries);
                App.removeCollection(selectedSeries);
                selectedSeries = null;
                seriesList = App.getCollection();
                showSeries();
            }
            hideMenu();
        } catch (IOException e) {
            System.err.println("DesktopViewController: Error trying to remove collection");
        }
    }
    @FXML
    void removeSeason(MouseEvent event){
        seasonList.remove(selectedSeason);
        App.removeSeason(selectedSeason.getId());

        selectedSeason = null;
        selectSeries(selectedSeries);
        hideMenu();
    }
    @FXML
    void removeDisc(MouseEvent event){
        removeDisc(selectedDisc);
    }
    void removeDisc(Disc d) {
        if (d == null)
            return;

        int index = discList.indexOf(d);

        if (index != -1) {
            discContainer.getChildren().remove(index);
            discControllers.remove(index);
        }

        discList.remove(d);
        App.removeDisc(d);
        selectedSeason.removeDisc(d.id);

        selectSeason(selectedSeason);
        hideMenu();
    }
    //endregion

    //region FULLSCREEN
    @FXML
    void switchToFullScreen(MouseEvent event) {
        fullScreen();
    }
    private void fullScreen(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("fullscreenMode"));
            Scene scene = new Scene(root);
            scene.setFill(Color.BLACK);
            //scene.setCursor(Cursor.NONE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth());
            stage.setHeight(Screen.getPrimary().getBounds().getHeight());
            stage.show();

            Stage thisStage = (Stage) mainBox.getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region MENU
    @FXML
    void openMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        mainMenu.setLayoutX(event.getSceneX());
        mainMenu.setLayoutY(event.getSceneY());
        mainMenu.setVisible(true);
    }
    @FXML
    void openSettings(MouseEvent event) {
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainMenuDesktop-view.fxml"));
            Parent root1 = fxmlLoader.load();
            MainMenuDesktopController addDiscController = fxmlLoader.getController();
            addDiscController.initValues(this);
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }
    @FXML
    void openSeriesMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        seriesMenu.setLayoutX(event.getSceneX());
        seriesMenu.setLayoutY(event.getSceneY() - seriesMenu.getHeight());
        seriesMenu.setVisible(true);
    }
    @FXML
    void openSeasonMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        seasonMenu.setLayoutX(event.getSceneX());
        seasonMenu.setLayoutY(event.getSceneY() - seasonMenu.getHeight());
        seasonMenu.setVisible(true);
    }
    public void openDiscMenu(MouseEvent event, Disc disc) {
        menuParentPane.setVisible(true);
        discMenu.setLayoutX(event.getSceneX());
        if (discList.indexOf(disc) > 2)
            discMenu.setLayoutY(event.getSceneY() - discMenu.getHeight());
        else
            discMenu.setLayoutY(event.getSceneY());
        discMenu.setVisible(true);
    }
    private void hideMenu(){
        discMenu.setVisible(false);
        seasonMenu.setVisible(false);
        seriesMenu.setVisible(false);
        mainMenu.setVisible(false);
        menuParentPane.setVisible(false);
    }
    //endregion

    //region BACKGROUND SHADOW
    public void showBackgroundShadow(){
        backgroundShadow.setVisible(true);
    }
    public void hideBackgroundShadow(){
        backgroundShadow.setVisible(false);
    }
    //endregion
}
