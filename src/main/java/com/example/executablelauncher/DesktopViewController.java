package com.example.executablelauncher;

import com.example.executablelauncher.entities.*;
import com.example.executablelauncher.tmdbMetadata.common.Crew;
import com.example.executablelauncher.tmdbMetadata.common.Genre;
import com.example.executablelauncher.tmdbMetadata.common.ProductionCompany;
import com.example.executablelauncher.tmdbMetadata.groups.*;
import com.example.executablelauncher.tmdbMetadata.images.Backdrop;
import com.example.executablelauncher.tmdbMetadata.images.Images;
import com.example.executablelauncher.tmdbMetadata.images.Logo;
import com.example.executablelauncher.tmdbMetadata.images.Poster;
import com.example.executablelauncher.tmdbMetadata.movieCredits.Credits;
import com.example.executablelauncher.tmdbMetadata.movies.MovieMetadata;
import com.example.executablelauncher.tmdbMetadata.series.EpisodeMetadata;
import com.example.executablelauncher.tmdbMetadata.series.SeasonMetadata;
import com.example.executablelauncher.tmdbMetadata.series.SeasonMetadataBasic;
import com.example.executablelauncher.tmdbMetadata.series.SeriesMetadata;
import com.example.executablelauncher.utils.Configuration;
import com.example.executablelauncher.utils.Utils;
import com.example.executablelauncher.utils.WindowDecoration;
import com.example.executablelauncher.videoPlayer.VideoPlayer;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbTvEpisodes;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
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
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.executablelauncher.App.analysisExecutor;
import static com.example.executablelauncher.App.getBaseFontSize;
import static com.example.executablelauncher.utils.Utils.*;

public class DesktopViewController {
    //region FXML ATTRIBUTES
    @FXML VBox activityBox;
    @FXML VBox activityMessageBox;
    @FXML Button activityButton;
    @FXML Button changePosterButton;
    @FXML Label downloadingMetadataText;
    @FXML Label downloadingImagesText;
    @FXML Label downloadingSongsText;
    @FXML Label totalDurationText;
    @FXML Label rootFolderText;
    @FXML Label totalDurationField;
    @FXML Label rootFolderField;
    @FXML HBox rightArea;
    @FXML Button minButton;
    @FXML Button maxButton;
    @FXML Button closeButton;
    @FXML HBox leftArea;
    @FXML ImageView blackBackground;
    @FXML Button setWatchedSeries;
    @FXML Button setWatchedSeason;
    @FXML Button setWatchedEpisode;
    @FXML Button setUnwatchedSeries;
    @FXML Button setUnwatchedSeason;
    @FXML Button setUnwatchedEpisode;
    @FXML Button addLibraryButton;
    @FXML Label elementsSelectedText;
    @FXML VBox libraryMenu;
    @FXML VBox libraryContainer;
    @FXML Button editLibraryButton;
    @FXML Button searchFilesButton;
    @FXML Button removeLibraryButton;
    @FXML Button deleteSelectedButton;
    @FXML Button deselectAllButton;
    @FXML Button selectAllButton;
    @FXML Button identificationMovie;
    @FXML Button identificationShow;
    @FXML Button changeEpisodesGroup;
    @FXML BorderPane selectionOptions;
    @FXML ImageView backgroundShadow;
    @FXML Button librarySelector;
    @FXML VBox seasonsEpisodesBox;
    @FXML FlowPane episodesContainer;
    @FXML VBox episodeMenu;
    @FXML Button editSeriesButton;
    @FXML Button editEpisodeButton;
    @FXML Button editSeasonButton;
    @FXML Button exitButton;
    @FXML ImageView globalBackground;
    @FXML ImageView globalBackgroundShadow;
    @FXML ImageView globalBackgroundShadow2;
    @FXML BorderPane mainBorderPane;
    @FXML StackPane mainBox;
    @FXML VBox mainMenu;
    @FXML Pane menuParentPane;
    @FXML Button removeSeriesButton;
    @FXML Button removeEpisodeButton;
    @FXML Button removeSeasonButton;
    @FXML FlowPane seasonContainer;
    @FXML HBox seasonLogoBox;
    @FXML StackPane seasonInfoPane;
    @FXML ImageView seasonLogo;
    @FXML VBox seasonMenu;
    @FXML Label seasonNumberText;
    @FXML Label seasonNumberField;
    @FXML ScrollPane seasonScroll;
    @FXML VBox seriesContainer;
    @FXML ImageView seriesCover;
    @FXML VBox seriesMenu;
    @FXML ScrollPane seriesScrollPane;
    @FXML Button settingsButton;
    @FXML Button switchFSButton;
    @FXML BorderPane topBar;
    @FXML ImageView noiseImage;
    @FXML StackPane seriesStack;
    @FXML Label detailsText;
    @FXML Label yearText;
    @FXML Label episodesText;
    @FXML Label yearField;
    @FXML Label episodesField;
    @FXML VBox downloadingContentWindowStatic;
    @FXML Label downloadingContentTextStatic;
    //endregion

    //region ATTRIBUTES
    VideoPlayerController videoPlayerController;
    final ImageViewPane seasonBackground = new ImageViewPane();
    final ImageViewPane seasonBackgroundNoise = new ImageViewPane();
    final ImageViewPane seasonBackgroundClarity = new ImageViewPane();
    final ImageViewPane seriesBackgroundNoise = new ImageViewPane();
    final ImageViewPane seriesBackgroundClarity = new ImageViewPane();
    static List<Library> libraries = new ArrayList<>();
    static List<Series> seriesList = new ArrayList<>();
    static List<Season> seasonList = new ArrayList<>();
    static List<Episode> episodeList = new CopyOnWriteArrayList<>();
    static List<Button> seriesButtons = new ArrayList<>();
    static List<Button> seasonsButtons = new ArrayList<>();
    static Library currentLibrary = null;
    static Series selectedSeries = null;
    static Season selectedSeason = null;
    public static Episode selectedEpisode = null;
    static List<Episode> selectedEpisodes = new ArrayList<>();
    static List<DiscController> episodesControllers = new ArrayList<>();
    Series draggedSeries;
    double ASPECT_RATIO = 16.0 / 9.0;
    boolean acceptRemove = false;
    boolean canDragSeries = true;
    static int numFilesToCheck = 0;
    static int imageDownloadProcesses = 0;
    boolean searchingForFiles = false;
    boolean seasonPoster = true;
    static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    volatile boolean interrupted = false;
    WindowDecoration windowDecoration;
    MediaPlayer mp = null;
    ScheduledExecutorService musicExecutor = Executors.newScheduledThreadPool(1);
    //endregion

    //region THEMOVIEDB ATTRIBUTES
    static TmdbApi tmdbApi;
    boolean movieMetadataToCorrect = false;
    boolean seriesMetadataToCorrect = false;
    boolean changeEpisodeGroup = false;
    boolean watchingVideo = false;
    //endregion

    public void initValues(WindowDecoration windowDecoration) {
        Stage stage = (Stage) mainBox.getScene().getWindow();

        App.setDesktopController(this);

        if (windowDecoration != null){
            this.windowDecoration = windowDecoration;
            topBar.setCenter(windowDecoration);
            windowDecoration.setDesktopParent(this);
            windowDecoration.initialize(leftArea, rightArea);

            minButton.setOnAction(e -> windowDecoration.minimizeWindow());
            maxButton.setOnAction(e -> {
                if (stage.isMaximized())
                    windowDecoration.restoreWindow();
                else
                    windowDecoration.maximizeWindow();
            });
            closeButton.setOnAction(e -> closeWindow());
        }else{
            rightArea.setVisible(false);
        }

        if (App.isConnectedToInternet)
            tmdbApi = new TmdbApi(App.themoviedbAPIKey);

        selectionOptions.setVisible(false);

        activityBox.setVisible(false);

        changePosterButton.setOnAction(e -> {
            seasonPoster = !seasonPoster;

            try{
                Image image;
                if (seasonPoster && !selectedSeason.getCoverSrc().isEmpty()){
                    changePosterButton.setText(App.buttonsBundle.getString("collectionPoster"));

                    File file = new File(selectedSeason.getCoverSrc());
                    image = new Image(file.toURI().toURL().toExternalForm(), 300, 450, true, true);
                }else if (!selectedSeries.getCoverSrc().isEmpty()){
                    changePosterButton.setText(App.buttonsBundle.getString("seasonPoster"));

                    File file = new File(selectedSeries.getCoverSrc());
                    image = new Image(file.toURI().toURL().toExternalForm(), 300, 450, true, true);
                }else {
                    image = new Image(getFileAsIOStream("img/fileNotFound.png"), 300, 450, true, true);
                }
                seriesCover.setImage(image);
            } catch (MalformedURLException ex) {
                System.err.println("DesktopViewController: could not change poster");
            }

        });

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
            if (ratioHeight < newHeight.doubleValue()) {
                scaleFactor = newHeight.doubleValue() / globalBackground.getImage().getHeight();
                globalBackground.setFitHeight(newHeight.doubleValue());
                globalBackground.setFitWidth(globalBackground.getImage().getWidth() * scaleFactor);
            }
        };

        stage.widthProperty().addListener(widthListener);
        stage.heightProperty().addListener(heightListener);
        //endregion

        menuParentPane.setVisible(false);
        mainMenu.setVisible(false);
        libraryMenu.setVisible(false);
        seriesMenu.setVisible(false);
        seasonMenu.setVisible(false);
        episodeMenu.setVisible(false);
        libraryContainer.setVisible(false);

        //Elements size
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        seriesScrollPane.setPrefHeight(screenHeight);
        seriesContainer.setPrefHeight(screenHeight);

        if (screenWidth > 1920)
            seriesScrollPane.setPrefWidth(350);

        leftArea.setMaxWidth(seriesScrollPane.getPrefWidth());

        seasonsEpisodesBox.setPrefWidth(Integer.MAX_VALUE);

        seasonInfoPane.getChildren().add(0, seasonBackground);
        seasonInfoPane.getChildren().add(1, seasonBackgroundNoise);
        seasonInfoPane.getChildren().add(2, seasonBackgroundClarity);

        ImageView seasonNoise = new ImageView(new Image(getFileAsIOStream("img/noise.png")));
        seasonNoise.setPreserveRatio(false);
        seasonNoise.setOpacity(0.03);
        seasonBackgroundNoise.setImageView(seasonNoise);

        ImageView seasonClarity = new ImageView(new Image(getFileAsIOStream("img/white.png")));
        seasonClarity.setPreserveRatio(false);
        seasonClarity.setOpacity(0.03);
        seasonBackgroundClarity.setImageView(seasonClarity);

        seriesStack.prefHeightProperty().bind(seriesScrollPane.heightProperty());
        seriesStack.getChildren().add(0, seriesBackgroundNoise);
        seriesStack.getChildren().add(1, seriesBackgroundClarity);

        ImageView seriesNoise = new ImageView(new Image(getFileAsIOStream("img/noise.png")));
        seriesNoise.setPreserveRatio(false);
        seriesNoise.setOpacity(0.03);
        seriesBackgroundNoise.setImageView(seriesNoise);

        ImageView seriesClarity = new ImageView(new Image(getFileAsIOStream("img/white.png")));
        seriesClarity.setPreserveRatio(false);
        seriesClarity.setOpacity(0.03);
        seriesBackgroundClarity.setImageView(seriesClarity);

        globalBackground.setPreserveRatio(true);

        globalBackgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackgroundShadow.setPreserveRatio(false);
        globalBackgroundShadow2.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackgroundShadow2.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackgroundShadow2.setPreserveRatio(false);
        globalBackgroundShadow2.setVisible(false);

        noiseImage.setFitWidth(screenWidth);
        noiseImage.setFitHeight(screenHeight);
        noiseImage.setPreserveRatio(false);

        backgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        backgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        backgroundShadow.setPreserveRatio(false);

        blackBackground.fitWidthProperty().bind(mainBox.widthProperty());
        blackBackground.fitHeightProperty().bind(mainBox.heightProperty());
        blackBackground.setPreserveRatio(false);

        identificationMovie.setDisable(true);
        identificationShow.setDisable(true);
        changeEpisodesGroup.setDisable(true);

        MFXProgressSpinner metadataSpinner = getCircularProgress(25);
        MFXProgressSpinner imagesSpinner = getCircularProgress(25);
        MFXProgressSpinner songsSpinner = getCircularProgress(25);
        MFXProgressSpinner mainSpinner = getCircularProgress(60);

        ((StackPane) activityButton.getParent()).getChildren().add(mainSpinner);

        downloadingMetadataText.setDisable(true);
        ((BorderPane) downloadingMetadataText.getParent()).setRight(metadataSpinner);
        ((BorderPane) downloadingMetadataText.getParent()).getRight().setVisible(false);

        downloadingImagesText.setDisable(true);
        ((BorderPane) downloadingImagesText.getParent()).setRight(imagesSpinner);
        ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(false);

        downloadingSongsText.setDisable(true);
        ((BorderPane) downloadingSongsText.getParent()).setRight(songsSpinner);
        ((BorderPane) downloadingSongsText.getParent()).getRight().setVisible(false);

        mainSpinner.setOnMouseEntered(e -> fadeInEffect(activityMessageBox, 0.2f).play());
        mainSpinner.setOnMouseExited(e -> fadeOutEffect(activityMessageBox, 0.2f));

        selectedSeries = null;
        selectedEpisode = null;
        selectedSeason = null;

