package com.example.executablelauncher;

import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DesktopViewController {
    @FXML
    private GridPane centralBox;

    @FXML
    private FlowPane discContainer;

    @FXML
    private ScrollPane episodeScroll;

    @FXML
    private RowConstraints centralBoxTop;

    @FXML
    private VBox leftBox;

    @FXML
    private StackPane mainBox;

    @FXML
    private VBox mainMenu;

    @FXML
    private FlowPane menuParentPane;

    @FXML
    private VBox rightBox;

    @FXML
    private ImageView seasonBackground;

    @FXML
    private ImageView globalBackgroundShadow;

    @FXML
    private VBox seasonContainer;

    @FXML
    private HBox seasonCoverLogoBox;

    @FXML
    private StackPane seasonInfoPane;

    @FXML
    private ImageView seasonLogo;

    @FXML
    private ImageView globalBackground;

    @FXML
    private Label seasonName;

    @FXML
    private ScrollPane seasonScroll;

    @FXML
    private VBox selectMenu;

    @FXML
    private VBox seriesContainer;

    @FXML
    private ImageView seriesCover;

    @FXML
    private ScrollPane seriesScrollPane;

    @FXML
    private ChoiceBox<String> categorySelector;

    private List<Series> seriesList = new ArrayList<>();
    private List<Season> seasonList = new ArrayList<>();
    private List<Disc> discList = new ArrayList<>();

    private Series selectedSeries = null;
    private Season selectedSeason = null;
    private Disc selectedDisc = null;
    private String currentCategory;

    public void showSeries(){
        for (Series s : seriesList){
            Button seriesButton = new Button();
            seriesButton.setText(s.getName());

            seriesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    openSeriesMenu(event);
                }else{
                    selectSeriesButton(event);
                }
            });

            seriesContainer.getChildren().add(seriesButton);
        }

        blankSelection();
    }

    public void selectCategory(String category){
        currentCategory = category;
        seriesList = Main.getSeriesFromCategory(category);
        showSeries();
    }

    public void initValues(){
        categorySelector.getItems().addAll(Main.getCategories());

        seriesList = Main.getCollection();
        showSeries();

        categorySelector.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> selectCategory(newValue) );

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.F11 == event.getCode()) {
                fullScreen();
            }
        });

        mainBox.getScene().getWindow().setOnCloseRequest(e -> closeWindow());

        menuParentPane.setVisible(false);
        mainMenu.setVisible(false);

        //Elements size
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        seriesScrollPane.setPrefHeight(screenHeight);
        seriesContainer.setPrefHeight(screenHeight);
        seasonScroll.setPrefHeight(screenHeight);
        discContainer.setPrefHeight(screenHeight);

        seasonBackground.fitWidthProperty().bind(centralBox.widthProperty());
        seasonBackground.fitHeightProperty().bind(centralBox.heightProperty().multiply(0.6));
        seasonBackground.setPreserveRatio(false);

        globalBackground.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackground.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackground.setPreserveRatio(false);

        globalBackgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackgroundShadow.setPreserveRatio(false);
    }

    @FXML
    void close(MouseEvent e) throws IOException {
        closeWindow();
    }

    private void closeWindow(){
        try{
            Main.SaveData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) mainBox.getScene().getWindow();
        stage.close();
    }

    private void openSeriesMenu(MouseEvent event) {
    }

    public void blankSelection(){
        centralBox.setVisible(false);
        rightBox.setVisible(false);
    }

    public void selectSeries(Series s){
        selectedSeries = s;
        centralBox.setVisible(true);

        if (!s.getSeasons().isEmpty()){
            seasonList.clear();
            for (int i : s.getSeasons()){
                Season season = Main.findSeason(i);
                if (season != null)
                    seasonList.add(season);
            }

            showSeasons();

            selectedSeason = seasonList.get(0);

            fillSeasonInfo();

            if (!selectedSeason.getDiscs().isEmpty()){
                rightBox.setVisible(true);

                showDiscs(selectedSeason);
            }else{
                rightBox.setVisible(false);
            }
        }else{
            centralBox.setVisible(false);
            rightBox.setVisible(false);
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
        globalBackground.setImage(new Image(selectedSeason.getBackgroundSrc()));
        seasonBackground.setImage(new Image(selectedSeason.getBackgroundSrc()));
        fadeInTransition(globalBackground);
        fadeInTransition(seasonBackground);
        if (selectedSeason.getLogoSrc().equals("NO_LOGO")){
            seasonCoverLogoBox.getChildren().remove(1);
            Label seasonLogoText = new Label(selectedSeries.getName());
            seasonLogoText.setFont(new Font("Arial", 32));
            seasonLogoText.setStyle("-fx-font-weight: bold");
            seasonLogoText.setTextFill(Color.color(1, 1, 1));
            seasonLogoText.setEffect(new DropShadow());
            seasonLogoText.setPadding(new Insets(0, 0, 0, 15));
            seasonCoverLogoBox.getChildren().add(seasonLogoText);
        }else{
            seasonCoverLogoBox.getChildren().remove(1);
            seasonLogo = new ImageView();
            seasonLogo.setImage(new Image(selectedSeason.getLogoSrc()));
            seasonCoverLogoBox.getChildren().add(seasonLogo);
        }

        seriesCover.setImage(new Image(selectedSeries.getCoverSrc()));
        seasonName.setText(selectedSeason.getName());
    }

    private void showSeasons() {
        seasonContainer.getChildren().clear();

        for (Season s : seasonList){
            Button seasonButton = new Button();
            seasonButton.setText(s.getName());
            seasonButton.getStyleClass().add("desktopButton");

            seasonButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    openSeasonMenu(event);
                }else{
                    selectSeasonButton(event);
                }
            });

            seasonContainer.getChildren().add(seasonButton);
        }
    }

    private void selectSeasonButton(MouseEvent event) {
        String seasonName = ((Button)event.getSource()).getText();

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
        selectedSeason = s;

        fillSeasonInfo();

        if (!selectedSeason.getDiscs().isEmpty()){
            rightBox.setVisible(true);
            showDiscs(selectedSeason);
        }else{
            rightBox.setVisible(false);
        }
    }

    private void openSeasonMenu(MouseEvent event) {

    }

    private void showDiscs(Season s) {
        discList.clear();
        discContainer.getChildren().clear();
        for (int i : s.getDiscs()){
            Disc d = Main.findDisc(i);
            if (d != null){
                discList.add(d);
            }
        }

        if (!discList.isEmpty()){
            for (Disc d : discList){
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("discCard.fxml"));
                    VBox cardBox = fxmlLoader.load();
                    DiscController discController = fxmlLoader.getController();
                    discController.setDesktopParent(this);
                    discController.setData(d);

                    cardBox.setPadding(new Insets(10, 10, 10, 10));

                    //cardBox.setPadding(new Insets(10, 10, 10, 10));
                    //VBox.setMargin(cardBox, new Insets(10, 10, 10, 10));

                    discContainer.getChildren().add(cardBox);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @FXML
    void selectSeriesButton(MouseEvent event){
        String seriesName = ((Button)event.getSource()).getText();

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

    @FXML
    void addCategory(MouseEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCategory-view.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Category");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        categorySelector.getItems().clear();
        categorySelector.getItems().addAll(Main.getCategories());
    }

    @FXML
    void openMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        mainMenu.setVisible(true);
    }

    @FXML
    void switchToFullScreen(MouseEvent event){
        fullScreen();
    }

    private void fullScreen(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("VideoLauncher");
            stage.setScene(new Scene(root));
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

    public void openDiscMenu(Disc disc) {

    }

    public void selectDisc(Disc disc){
        if (selectedDisc != null && selectedDisc.getId() == disc.getId()){
            playEpisode(disc);
        }else{
            selectedDisc = disc;
        }
    }

    private void playEpisode(Disc disc) {
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
            final InputStream is = process.getInputStream();

            // in case you need to send information back to the process
            // get its output stream. Don't forget to close when through with it
            final OutputStream os = process.getOutputStream();

            // thread to handle or gobble text sent from input stream
            new Thread(() -> {
                // try with resources
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        // TODO: handle line
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();

            // thread to get exit value from process without blocking
            Thread waitForThread = new Thread(() -> {
                try {
                    int exitValue = process.waitFor();
                    // TODO: handle exit value here
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            waitForThread.start();

            // if you want to join after a certain time:
            long timeOut = 4000;
            waitForThread.join(timeOut);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addCollection(MouseEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCollection-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddCollectionController addColController = fxmlLoader.getController();
            addColController.setParentController(this);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Add Series");
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root1);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }

    public void addSeries(Series s){
        seriesList.add(s);
        selectCategory(currentCategory);
    }

    @FXML
    void editSeries(){
        /*if (seriesToEdit != null && seriesToEditController != null){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCollection-view.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                AddCollectionController addColController = fxmlLoader.getController();
                addColController.setParentController(this);
                addColController.setParentCardController(seriesToEditController);
                addColController.setSeries(seriesToEdit);
                Stage stage = new Stage();
                stage.setTitle("Edit Series");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hideMenu();
        }*/
    }

    @FXML
    void addSeason(){
        /*try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addSeason-view.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            AddSeasonController addSeasonController = fxmlLoader.getController();
            addSeasonController.setCollection(seriesToEdit);
            Stage stage = new Stage();
            stage.setTitle("Add Season");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideContextMenu();*/
    }

    @FXML
    void addDisc(MouseEvent event){
        /*try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addDisc-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddDiscController addDiscController = fxmlLoader.getController();
            addDiscController.setParentController(this);
            addDiscController.InitValues();
            Stage stage = new Stage();
            stage.setTitle("Add Discs/Episodes");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        contextMenu.setVisible(false);*/
    }

    @FXML
    void editSeason(MouseEvent event){
        /*try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addSeason-view.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            AddSeasonController addSeasonController = fxmlLoader.getController();
            addSeasonController.setSeason(seasons.get(currentSeason));
            Stage stage = new Stage();
            stage.setTitle("Add Season");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        contextMenu.setVisible(false);*/
    }

    @FXML
    void removeSeason(MouseEvent event){
        /*int currentID = seasons.get(currentSeason).getId();

        seasons.remove(seasons.get(currentSeason));
        Main.removeSeason(currentID);

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.setTitle("ExecutableLauncher");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth());
            stage.setHeight(Screen.getPrimary().getBounds().getHeight());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    @FXML
    void editDisc(MouseEvent event){
        /*try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addDisc-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddDiscController addDiscController = fxmlLoader.getController();
            addDiscController.setParentController(this);
            addDiscController.setDisc(discToEdit);
            Stage stage = new Stage();
            stage.setTitle("Add Discs/Episodes");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    @FXML
    void removeDisc(MouseEvent event) {
        /*if (discToEdit != null){
            discs.remove(discToEdit);
            Main.removeDisc(discToEdit);
            seasons.get(currentSeason).removeDisc(discToEdit);
            discToEdit = null;
            updateInfo(seasons.get(currentSeason));
        }*/
    }

    @FXML
    void openSettings(MouseEvent event){

    }

    private void hideMenu(){
        mainMenu.setVisible(false);
        menuParentPane.setVisible(false);
    }
}
