package com.example.executablelauncher;

import com.example.executablelauncher.entities.*;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private String currentCategory = "";
    private double xOffset = 0;
    private double yOffset = 0;
    private double ASPECT_RATIO = 16.0 / 9.0;
    private int downloadedMetadataCount = 0;
    private int downloadedMetadataTotal = 0;
    //endregion

    //region DISC VALUES
    private List<File> selectedFiles = null;
    private File selectedFolder = null;
    private String text = "";
    private String typeValue = "";
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

        seasonsEpisodesBox.minHeightProperty().bind(discContainer.heightProperty());
        seasonsEpisodesBox.prefHeightProperty().bind(discContainer.heightProperty());
        seasonsEpisodesBox.maxHeightProperty().bind(discContainer.heightProperty());
        seasonBorderPane.prefHeightProperty().bind(seasonsEpisodesBox.heightProperty());
        seasonBorderPane.maxHeightProperty().bind(seasonsEpisodesBox.heightProperty());
        seasonInfoPane.minHeightProperty().bind(seasonBorderPane.heightProperty());
        seasonInfoPane.prefHeightProperty().bind(seasonBorderPane.heightProperty());
        seasonInfoPane.maxHeightProperty().bind(seasonBorderPane.heightProperty());
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
    }

    public void showSeries(){
        seriesButtons.clear();
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
            selectionOptions.setVisible(false);
            selectedSeries = s;
            selectSeriesButton(seriesButtons.get(getSeriesIndex(s)));
            seasonScroll.setVisible(true);


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
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), imageV);
        fadeIn.setFromValue(0.5);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void fillSeasonInfo() {
        selectedDiscs.clear();
        selectionOptions.setVisible(false);
        Image i = new Image("file:" + "src/main/resources/img/backgrounds/" + selectedSeason.getId() + "_desktopBlur.png");
        ASPECT_RATIO = i.getWidth() / i.getHeight();
        globalBackground.setImage(i);
        ImageView img = new ImageView(new Image("file:" + selectedSeason.getDesktopBackgroundEffect()));
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
            selectedSeries = App.findSeries(selectedSeries);
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



    private void showDiscs(Season s) {
        discList.clear();
        discContainer.getChildren().clear();
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

    //region UPDATE VALUES
    public void updateCategories(){
        List<String> categories = App.getCategories();
        categorySelector.getItems().clear();
        categorySelector.getItems().addAll(categories);
        if (categories.size() > 1){
            categorySelector.setValue(categories.get(1));
            selectCategory(categories.get(1));
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
        currentCategory = category;
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

    private void selectSeason(Season s) {
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
    //endregion
    public boolean isDiscSelected(){
        return !selectedDiscs.isEmpty();
    }
    public void refreshSeason(Season s){
        selectedSeason = null;
        selectSeason(s);
    }

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

    //region ADD SECTION
    public void addSeries(Series s){
        seriesList.add(s);
        selectCategory(currentCategory);
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

            if (selectedFiles != null){
                showDownloadWindow(Math.max(selectedFiles.size(), 1));
                addDiscs(selectedFiles,selectedFolder,text,typeValue);
            }

            this.selectedFiles = null;
            this.selectedFolder = null;
            this.text = "";
            this.typeValue = "";

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

    public void addDiscSetValues(List<File> selectedFiles, File selectedFolder, String text, String typeValue) {
        this.selectedFiles = selectedFiles;
        this.selectedFolder = selectedFolder;
        this.text = text;
        this.typeValue = typeValue;
    }
    public void addDiscs(List<File> selectedFiles, File selectedFolder, String text, String typeValue) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                if (text.equals(App.textBundle.getString("multipleSelection")))
                    for (File file : selectedFiles)
                        setDiscInfo(file, false, typeValue);
                else
                    setDiscInfo(selectedFolder, true, typeValue);

                return null;
            }
        };

        //Start the process in a new thread
        new Thread(task).start();
    }
    private void setDiscInfo(File file, boolean folder, String type) {
        Platform.runLater(() -> {
            Disc newDisc = new Disc();

            newDisc.setSeasonID(getCurrentSeason().getId());

            if (!folder){
                String fullName = file.getName().substring(0, file.getName().length() - 4);

                final String regexSeasonEpisode = "(?i)(?<season>S[0-9]{1,3}+)(?<episode>E[0-9]{1,4})";
                final String regexOnlyEpisode = "(?i)(?<episode>[0-9]{1,4})";

                final Pattern pattern = Pattern.compile(regexSeasonEpisode, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(fullName);

                if (!matcher.find()){
                    Pattern newPattern = Pattern.compile(regexOnlyEpisode, Pattern.MULTILINE);
                    Matcher newMatch = newPattern.matcher(fullName);

                    if (newMatch.find()){
                        setEpisodeNameAndThumbnail(newDisc, "NO_SEASON", newMatch.group("episode"));
                        newDisc.setEpisodeNumber(newMatch.group("episode"));
                    }else{
                        newDisc.setName(fullName);
                        newDisc.setEpisodeNumber("");
                    }
                }else{
                    setEpisodeNameAndThumbnail(newDisc, matcher.group("season").substring(1), matcher.group("episode").substring(1));
                    newDisc.setEpisodeNumber(matcher.group("episode").substring(1));
                }
            }else{
                newDisc.setName(App.textBundle.getString("disc") + " " + Objects.requireNonNull(App.findSeason(newDisc.getSeasonID())).getDiscs().size() + 1);
                newDisc.setEpisodeNumber("");
            }

            newDisc.setType(type);
            newDisc.setExecutableSrc(file.getAbsolutePath());

            App.addDisc(newDisc);
            addDisc(newDisc);
        });
    }

    public void setEpisodeNameAndThumbnail(Disc disc, String season, String episode){
        Season s = App.findSeason(disc.seasonID);
        assert s != null;
        Series series = App.findSeriesByName(s.collectionName);
        assert series != null;
        String tvdbId = String.valueOf(series.thetvdbID);

        List<EpisodeMetadata> episodeMetadata = App.episodesMetadata.get(tvdbId);

        if (episodeMetadata != null){
            EpisodeMetadata episodeMeta = null;

            int episodeToFind = Integer.parseInt(episode);
            int seasonToFind = -1;
            if (!season.equals("NO_SEASON"))
                seasonToFind = Integer.parseInt(season);

            for (EpisodeMetadata ep : episodeMetadata){
                if (season.equals("NO_SEASON")){
                    if (ep.absoluteEpisode == episodeToFind){
                        episodeMeta = ep;
                        break;
                    }
                }else{
                    if (ep.seasonNumber == seasonToFind && ep.episodeNumber == episodeToFind){
                        episodeMeta = ep;
                        break;
                    }
                }
            }

            if (episodeMeta == null){
                disc.name = "";
                disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
                updateImages();
                downloadedMetadataCount++;
                downloadingContentText.setText("Downloading images " + downloadedMetadataCount + "/" + downloadedMetadataTotal);
                if (downloadedMetadataCount == downloadedMetadataTotal)
                    hideDownloadWindow();
                return;
            }

            disc.name = episodeMeta.name;

            if (episodeMeta.imdbID.isEmpty()){
                disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
                updateImages();
                downloadedMetadataCount++;
                downloadingContentText.setText("Downloading images " + downloadedMetadataCount + "/" + downloadedMetadataTotal);
                if (downloadedMetadataCount == downloadedMetadataTotal)
                    hideDownloadWindow();
                return;
            }

            EpisodeMetadata finalEpisodeMeta = episodeMeta;
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try{
                        String imdbBase = "https://www.imdb.com/title/";
                        String mediaAll = "/mediaindex/?ref_=tt_mv_sm";
                        String posterSrc = null;

                        Document doc = Jsoup.connect(imdbBase + finalEpisodeMeta.imdbID + mediaAll).timeout(6000).get();
                        Elements body = doc.select("div.media_index_thumb_list");
                        List<String> imagesUrls = new ArrayList<>();
                        for (Element element : body){
                            Elements elements = element.select("a");
                            int i = 0;
                            for (Element e : elements){
                                if (i == 8)
                                    break;

                                if (i != 1)
                                    imagesUrls.add("https://www.imdb.com" + e.attr("href"));
                                i++;
                            }
                            break;
                        }

                        Files.createDirectories(Paths.get("src/main/resources/img/discCovers/" + disc.id + "/"));

                        int i = 0;
                        for (String url : imagesUrls){
                            doc = Jsoup.connect(url).timeout(6000).get();
                            body = doc.select("div.media-viewer");
                            for (Element element : body){
                                posterSrc = element.select("img").attr("src");
                            }

                            if (posterSrc != null){
                                Image originalImage = new Image(posterSrc, 480, 270, true, true);

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
                                        System.err.println("Disc downloaded thumbnail not saved");
                                    }
                                }
                            }
                            i++;
                        }
                    } catch (IOException e) {
                        System.err.println("AddDiscController: Error connecting to IMDB");
                    }

                    File img = new File("src/main/resources/img/discCovers/" + disc.id + "/0.png");
                    if (!img.exists()){
                        disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
                    }else{
                        disc.imgSrc = "src/main/resources/img/discCovers/" + disc.id + "/0.png";
                    }

                    return null;
                }
            };

            //Run when the process ends
            task.setOnSucceeded(e -> {
                updateImages();
                downloadedMetadataCount++;
                downloadingContentText.setText("Downloading images " + downloadedMetadataCount + "/" + downloadedMetadataTotal);
                if (downloadedMetadataCount == downloadedMetadataTotal)
                    hideDownloadWindow();
            });

            //Start the process in a new thread
            new Thread(task).start();
        }else{
            disc.name = "";
            disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";
        }
    }

    public void updateImages(){
        Platform.runLater(() -> {
            for (DiscController discController : discControllers) {
                discController.setThumbnail();
            }
        });
    }
    public void updateDisc(Disc d){
        for (DiscController discController : discControllers) {
            if (discController.disc.id == d.id){
                discController.setData(d);
                return;
            }
        }
    }
    //endregion

    //region EDIT SECTION
    @FXML
    void editCategory(MouseEvent event) {
        if (currentCategory.equals("NO CATEGORY"))
            return;

        Category cat = App.findCategory(currentCategory);
        if (cat != null){
            showBackgroundShadow();
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCategory-view.fxml"));
                Parent root1 = fxmlLoader.load();
                AddCategoryController addCategoryController = fxmlLoader.getController();
                addCategoryController.setParent(this);
                addCategoryController.setValues(cat.name, cat.showOnFullscreen);
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
        App.removeCategory(currentCategory);
        updateCategories();
    }
    @FXML
    void removeCollection(MouseEvent event) throws IOException {
        if (selectedSeries != null){
            seriesList.remove(selectedSeries);
            App.removeCollection(selectedSeries);
            selectedSeries = null;
            seriesList = App.getCollection();
            showSeries();
        }
        hideMenu();
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
