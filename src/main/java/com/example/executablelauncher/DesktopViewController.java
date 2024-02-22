package com.example.executablelauncher;

import com.example.executablelauncher.entities.*;
import com.example.executablelauncher.tmdbMetadata.common.Genre;
import com.example.executablelauncher.tmdbMetadata.groups.*;
import com.example.executablelauncher.tmdbMetadata.images.Backdrop;
import com.example.executablelauncher.tmdbMetadata.images.Images;
import com.example.executablelauncher.tmdbMetadata.images.Logo;
import com.example.executablelauncher.tmdbMetadata.images.Poster;
import com.example.executablelauncher.tmdbMetadata.movies.MovieMetadata;
import com.example.executablelauncher.tmdbMetadata.series.EpisodeMetadata;
import com.example.executablelauncher.tmdbMetadata.series.SeasonMetadata;
import com.example.executablelauncher.tmdbMetadata.series.SeasonMetadataBasic;
import com.example.executablelauncher.tmdbMetadata.series.SeriesMetadata;
import com.example.executablelauncher.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbTvEpisodes;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesktopViewController {
    //region FXML ATTRIBUTES
    @FXML
    private Label elementsSelectedText;

    @FXML
    private VBox libraryMenu;

    @FXML
    private Button editLibraryButton;

    @FXML
    private Button searchFilesButton;

    @FXML
    private Button removeLibraryButton;

    @FXML
    private Button deleteSelectedButton;

    @FXML
    private Button deselectAllButton;

    @FXML
    private Button selectAllButton;

    @FXML
    private Button identificationMovie;

    @FXML
    private Button identificationShow;

    @FXML
    private Button changeEpisodesGroup;

    @FXML
    private BorderPane selectionOptions;

    @FXML
    private ImageView backgroundShadow;

    @FXML
    private ChoiceBox<String> librarySelector;

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
    private ImageView globalBackgroundShadow2;

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
    private FlowPane seasonContainer;

    @FXML
    private HBox seasonLogoBox;

    @FXML
    private StackPane seasonInfoPane;

    @FXML
    private ImageView seasonLogo;

    @FXML
    private VBox seasonMenu;

    @FXML
    private Label seasonNumberText;

    @FXML
    private Label seasonNumberField;

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
    private VBox downloadingContentWindowStatic;

    @FXML
    private Label downloadingContentTextStatic;

    @FXML
    private BorderPane seasonBorderPane;
    //endregion

    //region ATTRIBUTES
    private final ImageViewPane seasonBackground = new ImageViewPane();
    private final ImageViewPane seasonBackgroundNoise = new ImageViewPane();
    private final ImageViewPane seriesBackgroundNoise = new ImageViewPane();

    private List<Library> libraries = new ArrayList<>();
    private List<String> librariesNames = new ArrayList<>();
    private List<Series> seriesList = new ArrayList<>();
    private List<Season> seasonList = new ArrayList<>();
    private List<Episode> episodeList = new ArrayList<>();
    private List<Button> seriesButtons = new ArrayList<>();
    private List<Button> seasonsButtons = new ArrayList<>();
    private Library currentLibrary = null;
    private Series selectedSeries = null;
    private Season selectedSeason = null;
    public Episode selectedEpisode = null;
    private List<Episode> selectedEpisodes = new ArrayList<>();
    private List<DiscController> discControllers = new ArrayList<>();
    private double ASPECT_RATIO = 16.0 / 9.0;
    private boolean acceptRemove = false;
    private static int numFilesToCheck = 0;
    //endregion

    //region THEMOVIEDB ATTRIBUTES
    TmdbApi tmdbApi;
    boolean movieMetadataToCorrect = false;
    boolean seriesMetadataToCorrect = false;
    boolean changeEpisodeGroup = false;
    //endregion

    public void initValues(){
        Stage stage = (Stage) mainBox.getScene().getWindow();

        librarySelector.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> selectLibrary(newValue));

        if (App.isInternetAvailable())
            tmdbApi = new TmdbApi("4b46560aff5facd1d9ede196ce7d675f");

        selectionOptions.setVisible(false);

        downloadingContentWindow.setVisible(false);
        downloadingContentWindowStatic.setVisible(false);

        menuParentPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> hideMenu());

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

        //region BACKGROUND REACTION TO WINDOW RESIZE
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
            double ratioHeight = stage.widthProperty().doubleValue() / ASPECT_RATIO;
            if (ratioHeight < newHeight.doubleValue()){
                scaleFactor = newHeight.doubleValue() / globalBackground.getImage().getHeight();
                globalBackground.setFitHeight(newHeight.doubleValue());
                globalBackground.setFitWidth(globalBackground.getImage().getWidth() * scaleFactor);
            }
        };

        stage.widthProperty().addListener(widthListener);
        stage.heightProperty().addListener(heightListener);
        //endregion

        mainBox.getScene().getWindow().setOnCloseRequest(e -> closeWindow());

        //Remove horizontal and vertical scroll
        //scrollModification(seasonScroll);
        //scrollModification(seriesScrollPane);

        menuParentPane.setVisible(false);
        mainMenu.setVisible(false);
        libraryMenu.setVisible(false);
        seriesMenu.setVisible(false);
        seasonMenu.setVisible(false);
        discMenu.setVisible(false);

        //Elements size
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        seriesScrollPane.setPrefHeight(screenHeight);
        seriesContainer.setPrefHeight(screenHeight);

        seasonsEpisodesBox.setPrefWidth(Integer.MAX_VALUE);

        seasonInfoPane.getChildren().add(0, seasonBackground);
        seasonInfoPane.getChildren().add(1, seasonBackgroundNoise);

        ImageView seasonNoise = new ImageView(new Image("file:resources/img/noise.png"));
        seasonNoise.setPreserveRatio(false);
        seasonNoise.setOpacity(0.03);
        seasonBackgroundNoise.setImageView(seasonNoise);

        seriesStack.prefHeightProperty().bind(seriesScrollPane.heightProperty());
        seriesStack.getChildren().add(0, seriesBackgroundNoise);

        ImageView seriesNoise = new ImageView(new Image("file:resources/img/noise.png"));
        seriesNoise.setPreserveRatio(false);
        seriesNoise.setOpacity(0.03);
        seriesBackgroundNoise.setImageView(seriesNoise);

        globalBackground.setPreserveRatio(true);

        globalBackgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackgroundShadow.setPreserveRatio(false);
        globalBackgroundShadow2.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackgroundShadow2.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackgroundShadow2.setPreserveRatio(false);

        noiseImage.setFitWidth(screenWidth);
        noiseImage.setFitHeight(screenHeight);
        noiseImage.setPreserveRatio(false);

        backgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        backgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        backgroundShadow.setPreserveRatio(false);

        identificationMovie.setDisable(true);
        identificationShow.setDisable(true);
        changeEpisodesGroup.setDisable(true);

        Platform.runLater(() -> {
            updateLibraries();
            updateLanguage();
        });
    }

    //region CONTENT
    public void updateLanguage(){
        librarySelector.getItems().clear();
        librarySelector.getItems().addAll(App.getCategoriesNames());

        if (!librarySelector.getItems().isEmpty()) {
            librarySelector.setValue(librarySelector.getItems().get(0));
            selectLibrary(librarySelector.getValue());
        }

        settingsButton.setText(App.buttonsBundle.getString("settings"));
        exitButton.setText(App.buttonsBundle.getString("eixtButton"));
        switchFSButton.setText(App.buttonsBundle.getString("switchToFullscreen"));
        removeColButton.setText(App.buttonsBundle.getString("removeButton"));
        removeSeasonButton.setText(App.buttonsBundle.getString("removeButton"));
        removeDiscButton.setText(App.buttonsBundle.getString("removeButton"));
        removeLibraryButton.setText(App.buttonsBundle.getString("removeButton"));
        editColButton.setText(App.buttonsBundle.getString("editButton"));
        editSeasonButton.setText(App.buttonsBundle.getString("editButton"));
        editDiscButton.setText(App.buttonsBundle.getString("editButton"));
        editLibraryButton.setText(App.buttonsBundle.getString("editButton"));
        deleteSelectedButton.setText(App.buttonsBundle.getString("removeButton"));
        selectAllButton.setText(App.buttonsBundle.getString("selectAllButton"));
        deselectAllButton.setText(App.buttonsBundle.getString("deselectAllButton"));
        identificationMovie.setText(App.textBundle.getString("correctIdentification"));
        identificationShow.setText(App.textBundle.getString("correctIdentification"));
        changeEpisodesGroup.setText(App.buttonsBundle.getString("changeEpisodesGroup"));
        detailsText.setText(App.textBundle.getString("details"));
        yearText.setText(App.textBundle.getString("year"));
        orderText.setText(App.textBundle.getString("order"));

        if (currentLibrary != null && currentLibrary.type.equals("Shows"))
            episodesText.setText(App.textBundle.getString("episodes"));
        else
            episodesText.setText(App.textBundle.getString("videos"));

        if (currentLibrary != null && currentLibrary.type.equals("Shows")){
            seasonNumberText.setText(App.textBundle.getString("seasonNumber"));
        }else{
            seasonNumberText.setText("");
        }

        searchFilesButton.setText(App.buttonsBundle.getString("searchFiles"));
    }
    public void updateLibraries(){
        libraries = App.getLibraries(false);
        libraries.sort(new Utils.LibraryComparator());

        librarySelector.getItems().clear();
        for (Library library : libraries) {
            librarySelector.getItems().add(library.getName());
            librariesNames.add(library.getName());
        }

        if (!libraries.isEmpty()){
            librarySelector.setValue(libraries.get(0).getName());
            selectLibrary(libraries.get(0).getName());
        }else{
            blankSelection();
        }
    }
    public void updateLibraries(String newName){
        libraries = App.getLibraries(false);
        libraries.sort(new Utils.LibraryComparator());

        librarySelector.getItems().clear();
        for (Library library : libraries) {
            librarySelector.getItems().add(library.getName());
            librariesNames.add(library.getName());
        }

        if (!libraries.isEmpty()){
            librarySelector.setValue(newName);
            selectLibrary(libraries.get(0).getName());
        }else{
            blankSelection();
        }
    }
    public void showSeries(){
        seriesButtons.clear();

        if (seriesList == null || seriesList.isEmpty()) {
            blankSelection();
            return;
        }

        for (Series series : seriesList)
            addSeriesCard(series);

        if (!seriesButtons.isEmpty()) {
            selectSeriesButton(seriesButtons.get(0));
        }
    }
    private void showSeasons() {
        seasonContainer.getChildren().clear();
        seasonsButtons.clear();

        for (Season season : seasonList) {
            Button seasonButton = createSeasonButton(season);
            seasonContainer.getChildren().add(seasonButton);
            seasonsButtons.add(seasonButton);
        }
    }
    private void showEpisodes(Season s) {
        discContainer.getChildren().clear();
        discControllers.clear();
        episodeList = s.getEpisodes();

        if (!episodeList.isEmpty()) {
            episodeList.sort(new Utils.EpisodeComparator().reversed());
            addEpisodeCardWithDelay(0);
        }
    }
    private void fillSeasonInfo() {
        selectedEpisodes.clear();
        selectionOptions.setVisible(false);

        Image i = new Image("file:" + "resources/img/backgrounds/" + selectedSeason.getId() + "/" + "background.png");
        ASPECT_RATIO = i.getWidth() / i.getHeight();
        globalBackground.setImage(i);
        ImageView img = new ImageView(new Image("file:" + "resources/img/backgrounds/" + selectedSeason.getId() + "/" + "transparencyEffect.png"));
        img.setPreserveRatio(true);
        seasonBackground.setImageView(img);
        fadeInTransition(globalBackground);
        fadeInTransition(seasonBackground.getImageView());

        //Fill info
        detailsText.setText(App.textBundle.getString("details"));
        yearText.setText(App.textBundle.getString("year"));
        orderText.setText(App.textBundle.getString("order"));

        if (currentLibrary.type.equals("Shows")) {
            episodesText.setText(App.textBundle.getString("episodes"));
            seasonNumberText.setText(App.textBundle.getString("seasonNumber"));
            seasonNumberField.setText(String.valueOf(selectedSeason.seasonNumber));

            if (selectedSeries.logoSrc.isEmpty()){
                setTextNoLogo();
            }else{
                seasonLogoBox.getChildren().remove(0);
                seasonLogo = new ImageView();
                File file = new File(selectedSeries.logoSrc);
                setLogoNoText(file);
            }
        }else {
            episodesText.setText(App.textBundle.getString("videos"));
            seasonNumberText.setText("");
            seasonNumberField.setText("");

            if (selectedSeason.getLogoSrc().isEmpty()){
                setTextNoLogo();
            }else{
                seasonLogoBox.getChildren().remove(0);
                seasonLogo = new ImageView();
                File file = new File(selectedSeason.getLogoSrc());
                setLogoNoText(file);
            }
        }

        yearField.setText(selectedSeason.getYear());
        orderField.setText(Integer.toString(selectedSeason.getOrder()));
        episodesField.setText(Integer.toString(selectedSeason.getEpisodes().size()));

        try{
            File file;
            if (!selectedSeries.getCoverSrc().isEmpty()){
                file = new File(selectedSeries.getCoverSrc());
            }else{
                file = new File("resources/img/DefaultPoster.png");
            }

            Image image = new Image(file.toURI().toURL().toExternalForm());
            seriesCover.setImage(image);
        } catch (MalformedURLException e) {
            System.err.println("Series cover not found");
        }
    }
    private void setLogoNoText(File file) {
        try{
            seasonLogo.setImage(new Image(file.toURI().toURL().toExternalForm()));
            seasonLogo.setFitHeight(300);
            seasonLogo.setFitWidth(600);
            seasonLogo.setPreserveRatio(true);
            seasonLogoBox.getChildren().add(seasonLogo);
        } catch (MalformedURLException e) {
            System.err.println("DesktopViewController: Logo not loaded");
        }
    }
    private void setTextNoLogo() {
        seasonLogoBox.getChildren().remove(0);
        Label seasonLogoText = new Label(selectedSeries.getName());
        seasonLogoText.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 42));
        seasonLogoText.setTextFill(Color.color(1, 1, 1));
        seasonLogoText.setEffect(new DropShadow());
        seasonLogoText.setPadding(new Insets(0, 0, 0, 15));
        seasonLogoBox.getChildren().add(seasonLogoText);
    }
    private Button createSeasonButton(Season season) {
        Button seasonButton = new Button(season.getName());
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

        return seasonButton;
    }
    private void addEpisodeCardWithDelay(int index) {
        if (index < episodeList.size()) {
            Episode episode = episodeList.get(index);
            addEpisodeCard(episode);

            index++;

            Duration delay = Duration.millis(10);

            int finalIndex = index;
            Timeline timeline = new Timeline(new KeyFrame(delay, event -> addEpisodeCardWithDelay(finalIndex)));
            timeline.play();
        }
    }
    //endregion

    //region UPDATE DATA
    public void refreshSeries(){
        seriesButtons.get(seriesList.indexOf(selectedSeries)).setText(selectedSeries.getName());

        File[] files = new File("resources/img/seriesCovers/" + selectedSeries.getId() + "/").listFiles();

        if (files != null){
            File posterDir = files[0];
            selectedSeries.coverSrc = "resources/img/seriesCovers/" + selectedSeries.getId() + "/" + posterDir.getName();
        }

        Series series = selectedSeries;
        selectedSeries = null;
        selectSeries(series);
    }
    public void refreshSeason(Season s){
        Platform.runLater(() -> {
            selectedSeason = null;
            selectSeason(s);
        });
    }
    //endregion

    //region SELECTION
    public void blankSelection(){
        seasonScroll.setVisible(false);
        globalBackground.setImage(new Image("file:resources/img/Background.png"));
        editLibraryButton.setDisable(true);
        searchFilesButton.setDisable(true);
        removeLibraryButton.setDisable(true);
    }
    public void selectLibrary(String catName){
        seriesContainer.getChildren().clear();
        currentLibrary = DataManager.INSTANCE.getLibrary(catName);

        DataManager.INSTANCE.currentLibrary = currentLibrary;

        if (currentLibrary == null)
            return;

        seriesList = currentLibrary.getSeries();
        seriesList.sort(new Utils.SeriesComparator());

        if (currentLibrary.type.equals("Shows")) {
            changeEpisodesGroup.setDisable(false);
            identificationShow.setDisable(false);
            identificationMovie.setDisable(true);
        }else {
            changeEpisodesGroup.setDisable(true);
            identificationMovie.setDisable(false);
            identificationShow.setDisable(true);
        }

        showSeries();
    }
    public void selectSeries(Series selectedSeries) {
        if (this.selectedSeries == selectedSeries) {
            return;
        }

        this.selectedEpisodes.clear();
        seasonScroll.setVisible(true);
        selectionOptions.setVisible(false);
        this.selectedSeries = selectedSeries;

        seasonList = selectedSeries.getSeasons();
        if (!seasonList.isEmpty()) {
            seasonList.sort(new Utils.SeasonComparator());
            showSeasons();
            selectedSeason = seasonList.get(0);
            selectSeasonButton(seasonsButtons.get(0));

            fillSeasonInfo();

            if (!selectedSeason.getEpisodes().isEmpty())
                showEpisodes(selectedSeason);
            else
                discContainer.getChildren().clear();
        } else {
            seasonScroll.setVisible(false);
        }
    }
    public void selectSeason(Season s) {
        if (selectedSeason != s){
            selectedSeason = s;

            fillSeasonInfo();

            if (!selectedSeason.getEpisodes().isEmpty()){
                showEpisodes(selectedSeason);
            }else{
                discContainer.getChildren().clear();
            }
        }
    }
    private void selectSeriesButton(Button btn){
        //Clear Selected Button
        for (Button b : seriesButtons){
            b.getStyleClass().clear();
            b.getStyleClass().add("desktopTextButton");
        }
        //Select current button
        btn.getStyleClass().clear();
        btn.getStyleClass().add("desktopButtonActive");

        if (seriesList.get(seriesButtons.indexOf(btn)) != selectedSeries)
            selectSeries(seriesList.get(seriesButtons.indexOf(btn)));
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

        selectSeason(seasonList.get(seasonsButtons.indexOf(btn)));
    }
    public boolean isDiscSelected(){
        return !selectedEpisodes.isEmpty();
    }
    //endregion

    //region EFFECTS AND MODIFICATIONS
    public void saveBackground(Season s, String imageToCopy){
        try{
            Files.createDirectories(Paths.get("resources/img/backgrounds/" + s.getId() + "/"));
        } catch (IOException e) {
            System.err.println("saveBackground: Directory could not be created");
        }

        //Clear old images
        File dir = new File("resources/img/backgrounds/" + s.getId());
        if (dir.exists()){
            try {
                deleteFile(s.getBackgroundSrc());
                deleteFile("resources/img/backgrounds/" + s.getId() + "/fullBlur.png");
                deleteFile("resources/img/backgrounds/" + s.getId() + "/transparencyEffect.png");
            } catch (IOException e) {
                System.err.println("EditSeasonController: Error removing old images");
            }
        }

        File destination = new File("resources/img/backgrounds/" + s.getId() + "/background.png");
        try {
            Files.copy(Paths.get(imageToCopy), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Background not copied");
        }

        s.backgroundSrc = "resources/img/backgrounds/" + s.getId() + "/background.png";

        setTransparencyEffect(s.getBackgroundSrc(), "resources/img/backgrounds/" + s.getId() + "/transparencyEffect.png");
        processBlurAndSave(s.getBackgroundSrc(), "resources/img/backgrounds/" + s.getId() + "/fullBlur.png");
    }
    private void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
    private void setTransparencyEffect(String src, String outputPath) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(src));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            BufferedImage blendedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < height; y++) {
                float opacity = 1.0f - ((float) y / (height / 1.15f));
                opacity = Math.min(1.0f, Math.max(0.0f, opacity));

                for (int x = 0; x < width; x++) {
                    java.awt.Color originalColor = new java.awt.Color(originalImage.getRGB(x, y), true);
                    int blendedAlpha = (int) (originalColor.getAlpha() * opacity);
                    java.awt.Color blendedColor = new java.awt.Color(originalColor.getRGB() & 0xFFFFFF | blendedAlpha << 24, true);

                    blendedImage.setRGB(x, y, blendedColor.getRGB());
                }
            }

            File outputFilePath = new File(outputPath);
            ImageIO.write(blendedImage, "png", outputFilePath);

            originalImage.flush();
            blendedImage.flush();
        } catch (IOException e) {
            System.err.println("setTransparencyEffect: error applying transparency effect to background");
        }
    }
    public static void processBlurAndSave(String imagePath, String outputFilePath) {
        Platform.runLater(() -> {
            try {
                Image originalImage = new Image(new File(imagePath).toURI().toURL().toExternalForm());

                // Apply GaussianBlur effect
                ImageView backgroundBlur = new ImageView(originalImage);
                GaussianBlur blur = new GaussianBlur();
                blur.setRadius(27);
                backgroundBlur.setEffect(blur);

                Image imageWithBlur = backgroundBlur.snapshot(null, null);

                // Crop and save a portion of the blurred image
                int cropX = (int) (imageWithBlur.getWidth() * 0.03);
                int cropY = (int) (imageWithBlur.getHeight() * 0.05);
                int cropWidth = (int) (imageWithBlur.getWidth() * 0.93);
                int cropHeight = (int) (imageWithBlur.getHeight() * 0.9);

                PixelReader reader = imageWithBlur.getPixelReader();
                WritableImage newImage = new WritableImage(reader, cropX, cropY, cropWidth, cropHeight);

                File outputFile = new File(outputFilePath);
                ImageIO.write(SwingFXUtils.fromFXImage(newImage, null), "png", outputFile);
            } catch (MalformedURLException e) {
                System.err.println("Image loading error");
            } catch (IOException e) {
                System.err.println("Image processing error");
            }
        });
    }
    private void fadeInTransition(ImageView imageV){
        //Fade In Transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), imageV);
        fadeIn.setFromValue(0.1);
        fadeIn.setToValue(1.0);
        fadeIn.play();
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
    //endregion

    //region PLAY EPISODE
    public void playEpisode(Episode episode) {
        //Check if the video file exists before showing the video player
        File videoFile = new File(episode.getVideoSrc());

        if (!videoFile.isFile()){
            App.showErrorMessage(App.textBundle.getString("playbackError"), "", App.textBundle.getString("videoErrorMessage"));
            return;
        }

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("windows")) {
            System.out.println("El sistema operativo es Windows.");
        } else if (osName.contains("linux")) {
            System.out.println("El sistema operativo es Linux.");
        }

        File player = new File("resources/lib/mpvnet.exe");

        try {
            ProcessBuilder pBuilder = new ProcessBuilder(player.getAbsolutePath(),"--save-position-on-quit", episode.getVideoSrc());

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
    public String setRuntime(int runtime){
        int h = runtime / 60;
        int m = runtime % 60;

        if (h == 0)
            return (m + "m");

        return (h + "h " + m + "m");
    }
    //endregion

    //region EPISODE SELECTION
    public void selectDisc(Episode episode){
        if (selectedEpisodes.contains(episode)) {
            selectedEpisodes.remove(episode);
        }else{
            selectedEpisodes.add(episode);
        }

        selectAllButton.setVisible(selectedEpisodes.size() != episodeList.size());
        selectionOptions.setVisible(!selectedEpisodes.isEmpty());

        if (selectedEpisodes.size() == 1)
            elementsSelectedText.setText(App.textBundle.getString("oneSelected"));
        else
            elementsSelectedText.setText(selectedEpisodes.size() + " " + App.textBundle.getString("selectedDiscs"));
    }
    @FXML
    void deleteSelected(ActionEvent event) {
        for (Episode episode : selectedEpisodes){
            removeEpisode(episode);
        }
        selectedEpisodes.clear();
        selectedEpisode = null;
        selectionOptions.setVisible(false);
    }

    @FXML
    void deselectAll(ActionEvent event) {
        for (DiscController d : discControllers){
            d.clearSelection();
        }
        selectionOptions.setVisible(false);
        selectedEpisodes.clear();
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
    @FXML
    private void closeWindow(){
        App.close();
    }
    /*@FXML
    void close(MouseEvent event) {
        closeWindow();
    }
    @FXML
    void maximizeWindow(MouseEvent event) {
        Stage stage = (Stage)((Button) event.getSource()).getScene().getWindow();

        if (stage.isMaximized())
            maximizeRestoreImage.setImage(new Image("file:resources/img/icons/windowMaximize.png"));
        else
            maximizeRestoreImage.setImage(new Image("file:resources/img/icons/windowRestore.png"));

        stage.setMaximized(!stage.isMaximized());
    }
    @FXML
    void minimizeWindow(MouseEvent event) {
        ((Stage)((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }*/
    //endregion

    //region IDENTIFICATION
    @FXML
    void correctIdentificationShow(ActionEvent event){
        showBackgroundShadow();
        hideMenu();

        seriesMetadataToCorrect = false;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchSeries.fxml"));
            Parent root1 = fxmlLoader.load();
            SearchSeriesController controller = fxmlLoader.getController();
            controller.initiValues(this, selectedSeries.name, true, tmdbApi, currentLibrary.language);
            Stage stage = new Stage();
            stage.setTitle("Correct Identification");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
            stage.showAndWait();

            hideBackgroundShadow();

            if (seriesMetadataToCorrect)
                correctIdentificationShow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void correctIdentificationMovie(ActionEvent event){
        showBackgroundShadow();
        hideMenu();

        movieMetadataToCorrect = false;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchSeries.fxml"));
            Parent root1 = fxmlLoader.load();
            SearchSeriesController controller = fxmlLoader.getController();
            controller.initiValues(this, selectedSeries.name, false, tmdbApi, currentLibrary.language);
            Stage stage = new Stage();
            stage.setTitle("Correct Identification");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
            stage.showAndWait();

            hideBackgroundShadow();

            if (movieMetadataToCorrect)
                correctIdentificationMovie();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void correctIdentificationShow(){
        showBackgroundShadow();
        downloadingContentTextStatic.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindowStatic.setVisible(true);
        Task<Void> correctIS = new Task<>() {
            @Override
            protected Void call() {
                File folder = new File(selectedSeries.getFolder());
                File[] filesInFolder = folder.listFiles();

                if (filesInFolder == null)
                    return null;

                //Remove Series Data
                DataManager.INSTANCE.deleteSeriesData(selectedSeries);
                selectedSeries.seasons.clear();

                //Scan Series
                scanTVShow(folder, filesInFolder, true);

                if (selectedSeries.getSeasons().isEmpty()) {
                    return null;
                }

                Season season = selectedSeries.getSeasons().get(0);

                //Process background music
                List<YoutubeVideo> results = searchYoutube(season.name + " main theme");

                if (results != null)
                    downloadMedia(season, results.get(0).watch_url);

                return null;
            }
        };

        correctIS.setOnSucceeded(e -> {
            downloadingContentWindowStatic.setVisible(false);
            hideBackgroundShadow();
            refreshSeries();
        });
        correctIS.setOnCancelled(e -> {
            downloadingContentWindowStatic.setVisible(false);
            hideBackgroundShadow();
            refreshSeries();
        });
        correctIS.setOnFailed(e -> {
            downloadingContentWindowStatic.setVisible(false);
            hideBackgroundShadow();
            refreshSeries();
        });

        new Thread(correctIS).start();
    }
    public void correctIdentificationMovie(){
        showBackgroundShadow();
        downloadingContentTextStatic.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindowStatic.setVisible(true);
        Task<Void> correctIM = new Task<>() {
            @Override
            protected Void call() {
                MovieMetadata metadata = downloadMovieMetadata(selectedSeason.themdbID);

                if (metadata == null)
                    return null;

                selectedSeason.name = metadata.title;
                selectedSeason.overview = metadata.overview;
                selectedSeason.year = metadata.release_date.substring(0, metadata.release_date.indexOf("-"));
                selectedSeason.score = (float) ((int) (metadata.vote_average * 10.0)) / 10.0f;
                selectedSeason.imdbID = metadata.imdb_id;

                if (selectedSeason.imdbID == null)
                    selectedSeason.imdbID = "";

                Images images = downloadImages(selectedSeason.themdbID);

                if (images != null)
                    loadImages(selectedSeason.getId(), images, selectedSeason.themdbID, false);

                File posterDir = new File("resources/img/seriesCovers/" + selectedSeason.getId() + "/0.png");
                if (posterDir.exists()){
                    selectedSeason.coverSrc = "resources/img/seriesCovers/" + selectedSeason.getId() + "/0.png";
                }

                downloadLogos(selectedSeries, selectedSeason, selectedSeason.themdbID);
                saveBackground(selectedSeason, "resources/img/DownloadCache/" + selectedSeason.themdbID + ".png");

                //Process background music
                List<YoutubeVideo> results = searchYoutube(selectedSeason.name + " main theme");

                if (results != null)
                    downloadMedia(selectedSeason, results.get(0).watch_url);

                if (selectedSeason.getEpisodes().size() == 1){
                    Episode episode = selectedSeason.getEpisodes().get(0);
                    episode.name = selectedSeason.getName();
                }

                for (Episode episode : selectedSeason.getEpisodes()){
                    if (episode == null)
                        continue;

                    episode.score = selectedSeason.score;
                    episode.overview = selectedSeason.getOverview();
                    episode.year = selectedSeason.getYear();
                    episode.seasonNumber = selectedSeason.getSeasonNumber();

                    if (!selectedSeason.imdbID.isEmpty())
                        setIMDBScore(selectedSeason.imdbID, episode);

                    setMovieThumbnail(episode, selectedSeason.themdbID);
                }

                refreshSeason(selectedSeason);
                return null;
            }
        };

        correctIM.setOnSucceeded(e -> {
            downloadingContentWindowStatic.setVisible(false);
            hideBackgroundShadow();
        });
        correctIM.setOnCancelled(e -> {
            downloadingContentWindowStatic.setVisible(false);
            hideBackgroundShadow();
        });
        correctIM.setOnFailed(e -> {
            downloadingContentWindowStatic.setVisible(false);
            hideBackgroundShadow();
        });

        new Thread(correctIM).start();
    }
    public void setCorrectIdentificationShow(int newID){
        seriesMetadataToCorrect = true;
        selectedSeries.themdbID = newID;
    }
    public void setCorrectIdentificationMovie(int newID){
        movieMetadataToCorrect = true;
        selectedSeason.themdbID = newID;
    }
    //endregion

    //region CHANGE EPISODES GROUP
    @FXML
    void searchEpisodesGroup(){
        showBackgroundShadow();
        hideMenu();

        seriesMetadataToCorrect = false;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchEpisodesGroup.fxml"));
            Parent root1 = fxmlLoader.load();
            SearchEpisodesGroupController controller = fxmlLoader.getController();
            controller.initiValues(this, selectedSeries.themdbID);
            Stage stage = new Stage();
            stage.setTitle("Change Episodes Group");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
            stage.showAndWait();

            hideBackgroundShadow();

            if (changeEpisodeGroup)
                changeEpisodesGroup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void changeEpisodesGroup(String id){
        selectedSeries.setEpisodeGroupID(id);
        changeEpisodeGroup = true;
    }
    public void changeEpisodesGroup(){
        showBackgroundShadow();
        downloadingContentTextStatic.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindowStatic.setVisible(true);
        Task<Void> changeEG = new Task<>() {
            @Override
            protected Void call() {
                File folder = new File(selectedSeries.getFolder());
                File[] filesInFolder = folder.listFiles();

                if (filesInFolder == null)
                    return null;

                //Remove Seasons
                for (Season season : selectedSeries.getSeasons())
                    DataManager.INSTANCE.deleteSeasonData(season);
                selectedSeries.seasons.clear();

                //Scan Series
                scanTVShow(folder, filesInFolder, false);

                if (selectedSeries.getSeasons().isEmpty()) {
                    return null;
                }

                Season season = selectedSeries.getSeasons().get(0);

                //Process background music
                List<YoutubeVideo> results = searchYoutube(season.name + " main theme");

                if (results != null)
                    downloadMedia(season, results.get(0).watch_url);

                return null;
            }
        };

        changeEG.setOnSucceeded(e -> {
            downloadingContentWindowStatic.setVisible(false);
            changeEpisodeGroup = false;
            hideBackgroundShadow();
            refreshSeries();
        });
        changeEG.setOnCancelled(e -> {
            downloadingContentWindowStatic.setVisible(false);
            changeEpisodeGroup = false;
            hideBackgroundShadow();
            refreshSeries();
        });
        changeEG.setOnFailed(e -> {
            downloadingContentWindowStatic.setVisible(false);
            changeEpisodeGroup = false;
            hideBackgroundShadow();
            refreshSeries();
        });

        new Thread(changeEG).start();
    }
    //endregion

    //region AUTOMATED FILE SEARCH
    @FXML
    public void searchFiles(){
        if (currentLibrary == null)
            return;

        if (!App.isConnectedToInternet) {
            App.showErrorMessage(App.textBundle.getString("connectionErrorTitle"), "", App.textBundle.getString("connectionErrorMessage"));
            return;
        }

        hideMenu();
        downloadingContentText.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindow.setVisible(true);

        //Disable library and fullscreen buttons
        topBar.setDisable(true);

        List<Series> series = List.copyOf(currentLibrary.getSeries());
        for (Series show : series){
            List<Season> seasons = List.copyOf(show.getSeasons());
            for (Season s : seasons){
                List<Episode> episodes = List.copyOf(s.getEpisodes());
                for (Episode episode : episodes){
                    File file = new File(episode.getVideoSrc());

                    if (!file.exists()) {
                        if (selectedSeason == s)
                            discContainer.getChildren().remove(episodeList.indexOf(episode));

                        s.removeEpisode(episode);
                        DataManager.INSTANCE.deleteEpisodeData(episode);
                    }
                }

                if (s.getEpisodes().isEmpty()){
                    if (selectedSeries == show)
                        seasonContainer.getChildren().remove(seasonList.indexOf(s));

                    show.removeSeason(s);
                    DataManager.INSTANCE.deleteSeasonData(s);
                }
            }

            if (show.getSeasons().isEmpty()){
                if (selectedSeries == show)
                    seriesContainer.getChildren().remove(seriesList.indexOf(show));

                seriesList.remove(show);
                currentLibrary.removeSeries(show);
                DataManager.INSTANCE.deleteSeriesData(show);
            }
        }

        List<String> folders = currentLibrary.folders;

        for (String folderSrc : folders){
            File folder = new File(folderSrc);

            if (!folder.exists())
                return;

            File[] files = folder.listFiles();

            if (files == null)
                return;

            List<File> filesList = Arrays.stream(files).toList();

            int totalSize = filesList.size();
            int midElement = totalSize / 2;

            if (totalSize <= 1){
                numFilesToCheck = 1;
                loadHalfTask(filesList, 0, totalSize);
            }else{
                numFilesToCheck = 2;
                loadHalfTask(filesList, 0, midElement);
                loadHalfTask(filesList, midElement, totalSize);
            }
        }
    }
    public void loadLibrary(Library library){
        librarySelector.getItems().add(library.getName());
        librarySelector.setValue(library.getName());

        currentLibrary = library;

        seasonScroll.setVisible(false);
        editLibraryButton.setDisable(false);
        searchFilesButton.setDisable(false);
        removeLibraryButton.setDisable(false);

        searchFiles();
    }
    private void loadHalfTask(List<File> files, int start, int end){
        Task<Void> loadHalfTask = new Task<>() {
            @Override
            protected Void call() {
                for (int i = start; i < end; i++){
                    File file = files.get(i);

                    if (currentLibrary.type.equals("Shows")) {
                        if (file.isFile())
                            continue;

                        File[] filesInDir = file.listFiles();
                        if (filesInDir == null)
                            continue;

                        scanTVShow(file, filesInDir, false);
                    }else {
                        scanMovie(file);
                    }
                }
                return null;
            }
        };

        loadHalfTask.setOnSucceeded(e -> {
            numFilesToCheck--;
            if (numFilesToCheck == 0) {
                downloadingContentWindow.setVisible(false);
                downloadDefaultMusic();
            }
        });

        loadHalfTask.setOnCancelled(e -> {
            numFilesToCheck--;
            if (numFilesToCheck == 0) {
                downloadingContentWindow.setVisible(false);
                downloadDefaultMusic();
            }
        });

        loadHalfTask.setOnFailed(e -> {
            numFilesToCheck--;
            if (numFilesToCheck == 0) {
                downloadingContentWindow.setVisible(false);
                downloadDefaultMusic();
            }
        });

        new Thread(loadHalfTask).start();
    }
    public void downloadDefaultMusic(){
        Task<Void> musicDownloadTask = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    downloadingContentText.setText(App.textBundle.getString("downloadingMusicMessage"));
                    downloadingContentWindow.setVisible(true);
                });

                for (Series series : currentLibrary.getSeries()){
                    if (series == null)
                        continue;

                    Season season = series.getSeasons().get(0);

                    if (!season.musicSrc.isEmpty())
                        continue;

                    List<YoutubeVideo> results = searchYoutube(series.name + " main theme");

                    if (results != null)
                        downloadMedia(season, results.get(0).watch_url);
                }
                return null;
            }
        };

        musicDownloadTask.setOnSucceeded(e -> {
            //Enable library and fullscreen buttons
            topBar.setDisable(false);
            downloadingContentWindow.setVisible(false);
        });

        musicDownloadTask.setOnCancelled(e -> {
            //Enable library and fullscreen buttons
            topBar.setDisable(false);
            downloadingContentWindow.setVisible(false);
        });

        musicDownloadTask.setOnFailed(e -> {
            //Enable library and fullscreen buttons
            topBar.setDisable(false);
            downloadingContentWindow.setVisible(false);
        });

        new Thread(musicDownloadTask).start();
    }
    private void scanTVShow(File directory, File[] filesInDir, boolean updateMetadata){
        //All video files in directory (not taking into account two subdirectories ahead, like Series/Folder1/Folder2/File)
        List<File> videoFiles = new ArrayList<>();

        //Analyze all files in directory except two subdirectories ahead
        for (File file : filesInDir){
            if (file.isDirectory()){
                File[] filesInDirectory = file.listFiles();

                if (filesInDirectory == null)
                    continue;

                for (File f : filesInDirectory){
                    if (f.isFile() && validVideoFile(f))
                        videoFiles.add(f);
                }
            }else{
                if (validVideoFile(file))
                    videoFiles.add(file);
            }
        }

        if (videoFiles.isEmpty())
            return;

        //region CREATE/EDIT SERIES
        Series series;
        boolean exists = false;

        if (currentLibrary.analyzedFolders.get(directory.getAbsolutePath()) != null){
            series = currentLibrary.getSeries(currentLibrary.analyzedFolders.get(directory.getAbsolutePath()));
            exists = true;
        }else{
            series = new Series();
            series.folder = directory.getAbsolutePath();
            currentLibrary.analyzedFolders.put(directory.getAbsolutePath(), series.getId());
        }

        int themdbID = series.themdbID;

        //This means that this is a new Folder to be analyzed, so we need to search for the show metadata
        if (themdbID == -1){
            String finalName = directory.getName();

            //Remove parenthesis from folder name
            String nameWithoutParenthesis = finalName.replaceAll("[()]", "");

            //Pattern to extract name and year
            Pattern pattern = Pattern.compile("^(.*?)(?:\\s(\\d{4}))?$");

            //Get name and year
            String year = "";
            Matcher matcher = pattern.matcher(nameWithoutParenthesis);
            if (matcher.matches()) {
                finalName = matcher.group(1);
                year = matcher.group(2);
            }

            if (year == null)
                year = "1";

            //Search show by name of the folder
            TvResultsPage tvResults = tmdbApi.getSearch().searchTv(finalName, Integer.valueOf(year), currentLibrary.language, true, 1);

            if (tvResults.getTotalResults() == 0)
                return;

            //Set themdbID as the id of the first result
            themdbID = tvResults.getResults().get(0).getId();
            series.themdbID = themdbID;

            //Set values in order to set the attributes from the search result in the new series and download images
            updateMetadata = true;
        }
        SeriesMetadata seriesMetadata = downloadSeriesMetadata(themdbID);

        if (seriesMetadata == null)
            return;

        if (updateMetadata)
            setSeriesMetadataAndImages(series, seriesMetadata, updateMetadata);
        //endregion

        //Download metadata for each season of the show
        List<SeasonMetadata> seasonsMetadata = new ArrayList<>();
        for (SeasonMetadataBasic seasonMetadataBasicBasic : seriesMetadata.seasons){
            SeasonMetadata seasonMetadata = downloadSeasonMetadata(series.themdbID, seasonMetadataBasicBasic.season_number);

            if (seasonMetadata != null) {
                seasonsMetadata.add(seasonMetadata);
            }
        }

        //Download Episode Group Metadata
        SeasonsGroupMetadata episodesGroup = null;
        if (!series.getEpisodeGroupID().isEmpty())
            episodesGroup = getEpisodesGroup(series);

        //Process each episode
        for (File video : videoFiles){
            if (currentLibrary.analyzedFiles.get(video.getAbsolutePath()) != null)
                continue;

            processEpisode(series, video, seasonsMetadata, episodesGroup);
        }

        //Change the name of every season to match the Episodes Group
        if (!series.getEpisodeGroupID().isEmpty() && episodesGroup != null){
            for (Season season : series.getSeasons()){
                for (EpisodeGroup episodeGroup : episodesGroup.groups){
                    if (episodeGroup.order == season.getSeasonNumber()){
                        season.setName(episodeGroup.name);
                    }
                }
            }
        }

        if (exists)
            updateSeries(series);
        else
            addSeries(series);
    }
    private void setSeriesMetadataAndImages(Series series, SeriesMetadata seriesMetadata, boolean downloadImages){
        //Set metadata
        series.name = seriesMetadata.name;
        series.overview = seriesMetadata.overview;
        series.score = (float) ((int) (seriesMetadata.vote_average * 10.0)) / 10.0f;
        series.playSameMusic = true;

        String year = seriesMetadata.first_air_date.substring(0, seriesMetadata.first_air_date.indexOf("-")) + "-";
        if (!seriesMetadata.in_production)
            year += seriesMetadata.last_air_date.substring(0, seriesMetadata.first_air_date.indexOf("-"));
        series.year = year;

        series.numberOfSeasons = seriesMetadata.number_of_seasons;
        series.numberOfEpisodes = seriesMetadata.number_of_episodes;

        List<Genre> genres = seriesMetadata.genres;

        if (genres != null){
            List<String> genreList = new ArrayList<>();
            for (Genre genre : genres){
                genreList.add(genre.name);
            }

            series.genres = genreList;
        }

        //Download posters and logos for the show
        if (downloadImages) {
            Images images = downloadImages(series.themdbID);

            if (images != null)
                loadImages(series.getId(), images, series.themdbID, false);

            File[] files = new File("resources/img/seriesCovers/" + series.getId() + "/").listFiles();

            if (files != null){
                File posterDir = files[0];
                series.coverSrc = "resources/img/seriesCovers/" + series.getId() + "/" + posterDir.getName();
            }
        }
    }
    private void loadImages(String id, Images images, int tmdbID, boolean onlyPosters){
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        //region Process Posters
        List<Poster> posterList = images.posters;
        if (!posterList.isEmpty()){
            Image originalImage = new Image(imageBaseURL + posterList.get(0).file_path);
            saveCover(id, 0, originalImage);

            Task<Void> posterTask = new Task<>() {
                @Override
                protected Void call() {
                    int processedFiles = 1;
                    for (int i = processedFiles; i < posterList.size(); i++){
                        if (processedFiles == 19)
                            break;

                        Image originalImage = new Image(imageBaseURL + posterList.get(i).file_path);

                        saveCover(id, processedFiles, originalImage);

                        processedFiles++;
                    }
                    return null;
                }
            };

            new Thread(posterTask).start();
        }
        //endregion

        //region Process Background
        if (!onlyPosters){
            List<Backdrop> backdropList = images.backdrops;
            if (!backdropList.isEmpty()){
                String path;
                if (currentLibrary.type.equals("Shows"))
                    path = backdropList.get(0).file_path;
                else
                    path = backdropList.get(backdropList.size() - 1).file_path;

                Image originalImage = new Image(imageBaseURL + path);

                try{
                    Files.createDirectory(Path.of("resources/img/DownloadCache/"));
                } catch (IOException e) {
                    System.err.println("loadImages: Error creating directory");
                }

                if (!originalImage.isError()){
                    File file;
                    file = new File("resources/img/DownloadCache/" + tmdbID + ".png");

                    try{
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(originalImage, null);
                        ImageIO.write(renderedImage,"png", file);
                    } catch (IOException e) {
                        System.err.println("DesktopViewController: Downloaded background not saved");
                    }
                }
            }
        }
        //endregion
    }
    private SeasonsGroupMetadata getEpisodesGroup(Series series){
        String seasonGroupID = "";
        try{
            if (series.getEpisodeGroupID().isEmpty()){
                //region GET EPISODE GROUPS
                OkHttpClient client = new OkHttpClient();
                Request requestGroups = new Request.Builder()
                        .url("https://api.themoviedb.org/3/tv/" + series.getThemdbID() + "/episode_groups")
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                        .build();

                Response response = client.newCall(requestGroups).execute();


                if (response.isSuccessful()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    assert response.body() != null;
                    SeasonsGroupRoot groups = objectMapper.readValue(response.body().string(), SeasonsGroupRoot.class);


                    for (SeasonsGroup seasonsGroup : groups.results) {
                        if (seasonsGroup.type == 6) {                                       //6 == Seasons
                            seasonGroupID = seasonsGroup.id;
                        }else if (seasonGroupID.isEmpty() && seasonsGroup.type == 5)        //5 == Sagas
                            seasonGroupID = seasonsGroup.id;
                    }

                    if (seasonGroupID.isEmpty()){
                        for (SeasonsGroup seasonsGroup : groups.results) {
                            if (seasonsGroup.name.equals("All episodes")) {
                                seasonGroupID = seasonsGroup.id;
                            }
                        }
                    }
                } else {
                    System.out.println("getEpisodeGroup: Response not successful: " + response.code());
                }
                //endregion
            }else{
                seasonGroupID = series.getEpisodeGroupID();
            }

            //region GET EPISODE GROUP DETAILS
            if (!seasonGroupID.isEmpty()){
                OkHttpClient client = new OkHttpClient();
                Request requestGroup = new Request.Builder()
                        .url("https://api.themoviedb.org/3/tv/episode_group/" + seasonGroupID)
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                        .build();

                Response response = client.newCall(requestGroup).execute();

                if (response.isSuccessful()) {
                    ObjectMapper objectMapper = new ObjectMapper();

                    assert response.body() != null;
                    return objectMapper.readValue(response.body().string(), SeasonsGroupMetadata.class);
                } else {
                    System.out.println("getEpisodeGroup: Response not successful: " + response.code());
                }
            }
            //endregion
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    private boolean validVideoFile(File file){
        String videoExtension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
        videoExtension = videoExtension.toLowerCase();

        return videoExtension.equals(".mkv") || videoExtension.equals(".mp4") || videoExtension.equals(".avi") || videoExtension.equals(".mov")
                || videoExtension.equals(".wmv") || videoExtension.equals(".mpeg") || videoExtension.equals(".m2ts");
    }
    private void processEpisode(Series series, File file, List<SeasonMetadata> seasonsMetadata, SeasonsGroupMetadata episodesGroup){
        //Name of the file without the extension
        String fullName = file.getName().substring(0, file.getName().lastIndexOf("."));

        //Regular expressions for identifying season and/or episode numbers in the file name
        final String regexSeasonEpisode = "(?i)(?<season>S[0-9]{1,3}+)(?<episode>E[0-9]{1,5})";
        final String regexOnlyEpisode = "(?i)(?<episode>[0-9]{1,5})";

        final Pattern pattern = Pattern.compile(regexSeasonEpisode, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(fullName);

        //SeasonMetadataBasic and episode metadata to find for the current file
        SeasonMetadata seasonMetadata = null;
        EpisodeMetadata episodeMetadata = null;

        int realSeason = 0;
        int realEpisode = -1;

        //If episode name only includes episode number
        if (!matcher.find()){
            Pattern newPattern = Pattern.compile(regexOnlyEpisode, Pattern.MULTILINE);
            Matcher newMatch = newPattern.matcher(fullName);

            //return if the second regular expression did not find match
            if (!newMatch.find())
                return;

            int absoluteNumber = Integer.parseInt(newMatch.group("episode"));
            int episodeNumber = 1;
            boolean episodeFound = false;

            //Find the real season and episode for the file
            for (SeasonMetadata season : seasonsMetadata){
                if (season.season_number >= 1){
                    if ((episodeNumber + season.episodes.size()) < absoluteNumber) {
                        episodeNumber += season.episodes.size();
                        continue;
                    }

                    for (int i = 0; i < season.episodes.size(); i++){
                        if (episodeNumber == absoluteNumber) {
                            episodeMetadata = season.episodes.get(i);
                            seasonMetadata = season;
                            episodeFound = true;
                            break;
                        }
                        episodeNumber++;
                    }
                }

                if (episodeFound)
                    break;
            }

            if (!episodeFound)
                return;
        }else{
            int seasonNumber = Integer.parseInt(matcher.group("season").substring(1));
            int episodeNumber = Integer.parseInt(matcher.group("episode").substring(1));

            realSeason = seasonNumber;
            realEpisode = episodeNumber;

            boolean toFindMetadata = true;
            for (SeasonMetadata season : seasonsMetadata){
                if (season.season_number == seasonNumber) {
                    toFindMetadata = false;
                    break;
                }
            }

            if (toFindMetadata || !series.getEpisodeGroupID().isEmpty()){
                if (episodesGroup == null)
                    episodesGroup = getEpisodesGroup(series);

                boolean found = false;
                if (episodesGroup != null){
                    for (EpisodeGroup episodeGroup : episodesGroup.groups){
                        if (episodeGroup.order != seasonNumber)
                            continue;

                        for (SeasonsGroupEpisode episode : episodeGroup.episodes){
                            if ((episode.order + 1) == episodeNumber){
                                seasonNumber = episode.season_number;
                                episodeNumber = episode.episode_number;
                                found = true;
                                break;
                            }
                        }

                        if (found)
                            break;
                    }

                    for (SeasonMetadata seasonMeta : seasonsMetadata){
                        if (seasonMeta.season_number == seasonNumber){
                            seasonMetadata = seasonMeta;

                            for (EpisodeMetadata episodeMeta : seasonMeta.episodes){
                                if (episodeMeta.episode_number == episodeNumber){
                                    episodeMetadata = episodeMeta;
                                    break;
                                }
                            }

                            break;
                        }
                    }
                }else{
                    return;
                }
            }else{
                for (SeasonMetadata seasonMeta : seasonsMetadata){
                    if (seasonMeta.season_number == seasonNumber){
                        seasonMetadata = seasonMeta;

                        for (EpisodeMetadata episodeMeta : seasonMeta.episodes){
                            if (episodeMeta.episode_number == episodeNumber){
                                episodeMetadata = episodeMeta;
                                break;
                            }
                        }

                        break;
                    }
                }
            }
        }

        if (seasonMetadata == null || episodeMetadata == null)
            return;

        Season season;
        if (realEpisode != -1)
            season = series.getSeason(realSeason);
        else
            season = series.getSeason(seasonMetadata.season_number);

        if (season == null){
            season = new Season();
            series.addSeason(season);

            season.name = seasonMetadata.name;
            season.overview = seasonMetadata.overview;
            season.year = seasonMetadata.episodes.get(0).air_date.substring(0, seasonMetadata.episodes.get(0).air_date.indexOf("-"));

            if (realEpisode != -1)
                season.seasonNumber = realSeason;
            else
                season.seasonNumber = seasonMetadata.season_number;

            season.score = (float) ((int) (seasonMetadata.vote_average * 10.0)) / 10.0f;
            season.seriesID = series.getId();

            if (season.backgroundSrc.isEmpty() || season.backgroundSrc.equals("resources/img/DefaultBackground.png")) {
                File f = new File("resources/img/DownloadCache/" + series.themdbID + ".png");
                if (!f.exists() && series.getSeasons().size() > 1){
                    Season s = series.getSeasons().get(0);

                    if (s != null){
                        saveBackground(season, s.backgroundSrc);
                    }
                }else if (f.exists()){
                    saveBackground(season, "resources/img/DownloadCache/" + series.themdbID + ".png");
                }
            }

            if (series.seasons.size() == 1)
                downloadLogos(series, season, series.themdbID);

            if (season.seasonNumber == 0)
                season.order = 100;
        }

        Episode episode;
        if (realEpisode != -1)
            episode = season.getEpisode(realEpisode);
        else
            episode = season.getEpisode(episodeMetadata.episode_number);

        if (episode != null) {
            episode.videoSrc = file.getAbsolutePath();
            return;
        }

        episode = new Episode();
        season.addEpisode(episode);

        //Set the metadata for the new episode
        episode.seasonID = season.getId();
        episode.videoSrc = file.getAbsolutePath();
        setEpisodeData(episode, episodeMetadata, series, realEpisode);

        currentLibrary.analyzedFiles.put(file.getAbsolutePath(), episode.getId());
    }
    private void setEpisodeData(Episode episode, EpisodeMetadata episodeMetadata, Series show, int realEpisode){
        episode.name = episodeMetadata.name;
        episode.overview = episodeMetadata.overview;

        if (realEpisode != -1)
            episode.episodeNumber = realEpisode;
        else
            episode.episodeNumber = episodeMetadata.episode_number;

        episode.year = Utils.episodeDateFormat(episodeMetadata.air_date, currentLibrary.language);
        episode.score = (float) ((int) (episodeMetadata.vote_average * 10.0)) / 10.0f;
        episode.runtime = episodeMetadata.runtime;
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        try{
            Files.createDirectories(Paths.get("resources/img/discCovers/" + episode.getId() + "/"));
        } catch (IOException e) {
            System.err.println("setEpisodeData: Directory could not be created");
        }

        MovieImages images = tmdbApi.getTvEpisodes().getEpisode(show.themdbID, episodeMetadata.season_number, episodeMetadata.episode_number, null, TmdbTvEpisodes.EpisodeMethod.images).getImages();
        List<Artwork> thumbnails = images.getStills();

        List<String> thumbnailsUrls = new ArrayList<>();
        if (thumbnails != null){
            for (Artwork artwork : thumbnails){
                thumbnailsUrls.add(imageBaseURL + artwork.getFilePath());
            }
        }

        //region THUMBNAIL DOWNLOADER
        if (!thumbnailsUrls.isEmpty()){
            saveThumbnail(episode, thumbnailsUrls.get(0), 0);

            Task<Void> thumbnailTask = new Task<>() {
                @Override
                protected Void call() {
                    for (int i = 1; i < thumbnailsUrls.size(); i++){
                        saveThumbnail(episode, thumbnailsUrls.get(i), i);
                    }
                    return null;
                }
            };

            new Thread(thumbnailTask).start();
        }
        //endregion

        File img = new File("resources/img/discCovers/" + episode.getId() + "/0.png");
        if (!img.exists()){
            episode.imgSrc = "resources/img/Default_video_thumbnail.jpg";
        }else{
            episode.imgSrc = "resources/img/discCovers/" + episode.getId() + "/0.png";
        }
    }
    private void saveCover(String id, int i, Image originalImage) {
        try{
            Files.createDirectories(Paths.get("resources/img/seriesCovers/" + id + "/"));
        } catch (IOException e) {
            System.err.println("Directory could not be created");
        }

        if (!originalImage.isError()){
            try{
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);

                //Compress image
                BufferedImage resizedImage = Thumbnails.of(bufferedImage)
                        .size(376, 540)
                        .outputFormat("jpg")
                        .outputQuality(1)
                        .asBufferedImage();

                originalImage = SwingFXUtils.toFXImage(resizedImage, null);
                bufferedImage.flush();
                resizedImage.flush();
            } catch (IOException e) {
                System.err.println("DesktopViewController: Error compressing image");
            }

            File file = new File("resources/img/seriesCovers/" + id + "/" + i + ".png");
            try{
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(originalImage, null);
                ImageIO.write(renderedImage,"png", file);
            } catch (IOException e) {
                System.err.println("DesktopViewController: Downloaded cover not saved");
            }
        }
    }
    private void saveLogo(String id, int i, Image originalImage) {
        try{
            Files.createDirectories(Paths.get("resources/img/logos/" + id + "/"));
        } catch (IOException e) {
            System.err.println("Directory could not be created");
        }

        if (!originalImage.isError()){
            File file = new File("resources/img/logos/" + id + "/" + i + ".png");
            try{
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(originalImage, null);
                ImageIO.write(renderedImage,"png", file);
            } catch (IOException e) {
                System.err.println("DesktopViewController: Downloaded logo not saved");
            }
        }
    }
    private void scanMovie(File f){
        //If the movie exists, do not add a new Card in the main view
        boolean exists = false;

        if (!f.isDirectory()){
            //region MOVIE FILE ONLY
            if (!validVideoFile(f)){
                return;
            }

            if (currentLibrary.analyzedFiles.get(f.getAbsolutePath()) != null)
                return;

            //region CREATE/EDIT SERIES
            Series series = new Series();
            series.folder = f.getAbsolutePath();
            currentLibrary.analyzedFolders.put(f.getAbsolutePath(), series.getId());

            NameYearContainer result = extractNameAndYear(f.getName().substring(0, f.getName().lastIndexOf(".")));

            String movieName = result.name;
            String year = result.year;

            if (year.isEmpty())
                year = "1";

            //Search show by name of the folder
            int themdbID = searchFirstMovie(movieName, Integer.parseInt(year));

            if (themdbID == -1) {
                series.name = movieName;
                Season newSeason = new Season();
                series.addSeason(newSeason);
                newSeason.name = movieName;
                newSeason.year = year;
                newSeason.seriesID = series.getId();
                newSeason.seasonNumber = series.seasons.size();

                currentLibrary.analyzedFolders.put(f.getAbsolutePath(), series.getId());

                saveDiscWithoutMetadata(f, newSeason);
                addSeries(series);
                return;
            }

            MovieMetadata movieMetadata = downloadMovieMetadata(themdbID);

            series.name = movieMetadata.title;
            Season season = new Season();
            series.addSeason(season);
            season.folder = f.getAbsolutePath();
            season.seriesID = series.getId();
            season.themdbID = themdbID;
            season.imdbID = movieMetadata.imdb_id;

            if (season.imdbID == null)
                season.imdbID = "";

            season.name = movieMetadata.title;
            season.overview = movieMetadata.overview;
            season.year = movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-"));
            season.score = (float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f;
            season.seasonNumber = series.seasons.size() + 1;

            List<Genre> genres = movieMetadata.genres;

            if (genres != null){
                List<String> genreList = new ArrayList<>();
                for (Genre genre : genres){
                    genreList.add(genre.name);
                }

                season.genres = genreList;
            }

            currentLibrary.seasonFolders.put(f.getAbsolutePath(), season.getId());

            Images images = downloadImages(season.themdbID);

            if (images != null){
                loadImages(season.getId(), images, season.themdbID, false);
                loadImages(series.getId(), images, season.themdbID, true);
            }

            File posterDir = new File("resources/img/seriesCovers/" + season.getId() + "/0.png");
            if (posterDir.exists()){
                season.coverSrc = "resources/img/seriesCovers/" + season.getId() + "/0.png";
            }

            posterDir = new File("resources/img/seriesCovers/" + series.getId() + "/0.png");
            if (posterDir.exists()){
                series.coverSrc = "resources/img/seriesCovers/" + season.getId() + "/0.png";
            }

            downloadLogos(series, season, season.themdbID);
            saveBackground(season, "resources/img/DownloadCache/" + season.themdbID + ".png");

            processMovie(f, season, movieMetadata.runtime);

            addSeries(series);
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

            Series series;

            if (currentLibrary.analyzedFolders.get(f.getAbsolutePath()) != null) {
                series = currentLibrary.getSeries(currentLibrary.analyzedFolders.get(f.getAbsolutePath()));
                exists = true;

                if (series == null)
                    return;
            }else{
                series = new Series();
                series.name = f.getName();
                series.folder = f.getAbsolutePath();
                currentLibrary.analyzedFolders.put(f.getAbsolutePath(), series.getId());
            }

            if (!folders.isEmpty()){
                //region FOLDERS CORRESPONDING DIFFERENT MOVIES FROM A COLLECTION
                for (File folder : folders){
                    File[] filesInFolder = folder.listFiles();
                    if (filesInFolder == null)
                        continue;

                    NameYearContainer result = extractNameAndYear(folder.getName());

                    String movieName = result.name;
                    String year = result.year;

                    if (year.isEmpty())
                        year = "1";

                    boolean updateMetadata = false;

                    Season season;
                    if (currentLibrary.seasonFolders.get(folder.getAbsolutePath()) != null){
                        season = series.getSeason(currentLibrary.seasonFolders.get(folder.getAbsolutePath()));
                    }else {
                        season = new Season();
                        series.addSeason(season);
                        currentLibrary.seasonFolders.put(folder.getAbsolutePath(), season.getId());
                        season.seasonNumber = series.seasons.size() + 1;
                        season.seriesID = series.getId();
                        season.folder = folder.getAbsolutePath();

                        updateMetadata = true;
                    }

                    int themdbID = season.themdbID;
                    MovieMetadata movieMetadata = null;

                    if (themdbID == -1){
                        themdbID = searchFirstMovie(movieName, Integer.parseInt(year));
                    }

                    if (themdbID != -1)
                        movieMetadata = downloadMovieMetadata(themdbID);

                    season.themdbID = themdbID;

                    if (movieMetadata == null){
                        season.name = movieName;

                        for (File file : filesInFolder){
                            if (file.isFile() && validVideoFile(file))
                                saveDiscWithoutMetadata(file, season);
                        }

                        continue;
                    }

                    if (updateMetadata){
                        season.folder = f.getAbsolutePath();
                        season.seriesID = series.getId();

                        season.name = movieMetadata.title;
                        season.overview = movieMetadata.overview;
                        season.year = movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-"));
                        season.score = (float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f;
                        season.imdbID = movieMetadata.imdb_id;

                        if (season.imdbID == null)
                            season.imdbID = "";

                        List<Genre> genres = movieMetadata.genres;

                        if (genres != null){
                            List<String> genreList = new ArrayList<>();
                            for (Genre genre : genres){
                                genreList.add(genre.name);
                            }

                            season.genres = genreList;
                        }

                        Images images = downloadImages(season.themdbID);

                        if (images != null){
                            loadImages(season.getId(), images, season.themdbID, false);
                            loadImages(series.getId(), images, season.themdbID, true);
                        }

                        File posterDir = new File("resources/img/seriesCovers/" + season.getId() + "/0.png");
                        if (posterDir.exists()){
                            season.coverSrc = "resources/img/seriesCovers/" + season.getId() + "/0.png";
                        }

                        posterDir = new File("resources/img/seriesCovers/" + series.getId() + "/0.png");
                        if (posterDir.exists()){
                            series.coverSrc = "resources/img/seriesCovers/" + season.getId() + "/0.png";
                        }

                        downloadLogos(series, season, season.themdbID);
                        saveBackground(season, "resources/img/DownloadCache/" + season.themdbID + ".png");
                    }

                    for (File file : filesInFolder){
                        if (file.isFile() && validVideoFile(file))
                            processMovie(file, season, movieMetadata.runtime);
                    }
                }

                if (!exists)
                    addSeries(series);
                //endregion
            }else if (!filesInRoot.isEmpty()){
                //region MOVIE FILE/CONCERT FILES INSIDE FOLDER
                NameYearContainer result = extractNameAndYear(f.getName());

                String movieName = result.name;
                String year = result.year;

                if (year.isEmpty())
                    year = "1";

                boolean updateMetadata = false;

                Season season;
                if (currentLibrary.seasonFolders.get(f.getAbsolutePath()) != null){
                    season = series.getSeason(currentLibrary.seasonFolders.get(f.getAbsolutePath()));
                }else {
                    season = new Season();
                    series.addSeason(season);
                    currentLibrary.seasonFolders.put(f.getAbsolutePath(), season.getId());
                    season.seasonNumber = series.seasons.size() + 1;
                    season.seriesID = series.getId();
                    season.folder = f.getAbsolutePath();

                    updateMetadata = true;
                }

                int themdbID = season.themdbID;

                if (themdbID == -1){
                    themdbID = searchFirstMovie(movieName, Integer.parseInt(year));
                }

                MovieMetadata movieMetadata = null;

                if (themdbID != -1)
                    movieMetadata = downloadMovieMetadata(themdbID);

                season.themdbID = themdbID;

                if (movieMetadata == null){
                    season.name = movieName;

                    for (File file : filesInRoot){
                        if (file.isFile() && validVideoFile(file))
                            saveDiscWithoutMetadata(file, season);
                    }

                    if (!exists)
                        addSeries(series);
                    return;
                }

                if (updateMetadata){
                    season.folder = f.getAbsolutePath();
                    season.seriesID = series.getId();

                    season.name = movieMetadata.title;
                    season.overview = movieMetadata.overview;
                    season.year = movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-"));
                    season.score = (float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f;
                    season.imdbID = movieMetadata.imdb_id;

                    if (season.imdbID == null)
                        season.imdbID = "";

                    List<Genre> genres = movieMetadata.genres;

                    if (genres != null){
                        List<String> genreList = new ArrayList<>();
                        for (Genre genre : genres){
                            genreList.add(genre.name);
                        }

                        season.genres = genreList;
                    }

                    currentLibrary.seasonFolders.put(f.getAbsolutePath(), season.getId());

                    Images images = downloadImages(season.themdbID);

                    if (images != null){
                        loadImages(season.getId(), images, season.themdbID, false);
                        loadImages(series.getId(), images, season.themdbID, true);
                    }

                    File posterDir = new File("resources/img/seriesCovers/" + season.getId() + "/0.png");
                    if (posterDir.exists()){
                        season.coverSrc = "resources/img/seriesCovers/" + season.getId() + "/0.png";
                    }

                    posterDir = new File("resources/img/seriesCovers/" + series.getId() + "/0.png");
                    if (posterDir.exists()){
                        series.coverSrc = "resources/img/seriesCovers/" + season.getId() + "/0.png";
                    }

                    downloadLogos(series, season, season.themdbID);
                    saveBackground(season, "resources/img/DownloadCache/" + season.themdbID + ".png");
                }

                for (File file : filesInRoot){
                    if (file.isFile() && validVideoFile(file))
                        processMovie(file, season, movieMetadata.runtime);
                }

                if (!exists)
                    addSeries(series);
                //endregion
            }
        }
    }
    private static NameYearContainer extractNameAndYear(String source) {
        NameYearContainer container = new NameYearContainer("", "");

        //Remove parenthesis
        String cleanSource = source.replaceAll("[()]", "");

        //Regex to get name and year
        String regex = "^(.*?)(?:\\s(\\d{4}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cleanSource);

        if (matcher.matches()) {
            container.name = matcher.group(1);
            container.year = matcher.group(2) != null ? matcher.group(2) : "";
        } else {
            container.name = cleanSource.replaceAll("\\([^()]*\\)", "");
            container.year = "";
        }

        // Limpiar y formatear el nombre
        container.name = container.name.replaceAll("-", " ").replaceAll("_", " ").replaceAll("\\s{1,}", " ").trim();

        return container;
    }
    private int searchFirstMovie(String name, int year){
        MovieResultsPage movieResults = tmdbApi.getSearch().searchMovie(name, year, currentLibrary.language, true, 1);

        if (movieResults.getTotalResults() > 0)
            return movieResults.getResults().get(0).getId();

        return -1;
    }
    private void saveDiscWithoutMetadata(File f, Season season) {
        Episode episode;

        if (currentLibrary.analyzedFiles.get(f.getAbsolutePath()) != null){
            episode = season.getEpisode(currentLibrary.analyzedFiles.get(f.getAbsolutePath()));
        }else{
            episode = new Episode();
            season.addEpisode(episode);
            episode.seasonID = season.getId();
            currentLibrary.analyzedFiles.put(f.getAbsolutePath(), episode.getId());
        }

        episode.name = f.getName().substring(0, f.getName().lastIndexOf("."));
        episode.videoSrc = f.getAbsolutePath();
        episode.seasonNumber = season.seasonNumber;
        episode.imgSrc = "resources/img/Default_video_thumbnail.jpg";
    }
    private void processMovie(File file, Season season, int runtime){
        Episode episode;

        if (currentLibrary.analyzedFiles.get(file.getAbsolutePath()) != null){
            episode = season.getEpisode(currentLibrary.analyzedFiles.get(file.getAbsolutePath()));
        }else{
            episode = new Episode();
            season.addEpisode(episode);
            episode.seasonID = season.getId();
            currentLibrary.analyzedFiles.put(file.getAbsolutePath(), episode.getId());
        }

        episode.name = file.getName().substring(0, file.getName().lastIndexOf("."));
        episode.episodeNumber = season.getEpisodes().size();
        episode.score = season.score;
        episode.overview = season.overview;
        episode.year = season.year;
        episode.runtime = runtime;
        episode.seasonNumber = season.seasonNumber;
        episode.videoSrc = file.getAbsolutePath();

        if (!season.imdbID.isEmpty())
            setIMDBScore(season.imdbID, episode);

        setMovieThumbnail(episode, season.themdbID);
    }
    private void setIMDBScore(String imdbID, Episode episode){
        try{
            Document doc= Jsoup.connect("https://www.imdb.com/title/" + imdbID).timeout(6000).get();
            Elements body = doc.select("div.sc-bde20123-2");
            for (Element e : body.select("span.sc-bde20123-1"))
            {
                String score = e.text();
                episode.imdbScore = Float.parseFloat(score);
                break;
            }
        } catch (IOException e) {
            System.err.println("setIMDBScore: IMDB connection lost");
        }
    }
    private void setMovieThumbnail(Episode episode, int themdbID){
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        try{
            Files.createDirectories(Paths.get("resources/img/discCovers/" + episode.getId() + "/"));
        } catch (IOException e) {
            System.err.println("setEpisodeData: Directory could not be created");
        }

        MovieImages images = tmdbApi.getMovies().getImages(themdbID, null);
        List<Artwork> thumbnails = images.getBackdrops();

        List<String> thumbnailsUrls = new ArrayList<>();
        if (thumbnails != null){
            for (Artwork artwork : thumbnails){
                thumbnailsUrls.add(imageBaseURL + artwork.getFilePath());
            }
        }

        //region THUMBNAIL DOWNLOADER
        if (!thumbnailsUrls.isEmpty()){
            saveThumbnail(episode, thumbnailsUrls.get(0), 0);

            Task<Void> thumbnailTask = new Task<>() {
                @Override
                protected Void call() {
                    for (int i = 1; i < thumbnailsUrls.size(); i++){
                        saveThumbnail(episode, thumbnailsUrls.get(i), i);
                    }
                    return null;
                }
            };

            new Thread(thumbnailTask).start();
        }
        //endregion

        File img = new File("resources/img/discCovers/" + episode.getId() + "/0.png");
        if (!img.exists()){
            episode.imgSrc = "resources/img/Default_video_thumbnail.jpg";
        }else{
            episode.imgSrc = "resources/img/discCovers/" + episode.getId() + "/0.png";
        }
    }
    private void saveThumbnail(Episode episode, String url, int number){
        try{
            Image originalImage = new Image(url, 480, 270, true, true);

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
                File file = new File("resources/img/discCovers/" + episode.getId() + "/" + number + ".png");
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
    public MovieMetadata downloadMovieMetadata(int tmdbID){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/" + tmdbID + "?language=" + currentLibrary.language)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();

                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), MovieMetadata.class);
            } else {
                System.out.println("Response not successful: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    public SeriesMetadata downloadSeriesMetadata(int tmdbID){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/tv/" + tmdbID + "?language=" + currentLibrary.language)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();

                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), SeriesMetadata.class);
            } else {
                System.out.println("downloadSeriesMetadata: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public SeasonMetadata downloadSeasonMetadata(int tmdbID, int season){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/tv/" + tmdbID + "/season/" + season + "?language=" + currentLibrary.language)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();

                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), SeasonMetadata.class);
            } else {
                System.out.println("downloadSeasonMetadata: Response not successful for " + tmdbID + " season: " + season);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    private Images downloadImages(int tmdbID){
        String imageBaseURL = "https://image.tmdb.org/t/p/original";
        String type = "tv";
        String languages = "null%2C" + currentLibrary.language;

        if (!currentLibrary.type.equals("Shows")){
            type = "movie";
        }

        if (!currentLibrary.language.equals("en"))
            languages += "%2Cen";

        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/"+ type +"/" + tmdbID + "/images?include_image_language=" + languages)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();

                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), Images.class);
            } else {
                System.out.println("downloadImages: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private void downloadLogos(Series series, Season season, int tmdbID){
        String imageBaseURL = "https://image.tmdb.org/t/p/original";
        String type = "tv";
        String languages = "null%2Cja%2Cen";

        String id;

        if (currentLibrary.type.equals("Shows"))
            id = series.getId();
        else
            id = season.getId();

        if (!currentLibrary.type.equals("Shows")){
            type = "movie";
            languages = "null%2Cen";

            if (!currentLibrary.language.equals("en"))
                languages += "%2C" + currentLibrary.language;
        }

        //region GET IMAGES
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/"+ type +"/" + tmdbID + "/images?include_image_language=" + languages)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                assert response.body() != null;
                Images images = objectMapper.readValue(response.body().string(), Images.class);

                if (images == null)
                    return;

                //region Process Logos
                List<Logo> logosList = images.logos;
                if (!logosList.isEmpty()){
                    Image originalImage = new Image(imageBaseURL + logosList.get(0).file_path);
                    saveLogo(id, 0, originalImage);

                    Task<Void> logosTask = new Task<>() {
                        @Override
                        protected Void call() {
                            int processedFiles = 1;
                            for (int i = processedFiles; i < logosList.size(); i++){
                                if (processedFiles == 16)
                                    break;

                                Image originalImage = new Image(imageBaseURL + logosList.get(i).file_path);
                                saveLogo(id, processedFiles, originalImage);

                                processedFiles++;
                            }
                            return null;
                        }
                    };

                    new Thread(logosTask).start();
                }

                File posterDir = new File("resources/img/logos/" + id + "/0.png");
                if (posterDir.exists()){
                    if (currentLibrary.type.equals("Shows"))
                        series.logoSrc = "resources/img/logos/" + id + "/0.png";
                    else
                        season.logoSrc = "resources/img/logos/" + id + "/0.png";
                }
                //endregion
            } else {
                System.out.println("downloadLogos: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //endregion
    }
    public void clearImageCache(){
        try{
            FileUtils.cleanDirectory(new File("resources/img/DownloadCache"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region DOWNLOAD MEDIA
    public List<YoutubeVideo> searchYoutube(String videoName){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "resources/python/YoutubeSearch.py", videoName);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            String regex = "\\[\\{\".*?\"\\}\\]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(output);

            YoutubeVideo[] searchResults = null;
            if (matcher.find()) {
                ObjectMapper objectMapper = new ObjectMapper();
                searchResults = objectMapper.readValue(matcher.group(0), YoutubeVideo[].class);
            }

            process.waitFor();

            if (searchResults != null)
                return List.of(searchResults);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error searching in youtube");
        }

        return null;
    }

    public boolean downloadMedia(Season season, String url){
        String seasonID = season.getId();

        try {
            Files.createDirectories(Paths.get("resources/downloadedMediaCache/" + seasonID + "/"));
            File directory = new File("resources/downloadedMediaCache/" + seasonID + "/");

            ProcessBuilder pb;
            pb = new ProcessBuilder("pytube"
                , url
                , "-a", "-t", directory.getAbsolutePath());

            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            File mediaCahceDir = new File("resources/downloadedMediaCache/" + season.getId() + "/");
            File[] filesInMediaCache = mediaCahceDir.listFiles();

            if (filesInMediaCache != null && filesInMediaCache.length != 0){
                File audioFile = filesInMediaCache[0];

                try{
                    Files.copy(audioFile.toPath(), Paths.get("resources/music/" + season.getId() + ".mp4"), StandardCopyOption.REPLACE_EXISTING);
                    season.musicSrc = "resources/music/" + season.getId() + ".mp4";

                    File dir = new File("resources/downloadedMediaCache/" + season.getId() + "/");
                    FileUtils.deleteDirectory(directory);
                    FileUtils.deleteDirectory(dir);
                } catch (IOException error) {
                    System.err.println("downloadMedia: Could not copy downloaded audio file");
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("downloadMedia: Error downloading media");
            return false;
        }

        return true;
    }
    //endregion

    //region ADD SECTION
    private void updateSeries(Series series){
        if (series == selectedSeries){
            selectedSeries = null;
            selectSeries(series);
        }
    }
    public void addSeries(Series s){
        Platform.runLater(() -> {
            DataManager.INSTANCE.checkEmptySeasons(currentLibrary, s, true);

            if (s.getSeasons().isEmpty())
                return;

            seriesList.add(s);
            addSeriesCard(s);
        });
    }
    private void addSeriesCard(Series s){
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
    @FXML
    void addLibrary(MouseEvent event) {
        if (!App.isConnectedToInternet) {
            App.showErrorMessage(App.textBundle.getString("connectionErrorTitle"), "", App.textBundle.getString("connectionErrorMessage"));
            return;
        }

        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addLibrary-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddLibraryController addLibraryController = fxmlLoader.getController();
            addLibraryController.setParent(this);
            addLibraryController.initValues();
            Stage stage = new Stage();
            stage.setTitle("Add Library");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void addEpisodeCard(Episode episode){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("discCard.fxml"));
            Pane cardBox = fxmlLoader.load();
            DiscController discController = fxmlLoader.getController();
            discController.setDesktopParentParent(this);
            discController.setData(episode);

            discContainer.getChildren().add(cardBox);
            discControllers.add(discController);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateDisc(Episode d){
        for (DiscController discController : discControllers) {
            if (discController.episode.getId().equals(d.getId())){
                discController.setData(d);
                return;
            }
        }
    }
    //endregion

    //region EDIT SECTION
    @FXML
    void editLibrary(MouseEvent event) {
        if (!App.isConnectedToInternet) {
            App.showErrorMessage(App.textBundle.getString("connectionErrorTitle"), "", App.textBundle.getString("connectionErrorMessage"));
            return;
        }

        if (currentLibrary != null){
            showBackgroundShadow();
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addLibrary-view.fxml"));
                Parent root1 = fxmlLoader.load();
                AddLibraryController addLibraryController = fxmlLoader.getController();
                addLibraryController.setParent(this);
                addLibraryController.setValues(currentLibrary);
                Stage stage = new Stage();
                stage.setTitle("Edit Library");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(new Scene(root1));
                App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hideMenu();
        }
    }
    @FXML
    void editSeries(){
        showBackgroundShadow();
        if (selectedSeries != null){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editCollection-view.fxml"));
                Parent root1 = fxmlLoader.load();
                EditCollectionController addColController = fxmlLoader.getController();
                addColController.setParentController(this);
                addColController.setSeries(selectedSeries, currentLibrary.type.equals("Shows"));
                Stage stage = new Stage();
                stage.setTitle(App.textBundle.getString("collectionWindowTitleEdit"));
                stage.initStyle(StageStyle.UNDECORATED);
                Scene scene = new Scene(root1);
                scene.setFill(Color.BLACK);
                stage.setScene(scene);
                App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editSeason-view.fxml"));
            Parent root1 = fxmlLoader.load();
            EditSeasonController addSeasonController = fxmlLoader.getController();
            addSeasonController.setParentController(this);
            addSeasonController.setSeason(selectedSeason, currentLibrary.type.equals("Shows"));
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("seasonWindowTitle"));
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            scene.setFill(Color.BLACK);
            stage.setScene(scene);
            App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        hideMenu();
    }
    @FXML
    void editDisc(MouseEvent event){
        editDisc(selectedEpisode);
    }
    void editDisc(Episode d){
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editDisc-view.fxml"));
            Parent root1 = fxmlLoader.load();
            EditDiscController addDiscController = fxmlLoader.getController();
            addDiscController.setParentController(this);
            addDiscController.setDisc(d);
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("episodeWindowTitleEdit"));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }
    //endregion

    //region REMOVE SECTION
    public void acceptRemove(){
        acceptRemove = true;
    }
    private Stage showConfirmationWindow(String title, String message){
        showBackgroundShadow();
        hideMenu();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("confirmationWindow.fxml"));
            Parent root1 = fxmlLoader.load();
            ConfirmationWindowController controller = fxmlLoader.getController();
            controller.setParent(this);
            controller.initValues(title, message);
            Stage stage = new Stage();
            stage.setTitle("ImageURLDownloader");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            stage.setScene(scene);
            App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
            return stage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void removeLibrary() {
        if (currentLibrary == null)
            return;

        Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
        stage.showAndWait();

        if (acceptRemove){
            acceptRemove = false;
            DataManager.INSTANCE.deleteLibrary(currentLibrary);
            updateLibraries();
        }

        if (librarySelector.getItems().isEmpty())
            blankSelection();

        hideBackgroundShadow();
    }
    @FXML
    void removeCollection() {
        if (selectedSeries != null){
            Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
            stage.showAndWait();

            if (acceptRemove){
                acceptRemove = false;
                removeCollection(selectedSeries);
            }
        }

        hideBackgroundShadow();
    }
    private void removeCollection(Series series){
        int index = seriesList.indexOf(series);
        seriesList.remove(series);
        seriesButtons.remove(index);
        seriesContainer.getChildren().remove(index);
        currentLibrary.removeSeries(series);
        DataManager.INSTANCE.deleteSeriesData(series);
        selectedSeries = null;
        if (!seriesList.isEmpty()){
            selectSeriesButton(seriesButtons.get(0));
        }else{
            seasonScroll.setVisible(false);
        }
    }
    @FXML
    void removeSeason(){
        Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
        stage.showAndWait();

        if (acceptRemove){
            acceptRemove = false;
            removeSeason(selectedSeason);
        }

        hideBackgroundShadow();
    }
    private void removeSeason(Season season){
        seasonContainer.getChildren().remove(seasonList.indexOf(season));
        seasonList.remove(season);
        DataManager.INSTANCE.deleteSeasonData(season);
        selectedSeason = null;

        if (selectedSeries.getSeasons().isEmpty()) {
            removeCollection(selectedSeries);
        }

        Series s = selectedSeries;
        if (selectedSeries != null) {
            selectedSeries = null;
            selectSeries(s);
        }
    }
    @FXML
    void removeEpisode(){
        Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
        stage.showAndWait();

        if (acceptRemove){
            acceptRemove = false;
            removeEpisode(selectedEpisode);
        }

        hideBackgroundShadow();
    }
    void removeEpisode(Episode d) {
        if (d == null)
            return;

        int index = episodeList.indexOf(d);

        if (index != -1) {
            discContainer.getChildren().remove(index);
            discControllers.remove(index);
        }

        episodeList.remove(d);
        DataManager.INSTANCE.deleteEpisodeData(d);

        if (episodeList.isEmpty()){
            removeSeason(selectedSeason);
        }else{
            selectSeason(selectedSeason);
        }

        hideBackgroundShadow();
    }
    //endregion

    //region FULLSCREEN
    @FXML
    void switchToFullScreen(MouseEvent event) {
        fullScreen();
    }
    private void fullScreen(){
        hideMenu();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            //Controller controller = fxmlLoader.getController();
            //controller.playIntroVideo();
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("fullscreenMode"));
            stage.getIcons().add(new Image("file:resources/img/icons/AppIcon.png"));
            Scene scene = new Scene(root);
            scene.setCursor(Cursor.NONE);
            scene.setFill(Color.BLACK);
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
            App.setPopUpProperties(stage, (Stage) mainBorderPane.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }
    @FXML
    void openLibraryMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        libraryMenu.setLayoutX(event.getSceneX());
        libraryMenu.setLayoutY(event.getSceneY());
        libraryMenu.setVisible(true);
    }
    @FXML
    void openSeriesMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        seriesMenu.setLayoutX(event.getSceneX());

        if (seriesList.indexOf(selectedSeries) <= 1)
            seriesMenu.setLayoutY(event.getSceneY());
        else
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
    public void openDiscMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        discMenu.setLayoutX(event.getSceneX());
        discMenu.setLayoutY(event.getSceneY() - discMenu.getHeight());
        discMenu.setVisible(true);
    }
    private void hideMenu(){
        discMenu.setVisible(false);
        seasonMenu.setVisible(false);
        libraryMenu.setVisible(false);
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

class NameYearContainer {
    public String name;
    public String year;

    public NameYearContainer(String name, String year){
        this.name = name;
        this.year = year;
    }
}
