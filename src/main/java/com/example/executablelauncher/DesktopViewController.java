package com.example.executablelauncher;

import com.example.executablelauncher.entities.*;
import com.example.executablelauncher.tmdbMetadata.common.Genre;
import com.example.executablelauncher.tmdbMetadata.images.*;
import com.example.executablelauncher.tmdbMetadata.movies.MovieMetadata;
import com.example.executablelauncher.tmdbMetadata.series.EpisodeMetadata;
import com.example.executablelauncher.tmdbMetadata.series.SeasonMetadata;
import com.example.executablelauncher.tmdbMetadata.series.SeasonMetadataBasic;
import com.example.executablelauncher.tmdbMetadata.series.SeriesMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.movito.themoviedbapi.*;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.tv.TvSeries;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.*;
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
    public Category currentCategory;
    private double xOffset = 0;
    private double yOffset = 0;
    private double ASPECT_RATIO = 16.0 / 9.0;
    private boolean acceptRemove = false;
    private static int numFilesToCheck = 0;
    //endregion

    //region THEMOVIEDB ATTRIBUTES
    TmdbApi tmdbApi = new TmdbApi("4b46560aff5facd1d9ede196ce7d675f");
    SeasonsGroupMetadata episodesGroup = null;                                          //Create metadata holders for episode groups
    boolean movieMetadataToCorrect = false;
    TvSeries seriesMetadataToCorrect = null;
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
        seasonNumberText.setText(App.textBundle.getString("seasonNumber"));
        seasonNumberField.setText(String.valueOf(selectedSeason.seasonNumber));

        if (currentCategory.type.equals("Shows")){
            if (selectedSeries.logoSrc.isEmpty()){
                setTextNoLogo();
            }else{
                seasonLogoBox.getChildren().remove(0);
                seasonLogo = new ImageView();
                File file = new File(selectedSeries.logoSrc);
                setLogoNoText(file);
            }
        }else{
            if (selectedSeason.getLogoSrc().isEmpty()){
                setTextNoLogo();
            }else{
                seasonLogoBox.getChildren().remove(0);
                seasonLogo = new ImageView();
                File file = new File(selectedSeason.getLogoSrc());
                setLogoNoText(file);
            }
        }

        try{
            File file;
            if (!selectedSeries.getCoverSrc().isEmpty()){
                file = new File(selectedSeries.getCoverSrc());
            }else{
                file = new File("src/main/resources/img/DefaultPoster.png");
            }

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
        } catch (MalformedURLException e) {
            System.err.println("Series cover not found");
        } catch (IOException e) {
            System.err.println("DesktopView: Series Cover not properly compressed");
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


        if (!discList.isEmpty()) {
            discList.sort(new Utils.DiscComparator().reversed());
            addEpisodeCardWithDelay(0);
        }
    }

    private void addEpisodeCardWithDelay(int index) {
        if (index < discList.size()) {
            Disc disc = discList.get(index);
            addEpisodeCard(disc);

            index++;

            Duration delay = Duration.millis(10);

            int finalIndex = index;
            KeyFrame keyFrame = new KeyFrame(delay, event -> addEpisodeCardWithDelay(finalIndex));
            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
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

            if (movieMetadataToCorrect)
                correctIdentificationMovie();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void correctIdentificationShow(){
        /*showBackgroundShadow();
        downloadingContentTextStatic.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindowStatic.setVisible(true);
        Task<Void> correctIdentificationTask = new Task<>() {
            @Override
            protected Void call() {
                selectedSeries.name = seriesMetadataToCorrect.getName();
                selectedSeries.year = seriesMetadataToCorrect.getFirstAirDate();
                selectedSeries.themdbID = seriesMetadataToCorrect.getId();
                selectedSeries.overview = seriesMetadataToCorrect.getOverview();

                try{
                    FileUtils.deleteDirectory(new File("src/main/resources/img/seriesCovers/" + selectedSeries.id));
                } catch (IOException e) {
                    System.err.println("DesktopViewController.correctIdentificationShow: Error deleting directories");
                }

                try{
                    Files.createDirectories(Paths.get("src/main/resources/img/seriesCovers/" + selectedSeries.id + "/"));
                } catch (IOException e) {
                    System.err.println("correctIdentificationShow: Directory could not be created");
                }

                for (String seasonID : selectedSeries.getSeasons()){
                    SeasonMetadataBasic season = App.findSeason(seasonID);

                    if (season == null)
                        continue;

                    try{
                        FileUtils.deleteDirectory(new File("src/main/resources/img/logos/" + season.id));
                    } catch (IOException e) {
                        System.err.println("DesktopViewController.correctIdentificationShow: Error deleting directories");
                    }

                    try{
                        Files.createDirectories(Paths.get("src/main/resources/img/logos/" + season.id + "/"));
                        Files.createDirectories(Paths.get("src/main/resources/img/backgrounds/" + season.id + "/"));
                    } catch (IOException e) {
                        System.err.println("correctIdentificationShow: Directory could not be created");
                    }

                    downloadLogos(selectedSeries, season, selectedSeason.themdbID);
                    downloadImages(selectedSeries, selectedSeason.themdbID);
                    saveBackground(season, "src/main/resources/img/DownloadCache/" + selectedSeason.themdbID + ".png");

                    if (selectedSeason.getDiscs().size() == 1){
                        Disc disc = App.findDisc(selectedSeason.getDiscs().get(0));

                        if (disc != null){
                            disc.name = selectedSeason.name;
                            disc.overview = selectedSeason.overview;
                        }
                    }

                    for (String discID : selectedSeason.getDiscs()){
                        Disc disc = App.findDisc(discID);

                        if (disc == null)
                            continue;

                        try{
                            FileUtils.deleteDirectory(new File("src/main/resources/img/discCovers/" + discID));
                        } catch (IOException e) {
                            System.err.println("DesktopViewController.correctIdentificationShow: Error deleting thumbnails");
                        }

                        try{
                            Files.createDirectories(Paths.get("src/main/resources/img/discCovers/" + discID + "/"));
                        } catch (IOException e) {
                            System.err.println("correctIdentificationShow: Directory could not be created");
                        }

                        setMovieThumbnail(disc, selectedSeason.themdbID);
                    }
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

        new Thread(correctIdentificationTask).start();*/
    }
    public void correctIdentificationMovie(){
        MovieMetadata metadata = downloadMovieMetadata(selectedSeason.themdbID);

        if (metadata == null)
            return;

        selectedSeason.name = metadata.title;
        selectedSeason.overview = metadata.overview;
        selectedSeason.year = metadata.release_date.substring(0, metadata.release_date.indexOf("-"));
        selectedSeason.score = (float) ((int) (metadata.vote_average * 10.0)) / 10.0f;
        selectedSeason.tagline = metadata.tagline;

        Images images = downloadImages(selectedSeason.themdbID);

        if (images != null)
            loadImages(selectedSeason.id, images, selectedSeason.themdbID, false);

        File posterDir = new File("src/main/resources/img/seriesCovers/" + selectedSeason.id + "/0.png");
        if (posterDir.exists()){
            selectedSeason.coverSrc = "src/main/resources/img/seriesCovers/" + selectedSeason.id + "/0.png";
        }

        downloadLogos(selectedSeries, selectedSeason, selectedSeason.themdbID);
        saveBackground(selectedSeason, "src/main/resources/img/DownloadCache/" + selectedSeason.themdbID + ".png");

        //Process background music
        List<YoutubeVideo> results = searchYoutube(selectedSeason.name + " main theme");

        if (results != null){
            downloadMedia(selectedSeason.id, results.get(0).watch_url);

            File mediaCahceDir = new File("src/main/resources/downloadedMediaCache/" + selectedSeason.id + "/");
            File[] filesInMediaCache = mediaCahceDir.listFiles();

            if (filesInMediaCache != null){
                File audioFile = filesInMediaCache[0];

                try{
                    Files.copy(audioFile.toPath(), Paths.get("src/main/resources/music/" + selectedSeason.id + ".mp3"), StandardCopyOption.REPLACE_EXISTING);
                    selectedSeason.musicSrc = "src/main/resources/music/" + selectedSeason.id + ".mp3";

                    File directory = new File("src/main/resources/downloadedMediaCache/" + selectedSeason.id + "/");
                    FileUtils.deleteDirectory(directory);
                } catch (IOException e) {
                    System.err.println("processEpisode: Could not copy downloaded audio file");
                }
            }
        }

        for (String discID : selectedSeason.getDiscs()){
            Disc disc = App.findDisc(discID);

            if (disc == null)
                continue;

            setMovieThumbnail(disc, selectedSeason.themdbID);
        }

        refreshSeason(selectedSeason);
    }
    public void setCorrectIdentificationShow(int newID){

    }
    public void setCorrectIdentificationMovie(int newID){
        movieMetadataToCorrect = true;
        selectedSeason.themdbID = newID;
    }
    //endregion

    //region AUTOMATED FILE SEARCH
    public void searchFiles(){
        downloadingContentText.setText(App.textBundle.getString("downloadingMessage"));
        downloadingContentWindow.setVisible(true);

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
    public void loadCategory(String name){
        categorySelector.getItems().add(name);
        categorySelector.setValue(name);

        currentCategory = App.findCategory(name);

        if (currentCategory == null)
            return;

        searchFiles();
    }
    private void loadHalfTask(List<File> files, int start, int end){
        Task<Void> loadHalfTask = new Task<>() {
            @Override
            protected Void call() {
                for (int i = start; i < end; i++){
                    File file = files.get(i);

                    if (currentCategory.type.equals("Shows")) {
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
            if (numFilesToCheck == 0)
                downloadingContentWindow.setVisible(false);
        });

        loadHalfTask.setOnCancelled(e -> {
            numFilesToCheck--;
            if (numFilesToCheck == 0)
                downloadingContentWindow.setVisible(false);
        });

        loadHalfTask.setOnFailed(e -> {
            numFilesToCheck--;
            if (numFilesToCheck == 0)
                downloadingContentWindow.setVisible(false);
        });

        new Thread(loadHalfTask).start();
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

        if (currentCategory.analyzedFolders.get(directory.getAbsolutePath()) != null){
            series = App.findSeries(currentCategory.analyzedFolders.get(directory.getAbsolutePath()));
        }else{
            series = new Series();
            series.folder = directory.getAbsolutePath();
            currentCategory.analyzedFolders.put(directory.getAbsolutePath(), series.id);
            currentCategory.series.add(series.id);
            App.addCollection(series);
        }

        int themdbID = series.themdbID;

        //This means that this is a new Folder to be analyzed, so we need to search for the show metadata
        if (themdbID == -1){
            //Search show by name of the folder
            TvResultsPage tvResults = tmdbApi.getSearch().searchTv(directory.getName(), 1, currentCategory.language, true, 1);

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
        //Process each episode
        for (File video : videoFiles){
            if (currentCategory.analyzedFiles.get(video.getAbsolutePath()) != null)
                continue;

            processEpisode(series, video, seasonsMetadata, episodesGroup);
        }

        episodesGroup = null;
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

        series.tagline = seriesMetadata.tagline;
        series.numberOfSeasons = seriesMetadata.number_of_seasons;
        series.numberOfEpisodes = seriesMetadata.number_of_episodes;
        series.category = currentCategory.name;

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
                loadImages(series.id, images, series.themdbID, false);

            File posterDir = new File("src/main/resources/img/seriesCovers/" + series.id + "/0.png");
            if (posterDir.exists()){
                series.coverSrc = "src/main/resources/img/seriesCovers/" + series.id + "/0.png";
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
                        if (processedFiles == 31)
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
                if (currentCategory.type.equals("Shows"))
                    path = backdropList.get(0).file_path;
                else
                    path = backdropList.get(backdropList.size() - 1).file_path;

                Image originalImage = new Image(imageBaseURL + path);

                if (!originalImage.isError()){
                    File file;
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
        //endregion
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
                System.out.println("getEpisodeGroup: Response not successful: " + response.code());
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
                || videoExtension.equals(".wmv") || videoExtension.equals(".mpeg") || videoExtension.equals(".m2ts") || videoExtension.equals(".iso");
    }
    private void processEpisode(Series series, File file, List<SeasonMetadata> seasonsMetadata, SeasonsGroupMetadata episodesGroup){
        //Name of the file without the extension
        String fullName = file.getName().substring(0, file.getName().lastIndexOf("."));

        //Regular expressions for identifying season and/or episode numbers in the file name
        final String regexSeasonEpisode = "(?i)(?<season>S[0-9]{1,3}+)(?<episode>E[0-9]{1,4})";
        final String regexOnlyEpisode = "(?i)(?<episode>[0-9]{1,4})";

        final Pattern pattern = Pattern.compile(regexSeasonEpisode, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(fullName);

        //SeasonMetadataBasic and episode metadata to find for the current file
        SeasonMetadata seasonMetadata = null;
        EpisodeMetadata episodeMetadata = null;

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

            boolean toFindMetadata = true;
            for (SeasonMetadata season : seasonsMetadata){
                if (season.season_number == seasonNumber) {
                    toFindMetadata = false;
                    break;
                }
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

        Season season = App.findSeason(series, seasonMetadata.season_number);
        if (season == null){
            season = new Season();

            season.name = seasonMetadata.name;
            season.overview = seasonMetadata.overview;
            season.year = seasonMetadata.air_date.substring(0, seasonMetadata.air_date.indexOf("-"));
            season.seasonNumber = seasonMetadata.season_number;
            season.score = (float) ((int) (seasonMetadata.vote_average * 10.0)) / 10.0f;
            season.seriesID = series.id;

            App.addSeason(season, series.id);

            if (season.backgroundSrc.isEmpty() || season.backgroundSrc.equals("src/main/resources/img/DefaultBackground.png")) {
                File f = new File("src/main/resources/img/DownloadCache/" + series.themdbID + ".png");
                if (!f.exists() && series.seasons.size() > 1){
                    Season s = App.findSeason(series.seasons.get(0));

                    if (s != null){
                        saveBackground(season, s.backgroundSrc);
                    }
                }else if (f.exists()){
                    saveBackground(season, "src/main/resources/img/DownloadCache/" + series.themdbID + ".png");
                }
            }

            if (series.seasons.size() == 1)
                downloadLogos(series, season, series.themdbID);

            if (season.seasonNumber == 0)
                season.order = 100;

            if (series.getSeasons().size() == 1){
                List<YoutubeVideo> results = searchYoutube(series.name + " main theme");

                if (results != null){
                    downloadMedia(season.id, results.get(0).watch_url);

                    File mediaCahceDir = new File("src/main/resources/downloadedMediaCache/" + season.id + "/");
                    File[] filesInMediaCache = mediaCahceDir.listFiles();

                    if (filesInMediaCache != null){
                        File audioFile = filesInMediaCache[0];

                        try{
                            Files.copy(audioFile.toPath(), Paths.get("src/main/resources/music/" + season.id + ".mp4"), StandardCopyOption.REPLACE_EXISTING);
                            season.musicSrc = "src/main/resources/music/" + season.id + ".mp4";

                            File directory = new File("src/main/resources/downloadedMediaCache/" + season.id + "/");
                            FileUtils.deleteDirectory(directory);
                        } catch (IOException e) {
                            System.err.println("processEpisode: Could not copy downloaded audio file");
                        }
                    }
                }
            }
        }

        Disc disc = App.findDisc(season, episodeMetadata.episode_number);
        if (disc != null) {
            disc.executableSrc = file.getAbsolutePath();
            return;
        }

        disc = new Disc();

        //Set the metadata for the new episode
        disc.seasonID = season.id;
        disc.executableSrc = file.getAbsolutePath();
        setEpisodeData(disc, episodeMetadata, series);

        currentCategory.analyzedFiles.put(file.getAbsolutePath(), disc.id);
        App.addDisc(disc);
    }
    private void setEpisodeData(Disc disc, EpisodeMetadata episodeMetadata, Series show){
        disc.name = episodeMetadata.name;
        disc.overview = episodeMetadata.overview;
        disc.episodeNumber =episodeMetadata.episode_number;
        disc.year = Utils.episodeDateFormat(episodeMetadata.air_date, currentCategory.language);
        disc.score = (float) ((int) (episodeMetadata.vote_average * 10.0)) / 10.0f;
        disc.runtime = episodeMetadata.runtime;
        String imageBaseURL = "https://image.tmdb.org/t/p/original";

        try{
            Files.createDirectories(Paths.get("src/main/resources/img/discCovers/" + disc.id + "/"));
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
    private void saveCover(String id, int i, Image originalImage) {
        try{
            Files.createDirectories(Paths.get("src/main/resources/img/seriesCovers/" + id + "/"));
        } catch (IOException e) {
            System.err.println("Directory could not be created");
        }

        if (!originalImage.isError()){
            File file = new File("src/main/resources/img/seriesCovers/" + id + "/" + i + ".png");
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
            Files.createDirectories(Paths.get("src/main/resources/img/logos/" + id + "/"));
        } catch (IOException e) {
            System.err.println("Directory could not be created");
        }

        if (!originalImage.isError()){
            File file = new File("src/main/resources/img/logos/" + id + "/" + i + ".png");
            try{
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(originalImage, null);
                ImageIO.write(renderedImage,"png", file);
            } catch (IOException e) {
                System.err.println("DesktopViewController: Downloaded logo not saved");
            }
        }
    }
    private void scanMovie(File f){
        //Regular expression to catch name and/or year of the movie
        Pattern regex = Pattern.compile("([^(]*)\\s*(?:\\((\\d{4})\\))?");

        //If the movie exists, do not add a new Card in the main view
        boolean exists = false;

        if (!f.isDirectory()){
            //region MOVIE FILE ONLY
            if (!validVideoFile(f)){
                return;
            }

            if (currentCategory.analyzedFiles.get(f.getAbsolutePath()) != null)
                return;

            //region CREATE/EDIT SERIES
            Series series = new Series();
            series.folder = f.getAbsolutePath();
            currentCategory.analyzedFolders.put(f.getAbsolutePath(), series.id);
            currentCategory.series.add(series.id);
            App.addCollection(series);

            Matcher matcher = regex.matcher(f.getName());
            if (!matcher.find())
                return;

            String movieName = matcher.group(1).trim();
            String year = matcher.group(2);

            if (year == null)
                year = "1";

            //Search show by name of the folder
            int themdbID = searchFirstMovie(movieName, Integer.parseInt(year));

            if (themdbID == -1) {
                series.name = movieName;
                Season newSeason = new Season();
                newSeason.name = movieName;
                newSeason.year = year;
                newSeason.seriesID = series.id;
                newSeason.seasonNumber = series.seasons.size();

                App.addSeason(newSeason, series.id);
                currentCategory.analyzedFolders.put(f.getAbsolutePath(), series.id);

                saveDiscWithoutMetadata(f, newSeason);
                addSeries(series);
                return;
            }

            MovieMetadata movieMetadata = downloadMovieMetadata(themdbID);

            series.name = movieMetadata.title;
            Season season = new Season();
            season.folder = f.getAbsolutePath();
            season.seriesID = series.id;
            season.themdbID = themdbID;

            season.name = movieMetadata.title;
            season.overview = movieMetadata.overview;
            season.year = movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-"));
            season.score = (float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f;
            season.tagline = movieMetadata.tagline;
            season.seasonNumber = series.seasons.size() + 1;

            List<Genre> genres = movieMetadata.genres;

            if (genres != null){
                List<String> genreList = new ArrayList<>();
                for (Genre genre : genres){
                    genreList.add(genre.name);
                }

                season.genres = genreList;
            }

            currentCategory.seasonFolders.put(f.getAbsolutePath(), season.id);
            App.addSeason(season, series.id);

            Images images = downloadImages(season.themdbID);

            if (images != null){
                loadImages(season.id, images, season.themdbID, false);
                loadImages(series.id, images, season.themdbID, true);
            }

            File posterDir = new File("src/main/resources/img/seriesCovers/" + season.id + "/0.png");
            if (posterDir.exists()){
                season.coverSrc = "src/main/resources/img/seriesCovers/" + season.id + "/0.png";
            }

            posterDir = new File("src/main/resources/img/seriesCovers/" + series.id + "/0.png");
            if (posterDir.exists()){
                series.coverSrc = "src/main/resources/img/seriesCovers/" + season.id + "/0.png";
            }

            downloadLogos(series, season, season.themdbID);
            saveBackground(season, "src/main/resources/img/DownloadCache/" + season.themdbID + ".png");

            //Process background music
            List<YoutubeVideo> results = searchYoutube(season.name + " main theme");

            if (results != null){
                downloadMedia(season.id, results.get(0).watch_url);

                File mediaCahceDir = new File("src/main/resources/downloadedMediaCache/" + season.id + "/");
                File[] filesInMediaCache = mediaCahceDir.listFiles();

                if (filesInMediaCache != null){
                    File audioFile = filesInMediaCache[0];

                    try{
                        Files.copy(audioFile.toPath(), Paths.get("src/main/resources/music/" + season.id + ".mp3"), StandardCopyOption.REPLACE_EXISTING);
                        season.musicSrc = "src/main/resources/music/" + season.id + ".mp3";

                        File directory = new File("src/main/resources/downloadedMediaCache/" + season.id + "/");
                        FileUtils.deleteDirectory(directory);
                    } catch (IOException e) {
                        System.err.println("processEpisode: Could not copy downloaded audio file");
                    }
                }
            }

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

            if (currentCategory.analyzedFolders.get(f.getAbsolutePath()) != null) {
                series = App.findSeries(currentCategory.analyzedFolders.get(f.getAbsolutePath()));
                exists = true;

                if (series == null)
                    return;
            }else{
                series = new Series();
                series.name = f.getName();
                series.folder = f.getAbsolutePath();
                currentCategory.analyzedFolders.put(f.getAbsolutePath(), series.id);
                currentCategory.series.add(series.id);
                App.addCollection(series);
            }

            if (!folders.isEmpty()){
                //region FOLDERS CORRESPONDING DIFFERENT MOVIES FROM A COLLECTION
                for (File folder : folders){
                    File[] filesInFolder = folder.listFiles();
                    if (filesInFolder == null)
                        continue;

                    Matcher matcher = regex.matcher(folder.getName());
                    if (!matcher.find())
                        return;

                    String movieName = matcher.group(1).trim();
                    String year = matcher.group(2);

                    if (year == null)
                        year = "1";

                    boolean updateMetadata = false;

                    Season season;
                    if (currentCategory.seasonFolders.get(folder.getAbsolutePath()) != null){
                        season = App.findSeason(currentCategory.seasonFolders.get(folder.getAbsolutePath()));
                    }else {
                        season = new Season();
                        currentCategory.seasonFolders.put(folder.getAbsolutePath(), season.id);
                        season.seasonNumber = series.seasons.size() + 1;
                        season.seriesID = series.id;
                        season.folder = folder.getAbsolutePath();
                        App.addSeason(season, series.id);

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
                        season.seriesID = series.id;

                        season.name = movieMetadata.title;
                        season.overview = movieMetadata.overview;
                        season.year = movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-"));
                        season.score = (float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f;
                        season.tagline = movieMetadata.tagline;

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
                            loadImages(season.id, images, season.themdbID, false);
                            loadImages(series.id, images, season.themdbID, true);
                        }

                        File posterDir = new File("src/main/resources/img/seriesCovers/" + season.id + "/0.png");
                        if (posterDir.exists()){
                            season.coverSrc = "src/main/resources/img/seriesCovers/" + season.id + "/0.png";
                        }

                        posterDir = new File("src/main/resources/img/seriesCovers/" + series.id + "/0.png");
                        if (posterDir.exists()){
                            series.coverSrc = "src/main/resources/img/seriesCovers/" + season.id + "/0.png";
                        }

                        downloadLogos(series, season, season.themdbID);
                        saveBackground(season, "src/main/resources/img/DownloadCache/" + season.themdbID + ".png");

                        //Process background music
                        List<YoutubeVideo> results = searchYoutube(season.name + " main theme");

                        if (results != null){
                            downloadMedia(season.id, results.get(0).watch_url);

                            File mediaCahceDir = new File("src/main/resources/downloadedMediaCache/" + season.id + "/");
                            File[] filesInMediaCache = mediaCahceDir.listFiles();

                            if (filesInMediaCache != null){
                                File audioFile = filesInMediaCache[0];

                                try{
                                    Files.copy(audioFile.toPath(), Paths.get("src/main/resources/music/" + season.id + ".mp3"), StandardCopyOption.REPLACE_EXISTING);
                                    season.musicSrc = "src/main/resources/music/" + season.id + ".mp3";

                                    File directory = new File("src/main/resources/downloadedMediaCache/" + season.id + "/");
                                    FileUtils.deleteDirectory(directory);
                                } catch (IOException e) {
                                    System.err.println("processEpisode: Could not copy downloaded audio file");
                                }
                            }
                        }
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
                Matcher matcher = regex.matcher(f.getName());
                if (!matcher.find())
                    return;

                String movieName = matcher.group(1).trim();
                String year = matcher.group(2);

                if (year == null)
                    year = "1";

                boolean updateMetadata = false;

                Season season;
                if (currentCategory.seasonFolders.get(f.getAbsolutePath()) != null){
                    season = App.findSeason(currentCategory.seasonFolders.get(f.getAbsolutePath()));
                }else {
                    season = new Season();
                    currentCategory.seasonFolders.put(f.getAbsolutePath(), season.id);
                    season.seasonNumber = series.seasons.size() + 1;
                    season.seriesID = series.id;
                    season.folder = f.getAbsolutePath();
                    App.addSeason(season, series.id);

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
                    season.seriesID = series.id;

                    season.name = movieMetadata.title;
                    season.overview = movieMetadata.overview;
                    season.year = movieMetadata.release_date.substring(0, movieMetadata.release_date.indexOf("-"));
                    season.score = (float) ((int) (movieMetadata.vote_average * 10.0)) / 10.0f;
                    season.tagline = movieMetadata.tagline;

                    List<Genre> genres = movieMetadata.genres;

                    if (genres != null){
                        List<String> genreList = new ArrayList<>();
                        for (Genre genre : genres){
                            genreList.add(genre.name);
                        }

                        season.genres = genreList;
                    }

                    currentCategory.seasonFolders.put(f.getAbsolutePath(), season.id);

                    Images images = downloadImages(season.themdbID);

                    if (images != null){
                        loadImages(season.id, images, season.themdbID, false);
                        loadImages(series.id, images, season.themdbID, true);
                    }

                    File posterDir = new File("src/main/resources/img/seriesCovers/" + season.id + "/0.png");
                    if (posterDir.exists()){
                        season.coverSrc = "src/main/resources/img/seriesCovers/" + season.id + "/0.png";
                    }

                    posterDir = new File("src/main/resources/img/seriesCovers/" + series.id + "/0.png");
                    if (posterDir.exists()){
                        series.coverSrc = "src/main/resources/img/seriesCovers/" + season.id + "/0.png";
                    }

                    downloadLogos(series, season, season.themdbID);
                    saveBackground(season, "src/main/resources/img/DownloadCache/" + season.themdbID + ".png");

                    //Process background music
                    List<YoutubeVideo> results = searchYoutube(season.name + " main theme");

                    if (results != null){
                        downloadMedia(season.id, results.get(0).watch_url);

                        File mediaCahceDir = new File("src/main/resources/downloadedMediaCache/" + season.id + "/");
                        File[] filesInMediaCache = mediaCahceDir.listFiles();

                        if (filesInMediaCache != null){
                            File audioFile = filesInMediaCache[0];

                            try{
                                Files.copy(audioFile.toPath(), Paths.get("src/main/resources/music/" + season.id + ".mp3"), StandardCopyOption.REPLACE_EXISTING);
                                season.musicSrc = "src/main/resources/music/" + season.id + ".mp3";

                                File directory = new File("src/main/resources/downloadedMediaCache/" + season.id + "/");
                                FileUtils.deleteDirectory(directory);
                            } catch (IOException e) {
                                System.err.println("processEpisode: Could not copy downloaded audio file");
                            }
                        }
                    }
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
    private int searchFirstMovie(String name, int year){
        MovieResultsPage movieResults = tmdbApi.getSearch().searchMovie(name, year, currentCategory.language, true, 1);

        if (movieResults.getTotalResults() > 0)
            return movieResults.getResults().get(0).getId();

        return -1;
    }
    private void saveDiscWithoutMetadata(File f, Season season) {
        Disc disc;

        if (currentCategory.analyzedFiles.get(f.getAbsolutePath()) != null){
            disc = App.findDisc(currentCategory.analyzedFiles.get(f.getAbsolutePath()));
        }else{
            disc = new Disc();
            disc.seasonID = season.id;
            currentCategory.analyzedFiles.put(f.getAbsolutePath(), disc.id);
            App.addDisc(disc);
        }

        disc.name = f.getName().substring(0, f.getName().lastIndexOf("."));
        disc.executableSrc = f.getAbsolutePath();
        disc.seasonNumber = season.seasonNumber;
        disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
    }
    private void processMovie(File file, Season season, int runtime){
        Disc disc;

        if (currentCategory.analyzedFiles.get(file.getAbsolutePath()) != null){
            disc = App.findDisc(currentCategory.analyzedFiles.get(file.getAbsolutePath()));
        }else{
            disc = new Disc();
            disc.seasonID = season.id;
            currentCategory.analyzedFiles.put(file.getAbsolutePath(), disc.id);
            App.addDisc(disc);
        }

        disc.name = file.getName().substring(0, file.getName().lastIndexOf("."));
        disc.episodeNumber = season.getDiscs().size();
        disc.score = season.score;
        disc.overview = season.overview;
        disc.year = season.year;
        disc.runtime = runtime;
        disc.seasonNumber = season.seasonNumber;
        disc.executableSrc = file.getAbsolutePath();

        setMovieThumbnail(disc, season.themdbID);
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
    public MovieMetadata downloadMovieMetadata(int tmdbID){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/" + tmdbID + "?language=" + currentCategory.language)
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
                    .url("https://api.themoviedb.org/3/tv/" + tmdbID + "?language=" + currentCategory.language)
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
                    .url("https://api.themoviedb.org/3/tv/" + tmdbID + "/season/" + season + "?language=" + currentCategory.language)
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
        String languages = "null%2C" + currentCategory.language;

        if (!currentCategory.type.equals("Shows")){
            type = "movie";
        }

        if (!currentCategory.language.equals("en"))
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

        if (currentCategory.type.equals("Shows"))
            id = series.id;
        else
            id = season.id;

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

                File posterDir = new File("src/main/resources/img/logos/" + id + "/0.png");
                if (posterDir.exists()){
                    if (currentCategory.type.equals("Shows"))
                        series.logoSrc = "src/main/resources/img/logos/" + id + "/0.png";
                    else
                        season.logoSrc = "src/main/resources/img/logos/" + id + "/0.png";
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
            FileUtils.cleanDirectory(new File("src/main/resources/img/DownloadCache"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region DOWNLOAD MEDIA
    public List<YoutubeVideo> searchYoutube(String videoName){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/resources/python/YoutubeSearch.py", videoName);
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

    public void downloadMedia(String seasonID, String url){
        try {
            Files.createDirectories(Paths.get("src/main/resources/downloadedMediaCache/" + seasonID + "/"));
            File directory = new File("src/main/resources/downloadedMediaCache/" + seasonID + "/");

            ProcessBuilder pb;
            pb = new ProcessBuilder("pytube"
                , url
                , "-a", "-t", directory.getAbsolutePath());

            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("downloadMedia: Error downloading media");
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
                addCategoryController.setValues(currentCategory);
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
                addColController.setSeries(selectedSeries, currentCategory.type.equals("Shows"));
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
            addSeasonController.setSeason(selectedSeason, currentCategory.type.equals("Shows"));
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
                int index = seriesList.indexOf(selectedSeries);
                seriesList.remove(selectedSeries);
                seriesButtons.remove(index);
                seriesContainer.getChildren().remove(index);
                App.removeCollection(selectedSeries, currentCategory);
                selectedSeries = null;
                if (!seriesList.isEmpty()){
                    selectSeriesButton(seriesButtons.get(0));
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
            App.removeSeason(selectedSeason, selectedSeries, currentCategory);

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
        App.removeDisc(d, selectedSeason, currentCategory);
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