        Platform.runLater(() -> {
            updateLibraries();
            updateLanguage();
        });
    }

    public void onRestoreWindow(){
        ((ImageView) maxButton.getGraphic()).setImage(new Image(getFileAsIOStream("img/icons/windowMax.png")));
    }

    public void onMaximizeWindow(){
        ((ImageView) maxButton.getGraphic()).setImage(new Image(getFileAsIOStream("img/icons/windowRestore.png")));
    }

    //region CONTENT
    public void updateLanguage() {
        if (seasonPoster)
            changePosterButton.setText(App.buttonsBundle.getString("collectionPoster"));
        else
            changePosterButton.setText(App.buttonsBundle.getString("seasonPoster"));

        settingsButton.setText(App.buttonsBundle.getString("settings"));
        exitButton.setText(App.buttonsBundle.getString("eixtButton"));
        switchFSButton.setText(App.buttonsBundle.getString("switchToFullscreen"));
        removeSeriesButton.setText(App.buttonsBundle.getString("removeButton"));
        removeSeasonButton.setText(App.buttonsBundle.getString("removeButton"));
        removeEpisodeButton.setText(App.buttonsBundle.getString("removeButton"));
        removeLibraryButton.setText(App.buttonsBundle.getString("removeButton"));
        editSeriesButton.setText(App.buttonsBundle.getString("editButton"));
        editSeasonButton.setText(App.buttonsBundle.getString("editButton"));
        editEpisodeButton.setText(App.buttonsBundle.getString("editButton"));
        editLibraryButton.setText(App.buttonsBundle.getString("editButton"));
        deleteSelectedButton.setText(App.buttonsBundle.getString("removeButton"));
        selectAllButton.setText(App.buttonsBundle.getString("selectAllButton"));
        deselectAllButton.setText(App.buttonsBundle.getString("deselectAllButton"));
        identificationMovie.setText(App.textBundle.getString("correctIdentification"));
        identificationShow.setText(App.textBundle.getString("correctIdentification"));
        changeEpisodesGroup.setText(App.buttonsBundle.getString("changeEpisodesGroup"));
        detailsText.setText(App.textBundle.getString("details"));
        yearText.setText(App.textBundle.getString("year"));
        totalDurationText.setText(App.textBundle.getString("totalDuration"));
        rootFolderText.setText(App.textBundle.getString("rootFolder"));
        setWatchedSeries.setText(App.buttonsBundle.getString("markWatched"));
        setWatchedSeason.setText(App.buttonsBundle.getString("markWatched"));
        setWatchedEpisode.setText(App.buttonsBundle.getString("markWatched"));
        setUnwatchedSeries.setText(App.buttonsBundle.getString("markUnwatched"));
        setUnwatchedSeason.setText(App.buttonsBundle.getString("markUnwatched"));
        setUnwatchedEpisode.setText(App.buttonsBundle.getString("markUnwatched"));

        downloadingSongsText.setText(App.textBundle.getString("downloadingMusicMessage"));
        downloadingMetadataText.setText(App.textBundle.getString("downloadingMessage"));
        downloadingImagesText.setText(App.textBundle.getString("downloadingImages"));

        if (currentLibrary != null && currentLibrary.getType().equals("Shows"))
            episodesText.setText(App.textBundle.getString("episodes"));
        else
            episodesText.setText(App.textBundle.getString("videos"));

        if (currentLibrary != null && currentLibrary.getType().equals("Shows")) {
            seasonNumberText.setText(App.textBundle.getString("seasonNumber"));
        } else {
            seasonNumberText.setText("");
        }

        searchFilesButton.setText(App.buttonsBundle.getString("searchFiles"));
    }

    public void updateLibraries() {
        libraries = DataManager.INSTANCE.getLibraries(false);

        if (libraries.isEmpty()) {
            blankSelection();
            return;
        }

        if (currentLibrary == null)
            currentLibrary = libraries.get(0);

        libraries.sort(new Utils.LibraryComparator());

        librarySelector.setText(currentLibrary.getName());
        libraryContainer.getChildren().clear();
        for (Library library : libraries) {
            Button btn = new Button(library.getName());
            btn.setPadding(new Insets(10));
            btn.setTextAlignment(TextAlignment.LEFT);
            btn.setMaxWidth(Integer.MAX_VALUE);
            btn.getStyleClass().clear();
            btn.getStyleClass().addAll("libraryMenuButton", "tiny-text");

            btn.setOnMouseClicked(event -> {
                for (Node node : libraryContainer.getChildren()) {
                    Button button = (Button) node;
                    button.getStyleClass().clear();
                    button.getStyleClass().addAll("libraryMenuButton", "tiny-text");
                }

                btn.getStyleClass().clear();
                btn.getStyleClass().addAll("libraryMenuButtonSelected", "tiny-text");

                selectLibrary(btn.getText());
                hideMenu();
            });

            libraryContainer.getChildren().add(btn);
        }

        for (Node node : libraryContainer.getChildren()) {
            Button btn = (Button) node;

            if (btn.getText().equals(currentLibrary.getName())) {
                btn.getStyleClass().clear();
                btn.getStyleClass().addAll("libraryMenuButtonSelected", "tiny-text");

                selectLibrary(btn.getText());
            }
        }
    }

    public void showSeries() {
        seriesButtons.clear();

        if (seriesList == null || seriesList.isEmpty()) {
            blankSelection();
            return;
        } else {
            editLibraryButton.setDisable(false);
            searchFilesButton.setDisable(false);
            removeLibraryButton.setDisable(false);
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
        episodesContainer.getChildren().clear();
        episodesControllers.clear();
        episodeList = s.getEpisodes();

        if (!episodeList.isEmpty()) {
            episodeList.sort(new Utils.EpisodeComparator());
            addEpisodeCardWithDelay(0);
        }
    }

    private void fillSeasonInfo() {
        selectedEpisodes.clear();
        selectionOptions.setVisible(false);

        Image secondImg = new Image("file:" + "resources/img/backgrounds/" + selectedSeason.getId() + "/" + "background.jpg",
                globalBackground.getImage().getWidth(), globalBackground.getImage().getHeight(), true, true);
        BufferedImage img1 = SwingFXUtils.fromFXImage(globalBackground.getImage(), null);
        BufferedImage img2 = SwingFXUtils.fromFXImage(secondImg, null);

        boolean nullImage = false;
        if (img2 == null) {
            img2 = SwingFXUtils.fromFXImage(new Image("file:" + "resources/img/backgroundDefault.png",
                    globalBackground.getImage().getWidth(), globalBackground.getImage().getHeight(), true, true), null);
            nullImage = true;
        }

        boolean sameImage = compareImages(img1, img2);

        img1.flush();
        img2.flush();

        if (!sameImage){
            Image i;

            if (nullImage)
                i = new Image("file:" + "resources/img/backgroundDefault.png",
                        Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight(), true, true);
            else
                i = new Image("file:" + "resources/img/backgrounds/" + selectedSeason.getId() + "/" + "background.jpg",
                        Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight(), true, true);


            ASPECT_RATIO = i.getWidth() / i.getHeight();
            globalBackground.setImage(i);
            ImageView img = null;

            if (!nullImage) {
                img = new ImageView(new Image("file:" + "resources/img/backgrounds/" + selectedSeason.getId() + "/" + "transparencyEffect.png"));
                img.setPreserveRatio(true);
            }

            seasonBackground.setImageView(img);
            fadeInTransition(globalBackground);
            fadeInTransition(seasonBackground.getImageView());
        }

        //Fill info
        detailsText.setText(App.textBundle.getString("details"));
        yearText.setText(App.textBundle.getString("year"));

        int duration = 0;
        for (Episode episode : selectedSeason.getEpisodes()){
            duration += episode.getRuntime();
        }
        totalDurationField.setText(duration + " min");

        if (currentLibrary.getType().equals("Shows")) {
            episodesText.setText(App.textBundle.getString("episodes"));
            seasonNumberText.setText(App.textBundle.getString("seasonNumber"));
            seasonNumberField.setText(String.valueOf(selectedSeason.getSeasonNumber()));
            seasonNumberText.setVisible(true);
            seasonNumberText.setManaged(true);
            seasonNumberField.setVisible(true);
            seasonNumberField.setManaged(true);

            rootFolderField.setText(getFolderSrc(selectedSeries.getFolder()));

            File logoFile = new File(selectedSeries.getLogoSrc());
            if (!logoFile.exists() || !logoFile.isFile()){
                setTextNoLogo();
            }else {
                seasonLogoBox.getChildren().remove(0);
                seasonLogo = new ImageView();
                File file = new File(selectedSeries.getLogoSrc());
                setLogoNoText(file);
            }
        }else {
            episodesText.setText(App.textBundle.getString("videos"));
            seasonNumberText.setVisible(false);
            seasonNumberText.setManaged(false);
            seasonNumberField.setVisible(false);
            seasonNumberField.setManaged(false);

            rootFolderField.setText(getFolderSrc(selectedSeason.getFolder()));

            File logoFile = new File(selectedSeason.getLogoSrc());
            if (!logoFile.exists() || !logoFile.isFile()) {
                setTextNoLogo();
            }else {
                seasonLogoBox.getChildren().remove(0);
                seasonLogo = new ImageView();
                File file = new File(selectedSeason.getLogoSrc());
                setLogoNoText(file);
            }
        }

        rootFolderField.setOnMouseClicked(e -> {
            try {
                File folder = new File(rootFolderField.getText());

                if (Desktop.isDesktopSupported() && folder.exists()) {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(folder);
                } else {
                    App.showErrorMessage(App.textBundle.getString("rootFolder"), "", App.textBundle.getString("rootFolderError"));
                }
            } catch (IOException err) {
                App.showErrorMessage(App.textBundle.getString("rootFolder"), "", App.textBundle.getString("rootFolderError"));
            }
        });

        yearField.setText(selectedSeason.getYear());
        episodesField.setText(Integer.toString(selectedSeason.getEpisodes().size()));

        setCoverImage();
    }

    private void setCoverImage() {
        try {
            Image image;
            if (seasonPoster && !selectedSeason.getCoverSrc().isEmpty()){
                File file = new File(selectedSeason.getCoverSrc());
                image = new Image(file.toURI().toURL().toExternalForm(), 300, 450, true, true);
            }else if (!seasonPoster && !selectedSeries.getCoverSrc().isEmpty()){
                File file = new File(selectedSeries.getCoverSrc());
                image = new Image(file.toURI().toURL().toExternalForm(), 300, 450, true, true);
            } else {
                image = new Image(getFileAsIOStream("img/fileNotFound.png"), 300, 450, true, true);
            }

            seriesCover.setImage(image);
        } catch (MalformedURLException e) {
            System.err.println("Series cover not found");
        }
    }

    private void setLogoNoText(File file) {
        seasonLogoBox.getChildren().clear();
        try {
            seasonLogo.setImage(new Image(file.toURI().toURL().toExternalForm(), 600, 300, true, true));
            seasonLogoBox.getChildren().add(seasonLogo);
        } catch (MalformedURLException e) {
            System.err.println("DesktopViewController: Logo not loaded");
        }
    }

    private void setTextNoLogo() {
        seasonLogoBox.getChildren().remove(0);
        Label seasonLogoText = new Label(selectedSeries.getName());
        seasonLogoText.getStyleClass().clear();
        seasonLogoText.getStyleClass().addAll("title-text", "bold");
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
    public void refreshSeries() {
        Platform.runLater(() -> {
            Button btn = seriesButtons.get(seriesList.indexOf(selectedSeries));
            BorderPane pane = (BorderPane) btn.getGraphic();
            ((Label) pane.getLeft()).setText(selectedSeries.getName());

            if (selectedSeries.getCoverSrc().isEmpty()){
                File[] files = new File("resources/img/seriesCovers/" + selectedSeries.getId() + "/").listFiles();

                if (files != null) {
                    File posterDir = files[0];
                    selectedSeries.setCoverSrc("resources/img/seriesCovers/" + selectedSeries.getId() + "/" + posterDir.getName());
                }
            }

            Series series = selectedSeries;
            selectedSeries = null;
            selectSeries(series);
        });
    }

    public void refreshSeason(Season s) {
        Platform.runLater(() -> {
            seasonsButtons.get(seasonList.indexOf(selectedSeason)).setText(selectedSeason.getName());
            selectedSeason = null;
            selectSeason(s);

            DataManager.INSTANCE.saveData();
        });
    }
    //endregion

    //region SELECTION
    public void blankSelection() {
        librarySelector.setText("");
        seasonScroll.setVisible(false);
        globalBackground.setImage(new Image(getFileAsIOStream("img/Background.png")));
        editLibraryButton.setDisable(true);
        searchFilesButton.setDisable(true);
        removeLibraryButton.setDisable(false);

        seriesContainer.getChildren().clear();
    }

    public void selectLibrary(String libraryName) {
        seriesContainer.getChildren().clear();
        currentLibrary = DataManager.INSTANCE.getLibrary(libraryName);

        librarySelector.setText(libraryName);

        DataManager.INSTANCE.currentLibrary = currentLibrary;

        if (currentLibrary == null)
            return;

        seriesList = currentLibrary.getSeries();
        seriesList.sort(new Utils.SeriesComparator());

        if (currentLibrary.getType().equals("Shows")) {
            changeEpisodesGroup.setDisable(false);
            identificationShow.setDisable(false);
            identificationMovie.setDisable(true);

            seasonPoster = false;
            changePosterButton.setVisible(false);
        } else {
            changeEpisodesGroup.setDisable(true);
            identificationMovie.setDisable(false);
            identificationShow.setDisable(true);

            seasonPoster = true;
            changePosterButton.setVisible(true);
        }

        if (seasonPoster)
            changePosterButton.setText(App.buttonsBundle.getString("collectionPoster"));
        else
            changePosterButton.setText(App.buttonsBundle.getString("seasonPoster"));

        showSeries();

        if (Configuration.loadConfig("autoScan", "true").equals("true") && !searchingForFiles)
            searchFiles();
    }

    public void selectSeries(Series selectedSeries) {
        if (DesktopViewController.selectedSeries == selectedSeries)
            return;

        App.setSelectedSeries(selectedSeries);
        selectedEpisodes.clear();
        seasonScroll.setVisible(true);
        selectionOptions.setVisible(false);
        DesktopViewController.selectedSeries = selectedSeries;

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
                episodesContainer.getChildren().clear();

            playMusic();
        } else {
            seasonScroll.setVisible(false);
        }
    }

    public void selectSeason(Season s) {
        if (selectedSeason != s) {
            selectedSeason = s;

            fillSeasonInfo();

            if (!selectedSeason.getEpisodes().isEmpty()) {
                showEpisodes(selectedSeason);
            } else {
                episodesContainer.getChildren().clear();
            }

            playMusic();
        }
    }
    private void playMusic(){
        if (!Boolean.parseBoolean(Configuration.loadConfig("playMusicDesktop", "false")))
            return;

        if (mp != null && selectedSeries.isPlaySameMusic() && selectedSeason.getMusicSrc().isEmpty())
            return;
        else if (mp != null)
            mp.stop();

        if (selectedSeries.isPlaySameMusic() && !selectedSeason.getMusicSrc().isEmpty()){
            File file = new File(selectedSeason.getMusicSrc());
            Media media = new Media(file.toURI().toString());

            int volume = Integer.parseInt(Configuration.loadConfig("backgroundVolume", "0.4"));

            mp = new MediaPlayer(media);
            mp.setVolume((double) volume / 100);
            mp.setOnEndOfMedia(this::stopMusic);
        }else{
            for (Season season : selectedSeries.getSeasons()){
                if (!season.getMusicSrc().isEmpty()){
                    File file = new File(season.getMusicSrc());
                    Media media = new Media(file.toURI().toString());

                    int volume = Integer.parseInt(Configuration.loadConfig("backgroundVolume", "0.4"));

                    mp = new MediaPlayer(media);
                    mp.setVolume((double) volume / 100);
                    mp.setOnEndOfMedia(this::stopMusic);
                    break;
                }
            }
        }

        musicExecutor.schedule(() -> {
            if (mp != null) {
                mp.play();
            }
        }, 1, TimeUnit.SECONDS);
    }
    public void stopMusic() {
        if (mp != null) {
            mp.stop();
        }
        musicExecutor.shutdown();
    }

    private void selectSeriesButton(Button btn) {
        //Clear Selected Button
        for (Button b : seriesButtons) {
            b.getStyleClass().clear();
            b.getStyleClass().add("desktopTextButtonParent");

            Label buttonText = (Label) ((BorderPane) b.getGraphic()).getLeft();
            buttonText.getStyleClass().clear();
            buttonText.getStyleClass().add("desktopTextButton");

            MFXProgressSpinner spinner = (MFXProgressSpinner) ((BorderPane) b.getGraphic()).getRight();
            if (spinner.isVisible()){
                spinner.setColor1(Color.web("8EDCE6"));
                spinner.setColor2(Color.web("8EDCE6"));
                spinner.setColor3(Color.web("8EDCE6"));
                spinner.setColor4(Color.web("8EDCE6"));
            }
        }
        //Select current button
        btn.getStyleClass().clear();
        btn.getStyleClass().add("desktopButtonActive");

        Label buttonText = (Label) ((BorderPane) btn.getGraphic()).getLeft();
        buttonText.getStyleClass().clear();
        buttonText.getStyleClass().add("desktopButtonActive");

        MFXProgressSpinner spinner = (MFXProgressSpinner) ((BorderPane) btn.getGraphic()).getRight();
        if (spinner.isVisible()){
            spinner.setColor1(Color.BLACK);
            spinner.setColor2(Color.BLACK);
            spinner.setColor3(Color.BLACK);
            spinner.setColor4(Color.BLACK);
        }

        if (seriesList.get(seriesButtons.indexOf(btn)) != selectedSeries)
            selectSeries(seriesList.get(seriesButtons.indexOf(btn)));
    }

    private void selectSeasonButton(Button btn) {
        //Clear Selected Button
        for (Button b : seasonsButtons) {
            b.getStyleClass().clear();
            b.getStyleClass().add("seasonButton");
        }
        //Select current button
        btn.getStyleClass().clear();
        btn.getStyleClass().add("seasonButtonActive");

        selectSeason(seasonList.get(seasonsButtons.indexOf(btn)));
    }

    public boolean isEpisodeSelected() {
        return !selectedEpisodes.isEmpty();
    }
    //endregion

    //region EFFECTS AND MODIFICATIONS
    public void saveBackground(Season s, String imageToCopy, boolean copyImages) {
        DataManager.INSTANCE.createFolder("resources/img/backgrounds/" + s.getId() + "/");

        if (copyImages) {
            String directoryPath = imageToCopy.substring(0, imageToCopy.lastIndexOf('/') + 1);
            copyAndRenameImage(directoryPath, "resources/img/backgrounds/" + s.getId() + "/", "background.jpg");
            copyAndRenameImage(directoryPath, "resources/img/backgrounds/" + s.getId() + "/", "transparencyEffect.png");
            copyAndRenameImage(directoryPath, "resources/img/backgrounds/" + s.getId() + "/", "fullBlur.jpg");

            s.setBackgroundSrc("resources/img/backgrounds/" + s.getId() + "/background.jpg");
        } else {
            //Compress and save image file
            File destination = new File("resources/img/backgrounds/" + s.getId() + "/background.jpg");
            try {
                Thumbnails.of(imageToCopy)
                        .scale(1)
                        .outputQuality(0.9)
                        .toFile(destination);
            } catch (IOException e) {
                System.err.println("saveBackground: error compressing background image");
            }

            s.setBackgroundSrc("resources/img/backgrounds/" + s.getId() + "/background.jpg");

            setTransparencyEffect(s.getBackgroundSrc(), "resources/img/backgrounds/" + s.getId() + "/transparencyEffect.png");
            BufferedImage blurImage = processBlurAndSave(s.getBackgroundSrc(), "resources/img/backgrounds/" + s.getId() + "/fullBlur.jpg");

            try {
                BufferedImage noiseImage = ImageIO.read(getFileAsIOStream("img/noise.png"));
                BufferedImage shadowImage = ImageIO.read(getFileAsIOStream("img/desktopBackgroundShadow.png"));

                if (blurImage == null)
                    return;

                BufferedImage resultImage = mergeImages(blurImage, noiseImage, 0.06f);
                BufferedImage result2 = mergeImages(resultImage, shadowImage, 0.4f);

                try {
                    Thumbnails.of(result2)
                            .scale(1)
                            .outputQuality(0.9)
                            .toFile("resources/img/backgrounds/" + s.getId() + "/fullBlur.jpg");
                } catch (IOException e) {
                    System.err.println("saveBackground: error compressing background image");
                }

                blurImage.flush();
                noiseImage.flush();
                shadowImage.flush();
                resultImage.flush();
                result2.flush();
            } catch (Exception e) {
                System.err.println("saveBackground: error processing image with blur");
            }
        }
    }
    public void copyAndRenameImage(String srcImagePath, String destDirPath, String imageName) {
        Path srcPath = Paths.get(srcImagePath + imageName);
        Path destDir = Paths.get(destDirPath + imageName);

        try {
            Files.copy(srcPath, destDir);
        } catch (IOException e) {
            System.err.println("copyAndRenameImage: image " + srcImagePath + imageName + " could not be copied to " + destDirPath);
        }
    }
    private static void setTransparencyEffect(String src, String outputPath) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(src));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            //Scale output image to match this height (it makes the PNG image A LOT smaller
            int resolutionHeight = 1080;

            if (resolutionHeight > height)
                resolutionHeight = height;

            int scaledWidth = (int) ((double) width / height * resolutionHeight);

            BufferedImage scaledImage = new BufferedImage(scaledWidth, resolutionHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(originalImage, 0, 0, scaledWidth, resolutionHeight, null);
            graphics2D.dispose();

            BufferedImage blendedImage = new BufferedImage(scaledWidth, resolutionHeight, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < resolutionHeight; y++) {
                float opacity = 1.0f - ((float) y / (resolutionHeight / 1.15f));
                opacity = Math.min(1.0f, Math.max(0.0f, opacity));

                for (int x = 0; x < scaledWidth; x++) {
                    java.awt.Color originalColor = new java.awt.Color(scaledImage.getRGB(x, y), true);
                    int blendedAlpha = (int) (originalColor.getAlpha() * opacity);
                    java.awt.Color blendedColor = new java.awt.Color(originalColor.getRGB() & 0xFFFFFF | blendedAlpha << 24, true);

                    blendedImage.setRGB(x, y, blendedColor.getRGB());
                }
            }

            File outputFilePath = new File(outputPath);
            ImageIO.write(blendedImage, "png", outputFilePath);

            originalImage.flush();
            scaledImage.flush();
            blendedImage.flush();
        } catch (IOException e) {
            System.err.println("setTransparencyEffect: error applying transparency effect to background");
        }
    }
    public static BufferedImage processBlurAndSave(String imagePath, String outputFilePath) {
        try {
            // Load the image
            Mat originalImage = Imgcodecs.imread(imagePath);

            // Apply GaussianBlur effect
            Mat blurredImage = new Mat();
            int kernelSize = 41;
            Imgproc.GaussianBlur(originalImage, blurredImage, new Size(kernelSize, kernelSize), 0);

            // Crop a portion of the blurred image
            int cropX = (int) (blurredImage.cols() * 0.03);
            int cropY = (int) (blurredImage.rows() * 0.05);
            int cropWidth = (int) (blurredImage.cols() * 0.93);
            int cropHeight = (int) (blurredImage.rows() * 0.9);

            Mat croppedImage = new Mat(blurredImage, new Rect(cropX, cropY, cropWidth, cropHeight));

            // Save the cropped blurred image to cache
            Imgcodecs.imwrite(outputFilePath, croppedImage);

            // Release resources
            originalImage.release();
            blurredImage.release();
            croppedImage.release();

            return ImageIO.read(new File(outputFilePath));
        } catch (Exception e) {
            System.err.println("Image processing error: " + e.getMessage());
        }

        return null;
    }
    private static BufferedImage mergeImages(BufferedImage originalImage, BufferedImage secondImage, float opacity){
        java.awt.Image resizedNoiseImage = secondImage.getScaledInstance(originalImage.getWidth(), originalImage.getHeight(), java.awt.Image.SCALE_SMOOTH);
        BufferedImage resizedNoiseBufferedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2dResized = resizedNoiseBufferedImage.createGraphics();
        g2dResized.drawImage(resizedNoiseImage, 0, 0, null);
        g2dResized.dispose();

        BufferedImage combinedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dCombined = combinedImage.createGraphics();
        g2dCombined.drawImage(originalImage, 0, 0, null);
        g2dCombined.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2dCombined.drawImage(resizedNoiseBufferedImage, 0, 0, null);
        g2dCombined.dispose();

        resizedNoiseImage.flush();
        resizedNoiseBufferedImage.flush();

        return combinedImage;
    }
    private void fadeInTransition(ImageView imageV) {
        //Fade In Transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), imageV);
        fadeIn.setFromValue(0.5);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void fadeInTransition(Pane pane) {
        //Fade In Transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void fadeOutTransition(Pane pane) {
        //Fade In Transition
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), pane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
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
        if (watchingVideo)
            return;

        //Check if the video file exists before showing the video player
        File videoFile = new File(episode.getVideoSrc());

        if (!videoFile.isFile()) {
            App.showErrorMessage(App.textBundle.getString("playbackError"), "", App.textBundle.getString("videoErrorMessage"));
            return;
        }

        if (mp != null)
            stopPlayer();

        selectedEpisode = episode;
        watchingVideo = true;

        //Open Video Player
        fadeInEffect(blackBackground);
        mainBox.setDisable(true);

        if (System.getProperty("os.name").toLowerCase().contains("win")){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("videoPlayer.fxml"));
                Parent root = fxmlLoader.load();
                root.setStyle(getBaseFontSize());

                Stage videoStage = (Stage) mainBox.getScene().getWindow();

                Stage controlsStage = new Stage();
                controlsStage.setTitle("Video Player Controls");
                controlsStage.initOwner(videoStage);
                controlsStage.initModality(Modality.NONE);
                controlsStage.initStyle(StageStyle.TRANSPARENT);
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                controlsStage.setScene(scene);

                String name = selectedSeries.getName();
                if (!currentLibrary.getType().equals("Shows"))
                    name = selectedSeason.getName();

                videoPlayerController = fxmlLoader.getController();
                videoPlayerController.setDesktopPlayer(this, controlsStage);
                videoPlayerController.setVideo(selectedSeason, episode, name, videoStage);

                controlsStage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            VlcjJavaFxApplication videoPlayer = new VlcjJavaFxApplication();

            try {
                Stage stage = new Stage();
                videoPlayer.init();
                videoPlayer.setDesktopParent(this);
                videoPlayer.startVideo(selectedSeason, episode);
                videoPlayer.start(stage);
            } catch (Exception e) {
                System.err.println("playEpisode: could not load VLC video player");
            }
        }
    }

    public void stopPlayer() {
        watchingVideo = false;
        mainBox.setDisable(false);
        fadeOutEffect(blackBackground);

        if (selectedEpisode == null)
            return;

        markPreviousAndNextEpisodes(selectedSeries, selectedSeason, selectedEpisode);

        for (Episode episode : selectedSeason.getEpisodes())
            updateDisc(episode);
    }

    public String setRuntime(int runtime) {
        int h = runtime / 60;
        int m = runtime % 60;

        if (h == 0)
            return (m + "m");

        return (h + "h " + m + "m");
    }
    //endregion

    //region SET WATCHED/UNWATCHED
    private void setWatchedEpisode(Season season, Episode episode){
        episode.setWatched();
        markPreviousAndNextEpisodes(selectedSeries, season, episode);

        for (Episode e : selectedSeason.getEpisodes())
            updateDisc(e);
    }
    private void setUnwatchedEpisode(Season season, Episode episode){
        episode.setUnWatched();
        markPreviousAndNextEpisodes(selectedSeries, season, episode);

        for (Episode e : selectedSeason.getEpisodes())
            updateDisc(e);
    }
    @FXML
    void setWatchedEpisode() {
        hideMenu();
        //episodesControllers.get(episodeList.indexOf(selectedEpisode)).setWatched();

        setWatchedEpisode(selectedSeason, selectedEpisode);
    }
    @FXML
    void setUnwatchedEpisode() {
        hideMenu();
        setUnwatchedEpisode(selectedSeason, selectedEpisode);
    }
    @FXML
    void setWatchedSeries() {
        hideMenu();

        if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows")){
            setWatchedEpisode(selectedSeries.getSeasons().getLast()
                    , selectedSeries.getSeasons().getLast().getEpisodes().getLast());
        }else{
            for (Season season : selectedSeries.getSeasons()){
                season.setCurrentlyWatchingEpisode(-1);
                for (Episode episode : season.getEpisodes())
                    episode.setWatched();
            }

            selectedSeries.setCurrentlyWatchingSeason(-1);

            for (Episode e : selectedSeason.getEpisodes())
                updateDisc(e);
        }
    }
    @FXML
    void setUnwatchedSeries() {
        hideMenu();

        if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows")){
            setUnwatchedEpisode(selectedSeries.getSeasons().getFirst()
                    , selectedSeries.getSeasons().getLast().getEpisodes().getFirst());
        }else{
            for (Season season : selectedSeries.getSeasons()){
                season.setCurrentlyWatchingEpisode(-1);
                for (Episode episode : season.getEpisodes())
                    episode.setUnWatched();
            }

            selectedSeries.setCurrentlyWatchingSeason(-1);

            for (Episode e : selectedSeason.getEpisodes())
                updateDisc(e);
        }
    }
    @FXML
    void setWatchedSeason() {
        hideMenu();

        if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows")){
            setWatchedEpisode(selectedSeason
                    , selectedSeason.getEpisodes().getLast());
        }else{
            selectedSeason.setCurrentlyWatchingEpisode(-1);
            for (Episode episode : selectedSeason.getEpisodes())
                episode.setWatched();

            selectedSeries.setCurrentlyWatchingSeason(-1);

            for (Episode e : selectedSeason.getEpisodes())
                updateDisc(e);
        }

    }
    @FXML
    void setUnwatchedSeason() {
        hideMenu();

        if (DataManager.INSTANCE.currentLibrary.getType().equals("Shows")){
            setUnwatchedEpisode(selectedSeason
                    , selectedSeason.getEpisodes().getFirst());
        }else{
            selectedSeason.setCurrentlyWatchingEpisode(-1);
            for (Episode episode : selectedSeason.getEpisodes())
                episode.setUnWatched();

            selectedSeries.setCurrentlyWatchingSeason(-1);

            for (Episode e : selectedSeason.getEpisodes())
                updateDisc(e);
        }
    }
    //endregion

    //region EPISODE SELECTION
    public void selectEpisode(Episode episode) {
        if (selectedEpisodes.contains(episode)) {
            selectedEpisodes.remove(episode);
        } else {
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
        for (Episode episode : selectedEpisodes) {
            removeEpisode(episode);
        }
        selectedEpisodes.clear();
        selectedEpisode = null;
        selectionOptions.setVisible(false);
    }

    @FXML
    void deselectAll(ActionEvent event) {
        for (DiscController d : episodesControllers) {
            d.clearSelection();
        }
        selectionOptions.setVisible(false);
        selectedEpisodes.clear();
    }

    @FXML
    void selectAll(ActionEvent event) {
        for (DiscController d : episodesControllers) {
            if (!d.discSelected)
                d.selectDiscDesktop();
        }
    }
    //endregion

    //region WINDOW
    @FXML
    public void closeWindow() {
        stopMusic();
        VideoPlayer videoPlayer = null;
        if (videoPlayerController != null)
            videoPlayer = videoPlayerController.getVideoPlayer();

        if (videoPlayer != null)
            videoPlayer.stop();

        Task<Void> closeTask = new Task<>() {
            @Override
            protected Void call() {
                interrupted = true;

                analysisExecutor.close();
                try {
                    if (!analysisExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                        System.out.println("Executor did not terminate in the specified time. Attempting to shutdown now.");
                        List<Runnable> notExecutedTasks = analysisExecutor.shutdownNow();
                        System.out.println("Not executed tasks: " + notExecutedTasks.size());

                        if (!analysisExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                            System.err.println("Executor did not terminate after being forced to shut down.");
                        }
                    }
                } catch (InterruptedException e) {
                    System.err.println("Interrupted while waiting for termination.");
                    analysisExecutor.shutdownNow();
                    Thread.currentThread().interrupt();
                }

                return null;
            }
        };

        new Thread(closeTask).start();

        App.close();
    }
    //endregion

    //region IDENTIFICATION
    @FXML
    void correctIdentificationShow(ActionEvent event) {
        showBackgroundShadow();
        hideMenu();

        seriesMetadataToCorrect = false;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchSeries.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(getBaseFontSize());
            SearchSeriesController controller = fxmlLoader.getController();
            controller.initiValues(this, selectedSeries.getName(), true, tmdbApi, currentLibrary.getLanguage());
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
    void correctIdentificationMovie(ActionEvent event) {
        showBackgroundShadow();
        hideMenu();

        movieMetadataToCorrect = false;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchSeries.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(getBaseFontSize());
            SearchSeriesController controller = fxmlLoader.getController();
            controller.initiValues(this, selectedSeason.getName(), false, tmdbApi, currentLibrary.getLanguage());
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

    public void correctIdentificationShow() {
        showBackgroundShadow();
        downloadingContentTextStatic.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindowStatic.setVisible(true);

        App.initializeAnalysisExecutor();
        analysisExecutor.submit(() -> {
            try {
                File folder = new File(selectedSeries.getFolder());
                File[] filesInFolder = folder.listFiles();

                if (filesInFolder == null) return;

                // Remove Series Data
                DataManager.INSTANCE.deleteSeriesData(selectedSeries);
                selectedSeries.getSeasons().clear();

                // Scan Series
                scanTVShow(currentLibrary, folder, filesInFolder, true);

                if (selectedSeries.getSeasons().isEmpty())
                    return;

                Season season = selectedSeries.getSeasons().get(0);

                // Process background music
                List<YoutubeVideo> results = searchYoutube(selectedSeries.getName() + " main theme");

                if (results != null)
                    downloadMedia(season, results.get(0).watch_url);

                Platform.runLater(() -> {
                    downloadingContentWindowStatic.setVisible(false);
                    hideBackgroundShadow();
                    refreshSeries();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    downloadingContentWindowStatic.setVisible(false);
                    hideBackgroundShadow();
                    refreshSeries();
                });
            }
        });
        analysisExecutor.shutdown();
    }

    public void correctIdentificationMovie() {
        showBackgroundShadow();
        downloadingContentTextStatic.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindowStatic.setVisible(true);

        App.initializeAnalysisExecutor();
        analysisExecutor.submit(() -> {
            try {
                MovieMetadata metadata = downloadMovieMetadata(currentLibrary, selectedSeason.getThemdbID());

                if (metadata == null)
                    return;

                selectedSeason.setName(metadata.title);
                selectedSeason.setOverview(metadata.overview);
                selectedSeason.setYear(metadata.release_date.substring(0, metadata.release_date.indexOf("-")));
                selectedSeason.setScore((float) ((int) (metadata.vote_average * 10.0)) / 10.0f);
                selectedSeason.setImdbID(metadata.imdb_id);

                if (selectedSeason.getImdbID() == null)
                    selectedSeason.setImdbID("");

                Images images = downloadImages(currentLibrary, selectedSeason.getThemdbID());

                if (images != null)
                    loadImages(currentLibrary, selectedSeason.getId(), images, selectedSeason.getThemdbID(), false);

                File posterDir = new File("resources/img/seriesCovers/" + selectedSeason.getId() + "/0.jpg");
                if (posterDir.exists()) {
                    selectedSeason.setCoverSrc("resources/img/seriesCovers/" + selectedSeason.getId() + "/0.jpg");

                    if (selectedSeries.getSeasons().size() == 1 || selectedSeries.getCoverSrc().equals("resources/img/fileNotFound.png") || selectedSeries.getCoverSrc().isEmpty()) {
                        posterDir = new File("resources/img/seriesCovers/" + selectedSeason.getId() + "/0.jpg");
                        if (posterDir.exists()) {
                            Path sourcePath = Paths.get(posterDir.toURI());
                            Path destinationPath = Paths.get("resources/img/seriesCovers/" + selectedSeries.getId() + "/0.jpg");

                            DataManager.INSTANCE.createFolder("resources/img/seriesCovers/" + selectedSeries.getId() + "/");

                            try {
                                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                System.err.println("correctIdentificationMovie: error copying season cover image");
                            }

                            selectedSeries.setCoverSrc("resources/img/seriesCovers/" + selectedSeries.getId() + "/0.jpg");
                        }
                    }
                }

                downloadLogos(currentLibrary, selectedSeries, selectedSeason, selectedSeason.getThemdbID());
                saveBackground(selectedSeason, "resources/img/DownloadCache/" + selectedSeason.getThemdbID() + ".jpg", false);

                // Process background music
                List<YoutubeVideo> results = searchYoutube(selectedSeason.getName() + " main theme");

                if (results != null)
                    downloadMedia(selectedSeason, results.get(0).watch_url);

                if (selectedSeason.getEpisodes().size() == 1) {
                    Episode episode = selectedSeason.getEpisodes().get(0);
                    episode.setName(selectedSeason.getName());
                }

                for (Episode episode : selectedSeason.getEpisodes()) {
                    if (episode == null)
                        continue;

                    episode.setScore(selectedSeason.getScore());
                    episode.setOverview(selectedSeason.getOverview());
                    episode.setYear(selectedSeason.getYear());
                    episode.setSeasonNumber(selectedSeason.getSeasonNumber());
                    episode.setRuntime(metadata.runtime);

                    if (!selectedSeason.getImdbID().isEmpty())
                        setIMDBScore(selectedSeason.getImdbID(), episode);

                    setMovieThumbnail(episode, selectedSeason.getThemdbID());
                }

                refreshSeason(selectedSeason);

                Platform.runLater(() -> {
                    downloadingContentWindowStatic.setVisible(false);
                    hideBackgroundShadow();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    downloadingContentWindowStatic.setVisible(false);
                    hideBackgroundShadow();
                });
            }
        });
        analysisExecutor.shutdown();
    }

    public void setCorrectIdentificationShow(int newID) {
        seriesMetadataToCorrect = true;
        selectedSeries.setThemdbID(newID);
    }

    public void setCorrectIdentificationMovie(int newID) {
        movieMetadataToCorrect = true;
        selectedSeason.setThemdbID(newID);
    }
    //endregion

    //region CHANGE EPISODES GROUP
    @FXML
    void searchEpisodesGroup() {
        showBackgroundShadow();
        hideMenu();

        seriesMetadataToCorrect = false;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchEpisodesGroup.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(getBaseFontSize());
            SearchEpisodesGroupController controller = fxmlLoader.getController();
            controller.initiValues(this, selectedSeries.getThemdbID());
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

    public void changeEpisodesGroup(String id) {
        selectedSeries.setEpisodeGroupID(id);
        changeEpisodeGroup = true;
    }

    public void changeEpisodesGroup() {
        showBackgroundShadow();
        downloadingContentTextStatic.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindowStatic.setVisible(true);

        App.initializeAnalysisExecutor();
        analysisExecutor.submit(() -> {
            try {
                File folder = new File(selectedSeries.getFolder());
                File[] filesInFolder = folder.listFiles();

                if (filesInFolder == null)
                    return;

                Path srcPath = Paths.get(selectedSeries.getSeasons().getFirst().getBackgroundSrc());
                Path destDir = Paths.get("resources/img/DownloadCache/" + selectedSeries.getThemdbID() + ".jpg");

                Files.copy(srcPath, destDir, StandardCopyOption.REPLACE_EXISTING);

                // Remove Seasons
                for (Season season : selectedSeries.getSeasons())
                    DataManager.INSTANCE.deleteSeasonData(season);
                selectedSeries.getSeasons().clear();

                // Scan Series
                scanTVShow(currentLibrary, folder, filesInFolder, false);

                if (selectedSeries.getSeasons().isEmpty())
                    return;

                Season season = selectedSeries.getSeasons().get(0);

                // Process background music
                List<YoutubeVideo> results = searchYoutube(season.getName() + " main theme");

                if (results != null)
                    downloadMedia(season, results.get(0).watch_url);

                Platform.runLater(() -> {
                    clearImageCache();
                    downloadingContentWindowStatic.setVisible(false);
                    changeEpisodeGroup = false;
                    hideBackgroundShadow();
                    refreshSeries();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    clearImageCache();
                    downloadingContentWindowStatic.setVisible(false);
                    changeEpisodeGroup = false;
                    hideBackgroundShadow();
                    refreshSeries();
                });
            }
        });
        analysisExecutor.shutdown();
    }
    //endregion

    //region AUTOMATED FILE SEARCH
    @FXML
    public void searchFiles() {
        if (currentLibrary == null)
            return;

        if (!App.isConnectedToInternet) {
            App.showErrorMessage(App.textBundle.getString("connectionErrorTitle"), "", App.textBundle.getString("connectionErrorMessage"));
            return;
        }

        searchFiles(currentLibrary);
    }

    public void loadLibrary(Library library) {
        currentLibrary = library;

        updateLibraries();
        librarySelector.setText(library.getName());

        seasonScroll.setVisible(false);
        editLibraryButton.setDisable(false);
        searchFilesButton.setDisable(false);
        removeLibraryButton.setDisable(false);

        searchFiles(currentLibrary);
    }

    private void searchFiles(Library library) {
        hideMenu();

        searchingForFiles = true;

        //Disable library buttons in order to prevent the user from starting another process
        editLibraryButton.setDisable(true);
        removeLibraryButton.setDisable(true);
        searchFilesButton.setDisable(true);
        addLibraryButton.setDisable(true);

        //Search for new shows/episodes
        searchFilesTask(library);
    }

    private void searchFilesTask(Library library) {
        //Get every file and folder from within the root folders
        List<File> files = getFiles(library);

        if (files.isEmpty()) {
            acceptRemove = true;
            hardRemoveLibrary();

            searchingForFiles = false;

            DataManager.INSTANCE.saveData();

            //Restore library buttons
            editLibraryButton.setDisable(false);
            removeLibraryButton.setDisable(false);
            removeLibraryButton.setDisable(false);
            searchFilesButton.setDisable(false);
            addLibraryButton.setDisable(false);

            return;
        }

        numFilesToCheck = files.size();

        //Toggle Activity
        downloadingMetadataText.setDisable(false);
        ((BorderPane) downloadingMetadataText.getParent()).getRight().setVisible(true);
        showActivityBox();

        App.initializeAnalysisExecutor();
        Task<Void> searchMissingTask = new Task<>() {
            @Override
            protected Void call() {
                //Scan missing files
                DataManager.INSTANCE.scanForMissingFiles(currentLibrary);

                Platform.runLater(() -> {
                    seriesList = currentLibrary.getSeries();

                    if (!seriesList.contains(selectedSeries)) {
                        int index = -1;
                        for (Button btn : seriesButtons){
                            if (btn.getStyleClass().getFirst().equals("desktopButtonActive")){
                                index = seriesButtons.indexOf(btn);
                                break;
                            }
                        }

                        if (index != -1){
                            seriesButtons.remove(index);
                            seriesContainer.getChildren().remove(index);
                        }

                        selectedSeries = null;
                        if (!seriesList.isEmpty()) {
                            selectSeriesButton(seriesButtons.get(0));
                        } else {
                            seasonScroll.setVisible(false);
                        }
                    }else if (selectedSeries != null)
                        selectSeriesButton(seriesButtons.get(seriesList.indexOf(selectedSeries)));
                });
                return null;
            }
        };

        searchMissingTask.setOnSucceeded(e -> {
            //Execute a new thread for each file
            for (File file : files) {
                analysisExecutor.submit(() -> {
                    try {
                        if (!interrupted) {
                            if (library.getType().equals("Shows")) {
                                if (file.isDirectory()) {
                                    File[] filesInDir = file.listFiles();
                                    if (filesInDir != null)
                                        scanTVShow(library, file, filesInDir, false);
                                }
                            } else {
                                scanMovie(library, file);
                            }
                        }

                        synchronized (DesktopViewController.class) {
                            numFilesToCheck--;
                            if (numFilesToCheck == 0)
                                Platform.runLater(() -> postFileSearch(library));
                        }
                    } catch (Exception err) {
                        synchronized (DesktopViewController.class) {
                            numFilesToCheck--;
                            err.printStackTrace();
                            System.out.println("\nThread " + Thread.currentThread().getName() + " encountered an exception" + err.getMessage());
                            if (numFilesToCheck == 0)
                                Platform.runLater(() -> postFileSearch(library));
                        }
                    }
                });
            }
        });

        analysisExecutor.submit(searchMissingTask);
    }

    private static List<File> getFiles(Library library) {
        List<String> folders = library.getFolders();

        List<File> files = new ArrayList<>();
        for (String folderPath : folders) {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                File[] folderFiles = folder.listFiles();
                if (folderFiles != null) {
                    for (File file : folderFiles) {
                        if (file.exists()) {
                            if (file.isDirectory() || validVideoFile(file))
                                files.add(file);
                        }
                    }
                }
            }
        }
        return files;
    }

    private void postFileSearch(Library library) {
        if (library.getSeries().isEmpty()){
            acceptRemove = true;
            hardRemoveLibrary();
        }

        searchingForFiles = false;

        //Restore library buttons
        editLibraryButton.setDisable(false);
        removeLibraryButton.setDisable(false);
        removeLibraryButton.setDisable(false);
        searchFilesButton.setDisable(false);
        addLibraryButton.setDisable(false);

        //Update Activity
        downloadingMetadataText.setDisable(true);
        ((BorderPane) downloadingMetadataText.getParent()).getRight().setVisible(false);
        hideActivityBox();

        clearImageCache();
        DataManager.INSTANCE.saveData();
        downloadDefaultMusic(library);
    }

    public void downloadDefaultMusic(Library library) {
        Platform.runLater(() -> {
            downloadingSongsText.setDisable(false);
            ((BorderPane) downloadingSongsText.getParent()).getRight().setVisible(true);
            showActivityBox();
        });

        for (Series series : library.getSeries()) {
            if (series == null)
                continue;

            if (interrupted)
                break;

            analysisExecutor.submit(() -> {
                try {
                    if (library.getType().equals("Shows")) {
                        Season season = series.getSeasons().getFirst();

                        if (season.getMusicSrc().isEmpty()) {
                            List<YoutubeVideo> results = searchYoutube(series.getName() + " main theme");

                            if (results != null)
                                downloadMedia(season, results.getFirst().watch_url);
                        }
                    } else {
                        for (Season season : series.getSeasons()) {
                            if (interrupted)
                                break;

                            if (season.getMusicSrc().isEmpty()) {
                                List<YoutubeVideo> results = searchYoutube(season.getName() + " main theme");

                                if (results != null)
                                    downloadMedia(season, results.getFirst().watch_url);
                            }
                        }
                    }

                    Platform.runLater(this::postDownloadDefaultMusic);
                } catch (Exception e) {
                    Platform.runLater(this::postDownloadDefaultMusic);
                }
            });
        }

        analysisExecutor.shutdown();
    }

    public void postDownloadDefaultMusic() {
        //Update Activity
        downloadingSongsText.setDisable(true);
        ((BorderPane) downloadingSongsText.getParent()).getRight().setVisible(false);
        hideActivityBox();

        searchingForFiles = false;
    }

    private void scanTVShow(Library library, File directory, File[] filesInDir, boolean updateMetadata) {
        //All video files in directory (not taking into account two subdirectories ahead, like Series/Folder1/Folder2/File)
        List<File> videoFiles = new ArrayList<>();

        //Analyze all files in directory except two subdirectories ahead
        for (File file : filesInDir) {
            if (file.isDirectory()) {
                File[] filesInDirectory = file.listFiles();

                if (filesInDirectory == null)
                    continue;

                for (File f : filesInDirectory) {
                    if (f.isFile() && validVideoFile(f))
                        videoFiles.add(f);
                }
            } else {
                if (validVideoFile(file))
                    videoFiles.add(file);
            }
        }

        if (videoFiles.isEmpty())
            return;

        //region CREATE/EDIT SERIES
        Series series;
        boolean exists = false;

        if (library.getAnalyzedFolders().get(directory.getAbsolutePath()) != null) {
            series = library.getSeries(library.getAnalyzedFolders().get(directory.getAbsolutePath()));

            if (series == null)
                return;

            exists = true;

            series.setAnalyzingFiles(true);
            if (selectedSeries == series) {
                Platform.runLater(() -> {
                    Button showButton = seriesButtons.get(seriesList.indexOf(series));

                    MFXProgressSpinner loadingIndicator = (MFXProgressSpinner) ((BorderPane) showButton.getGraphic()).getRight();
                    loadingIndicator.setVisible(true);
                });
            }
        } else {
            series = new Series(library.getSeries().size());
            series.setFolder(directory.getAbsolutePath());
            library.getAnalyzedFolders().put(directory.getAbsolutePath(), series.getId());
        }

        int themdbID = series.getThemdbID();

        //This means that this is a new Folder to be analyzed, so we need to search for the show metadata
        if (themdbID == -1) {
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
            TvResultsPage tvResults = tmdbApi.getSearch().searchTv(finalName, Integer.valueOf(year), library.getLanguage(), true, 1);

            if (tvResults.getTotalResults() == 0)
                return;

            //Set themdbID as the id of the first result
            themdbID = tvResults.getResults().get(0).getId();
            series.setThemdbID(themdbID);

            //Set values in order to set the attributes from the search result in the new series and download images
            updateMetadata = true;
        }

        SeriesMetadata seriesMetadata = downloadSeriesMetadata(library, themdbID);

        if (seriesMetadata == null)
            return;

        if (updateMetadata)
            setSeriesMetadataAndImages(library, series, seriesMetadata, updateMetadata);
        //endregion

        //Download metadata for each season of the show
        List<SeasonMetadata> seasonsMetadata = new ArrayList<>();
        for (SeasonMetadataBasic seasonMetadataBasicBasic : seriesMetadata.seasons) {
            SeasonMetadata seasonMetadata = downloadSeasonMetadata(library, series.getThemdbID(), seasonMetadataBasicBasic.season_number);

            if (seasonMetadata != null) {
                seasonsMetadata.add(seasonMetadata);
            }
        }

        //Download Episode Group Metadata
        SeasonsGroupMetadata episodesGroup = null;
        if (!series.getEpisodeGroupID().isEmpty())
            episodesGroup = getEpisodesGroup(series);

        //Process each episode
        for (File video : videoFiles) {
            if (interrupted)
                break;

            if (library.getAnalyzedFiles().get(video.getAbsolutePath()) != null)
                continue;

            processEpisode(library, series, video, seasonsMetadata, episodesGroup, exists);
        }

        //Change the name of every season to match the Episodes Group
        if (!series.getEpisodeGroupID().isEmpty() && episodesGroup != null) {
            for (Season season : series.getSeasons()) {
                for (EpisodeGroup episodeGroup : episodesGroup.groups) {
                    if (episodeGroup.order == season.getSeasonNumber()) {
                        season.setName(episodeGroup.name);
                    }
                }
            }
        }else if (series.getSeasons().size() > 1){
            //If two seasons have the same name
            if (series.getSeasons().getFirst().getName().equals(series.getSeasons().get(1).getName())){
                for (Season season : series.getSeasons()){
                    if (season.getSeasonNumber() != 0)
                        season.setName(App.textBundle.getString("season") + " " + season.getSeasonNumber());
                }
            }
        }

        if (series.getSeasons().isEmpty())
            library.getAnalyzedFolders().remove(series.getFolder());

        //Hide loading circle in view
        series.setAnalyzingFiles(false);
        Platform.runLater(() -> {
            for (Series s : seriesList) {
                if (s == series) {
                    Button showButton = seriesButtons.get(seriesList.indexOf(s));

                    MFXProgressSpinner loadingIndicator = (MFXProgressSpinner) ((BorderPane) showButton.getGraphic()).getRight();
                    loadingIndicator.setVisible(false);
                    break;
                }
            }

            if (series == selectedSeries)
                refreshSeries();
        });
    }

    private void setSeriesMetadataAndImages(Library library, Series series, SeriesMetadata seriesMetadata, boolean downloadImages) {
        //Set metadata
        series.setName(seriesMetadata.name);
        series.setOverview(seriesMetadata.overview);
        series.setScore((float) ((int) (seriesMetadata.vote_average * 10.0)) / 10.0f);
        series.setPlaySameMusic(true);

        String year = seriesMetadata.first_air_date.substring(0, seriesMetadata.first_air_date.indexOf("-")) + "-";
        if (!seriesMetadata.in_production)
            year += seriesMetadata.last_air_date.substring(0, seriesMetadata.first_air_date.indexOf("-"));
        series.setYear(year);

        series.setNumberOfSeasons(seriesMetadata.number_of_seasons);
        series.setNumberOfEpisodes(seriesMetadata.number_of_episodes);

        if (seriesMetadata.production_companies != null && !seriesMetadata.production_companies.isEmpty()){
            String finalStr = "";
            if (seriesMetadata.production_companies.size() > 3)
                finalStr = "...";

            StringBuilder productionCompanies = new StringBuilder();
            for (ProductionCompany pC : seriesMetadata.production_companies){
                if (seriesMetadata.production_companies.indexOf(pC) != 0)
                    productionCompanies.append(", ");

                productionCompanies.append(pC.name);
            }

            productionCompanies.append(finalStr);
            series.setProductionStudios(productionCompanies.toString());
        }

        List<Genre> genres = seriesMetadata.genres;

        if (genres != null) {
            List<String> genreList = new ArrayList<>();
            for (Genre genre : genres) {
                genreList.add(genre.name);
            }

            series.setGenres(genreList);
        }

        //Download posters and logos for the show
        if (downloadImages) {
            Images images = downloadImages(library, series.getThemdbID());

            if (images != null)
                loadImages(library, series.getId(), images, series.getThemdbID(), false);

            File[] files = new File("resources/img/seriesCovers/" + series.getId() + "/").listFiles();

            if (files != null) {
                File posterDir = files[0];
                series.setCoverSrc("resources/img/seriesCovers/" + series.getId() + "/" + posterDir.getName());
            }
        }
    }

    private void loadImages(Library library, String id, Images images, int tmdbID, boolean onlyPosters) {
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        //region Process Posters
        List<Poster> posterList = images.posters;
        if (posterList != null && !posterList.isEmpty()) {
            saveCover(id, 0, imageBaseURL + posterList.get(0).file_path);

            Platform.runLater(() -> {
                imageDownloadProcesses++;
                downloadingImagesText.setDisable(false);
                ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(true);
                showActivityBox();
            });

            analysisExecutor.submit(() -> {
                try {
                    int processedFiles = 1;
                    for (int i = processedFiles; i < posterList.size(); i++) {
                        if (processedFiles == 40)
                            break;

                        saveCover(id, processedFiles, imageBaseURL + posterList.get(i).file_path);

                        processedFiles++;
                    }

                    Platform.runLater(() -> {
                        imageDownloadProcesses--;

                        if (imageDownloadProcesses == 0){
                            downloadingImagesText.setDisable(true);
                            ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(false);
                            hideActivityBox();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("loadImages: thread task cancelled " + e.getMessage());
                }
            });
        }
        //endregion

        //region Process Background
        if (!onlyPosters) {
            List<Backdrop> backdropList = images.backdrops;
            if (backdropList != null && !backdropList.isEmpty()) {
                String path;
                if (library.getType().equals("Shows"))
                    path = backdropList.get(0).file_path;
                else
                    path = backdropList.get(backdropList.size() - 1).file_path;

                try {
                    URI uri = new URI(imageBaseURL + path);
                    URL url = uri.toURL();

                    BufferedImage image = ImageIO.read(url);
                    ImageIO.write(image, "jpg", new File("resources/img/DownloadCache/" + tmdbID + ".jpg"));
                    image.flush();
                } catch (IOException e) {
                    System.err.println("DesktopViewController: Downloaded background not saved");
                } catch (URISyntaxException e) {
                    System.err.println("DesktopViewController: Invalid URL syntax");
                }
            }
        }
        //endregion
    }

    private SeasonsGroupMetadata getEpisodesGroup(Series series) {
        String seasonGroupID = "";
        if (series.getEpisodeGroupID().isEmpty()) {
            //region GET EPISODE GROUPS
            Request requestGroups = new Request.Builder()
                    .url("https://api.themoviedb.org/3/tv/" + series.getThemdbID() + "/episode_groups")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                    .build();

            try (Response response = client.newCall(requestGroups).execute()) {
                if (response.isSuccessful()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    assert response.body() != null;
                    SeasonsGroupRoot groups = objectMapper.readValue(response.body().string(), SeasonsGroupRoot.class);


                    for (SeasonsGroup seasonsGroup : groups.results) {
                        if (seasonsGroup.type == 6) {                                       //6 == Seasons
                            seasonGroupID = seasonsGroup.id;
                        } else if (seasonGroupID.isEmpty() && seasonsGroup.type == 5)        //5 == Sagas
                            seasonGroupID = seasonsGroup.id;
                    }

                    if (seasonGroupID.isEmpty()) {
                        for (SeasonsGroup seasonsGroup : groups.results) {
                            if (seasonsGroup.name.equals("All episodes")) {
                                seasonGroupID = seasonsGroup.id;
                            }
                        }
                    }
                } else {
                    System.out.println("getEpisodeGroup: Response not successful: " + response.code());
                }
            } catch (IOException e) {
                System.out.println("getEpisodeGroup: Response not successful: " + e.getMessage());
            }
            //endregion
        } else {
            seasonGroupID = series.getEpisodeGroupID();
        }

        //region GET EPISODE GROUP DETAILS
        if (!seasonGroupID.isEmpty()) {
            Request requestGroup = new Request.Builder()
                    .url("https://api.themoviedb.org/3/tv/episode_group/" + seasonGroupID)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                    .build();

            try (Response response = client.newCall(requestGroup).execute()) {
                if (response.isSuccessful()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    assert response.body() != null;
                    return objectMapper.readValue(response.body().string(), SeasonsGroupMetadata.class);
                } else {
                    System.out.println("getEpisodeGroup: Response not successful: " + response.code());
                }
            } catch (IOException e) {
                System.out.println("getEpisodeGroup: Response not successful");
            }
        }
        //endregion

        return null;
    }

    private static boolean validVideoFile(File file) {
        if (file.isDirectory())
            return false;

        String videoExtension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
        videoExtension = videoExtension.toLowerCase();

        return videoExtension.equals(".mkv") || videoExtension.equals(".mp4") || videoExtension.equals(".avi") || videoExtension.equals(".mov")
                || videoExtension.equals(".wmv") || videoExtension.equals(".mpeg") || videoExtension.equals(".m2ts");
    }

    private void processEpisode(Library library, Series series, File file, List<SeasonMetadata> seasonsMetadata, SeasonsGroupMetadata episodesGroup, boolean seriesExists) {
        //SeasonMetadataBasic and episode metadata to find for the current file
        SeasonMetadata seasonMetadata = null;
        EpisodeMetadata episodeMetadata = null;

        int realSeason = 0;
        int realEpisode = -1;

        //Name of the file without the extension
        String fullName = file.getName().substring(0, file.getName().lastIndexOf("."));
        List<Integer> seasonEpisode = detectSeasonEpisode(fullName);

        //If there is no season and episode numbers
        if (seasonEpisode == null || seasonEpisode.isEmpty()){
            final String regexOnlyEpisode = "(?i)(?:\\([^\\)]*\\))?\\s*(?<episode>[0-9]{1,5})(?=\\s|-|\\.|$)";

            Pattern newPattern = Pattern.compile(regexOnlyEpisode, Pattern.MULTILINE);
            Matcher newMatch = newPattern.matcher(fullName);

            //return if the second regular expression did not find match
            if (!newMatch.find())
                return;

            int absoluteNumber = Integer.parseInt(newMatch.group("episode"));
            boolean episodeFound = false;
            int episodeNumber = 1;

            //Find the real season and episode for the file
            for (SeasonMetadata season : seasonsMetadata) {
                if (season.season_number >= 1) {
                    if ((episodeNumber + season.episodes.size()) < absoluteNumber) {
                        episodeNumber += season.episodes.size();
                        continue;
                    }

                    for (int i = 0; i < season.episodes.size(); i++) {
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
            int seasonNumber = seasonEpisode.getFirst();
            int episodeNumber = seasonEpisode.getLast();

            realSeason = seasonNumber;
            realEpisode = episodeNumber;

            boolean toFindMetadata = true;
            for (SeasonMetadata season : seasonsMetadata) {
                if (season.season_number == seasonNumber) {
                    toFindMetadata = false;
                    break;
                }
            }

            if (toFindMetadata || !series.getEpisodeGroupID().isEmpty()) {
                if (episodesGroup == null)
                    episodesGroup = getEpisodesGroup(series);

                boolean found = false;
                if (episodesGroup != null) {
                    for (EpisodeGroup episodeGroup : episodesGroup.groups) {
                        if (episodeGroup.order != seasonNumber)
                            continue;

                        for (SeasonsGroupEpisode episode : episodeGroup.episodes) {
                            if ((episode.order + 1) == episodeNumber) {
                                seasonNumber = episode.season_number;
                                episodeNumber = episode.episode_number;
                                found = true;
                                break;
                            }
                        }

                        if (found)
                            break;
                    }

                    for (SeasonMetadata seasonMeta : seasonsMetadata) {
                        if (seasonMeta.season_number == seasonNumber) {
                            seasonMetadata = seasonMeta;

                            for (EpisodeMetadata episodeMeta : seasonMeta.episodes) {
                                if (episodeMeta.episode_number == episodeNumber) {
                                    episodeMetadata = episodeMeta;
                                    break;
                                }
                            }

                            break;
                        }
                    }
                } else {
                    return;
                }
            } else {
                for (SeasonMetadata seasonMeta : seasonsMetadata) {
                    if (seasonMeta.season_number == seasonNumber) {
                        seasonMetadata = seasonMeta;

                        for (EpisodeMetadata episodeMeta : seasonMeta.episodes) {
                            if (episodeMeta.episode_number == episodeNumber) {
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

        if (season == null) {
            season = new Season();
            series.addSeason(season);

            season.setName(seasonMetadata.name);
            season.setOverview(seasonMetadata.overview);
            season.setYear(seasonMetadata.episodes.get(0).air_date.substring(0, seasonMetadata.episodes.get(0).air_date.indexOf("-")));
            season.setProductionStudios(series.getProductionStudios());

            if (realEpisode != -1)
                season.setSeasonNumber(realSeason);
            else
                season.setSeasonNumber(seasonMetadata.season_number);

            season.setScore((float) ((int) (seasonMetadata.vote_average * 10.0)) / 10.0f);
            season.setSeriesID(series.getId());

            //Save season background
            if (season.getBackgroundSrc().isEmpty() || season.getBackgroundSrc().equals("resources/img/DefaultBackground.png")) {
                if (series.getSeasons().size() > 1) {
                    Season s = null;

                    for (Season seasonToFind : series.getSeasons()) {
                        if (!seasonToFind.getBackgroundSrc().isEmpty()) {
                            s = seasonToFind;
                            break;
                        }
                    }

                    if (s != null)
                        saveBackground(season, s.getBackgroundSrc(), true);
                } else {
                    File f = new File("resources/img/DownloadCache/" + series.getThemdbID() + ".jpg");
                    if (f.exists())
                        saveBackground(season, "resources/img/DownloadCache/" + series.getThemdbID() + ".jpg", false);
                    else
                        saveBackground(season, "resources/img/DefaultBackground.png", false);
                }
            }

            downloadSeasonCredits(series, season, seasonMetadata);

            if (series.getSeasons().size() == 1)
                downloadLogos(library, series, season, series.getThemdbID());

            if (season.getSeasonNumber() == 0)
                season.setOrder(100);

            if (series == selectedSeries) {
                addSeasonButton(season);
            }
        }

        Episode episode;
        if (realEpisode != -1)
            episode = season.getEpisode(realEpisode);
        else
            episode = season.getEpisode(episodeMetadata.episode_number);

        if (episode != null) {
            episode.setVideoSrc(file.getAbsolutePath());
            return;
        }

        episode = new Episode();
        season.addEpisode(episode);

        //Set the metadata for the new episode
        episode.setSeasonID(season.getId());
        episode.setVideoSrc(file.getAbsolutePath());
        setEpisodeData(library, episode, episodeMetadata, series, realEpisode);

        getMediaInfo(episode);

        downloadEpisodeCrew(episode, episodeMetadata);

        library.getAnalyzedFiles().put(file.getAbsolutePath(), episode.getId());

        if (!seriesExists && series.getSeasons().size() == 1 && season.getEpisodes().size() == 1) {
            series.setAnalyzingFiles(true);
            addSeries(library, series);

            //Set loading circle in view if series is already added
            Platform.runLater(() -> {
                for (Series s : seriesList) {
                    if (s == series) {
                        Button showButton = seriesButtons.get(seriesList.indexOf(s));

                        MFXProgressSpinner loadingIndicator = (MFXProgressSpinner) ((BorderPane) showButton.getGraphic()).getRight();
                        loadingIndicator.setVisible(true);
                        break;
                    }
                }
            });
        }
    }

    private void setEpisodeData(Library library, Episode episode, EpisodeMetadata episodeMetadata, Series show, int realEpisode) {
        episode.setName(episodeMetadata.name);
        episode.setOverview(episodeMetadata.overview);
        episode.setRuntime(episodeMetadata.runtime);

        if (realEpisode != -1)
            episode.setEpisodeNumber(realEpisode);
        else
            episode.setEpisodeNumber(episodeMetadata.episode_number);

        episode.setYear(Utils.episodeDateFormat(episodeMetadata.air_date, library.getLanguage()));
        episode.setScore((float) ((int) (episodeMetadata.vote_average * 10.0)) / 10.0f);
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        //Create folder for episode thumbnails
        DataManager.INSTANCE.createFolder("resources/img/discCovers/" + episode.getId() + "/");

        MovieImages images = tmdbApi.getTvEpisodes().getEpisode(show.getThemdbID(), episodeMetadata.season_number, episodeMetadata.episode_number, null, TmdbTvEpisodes.EpisodeMethod.images).getImages();
        List<Artwork> thumbnails = images.getStills();

        List<String> thumbnailsUrls = new ArrayList<>();
        if (thumbnails != null) {
            for (Artwork artwork : thumbnails) {
                thumbnailsUrls.add(imageBaseURL + artwork.getFilePath());
            }
        }

        //region THUMBNAIL DOWNLOADER
        if (!thumbnailsUrls.isEmpty()) {
            saveThumbnail(episode, thumbnailsUrls.get(0), 0);

            Platform.runLater(() -> {
                imageDownloadProcesses++;
                downloadingImagesText.setDisable(false);
                ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(true);
                showActivityBox();
            });

            analysisExecutor.submit(() -> {
                try {
                    for (int i = 1; i < thumbnailsUrls.size(); i++) {
                        if (i == 10)
                            break;

                        saveThumbnail(episode, thumbnailsUrls.get(i), i);
                    }

                    Platform.runLater(() -> {
                        imageDownloadProcesses--;

                        if (imageDownloadProcesses == 0){
                            downloadingImagesText.setDisable(true);
                            ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(false);
                            hideActivityBox();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("setEpisodeData: thread task cancelled " + e.getMessage());
                }
            });
        }
        //endregion

        File img = new File("resources/img/discCovers/" + episode.getId() + "/0.jpg");
        if (!img.exists()) {
            episode.setImgSrc("resources/img/Default_video_thumbnail.jpg");
        } else {
            episode.setImgSrc("resources/img/discCovers/" + episode.getId() + "/0.jpg");
        }
    }
    private void downloadEpisodeCrew(Episode episode, EpisodeMetadata episodeMetadata){
        Request requestGroups = new Request.Builder()
                .url("https://api.themoviedb.org/3/tv/" + episodeMetadata.show_id + "/season/" + episodeMetadata.season_number + "/episode/" + episodeMetadata.episode_number + "credits?language=en-US")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                .build();

        try (Response response = client.newCall(requestGroups).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                assert response.body() != null;
                Credits credits = objectMapper.readValue(response.body().string(), Credits.class);

                List<String> directors = new ArrayList<>();
                List<String> writers = new ArrayList<>();
                for (Crew person : credits.crew){
                    if (person.job.equals("Director"))
                        directors.add(person.name);
                    else if (person.job.equals("Writer"))
                        writers.add(person.name);
                }

                StringBuilder directorsString = new StringBuilder();
                for (String director : directors){
                    if (director.indexOf(director) != 0)
                        directorsString.append(", ");

                    directorsString.append(director);
                }
                episode.setDirectedBy(directorsString.toString());

                StringBuilder writersString = new StringBuilder();
                for (String writer : writers){
                    if (writer.indexOf(writer) != 0)
                        writersString.append(", ");

                    writersString.append(writer);
                }
                episode.setWrittenBy(writersString.toString());
            } else {
                System.out.println("downloadEpisodeCrew: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("downloadEpisodeCrew: Response not successful: " + e.getMessage());
        }
    }
    private void downloadSeasonCredits(Series series, Season season, SeasonMetadata seasonMetadata){
        Request requestGroups = new Request.Builder()
                .url("https://api.themoviedb.org/3/tv/" + series.getThemdbID() + "/season/" + seasonMetadata.season_number + "/credits?language=en-US")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                .build();

        try (Response response = client.newCall(requestGroups).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                assert response.body() != null;
                Credits credits = objectMapper.readValue(response.body().string(), Credits.class);

                season.getCast().addAll(credits.cast);

                if (credits.guest_stars != null)
                    season.getCast().addAll(credits.guest_stars);

                String originalSourceCreator = "";
                String originalMusicComposer = "";
                for (Crew person : credits.crew){
                    if (person.job.equals("Author") || person.job.equals("Novel") || person.job.equals("Original Series Creator")
                            || person.job.equals("Comic Book") || person.job.equals("Idea") || person.job.equals("Original Story") || person.job.equals("Story")
                            || person.job.equals("Story by") || person.job.equals("Book"))
                        originalSourceCreator = person.name;
                    else if (person.job.equals("Original Music Composer"))
                        originalMusicComposer = person.name;
                }

                season.setCreator(originalSourceCreator);
                season.setMusicComposer(originalMusicComposer);
            } else {
                System.out.println("downloadSeasonCredits: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("downloadSeasonCredits: Response not successful: " + e.getMessage());
        }
    }
    private void saveCover(String id, int i, String originalImage) {
        DataManager.INSTANCE.createFolder("resources/img/seriesCovers/" + id + "/");

        try {
            URI uri = new URI(originalImage);
            URL url = uri.toURL();

            BufferedImage bufferedImage = ImageIO.read(url);

            //Compress image
            Thumbnails.of(bufferedImage)
                    .size(720, 1080)
                    .outputFormat("jpg")
                    .outputQuality(0.8)
                    .toFile("resources/img/seriesCovers/" + id + "/" + i + ".jpg");

            bufferedImage.flush();
        } catch (IOException e) {
            System.err.println("DesktopViewController: Error compressing image");
        } catch (URISyntaxException e) {
            System.err.println("DesktopViewController: Invalid URL syntax " + originalImage);
        }
    }

    public void saveLogo(String id, int i, String urlImage) {
        DataManager.INSTANCE.createFolder("resources/img/logos/" + id + "/");

        try {
            URI uri = new URI(urlImage);
            URL url = uri.toURL();

            //Verify if it is an SVG file and skip
            if (urlImage.endsWith(".svg"))
                return;

            BufferedImage image = ImageIO.read(url);

            if (image != null) {
                ImageIO.write(image, "png", new File("resources/img/logos/" + id + "/" + i + ".png"));
                image.flush();
            } else {
                System.err.println("DesktopViewController: Failed to read image from URL " + urlImage);
            }
        } catch (IOException e) {
            System.err.println("DesktopViewController: Downloaded logo not saved");
        } catch (URISyntaxException e) {
            System.err.println("DesktopViewController: Invalid URL syntax " + urlImage);
        }
    }

    private void scanMovie(Library library, File f) {
        //If the movie exists, do not add a new Card in the main view
        boolean exists = false;

        if (!f.isDirectory()) {
            //region MOVIE FILE ONLY
            if (!validVideoFile(f)) {
                return;
            }

            if (library.getAnalyzedFiles().get(f.getAbsolutePath()) != null)
                return;

            //region CREATE/EDIT SERIES
            Series series = new Series(library.getSeries().size());
            series.setFolder(f.getAbsolutePath());
            library.getAnalyzedFolders().put(f.getAbsolutePath(), series.getId());

            NameYearContainer result = extractNameAndYear(f.getName().substring(0, f.getName().lastIndexOf(".")));

            String movieName = result.name;
            String year = result.year;

            if (year.isEmpty())
                year = "1";

            //Search show by name of the folder
            int themdbID = searchFirstMovie(library, movieName, Integer.parseInt(year));

            if (themdbID == -1) {
                series.setName(movieName);
                Season newSeason = new Season();
                series.addSeason(newSeason);
                newSeason.setName(movieName);
                newSeason.setYear(year);
                newSeason.setSeriesID(series.getId());
                newSeason.setSeasonNumber(series.getSeasons().size());

                library.getAnalyzedFolders().put(f.getAbsolutePath(), series.getId());

                saveDiscWithoutMetadata(library, f, newSeason);
                addSeries(library, series);
                return;
            }

            MovieMetadata movieMetadata = downloadMovieMetadata(library, themdbID);

            series.setName(movieMetadata.title);
            Season season = new Season();
            series.addSeason(season);
            season.setFolder(f.getAbsolutePath());
            season.setSeriesID(series.getId());
            season.setThemdbID(themdbID);
            season.setImdbID(movieMetadata.imdb_id);

            if (movieMetadata.production_companies != null && !movieMetadata.production_companies.isEmpty()){
                String finalStr = "";
                if (movieMetadata.production_companies.size() > 3)
                    finalStr = "...";

                StringBuilder productionCompanies = new StringBuilder();
                for (ProductionCompany pC : movieMetadata.production_companies){
                    if (movieMetadata.production_companies.indexOf(pC) != 0)
                        productionCompanies.append(", ");

                    productionCompanies.append(pC.name);
                }

                productionCompanies.append(finalStr);
                season.setProductionStudios(productionCompanies.toString());
            }

            if (season.getImdbID() == null)
                season.setImdbID("");

            season.setName(movieMetadata.title);
            season.setOverview(movieMetadata.overview);
            season.setYear(movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-")));
            season.setScore((float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f);
            season.setSeasonNumber(series.getSeasons().size() + 1);

            List<Genre> genres = movieMetadata.genres;

            if (genres != null) {
                List<String> genreList = new ArrayList<>();
                for (Genre genre : genres) {
                    genreList.add(genre.name);
                }

                season.setGenres(genreList);
            }

            library.getSeasonFolders().put(f.getAbsolutePath(), season.getId());

            Images images = downloadImages(library, season.getThemdbID());

            if (images != null) {
                loadImages(library, season.getId(), images, season.getThemdbID(), false);
                loadImages(library, series.getId(), images, season.getThemdbID(), true);
            }

            File posterDir = new File("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
            if (posterDir.exists()) {
                season.setCoverSrc("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
            }

            posterDir = new File("resources/img/seriesCovers/" + series.getId() + "/0.jpg");
            if (posterDir.exists()) {
                series.setCoverSrc("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
            }

            downloadLogos(library, series, season, season.getThemdbID());
            saveBackground(season, "resources/img/DownloadCache/" + season.getThemdbID() + ".jpg", false);
            downloadMovieCastAndCrew(season, movieMetadata);

            processMovie(library, f, season, movieMetadata.runtime);

            addSeries(library, series);
            //endregion
        } else {
            File[] filesInDir = f.listFiles();
            if (filesInDir == null)
                return;

            List<File> folders = new ArrayList<>();
            List<File> filesInRoot = new ArrayList<>();
            for (File file : filesInDir) {
                if (file.isDirectory())
                    folders.add(file);
                else
                    filesInRoot.add(file);
            }

            Series series;

            if (library.getAnalyzedFolders().get(f.getAbsolutePath()) != null) {
                series = library.getSeries(library.getAnalyzedFolders().get(f.getAbsolutePath()));
                exists = true;

                if (series == null)
                    return;
            } else {
                series = new Series(library.getSeries().size());
                series.setName(f.getName());
                series.setFolder(f.getAbsolutePath());
                library.getAnalyzedFolders().put(f.getAbsolutePath(), series.getId());
            }

            if (!folders.isEmpty()) {
                //region FOLDERS CORRESPONDING DIFFERENT MOVIES FROM A COLLECTION
                for (File folder : folders) {
                    File[] filesInFolder = folder.listFiles();
                    if (filesInFolder == null)
                        continue;

                    boolean videoFiles = false;
                    for (File file : filesInFolder)
                        if (validVideoFile(file))
                            videoFiles = true;

                    if (!videoFiles)
                        continue;

                    NameYearContainer result = extractNameAndYear(folder.getName());

                    String movieName = result.name;
                    String year = result.year;

                    if (year.isEmpty())
                        year = "1";

                    boolean updateMetadata = false;

                    Season season;
                    if (library.getSeasonFolders().get(folder.getAbsolutePath()) != null) {
                        season = series.getSeason(library.getSeasonFolders().get(folder.getAbsolutePath()));
                    } else {
                        season = new Season();
                        series.addSeason(season);
                        library.getSeasonFolders().put(folder.getAbsolutePath(), season.getId());
                        season.setSeasonNumber(series.getSeasons().size() + 1);
                        season.setSeriesID(series.getId());
                        season.setFolder(folder.getAbsolutePath());

                        updateMetadata = true;
                    }

                    int themdbID = season.getThemdbID();
                    MovieMetadata movieMetadata = null;

                    if (themdbID == -1) {
                        themdbID = searchFirstMovie(library, movieName, Integer.parseInt(year));
                    }

                    if (themdbID != -1)
                        movieMetadata = downloadMovieMetadata(library, themdbID);

                    season.setThemdbID(themdbID);

                    if (movieMetadata == null) {
                        season.setName(movieName);

                        for (File file : filesInFolder) {
                            if (file.isFile() && validVideoFile(file))
                                saveDiscWithoutMetadata(library, file, season);
                        }

                        if (season.getEpisodes().isEmpty())
                            series.removeSeason(season);

                        continue;
                    }

                    if (updateMetadata) {
                        season.setSeriesID(series.getId());

                        season.setName(movieMetadata.title);
                        season.setOverview(movieMetadata.overview);
                        season.setYear(movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-")));
                        season.setScore((float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f);
                        season.setImdbID(movieMetadata.imdb_id);

                        if (season.getImdbID() == null)
                            season.setImdbID("");

                        List<Genre> genres = movieMetadata.genres;

                        if (genres != null) {
                            List<String> genreList = new ArrayList<>();
                            for (Genre genre : genres) {
                                genreList.add(genre.name);
                            }

                            season.setGenres(genreList);
                        }

                        Images images = downloadImages(library, season.getThemdbID());

                        if (images != null) {
                            loadImages(library, season.getId(), images, season.getThemdbID(), false);
                            loadImages(library, series.getId(), images, season.getThemdbID(), true);
                        }

                        File posterDir = new File("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
                        if (posterDir.exists()) {
                            season.setCoverSrc("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
                        }

                        posterDir = new File("resources/img/seriesCovers/" + series.getId() + "/0.jpg");
                        if (posterDir.exists()) {
                            series.setCoverSrc("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
                        }

                        downloadLogos(library, series, season, season.getThemdbID());
                        saveBackground(season, "resources/img/DownloadCache/" + season.getThemdbID() + ".jpg", false);
                        downloadMovieCastAndCrew(season, movieMetadata);

                        if (movieMetadata.production_companies != null && !movieMetadata.production_companies.isEmpty()){
                            String finalStr = "";
                            if (movieMetadata.production_companies.size() > 3)
                                finalStr = "...";

                            StringBuilder productionCompanies = new StringBuilder();
                            for (ProductionCompany pC : movieMetadata.production_companies){
                                if (movieMetadata.production_companies.indexOf(pC) != 0)
                                    productionCompanies.append(", ");

                                productionCompanies.append(pC.name);
                            }

                            productionCompanies.append(finalStr);
                            season.setProductionStudios(productionCompanies.toString());
                        }
                    }

                    for (File file : filesInFolder) {
                        if (file.isFile() && validVideoFile(file))
                            processMovie(library, file, season, movieMetadata.runtime);
                    }

                    if (season.getEpisodes().isEmpty())
                        series.removeSeason(season);
                }

                if (!exists)
                    addSeries(library, series);
                else if (series == selectedSeries)
                    refreshSeries();
                //endregion
            } else if (!filesInRoot.isEmpty()) {
                //region MOVIE FILE/CONCERT FILES INSIDE FOLDER
                NameYearContainer result = extractNameAndYear(f.getName());

                String movieName = result.name;
                String year = result.year;

                if (year.isEmpty())
                    year = "1";

                boolean updateMetadata = false;

                Season season;
                if (library.getSeasonFolders().get(f.getAbsolutePath()) != null) {
                    season = series.getSeason(library.getSeasonFolders().get(f.getAbsolutePath()));
                } else {
                    season = new Season();
                    series.addSeason(season);
                    library.getSeasonFolders().put(f.getAbsolutePath(), season.getId());
                    season.setSeasonNumber(series.getSeasons().size() + 1);
                    season.setSeriesID(series.getId());
                    season.setFolder(f.getAbsolutePath());

                    updateMetadata = true;
                }

                int themdbID = season.getThemdbID();

                if (themdbID == -1) {
                    themdbID = searchFirstMovie(library, movieName, Integer.parseInt(year));
                }

                MovieMetadata movieMetadata = null;

                if (themdbID != -1)
                    movieMetadata = downloadMovieMetadata(library, themdbID);

                season.setThemdbID(themdbID);

                series.setName(movieName);

                if (movieMetadata == null) {
                    season.setName(movieName);

                    for (File file : filesInRoot) {
                        if (file.isFile() && validVideoFile(file))
                            saveDiscWithoutMetadata(library, file, season);
                    }

                    if (!exists)
                        addSeries(library, series);
                    else if (series == selectedSeries)
                        refreshSeries();
                    return;
                }

                if (updateMetadata) {
                    season.setSeriesID(series.getId());

                    season.setName(movieMetadata.title);
                    season.setOverview(movieMetadata.overview);
                    season.setYear(movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-")));
                    season.setScore((float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f);
                    season.setImdbID(movieMetadata.imdb_id);

                    if (season.getImdbID() == null)
                        season.setImdbID("");

                    List<Genre> genres = movieMetadata.genres;

                    if (genres != null) {
                        List<String> genreList = new ArrayList<>();
                        for (Genre genre : genres) {
                            genreList.add(genre.name);
                        }

                        season.setGenres(genreList);
                    }

                    library.getSeasonFolders().put(f.getAbsolutePath(), season.getId());

                    Images images = downloadImages(library, season.getThemdbID());

                    if (images != null) {
                        loadImages(library, season.getId(), images, season.getThemdbID(), false);
                        loadImages(library, series.getId(), images, season.getThemdbID(), true);
                    }

                    File posterDir = new File("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
                    if (posterDir.exists()) {
                        season.setCoverSrc("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
                    }

                    posterDir = new File("resources/img/seriesCovers/" + series.getId() + "/0.jpg");
                    if (posterDir.exists()) {
                        series.setCoverSrc("resources/img/seriesCovers/" + season.getId() + "/0.jpg");
                    }

                    downloadLogos(library, series, season, season.getThemdbID());
                    saveBackground(season, "resources/img/DownloadCache/" + season.getThemdbID() + ".jpg", false);
                    downloadMovieCastAndCrew(season, movieMetadata);

                    if (movieMetadata.production_companies != null && !movieMetadata.production_companies.isEmpty()){
                        String finalStr = "";
                        if (movieMetadata.production_companies.size() > 3)
                            finalStr = "...";

                        StringBuilder productionCompanies = new StringBuilder();
                        for (ProductionCompany pC : movieMetadata.production_companies){
                            if (movieMetadata.production_companies.indexOf(pC) != 0)
                                productionCompanies.append(", ");

                            productionCompanies.append(pC.name);
                        }

                        productionCompanies.append(finalStr);
                        season.setProductionStudios(productionCompanies.toString());
                    }
                }

                for (File file : filesInRoot) {
                    if (file.isFile() && validVideoFile(file))
                        processMovie(library, file, season, movieMetadata.runtime);
                }

                if (season.getEpisodes().isEmpty())
                    series.removeSeason(season);

                if (!exists)
                    addSeries(library, series);
                else if (series == selectedSeries)
                    refreshSeries();
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

    private int searchFirstMovie(Library library, String name, int year) {
        MovieResultsPage movieResults = tmdbApi.getSearch().searchMovie(name, year, library.getLanguage(), true, 1);

        if (movieResults.getTotalResults() > 0)
            return movieResults.getResults().get(0).getId();

        return -1;
    }

    private void saveDiscWithoutMetadata(Library library, File f, Season season) {
        Episode episode;

        if (library.getAnalyzedFiles().get(f.getAbsolutePath()) != null) {
            episode = season.getEpisode(library.getAnalyzedFiles().get(f.getAbsolutePath()));
        } else {
            episode = new Episode();
            season.addEpisode(episode);
            episode.setSeasonID(season.getId());
            library.getAnalyzedFiles().put(f.getAbsolutePath(), episode.getId());

            episode.setName(f.getName().substring(0, f.getName().lastIndexOf(".")));
            episode.setVideoSrc(f.getAbsolutePath());
            episode.setSeasonNumber(season.getSeasonNumber());
            episode.setImgSrc("resources/img/Default_video_thumbnail.jpg");
        }

        getMediaInfo(episode);
    }

    private void processMovie(Library library, File file, Season season, int runtime) {
        Episode episode;

        if (library.getAnalyzedFiles().get(file.getAbsolutePath()) != null) {
            episode = season.getEpisode(library.getAnalyzedFiles().get(file.getAbsolutePath()));
        } else {
            episode = new Episode();
            season.addEpisode(episode);
            episode.setSeasonID(season.getId());
            library.getAnalyzedFiles().put(file.getAbsolutePath(), episode.getId());

            episode.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
            episode.setEpisodeNumber(season.getEpisodes().size());
            episode.setScore(season.getScore());
            episode.setOverview(season.getOverview());
            episode.setYear(season.getYear());
            episode.setRuntime(runtime);
            episode.setSeasonNumber(season.getSeasonNumber());
            episode.setVideoSrc(file.getAbsolutePath());
            episode.setDirectedBy(season.getDirectedBy());
            episode.setWrittenBy(season.getWrittenBy());

            if (!season.getImdbID().isEmpty())
                setIMDBScore(season.getImdbID(), episode);

            setMovieThumbnail(episode, season.getThemdbID());
        }

        getMediaInfo(episode);
    }

    private void downloadMovieCastAndCrew(Season season, MovieMetadata metadata){
        Request requestGroups = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/" + metadata.id + "/credits?language=en-US")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                .build();

        try (Response response = client.newCall(requestGroups).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                assert response.body() != null;
                Credits credits = objectMapper.readValue(response.body().string(), Credits.class);

                season.getCast().addAll(credits.cast);

                if (credits.guest_stars != null)
                    season.getCast().addAll(credits.guest_stars);

                List<String> directors = new ArrayList<>();
                List<String> writers = new ArrayList<>();
                String originalSourceCreator = "";
                String originalMusicComposer = "";
                for (Crew person : credits.crew){
                    if (person.job.equals("Author") || person.job.equals("Novel") || person.job.equals("Original Series Creator")
                            || person.job.equals("Comic Book") || person.job.equals("Idea") || person.job.equals("Original Story") || person.job.equals("Story")
                            || person.job.equals("Story by") || person.job.equals("Book"))
                        originalSourceCreator = person.name;
                    else if (person.job.equals("Original Music Composer"))
                        originalMusicComposer = person.name;
                    else if (person.job.equals("Writer") || person.job.equals("Screenplay"))
                        writers.add(person.name);
                    else if (person.job.equals("Director"))
                        directors.add(person.name);
                }

                season.setCreator(originalSourceCreator);
                season.setMusicComposer(originalMusicComposer);

                StringBuilder directorsString = new StringBuilder();
                for (String director : directors){
                    if (directors.indexOf(director) != 0)
                        directorsString.append(", ");

                    directorsString.append(director);
                }
                season.setDirectedBy(directorsString.toString());

                StringBuilder writersString = new StringBuilder();
                for (String writer : writers){
                    if (writers.indexOf(writer) != 0)
                        writersString.append(", ");

                    writersString.append(writer);
                }
                season.setWrittenBy(writersString.toString());
            } else {
                System.out.println("downloadMovieCastAndCrew: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("downloadMovieCastAndCrew: Response not successful: " + e.getMessage());
        }
    }

    private void setIMDBScore(String imdbID, Episode episode) {
        try {
            Document doc = Jsoup.connect("https://www.imdb.com/title/" + imdbID).timeout(6000).get();
            Elements body = doc.select("div.sc-bde20123-2");
            for (Element e : body.select("span.sc-bde20123-1")) {
                String score = e.text();
                episode.setImdbScore(Float.parseFloat(score));
                break;
            }
        } catch (IOException e) {
            System.err.println("setIMDBScore: IMDB connection lost");
        }
    }

    private void setMovieThumbnail(Episode episode, int themdbID) {
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        DataManager.INSTANCE.createFolder("resources/img/discCovers/" + episode.getId() + "/");

        MovieImages images = tmdbApi.getMovies().getImages(themdbID, null);
        List<Artwork> thumbnails = images.getBackdrops();

        List<String> thumbnailsUrls = new ArrayList<>();
        if (thumbnails != null) {
            for (Artwork artwork : thumbnails) {
                thumbnailsUrls.add(imageBaseURL + artwork.getFilePath());
            }
        }

        //region THUMBNAIL DOWNLOADER
        if (!thumbnailsUrls.isEmpty()) {
            saveThumbnail(episode, thumbnailsUrls.get(0), 0);

            Platform.runLater(() -> {
                imageDownloadProcesses++;
                downloadingImagesText.setDisable(false);
                ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(true);
                showActivityBox();
            });

            analysisExecutor.submit(() -> {
                try {
                    for (int i = 1; i < thumbnailsUrls.size(); i++) {
                        if (i == 10)
                            break;

                        saveThumbnail(episode, thumbnailsUrls.get(i), i);
                    }

                    Platform.runLater(() -> {
                        imageDownloadProcesses--;

                        if (imageDownloadProcesses == 0){
                            downloadingImagesText.setDisable(true);
                            ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(false);
                            hideActivityBox();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("setMovieThumbnail: thread task cancelled " + e.getMessage());
                }
            });
        }
        //endregion

        File img = new File("resources/img/discCovers/" + episode.getId() + "/0.jpg");
        if (!img.exists()) {
            episode.setImgSrc("resources/img/Default_video_thumbnail.jpg");
        } else {
            episode.setImgSrc("resources/img/discCovers/" + episode.getId() + "/0.jpg");
        }
    }

    private void saveThumbnail(Episode episode, String urlImage, int number) {
        try {
            URI uri = new URI(urlImage);
            URL url = uri.toURL();

            BufferedImage bufferedImage = ImageIO.read(url);

            //Compress image
            Thumbnails.of(bufferedImage)
                    .scale(1)
                    .outputFormat("jpg")
                    .outputQuality(0.8)
                    .toFile("resources/img/discCovers/" + episode.getId() + "/" + number + ".jpg");

            bufferedImage.flush();
        } catch (IOException | URISyntaxException e) {
            System.err.println("DesktopViewController: Error compressing image");
        }
    }

    public MovieMetadata downloadMovieMetadata(Library library, int tmdbID) {
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/" + tmdbID + "?language=" + library.getLanguage())
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), MovieMetadata.class);
            } else {
                System.out.println("Response not successful: " + response.code());
            }
        } catch (IOException e) {
            System.err.println("Error executing request: " + e.getMessage());
        }

        return null;
    }

    public SeriesMetadata downloadSeriesMetadata(Library library, int tmdbID) {
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/tv/" + tmdbID + "?language=" + library.getLanguage())
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), SeriesMetadata.class);
            } else {
                System.out.println("downloadSeriesMetadata: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            System.err.println("downloadSeriesMetadata: show metadata could not be downloaded");
        }

        return null;
    }

    public SeasonMetadata downloadSeasonMetadata(Library library, int tmdbID, int season) {
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/tv/" + tmdbID + "/season/" + season + "?language=" + library.getLanguage())
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();

                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), SeasonMetadata.class);
            } else {
                System.out.println("downloadSeasonMetadata: Response not successful for " + tmdbID + " season: " + season);
            }
        } catch (IOException e) {
            System.out.println("downloadSeasonMetadata: Response not successful for " + tmdbID + " season: " + season);
        }

        return null;
    }

    private Images downloadImages(Library library, int tmdbID) {
        String type = "tv";
        String languages = "null%2C" + library.getLanguage();

        if (!library.getType().equals("Shows")) {
            type = "movie";
        }

        if (!library.getLanguage().equals("en"))
            languages += "%2Cen";

        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/" + type + "/" + tmdbID + "/images?include_image_language=" + languages)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();

                if (response.body() != null)
                    return objectMapper.readValue(response.body().string(), Images.class);
            } else {
                System.out.println("downloadImages: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("downloadImages: Response not successful: " + e.getMessage());
        }

        return null;
    }

    private void downloadLogos(Library library, Series series, Season season, int tmdbID) {
        String imageBaseURL = "https://image.tmdb.org/t/p/original";
        String type = "tv";
        String languages = "null%2Cja%2Cen";

        String id;

        if (library.getType().equals("Shows"))
            id = series.getId();
        else
            id = season.getId();

        if (!library.getType().equals("Shows")) {
            type = "movie";
            languages = "null%2Cen";

            if (!library.getLanguage().equals("en"))
                languages += "%2C" + library.getLanguage();
        }

        //region GET IMAGES
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/" + type + "/" + tmdbID + "/images?include_image_language=" + languages)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.themoviedbAPIToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                assert response.body() != null;
                Images images = objectMapper.readValue(response.body().string(), Images.class);

                if (images == null)
                    return;

                //region Process Logos
                List<Logo> logosList = images.logos;
                if (!logosList.isEmpty()) {
                    saveLogo(id, 0, imageBaseURL + logosList.get(0).file_path);

                    Platform.runLater(() -> {
                        imageDownloadProcesses++;
                        downloadingImagesText.setDisable(false);
                        ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(true);
                        showActivityBox();
                    });

                    analysisExecutor.submit(() -> {
                        try {
                            int processedFiles = 1;
                            for (int i = processedFiles; i < logosList.size(); i++) {
                                if (processedFiles == 16)
                                    break;

                                saveLogo(id, processedFiles, imageBaseURL + logosList.get(i).file_path);
                                processedFiles++;
                            }

                            Platform.runLater(() -> {
                                imageDownloadProcesses--;

                                if (imageDownloadProcesses == 0){
                                    downloadingImagesText.setDisable(true);
                                    ((BorderPane) downloadingImagesText.getParent()).getRight().setVisible(false);
                                    hideActivityBox();
                                }
                            });
                        } catch (Exception e) {
                            System.err.println("downloadLogos: thread task cancelled " + e.getMessage());
                        }
                    });
                }

                File posterDir = new File("resources/img/logos/" + id + "/0.png");
                if (posterDir.exists()) {
                    if (library.getType().equals("Shows"))
                        series.setLogoSrc("resources/img/logos/" + id + "/0.png");
                    else
                        season.setLogoSrc("resources/img/logos/" + id + "/0.png");
                }
                //endregion
            } else {
                System.out.println("downloadLogos: Response not successful: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("downloadLogos: Response not successful: " + e.getMessage());
        }
        //endregion
    }

    public void clearImageCache() {
        try {
            FileUtils.cleanDirectory(new File("resources/img/DownloadCache"));
        } catch (IOException e) {
            System.err.println("clearImageCache: cannot clean directory");
        }
    }

    private void showActivityBox(){
        if (activityBox.isVisible())
            return;

        activityMessageBox.setVisible(false);
        fadeInEffect(activityBox, 0.2f).play();
    }

    private void hideActivityBox(){
        if (!downloadingMetadataText.isDisabled() || !downloadingSongsText.isDisabled() || !downloadingImagesText.isDisabled())
            return;

        fadeOutEffect(activityBox, 0.2f);
    }
    //endregion

    //region DOWNLOAD MEDIA
    public List<YoutubeVideo> searchYoutube(String videoName) {
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

    public boolean downloadMedia(Season season, String url) {
        String seasonID = season.getId();

        try {
            DataManager.INSTANCE.createFolder("resources/downloadedMediaCache/" + seasonID + "/");
            File directory = new File("resources/downloadedMediaCache/" + seasonID + "/");

            ProcessBuilder pb;
            pb = new ProcessBuilder("pytube"
                    , url
                    , "-a", "-t", directory.getAbsolutePath());

            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            File[] filesInMediaCache = directory.listFiles();

            if (filesInMediaCache != null && filesInMediaCache.length != 0) {
                File audioFile = filesInMediaCache[0];

                try {
                    Files.copy(audioFile.toPath(), Paths.get("resources/music/" + season.getId() + ".mp4"), StandardCopyOption.REPLACE_EXISTING);
                    season.setMusicSrc("resources/music/" + season.getId() + ".mp4");

                    FileUtils.forceDelete(directory);
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
    public void updateSeries() {
        Button btn = seriesButtons.get(seriesList.indexOf(selectedSeries));
        BorderPane pane = (BorderPane) btn.getGraphic();
        ((Label) pane.getLeft()).setText(selectedSeries.getName());

        if (!seasonPoster)
            setCoverImage();

        if (currentLibrary.getType().equals("Shows") && !selectedSeries.getLogoSrc().isEmpty())
            setLogoNoText(new File(selectedSeries.getLogoSrc()));
    }

    public void addSeries(Library library, Series s) {
        if (s.getSeasons().isEmpty())
            return;

        library.getSeries().add(s);

        Platform.runLater(() ->
        {
            if (currentLibrary == library)
                addSeriesCard(s);
        });
    }

    private void addSeriesCard(Series s) {
        Button seriesButton = addSeriesButton(s);
        seriesButton.getStyleClass().add("desktopTextButtonParent");

        setDragAndDropButton(seriesButton);

        seriesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            selectSeriesButton(seriesButton);
            if (event.getButton() == MouseButton.SECONDARY) {
                openSeriesMenu(event);
            }
        });

        seriesContainer.getChildren().add(seriesButton);
        seriesButtons.add(seriesButton);
    }
    private Button addSeriesButton(Series s){
        Button seriesButton = new Button();
        Label buttonText = new Label(s.getName());
        buttonText.getStyleClass().add("desktopTextButton");
        buttonText.setMaxWidth(seriesScrollPane.getPrefWidth() - 50);
        buttonText.setAlignment(Pos.BASELINE_LEFT);
        buttonText.setWrapText(true);

        MFXProgressSpinner progressSpinner = getCircularProgress(25);
        progressSpinner.setVisible(s.isAnalyzingFiles());

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(buttonText);
        borderPane.setRight(progressSpinner);
        BorderPane.setAlignment(buttonText, Pos.CENTER_LEFT);
        BorderPane.setAlignment(progressSpinner, Pos.CENTER_RIGHT);
        BorderPane.setMargin(buttonText, new Insets(0, 0, 0, 0));

        seriesButton.setGraphic(borderPane);
        seriesButton.setBackground(null);
        seriesButton.setMaxWidth(Double.MAX_VALUE);
        seriesButton.setPadding(new Insets(5, 5, 5, 5));

        return seriesButton;
    }

    @FXML
    void addLibrary() {
        if (!App.isConnectedToInternet) {
            App.showErrorMessage(App.textBundle.getString("connectionErrorTitle"), "", App.textBundle.getString("connectionErrorMessage"));
            return;
        }

        showBackgroundShadow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addLibrary-view.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(getBaseFontSize());
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

    private void addSeasonButton(Season season) {
        Platform.runLater(() -> {
            seasonList.sort(new Utils.SeasonComparator());
            int index = seasonList.indexOf(season);

            Button seasonButton = createSeasonButton(season);
            seasonContainer.getChildren().add(index, seasonButton);
            seasonsButtons.add(index, seasonButton);
        });
    }

    private void addEpisodeCard(Episode episode) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("discCard.fxml"));
            Pane cardBox = fxmlLoader.load();
            cardBox.setStyle(getBaseFontSize());
            DiscController discController = fxmlLoader.getController();
            discController.setDesktopParentParent(this);
            discController.setData(episode);

            episodesContainer.getChildren().add(cardBox);
            episodesControllers.add(discController);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateDisc(Episode d) {
        DataManager.INSTANCE.saveData();

        for (DiscController discController : episodesControllers) {
            if (discController.episode.getId().equals(d.getId())) {
                discController.setData(d);
                return;
            }
        }
    }
    //endregion

    //region DRAG AND DROP SERIES
    private void setDragAndDropButton(Button button) {
        // Start the drag event
        button.setOnDragDetected(event -> {
            if (canDragSeries){
                draggedSeries = seriesList.get(seriesContainer.getChildren().indexOf(button));
                Dragboard db = button.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(button.getText());
                db.setContent(content);
            }

            event.consume();
        });

        // Allow the VBox to accept the drop
        seriesContainer.setOnDragOver(event -> {
            if (event.getGestureSource() != seriesContainer && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Handle the drop event
        seriesContainer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Button draggedButton = (Button) event.getGestureSource();
                int draggedIndex = seriesContainer.getChildren().indexOf(draggedButton);

                int dropIndex = getDropIndex(event.getY());

                if (dropIndex >= 0 && dropIndex < seriesContainer.getChildren().size()) {
                    // Avoid reordering if the button is dropped at the same position
                    if (dropIndex != draggedIndex) {
                        seriesContainer.getChildren().remove(draggedButton);
                        seriesContainer.getChildren().add(dropIndex, draggedButton);

                        // Update the ArrayList to reflect the new order
                        seriesList.remove(draggedSeries);
                        seriesList.add(dropIndex, draggedSeries);

                        seriesButtons.remove(draggedButton);
                        seriesButtons.add(dropIndex, draggedButton);

                        for (int i = 0; i < seriesList.size(); i++){
                            seriesList.get(i).setOrder(i);
                        }
                    }
                }

                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
    // Method to determine the drop position based on the Y coordinate
    private int getDropIndex(double y) {
        for (int i = 0; i < seriesContainer.getChildren().size(); i++) {
            if (y < seriesContainer.getChildren().get(i).getBoundsInParent().getMaxY()) {
                return i;
            }
        }
        return seriesContainer.getChildren().size(); // Add to the end if no position is found
    }
    //endregion

    //region EDIT SECTION
    @FXML
    void editLibrary() {
        if (!App.isConnectedToInternet) {
            App.showErrorMessage(App.textBundle.getString("connectionErrorTitle"), "", App.textBundle.getString("connectionErrorMessage"));
            return;
        }

        if (currentLibrary != null) {
            showBackgroundShadow();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addLibrary-view.fxml"));
                Parent root1 = fxmlLoader.load();
                root1.setStyle(getBaseFontSize());
                root1.setStyle(getBaseFontSize());
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
    void editSeries() {
        showBackgroundShadow();
        if (selectedSeries != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editCollection-view.fxml"));
                Parent root1 = fxmlLoader.load();
                root1.setStyle(getBaseFontSize());
                EditCollectionController addColController = fxmlLoader.getController();
                addColController.setParentController(this);
                addColController.setSeries(selectedSeries, currentLibrary.getType().equals("Shows"));
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
    void editSeason() {
        showBackgroundShadow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editSeason-view.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(getBaseFontSize());
            EditSeasonController addSeasonController = fxmlLoader.getController();
            addSeasonController.setParentController(this);
            addSeasonController.setSeason(selectedSeason, currentLibrary.getType().equals("Shows"));
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
    void editDisc() {
        editDisc(selectedEpisode);
    }

    void editDisc(Episode d) {
        showBackgroundShadow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editDisc-view.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(getBaseFontSize());
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
    public void acceptRemove() {
        acceptRemove = true;
    }

    private Stage showConfirmationWindow(String title, String message) {
        showBackgroundShadow();
        hideMenu();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("confirmationWindow.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(getBaseFontSize());
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

        Stage stage = showConfirmationWindow(App.textBundle.getString("removeLibrary"), App.textBundle.getString("removeLibraryMessage"));
        stage.showAndWait();

        hardRemoveLibrary();

        hideBackgroundShadow();
    }

    private void hardRemoveLibrary(){
        if (acceptRemove) {
            acceptRemove = false;
            DataManager.INSTANCE.deleteLibrary(currentLibrary);

            currentLibrary = null;
            updateLibraries();
        }

        if (libraryContainer.getChildren().isEmpty())
            blankSelection();
    }

    @FXML
    void removeCollection() {
        if (selectedSeries != null) {
            Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
            stage.showAndWait();

            if (acceptRemove) {
                acceptRemove = false;
                removeCollection(selectedSeries);
            }
        }

        hideBackgroundShadow();
    }

    private void removeCollection(Series series) {
        int index = seriesList.indexOf(series);
        seriesList.remove(series);
        seriesButtons.remove(index);
        seriesContainer.getChildren().remove(index);
        currentLibrary.removeSeries(series);
        DataManager.INSTANCE.deleteSeriesData(series);
        selectedSeries = null;
        if (!seriesList.isEmpty()) {
            selectSeriesButton(seriesButtons.get(0));
        } else {
            seasonScroll.setVisible(false);
        }
    }

    @FXML
    void removeSeason() {
        Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
        stage.showAndWait();

        if (acceptRemove) {
            acceptRemove = false;
            removeSeason(selectedSeason);
        }

        hideBackgroundShadow();
    }

    private void removeSeason(Season season) {
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
    void removeEpisode() {
        Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
        stage.showAndWait();

        if (acceptRemove) {
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
            episodesContainer.getChildren().remove(index);
            episodesControllers.remove(index);
        }

        episodeList.remove(d);
        DataManager.INSTANCE.deleteEpisodeData(d);

        if (episodeList.isEmpty()) {
            removeSeason(selectedSeason);
        } else {
            selectSeason(selectedSeason);
        }

        hideBackgroundShadow();
    }
    //endregion

    //region FULLSCREEN
    @FXML
    void switchToFullScreen() {
        fullScreen();
    }

    private void fullScreen() {
        hideMenu();

        DataManager.INSTANCE.currentLibrary = null;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            root.setStyle(getBaseFontSize());
            //Controller controller = fxmlLoader.getController();
            //controller.playIntroVideo();
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("fullscreenMode"));
            stage.getIcons().add(new Image(getFileAsIOStream("img/icons/AppIcon.png")));
            Scene scene = new Scene(root);
            //scene.setCursor(Cursor.NONE);
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
        fadeInTransition(mainMenu);
    }

    @FXML
    void openSettings(MouseEvent event) {
        showBackgroundShadow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainMenuDesktop-view.fxml"));
            Parent root1 = fxmlLoader.load();
            root1.setStyle(getBaseFontSize());
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
        fadeInTransition(libraryMenu);
    }

    @FXML
    void showLibraries(MouseEvent event) {
        menuParentPane.setVisible(true);
        libraryContainer.setLayoutY(librarySelector.getLayoutY() + 40);
        libraryContainer.setLayoutX(librarySelector.getLayoutX() + 15);

        //Change libraries button icon
        ImageView icon = (ImageView) librarySelector.getGraphic();
        icon.setImage(new Image(getFileAsIOStream("img/icons/triangleInverted.png"), 25, 25, true, true));
        librarySelector.setGraphic(icon);

        libraryContainer.setVisible(true);
        fadeInTransition(libraryContainer);
    }

    @FXML
    void openSeriesMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        seriesMenu.setLayoutX(event.getSceneX());

        if (seriesList.indexOf(selectedSeries) <= 2)
            seriesMenu.setLayoutY(event.getSceneY());
        else
            seriesMenu.setLayoutY(event.getSceneY() - seriesMenu.getHeight());

        seriesMenu.setVisible(true);
        fadeInTransition(seriesMenu);
    }

    @FXML
    void openSeasonMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        seasonMenu.setLayoutX(event.getSceneX());
        seasonMenu.setLayoutY(event.getSceneY() - seasonMenu.getHeight());

        seasonMenu.setVisible(true);
        fadeInTransition(seasonMenu);
    }

    public void openDiscMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        episodeMenu.setLayoutX(event.getSceneX());
        episodeMenu.setLayoutY(event.getSceneY() - episodeMenu.getHeight());

        episodeMenu.setVisible(true);
        fadeInTransition(episodeMenu);
    }

    private void hideMenu() {
        if (episodeMenu.isVisible())
            fadeOutTransition(episodeMenu);
        episodeMenu.setVisible(false);

        if (seasonMenu.isVisible())
            fadeOutTransition(seasonMenu);
        seasonMenu.setVisible(false);

        if (libraryMenu.isVisible())
            fadeOutTransition(libraryMenu);
        libraryMenu.setVisible(false);

        if (seriesMenu.isVisible())
            fadeOutTransition(seriesMenu);
        seriesMenu.setVisible(false);

        if (mainMenu.isVisible())
            fadeOutTransition(mainMenu);
        mainMenu.setVisible(false);

        if (libraryContainer.isVisible())
            fadeOutTransition(libraryContainer);
        libraryContainer.setVisible(false);

        menuParentPane.setVisible(false);

        //Change libraries button icon
        ImageView icon = (ImageView) librarySelector.getGraphic();
        icon.setImage(new Image(getFileAsIOStream("img/icons/triangle.png"), 25, 25, true, true));
        librarySelector.setGraphic(icon);
    }
    //endregion

    //region BACKGROUND SHADOW
    public void showBackgroundShadow() {
        backgroundShadow.setVisible(true);
    }

    public void hideBackgroundShadow() {
        backgroundShadow.setVisible(false);
    }
    //endregion
}
