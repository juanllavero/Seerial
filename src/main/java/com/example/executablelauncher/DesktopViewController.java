package com.example.executablelauncher;

import com.example.executablelauncher.entities.Category;
import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.tmdbMetadata.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private Label elementsSelectedText;

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
    private BorderPane selectionOptions;

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
    private boolean acceptRemove = false;
    private static int numFilesToCheck = 0;
    //endregion

    //region THEMOVIEDB ATTRIBUTES
    TmdbApi tmdbApi = new TmdbApi("4b46560aff5facd1d9ede196ce7d675f");
    TmdbTV seriesMetadata;                                                              //Saves all series from TheMovieDB
    TmdbMovies moviesMetadata;                                                          //Saves all movies from TheMovieDB
    SeasonsGroupMetadata episodesGroup = null;                                          //Create metadata holders for episode groups
    MovieDb movieMetadataToCorrect = null;                                              //Metadata of the movie that is going to be correctly identified
    TvSeries seriesMetadataToCorrect = null;                                            //Metadata of the show that is going to be correctly identified
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
        downloadingContentWindowStatic.setVisible(false);

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
        //scrollModification(seasonScroll);
        //scrollModification(seriesScrollPane);

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


        //seasonScroll.prefHeightProperty().bind(seasonInfoPane.heightProperty());
        //seasonScroll.setMinHeight(screenHeight);

        //seasonsEpisodesBox.setMinHeight(discContainer.getMinHeight());
        //seasonsEpisodesBox.prefHeightProperty().bind(discContainer.heightProperty());
        //seasonsEpisodesBox.maxHeightProperty().bind(discContainer.heightProperty());
        //seasonBorderPane.prefHeightProperty().bind(seasonsEpisodesBox.heightProperty().add(seasonLogoBox.heightProperty()));
        //seasonBorderPane.maxHeightProperty().bind(seasonsEpisodesBox.heightProperty().add(seasonLogoBox.heightProperty()));
        //seasonScroll.minHeightProperty().bind(seasonBackground.heightProperty());
        //seasonInfoPane.minHeightProperty().bind(seasonBackground.heightProperty().add(seasonsEpisodesBox.heightProperty()));
        //seasonInfoPane.prefHeightProperty().bind(seasonBackground.heightProperty().add(seasonsEpisodesBox.heightProperty()));
        //seasonInfoPane.maxHeightProperty().bind(seasonBorderPane.heightProperty());

        seasonsEpisodesBox.setPrefWidth(Integer.MAX_VALUE);


        //discContainer.prefHeightProperty().bind(seasonsEpisodesBox.heightProperty());
        //discContainer.prefWidthProperty().bind(seasonsEpisodesBox.widthProperty());
        //discContainer.prefHeightProperty().bind(seasonsEpisodesBox.heightProperty());

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

        updateCategories();
    }

    public void showSeries(){
        seriesButtons.clear();

        if (seriesList == null)
            return;

        for (Series s : seriesList){
            if (s == null)
                continue;

            addSeriesCard(s);
        }

        if (seriesList.isEmpty())
            blankSelection();
        else{
            if (!seriesButtons.isEmpty()){
                selectSeriesButton(seriesButtons.get(0));
                selectSeries(seriesList.get(0));
            }
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
        Image i = new Image("file:" + "src/main/resources/img/backgrounds/" + selectedSeason.getId() + "/" + "background.png");
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
                seasonLogo.setFitHeight(300);
                seasonLogo.setFitWidth(600);
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
            discList.sort(new Utils.DiscComparator().reversed());
            for (Disc d : discList){
                addEpisodeCard(d);
            }
        }
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
    public void acceptRemove(){
        acceptRemove = true;
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
        }

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

        if (currentCategory == null)
            return;

        seriesList = App.getSeriesFromCategory(category);

        if (currentCategory.type.equals("Shows")) {
            identificationShow.setDisable(false);
            identificationMovie.setDisable(true);
        }else {
            identificationMovie.setDisable(false);
            identificationShow.setDisable(true);
        }

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
    public void saveBackground(Season s, String imageToCopy){
        try{
            Files.createDirectories(Paths.get("src/main/resources/img/backgrounds/" + s.id + "/"));
        } catch (IOException e) {
            System.err.println("saveBackground: Directory could not be created");
        }

        //Clear old images
        File dir = new File("src/main/resources/img/backgrounds/" + s.id);
        if (dir.exists()){
            try {
                deleteFile(s.getBackgroundSrc());
                deleteFile("src/main/resources/img/backgrounds/" + s.id + "/fullBlur.png");
                deleteFile("src/main/resources/img/backgrounds/" + s.id + "/transparencyEffect.png");
            } catch (IOException e) {
                System.err.println("EditSeasonController: Error removing old images");
            }
        }

        File destination = new File("src/main/resources/img/backgrounds/" + s.id + "/background.png");
        try {
            Files.copy(Paths.get(imageToCopy), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Background not copied");
        }

        s.backgroundSrc = "src/main/resources/img/backgrounds/" + s.id + "/background.png";

        setTransparencyEffect(s.getBackgroundSrc(), "src/main/resources/img/backgrounds/" + s.id + "/transparencyEffect.png");
        processBlurAndSave(s.getBackgroundSrc(), "src/main/resources/img/backgrounds/" + s.id + "/fullBlur.png");
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
            System.err.println("EditSeasonController: error applying transparency effect to background");
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
    //endregion

    //region PLAY EPISODE
    public void playEpisode(Disc disc) {
        //Run file in vlc
        String command = null;
        String extension = disc.getExecutableSrc().substring(disc.getExecutableSrc().length() - 3);

        if (extension.equals("iso") || extension.equals("ISO"))
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
        topBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) mainBox.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
    //endregion

    //region IDENTIFICATION
    @FXML
    void correctIdentificationShow(ActionEvent event){
        showBackgroundShadow();
        hideMenu();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchSeries.fxml"));
            Parent root1 = fxmlLoader.load();
            SearchSeriesController controller = fxmlLoader.getController();
            controller.initiValues(this, selectedSeries.name, true, tmdbApi, currentCategory.language);
            Stage stage = new Stage();
            stage.setTitle("Correct Identification");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage);
            stage.showAndWait();

            hideBackgroundShadow();

            if (seriesMetadataToCorrect != null)
                correctIdentificationShow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void correctIdentificationMovie(ActionEvent event){
        showBackgroundShadow();
        hideMenu();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchSeries.fxml"));
            Parent root1 = fxmlLoader.load();
            SearchSeriesController controller = fxmlLoader.getController();
            controller.initiValues(this, selectedSeries.name, false, tmdbApi, currentCategory.language);
            Stage stage = new Stage();
            stage.setTitle("Correct Identification");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            App.setPopUpProperties(stage);
            stage.showAndWait();

            hideBackgroundShadow();

            if (movieMetadataToCorrect != null)
                correctIdentificationMovie();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void correctIdentificationShow(){
    }
    public void correctIdentificationMovie(){
        showBackgroundShadow();
        downloadingContentTextStatic.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindowStatic.setVisible(true);
        Task<Void> correctIdentificationTask = new Task<>() {
            @Override
            protected Void call() {
                selectedSeason.name = movieMetadataToCorrect.getTitle();
                selectedSeason.year = movieMetadataToCorrect.getReleaseDate();
                selectedSeason.themdbID = movieMetadataToCorrect.getId();
                selectedSeason.resume = movieMetadataToCorrect.getOverview();

                try{
                    FileUtils.deleteDirectory(new File("src/main/resources/img/logos/" + selectedSeason.id));
                } catch (IOException e) {
                    System.err.println("DesktopViewController.correctIdentificationMovie: Error deleting directories");
                }

                try{
                    Files.createDirectories(Paths.get("src/main/resources/img/logos/" + selectedSeason.id + "/"));
                    Files.createDirectories(Paths.get("src/main/resources/img/backgrounds/" + selectedSeason.id + "/"));
                } catch (IOException e) {
                    System.err.println("correctIdentificationMovie: Directory could not be created");
                }

                downloadLogos(selectedSeason, selectedSeason.themdbID);
                downloadImages(selectedSeries, selectedSeason.themdbID);
                saveBackground(selectedSeason, "src/main/resources/img/DownloadCache/" + selectedSeason.themdbID + ".png");

                if (selectedSeason.getDiscs().size() == 1){
                    Disc disc = App.findDisc(selectedSeason.getDiscs().get(0));

                    if (disc != null){
                        disc.name = selectedSeason.name;
                        disc.resume = selectedSeason.resume;
                    }
                }

                for (String discID : selectedSeason.getDiscs()){
                    Disc disc = App.findDisc(discID);

                    if (disc == null)
                        continue;

                    try{
                        FileUtils.deleteDirectory(new File("src/main/resources/img/discCovers/" + discID));
                    } catch (IOException e) {
                        System.err.println("DesktopViewController.correctIdentificationMovie: Error deleting thumbnails");
                    }

                    try{
                        Files.createDirectories(Paths.get("src/main/resources/img/discCovers/" + discID + "/"));
                    } catch (IOException e) {
                        System.err.println("correctIdentificationMovie: Directory could not be created");
                    }

                    setMovieThumbnail(disc, selectedSeason.themdbID);
                }
                return null;
            }
        };

        correctIdentificationTask.setOnSucceeded(event -> {
            backgroundShadow.setVisible(false);
            downloadingContentWindowStatic.setVisible(false);
            movieMetadataToCorrect = null;
            fillSeasonInfo();
            showDiscs(selectedSeason);
        });

        new Thread(correctIdentificationTask).start();
    }
    public void setCorrectIdentificationShow(TvSeries tvSeries){
        seriesMetadataToCorrect = tvSeries;
    }
    public void setCorrectIdentificationMovie(MovieDb movie){
        movieMetadataToCorrect = movie;
    }
    //endregion

    //region AUTOMATED FILE SEARCH
    public void loadCategory(String name){
        backgroundShadow.setVisible(false);
        downloadingContentText.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindow.setVisible(true);

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

    private void loadHalfTask(List<File> files, int start, int end){
        Task<Void> loadHalfTask = new Task<>() {
            @Override
            protected Void call() {
                for (int i = start; i < end; i++){
                    File file = files.get(i);

                    if (currentCategory.type.equals("Shows"))
                        addTVShow(file);
                    else
                        addMovieOrConcert(file);
                }
                return null;
            }
        };

        loadHalfTask.setOnSucceeded(e -> {
            numFilesToCheck--;
            if (numFilesToCheck == 0)
                downloadingContentWindow.setVisible(false);
        });

        new Thread(loadHalfTask).start();
    }

    private void addTVShow(File directory){
        if (directory.isFile())
            return;

        File[] filesInDir = directory.listFiles();
        if (filesInDir == null)
            return;

        //All video files in directory (not taking into account two subdirectories ahead, like Series/Folder1/Folder2/video.mkv)
        List<File> videoFiles = new ArrayList<>();

        //CREATE SERIES
        Series newSeries = searchFirstSeries(directory.getName());
        App.addCollection(newSeries);
        currentCategory.series.add(newSeries.id);

        boolean seriesNotFound = false;
        List<TvSeason> seasonsMetadata = new ArrayList<>();

        //region CREATE SEASONS
        if (newSeries.themdbID == -1){
            seriesNotFound = true;
            Season newSeason = new Season();
            newSeason.name = directory.getName();
            newSeason.seriesID = newSeries.id;
            App.addSeason(newSeason, newSeries.id);
        }else{
            for (int i = 0; i <= newSeries.seasonsNumber; i++){

                try {
                    TmdbTvSeasons tvSeasons = tmdbApi.getTvSeasons();
                    TvSeason season = tvSeasons.getSeason(newSeries.themdbID, i, currentCategory.language, TmdbTvSeasons.SeasonMethod.values());

                    if (season == null)
                        continue;

                    seasonsMetadata.add(season);
                    Season newSeason = new Season();
                    newSeason.name = season.getName();
                    newSeason.seriesID = newSeries.id;
                    newSeason.year = season.getAirDate();

                    newSeason.order = i;

                    if (i == 0) {
                        newSeason.order = newSeries.seasonsNumber + 1;
                        newSeries.seasonsNumber += 1;
                    }

                    App.addSeason(newSeason, newSeries.id);
                    // Resto del código...
                } catch (RuntimeException e) {
                    System.err.println("Error trying to retrieve content from themoviedb");
                }
            }
        }
        //endregion

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
                saveBackground(season, "src/main/resources/img/DownloadCache/" + newSeries.id + ".png");
                downloadLogos(season, newSeries.themdbID);
            }
        }

        //Analyze all files in directory except two subdirectories ahead
        for (File file : filesInDir){
            if (file.isDirectory()){
                File[] filesInDirectory = file.listFiles();

                if (filesInDirectory == null)
                    continue;

                for (File f : filesInDirectory){
                    if (validVideoFile(f))
                        videoFiles.add(f);
                }
            }else{
                if (validVideoFile(file))
                    videoFiles.add(file);
            }
        }

        //Get Metadata and process episode
        for (File video : videoFiles){
            processEpisode(newSeries, video, seasonsMetadata, episodesGroup);
        }

        episodesGroup = null;

        addSeries(newSeries);
    }
    private SeasonsGroupMetadata getEpisodesGroup(int tmdbID){
        try{
            //region GET EPISODE GROUPS
            OkHttpClient client = new OkHttpClient();
            Request requestGroups = new Request.Builder()
                    .url("https://api.themoviedb.org/3/tv/" + tmdbID + "/episode_groups")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                    .build();

            Response response = client.newCall(requestGroups).execute();

            String seasonGroupID = "";
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                assert response.body() != null;
                SeasonsGroupRoot groups = objectMapper.readValue(response.body().string(), SeasonsGroupRoot.class);


                for (SeasonsGroup seasonsGroup : groups.results) {
                    if (seasonsGroup.name.equals("Seasons")) {
                        seasonGroupID = seasonsGroup.id;
                    }
                }
            } else {
                System.out.println("Response not successful: " + response.code());
            }
            //endregion

            //region GET EPISODE GROUP DETAILS
            if (!seasonGroupID.isEmpty()){
                Request requestGroup = new Request.Builder()
                        .url("https://api.themoviedb.org/3/tv/episode_group/" + seasonGroupID)
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0YjQ2NTYwYWZmNWZhY2QxZDllZGUxOTZjZTdkNjc1ZiIsInN1YiI6IjYxZWRkY2I4NGE0YmZjMDAxYjg3ZDM3ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cZua6EdMzzNw5L96N2W94z66Q2YhrCrOsRMdo0RLcOQ")
                        .build();

                response = client.newCall(requestGroup).execute();

                if (response.isSuccessful()) {
                    ObjectMapper objectMapper = new ObjectMapper();

                    assert response.body() != null;
                    return objectMapper.readValue(response.body().string(), SeasonsGroupMetadata.class);
                } else {
                    System.out.println("Response not successful: " + response.code());
                }
            }
            //endregion
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    private boolean validVideoFile(File file){
        String videoExtension = file.getName().substring(file.getName().length() - 4);
        videoExtension = videoExtension.toLowerCase();

        return videoExtension.equals(".mkv") || videoExtension.equals(".mp4") || videoExtension.equals(".avi") || videoExtension.equals(".mov")
                || videoExtension.equals(".wmv") || videoExtension.equals("mpeg") || videoExtension.equals("m2ts") || videoExtension.equals(".iso");
    }
    private void processEpisode(Series series, File file, List<TvSeason> seasonsMetadata, SeasonsGroupMetadata episodesGroup){
        Disc newDisc = new Disc();

        String fullName = file.getName().substring(0, file.getName().lastIndexOf("."));

        final String regexSeasonEpisode = "(?i)(?<season>S[0-9]{1,3}+)(?<episode>E[0-9]{1,4})";
        final String regexOnlyEpisode = "(?i)(?<episode>[0-9]{1,4})";

        final Pattern pattern = Pattern.compile(regexSeasonEpisode, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(fullName);

        if (!matcher.find()){
            Pattern newPattern = Pattern.compile(regexOnlyEpisode, Pattern.MULTILINE);
            Matcher newMatch = newPattern.matcher(fullName);

            if (newMatch.find()){
                int absoluteNumber = Integer.parseInt(newMatch.group("episode"));
                int seasonNumber = 0;
                int episodeNumber = 1;
                boolean episodeFound = false;
                for (TvSeason season : seasonsMetadata){
                    if (season.getSeasonNumber() >= 1){
                        if ((episodeNumber + season.getEpisodes().size()) < absoluteNumber) {
                            episodeNumber += season.getEpisodes().size();
                            continue;
                        }

                        for (int i = 0; i < season.getEpisodes().size(); i++){
                            if (episodeNumber == absoluteNumber) {
                                seasonNumber = season.getSeasonNumber();
                                episodeNumber = i;
                                episodeFound = true;
                                break;
                            }
                            episodeNumber++;
                        }
                    }

                    if (episodeFound)
                        break;

                    seasonNumber++;
                }

                if (!episodeFound)
                    return;

                TvEpisode episode = seasonsMetadata.get(seasonNumber).getEpisodes().get(episodeNumber);

                newDisc.setEpisodeNumber(String.valueOf(episode.getEpisodeNumber()));
                newDisc.seasonID = series.getSeasons().get(seasonNumber);
                setEpisodeData(newDisc, episode, series);
            }else{
                return;
            }
        }else{
            int seasonNumber = Integer.parseInt(matcher.group("season").substring(1));
            int episodeNumber = Integer.parseInt(matcher.group("episode").substring(1));

            boolean toFindMetadata = true;
            for (TvSeason season : seasonsMetadata){
                if (season.getSeasonNumber() == seasonNumber)
                    toFindMetadata = false;
            }

            if (toFindMetadata){
                if (episodesGroup == null)
                    episodesGroup = getEpisodesGroup(series.themdbID);

                if (episodesGroup != null){
                    for (EpisodeGroup episodeGroup : episodesGroup.groups){
                        if (episodeGroup.order != seasonNumber)
                            continue;

                        for (SeasonsGroupEpisode episode : episodeGroup.episodes){
                            if ((episode.order + 1) == episodeNumber){
                                seasonNumber = episode.season_number;
                                episodeNumber = episode.episode_number;
                                break;
                            }
                        }

                        if (episodeGroup.order == seasonNumber)
                            break;
                    }

                    processMetadata(newDisc, series, seasonNumber, episodeNumber, seasonsMetadata);
                }else{
                    return;
                }
            }else{
                processMetadata(newDisc, series, seasonNumber, episodeNumber, seasonsMetadata);
            }
        }

        newDisc.setExecutableSrc(file.getAbsolutePath());
        currentCategory.analyzedFiles.put(file.getAbsolutePath(), true);
        App.addDisc(newDisc);
    }
    private void processMetadata(Disc newDisc, Series series, int seasonNumber, int episodeNumber, List<TvSeason> seasonsMetadata){
        TvEpisode episode = null;

        for (TvSeason tvSeason : seasonsMetadata){
            if (tvSeason.getSeasonNumber() == seasonNumber){
                for (TvEpisode tvEpisode : tvSeason.getEpisodes()){
                    if (tvEpisode.getEpisodeNumber() == episodeNumber){
                        episode = tvEpisode;
                        break;
                    }
                }
                break;
            }
        }

        if (episode == null)
            return;

        newDisc.setEpisodeNumber(String.valueOf(episode.getEpisodeNumber()));

        for (String seasonID : series.getSeasons()){
            Season season = App.findSeason(seasonID);
            if (season.order == seasonNumber){
                seasonNumber = series.getSeasons().indexOf(seasonID);
                break;
            }
        }

        newDisc.seasonID = series.getSeasons().get(seasonNumber);
        setEpisodeData(newDisc, episode, series);
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
        if (!thumbnailsUrls.isEmpty()){
            saveThumbnail(disc, thumbnailsUrls.get(0), 0);

            Task<Void> thumbnailTask = new Task<>() {
                @Override
                protected Void call() {
                    for (int i = 1; i < thumbnailsUrls.size(); i++){
                        saveThumbnail(disc, thumbnailsUrls.get(i), i);
                    }
                    return null;
                }
            };

            new Thread(thumbnailTask).start();
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

            downloadImages(newSeries, tvSeries.getId());

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
    private void saveLogo(Season newSeason, int i, Image originalImage) {
        try{
            Files.createDirectories(Paths.get("src/main/resources/img/logos/" + newSeason.id + "/"));
        } catch (IOException e) {
            System.err.println("Directory could not be created");
        }

        if (!originalImage.isError()){
            File file = new File("src/main/resources/img/logos/" + newSeason.id + "/" + i + ".png");
            try{
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(originalImage, null);
                ImageIO.write(renderedImage,"png", file);
            } catch (IOException e) {
                System.err.println("DesktopViewController: Downloaded logo not saved");
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
                newSeason.seriesID = newSeries.id;

                App.addCollection(newSeries);
                App.addSeason(newSeason, newSeries.id);
                currentCategory.series.add(newSeries.id);

                if (!f.isDirectory() && validVideoFile(f))
                    saveDiscWithoutMetadata(f, newSeason);
            }else{
                newSeason.name = newSeries.name;
                newSeason.seriesID = newSeries.id;
                newSeason.themdbID = newSeries.themdbID;
                newSeason.year = newSeries.year;

                downloadImages(newSeries, newSeries.themdbID);
                downloadLogos(newSeason, newSeries.themdbID);
                saveBackground(newSeason, "src/main/resources/img/DownloadCache/" + newSeries.themdbID + ".png");

                App.addCollection(newSeries);
                App.addSeason(newSeason, newSeries.id);
                currentCategory.series.add(newSeries.id);

                if (!f.isDirectory())
                    processMovieOrConcert(f, newSeason);
            }

            addSeries(newSeries);
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
                        newSeason.seriesID = newSeries.id;

                        downloadImages(newSeries, newSeason.themdbID);
                        downloadLogos(newSeason, newSeason.themdbID);
                        saveBackground(newSeason, "src/main/resources/img/DownloadCache/" + newSeason.themdbID + ".png");

                        App.addSeason(newSeason, newSeries.id);

                        for (File file : filesInFolder){
                            if (!file.isDirectory() && validVideoFile(file))
                                processMovieOrConcert(file, newSeason);
                        }
                    }else{
                        Season newSeason = new Season();
                        newSeason.name = folder.getName();
                        newSeason.seriesID = newSeries.id;

                        App.addSeason(newSeason, newSeries.id);

                        for (File file : filesInFolder){
                            if (!file.isDirectory() && validVideoFile(file))
                                saveDiscWithoutMetadata(file, newSeason);
                        }
                    }
                }

                addSeries(newSeries);
                //endregion
            }else if (!filesInRoot.isEmpty()){
                //region MOVIE FILE/CONCERT FILES INSIDE FOLDER
                //Create Series
                Series newSeries = searchFirstMovie(f.getName());
                Season newSeason = new Season();
                if (newSeries.themdbID == -1){
                    newSeason.name = f.getName();
                    newSeason.seriesID = newSeries.id;

                    App.addCollection(newSeries);
                    App.addSeason(newSeason, newSeries.id);
                    currentCategory.series.add(newSeries.id);

                    for (File file : filesInRoot){
                        if (file.isFile() && validVideoFile(file))
                            saveDiscWithoutMetadata(file, newSeason);
                    }
                }else{
                    newSeason.name = newSeries.name;
                    newSeason.seriesID = newSeries.id;
                    newSeason.year = newSeries.year;
                    newSeason.themdbID = newSeries.themdbID;

                    downloadImages(newSeries, newSeason.themdbID);
                    downloadLogos(newSeason, newSeason.themdbID);
                    saveBackground(newSeason, "src/main/resources/img/DownloadCache/" + newSeason.themdbID + ".png");

                    App.addCollection(newSeries);
                    App.addSeason(newSeason, newSeries.id);
                    currentCategory.series.add(newSeries.id);

                    for (File file : filesInRoot){
                        if (file.isFile() && validVideoFile(file))
                            processMovieOrConcert(file, newSeason);
                    }
                }

                addSeries(newSeries);
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
            saveThumbnail(disc, thumbnailsUrls.get(0), 0);

            Task<Void> thumbnailTask = new Task<>() {
                @Override
                protected Void call() {
                    for (int i = 1; i < thumbnailsUrls.size(); i++){
                        saveThumbnail(disc, thumbnailsUrls.get(i), i);
                    }
                    return null;
                }
            };

            new Thread(thumbnailTask).start();
        }
        //endregion

        File img = new File("src/main/resources/img/discCovers/" + disc.id + "/0.png");
        if (!img.exists()){
            disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
        }else{
            disc.imgSrc = "src/main/resources/img/discCovers/" + disc.id + "/0.png";
        }
    }
    private void saveThumbnail(Disc disc, String url, int number){
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
                File file = new File("src/main/resources/img/discCovers/" + disc.id + "/" + number + ".png");
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
            newSeries.year = movie.getReleaseDate();

            return newSeries;
        }

        newSeries.name = seriesName;
        return newSeries;
    }
    private void downloadImages(Series series, int tmdbID){
        String imageBaseURL = "https://image.tmdb.org/t/p/original";
        String type = "tv";
        String languages = "null%2C" + currentCategory.language;

        if (!currentCategory.type.equals("Shows")){
            type = "movie";
        }

        if (!currentCategory.language.equals("en"))
            languages += "%2Cen";

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

                //region Process Posters
                List<Poster> posterList = images.posters;
                if (!posterList.isEmpty()){
                    Image originalImage = new Image(imageBaseURL + posterList.get(0).file_path);
                    saveCover(series, 0, originalImage);

                    Task<Void> posterTask = new Task<>() {
                        @Override
                        protected Void call() {
                            int processedFiles = 1;
                            for (int i = processedFiles; i < posterList.size(); i++){
                                if (processedFiles == 19)
                                    break;

                                Image originalImage = new Image(imageBaseURL + posterList.get(i).file_path);
                                saveCover(series, processedFiles, originalImage);

                                processedFiles++;
                            }
                            return null;
                        }
                    };

                    new Thread(posterTask).start();
                }

                File posterDir = new File("src/main/resources/img/seriesCovers/" + series.id + "/0.png");
                if (posterDir.exists()){
                    series.coverSrc = "src/main/resources/img/seriesCovers/" + series.id + "/0.png";
                }
                //endregion

                //region Process Background
                List<Backdrop> backdropList = images.backdrops;
                if (!backdropList.isEmpty()){
                    String path;
                    if (currentCategory.type.equals("Shows"))
                        path = backdropList.get(0).file_path;
                    else
                        path = backdropList.get(backdropList.size() - 1).file_path;

                    Image originalImage = new Image(imageBaseURL + path);

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
                //endregion
            } else {
                System.out.println("Response not successful: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //endregion
    }
    private void downloadLogos(Season season, int tmdbID){
        String imageBaseURL = "https://image.tmdb.org/t/p/original";
        String type = "tv";
        String languages = "null%2Cja%2Cen";

        if (!currentCategory.type.equals("Shows")){
            type = "movie";
            languages = "null%2Cen";

            if (!currentCategory.language.equals("en"))
                languages += "%2C" + currentCategory.language;
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
                    saveLogo(season, 0, originalImage);

                    Task<Void> logosTask = new Task<>() {
                        @Override
                        protected Void call() {
                            int processedFiles = 1;
                            for (int i = processedFiles; i < logosList.size(); i++){
                                if (processedFiles == 19)
                                    break;

                                Image originalImage = new Image(imageBaseURL + logosList.get(i).file_path);
                                saveLogo(season, processedFiles, originalImage);

                                processedFiles++;
                            }
                            return null;
                        }
                    };

                    new Thread(logosTask).start();
                }

                File posterDir = new File("src/main/resources/img/logos/" + season.id + "/0.png");
                if (posterDir.exists()){
                    season.logoSrc = "src/main/resources/img/logos/" + season.id + "/0.png";
                }
                //endregion
            } else {
                System.out.println("Response not successful: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //endregion
    }
    public void clearImageCache(){
        try{
            FileUtils.cleanDirectory(new File("src/main/resources/img/DownloadCache"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region ADD SECTION
    public void addSeries(Series s){
        Platform.runLater(() -> {
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editSeason-view.fxml"));
            Parent root1 = fxmlLoader.load();
            EditSeasonController addSeasonController = fxmlLoader.getController();
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
            EditDiscController addDiscController = fxmlLoader.getController();
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
            App.setPopUpProperties(stage);
            return stage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void removeCategory(MouseEvent event) {
        Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
        stage.showAndWait();

        if (acceptRemove){
            acceptRemove = false;
            App.removeCategory(currentCategory.name);
            updateCategories();
        }

        hideBackgroundShadow();
    }
    @FXML
    void removeCollection(MouseEvent event) {
        if (selectedSeries != null){
            Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
            stage.showAndWait();

            if (acceptRemove){
                acceptRemove = false;
                seriesList.remove(selectedSeries);
                App.removeCollection(selectedSeries, currentCategory);
                selectedSeries = null;

                if (!App.getCategories().isEmpty()){
                    seriesList = App.getSeriesFromCategory(App.getCategories().get(0));
                    showSeries();
                }
            }
        }

        hideBackgroundShadow();
    }
    @FXML
    void removeSeason(MouseEvent event){
        Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
        stage.showAndWait();

        if (acceptRemove){
            acceptRemove = false;
            seasonList.remove(selectedSeason);
            App.removeSeason(selectedSeason, selectedSeries);

            selectedSeason = null;
            selectSeries(selectedSeries);
        }

        hideBackgroundShadow();
    }
    @FXML
    void removeDisc(MouseEvent event){
        Stage stage = showConfirmationWindow(App.textBundle.getString("removeElement"), App.textBundle.getString("removeElementMessage"));
        stage.showAndWait();

        if (acceptRemove){
            acceptRemove = false;
            removeDisc(selectedDisc);
        }

        hideBackgroundShadow();
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
        App.removeDisc(d, selectedSeason);
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
            stage.getIcons().add(new Image("file:src/main/resources/img/icons/AppIcon.png"));
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
