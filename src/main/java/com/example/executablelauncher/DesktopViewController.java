package com.example.executablelauncher;

import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class DesktopViewController {
    @FXML
    private Button addCollectionButton;

    @FXML
    private Button addDiscButton;

    @FXML
    private Button addSeasonButton;

    @FXML
    private ImageView backgroundShadow;

    @FXML
    private ChoiceBox<String> categorySelector;

    @FXML
    private ColumnConstraints centralBoxH;

    @FXML
    private RowConstraints centralBoxV;

    @FXML
    private VBox centralVBox;

    @FXML
    private VBox discContainer;

    @FXML
    private VBox discMenu;

    @FXML
    private Button editColButton;

    @FXML
    private Button editDiscButton;

    @FXML
    private Button editSeasonButton;

    @FXML
    private ScrollPane episodeScroll;

    @FXML
    private Button exitButton;

    @FXML
    private ImageView globalBackground;

    @FXML
    private ImageView globalBackgroundShadow;

    @FXML
    private VBox leftBox;

    @FXML
    private StackPane mainBox;

    @FXML
    private VBox mainMenu;

    @FXML
    private Pane menuParentPane;

    @FXML
    private Button removeColButton;

    @FXML
    private Button removeDiscButton;

    @FXML
    private Button removeSeasonButton;

    @FXML
    private VBox rightBox;

    @FXML
    private VBox seasonContainer;

    @FXML
    private HBox seasonCoverLogoBox;

    @FXML
    private VBox seasonInfoInside;

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
    private HBox topRightBar;

    @FXML
    private HBox topLeftBar;

    @FXML
    private Button closeButton;

    @FXML
    private ImageView maximizeRestoreImage;

    private final ImageViewPane seasonBackground = new ImageViewPane();

    private List<Series> seriesList = new ArrayList<>();
    private List<Season> seasonList = new ArrayList<>();
    private List<Disc> discList = new ArrayList<>();
    private List<Button> seriesButtons = new ArrayList<>();
    private List<Button> seasonsButtons = new ArrayList<>();
    private List<Button> discButtons = new ArrayList<>();

    private Series selectedSeries = null;
    private Season selectedSeason = null;
    private List<Disc> selectedDiscs = new ArrayList<>();
    private Disc discToEdit = null;
    private String currentCategory = "";

    private double xOffset = 0;
    private double yOffset = 0;

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

    public void selectCategory(String category){
        seriesContainer.getChildren().clear();
        currentCategory = category;
        seriesList = App.getSeriesFromCategory(category);
        showSeries();
    }

    public void updateCategories(){
        List<String> categories = App.getCategories();
        categorySelector.getItems().clear();
        categorySelector.getItems().addAll(categories);
        if (categories.size() > 1){
            categorySelector.setValue(categories.get(1));
            selectCategory(categories.get(1));
        }
    }

    public void initValues(){
        updateLanguage();

        categorySelector.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> selectCategory(newValue) );

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

        setDragWindow(topRightBar);
        setDragWindow(topLeftBar);

        menuParentPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            hideMenu();
        });

        mainBox.getScene().getWindow().setOnCloseRequest(e -> closeWindow());

        //Remove horizontal and vertical scroll
        scrollModification(seasonScroll);
        scrollModification(episodeScroll);
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
        discContainer.setPrefHeight(screenHeight);
        seasonScroll.setPrefHeight(screenHeight);
        centralVBox.setPrefHeight(screenHeight);
        seasonInfoPane.setPrefHeight(screenHeight * 0.6);
        seasonInfoInside.setPrefHeight(screenHeight * 0.6);
        seasonInfoPane.getChildren().add(0, seasonBackground);

        globalBackground.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackground.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackground.setPreserveRatio(false);
        //globalBackground.setVisible(false);

        globalBackgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        globalBackgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        globalBackgroundShadow.setPreserveRatio(false);

        backgroundShadow.fitWidthProperty().bind(mainBox.widthProperty());
        backgroundShadow.fitHeightProperty().bind(mainBox.heightProperty());
        backgroundShadow.setPreserveRatio(false);
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

    private void setDragWindow(HBox topLeftBar) {
        topLeftBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        topLeftBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) mainBox.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }

    static void scrollModification(ScrollPane scroll) {
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final double SPEED = 0.0025;
        scroll.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scroll.setVvalue(scroll.getVvalue() - deltaY);
        });
    }

    @FXML
    void close(MouseEvent event) {
        closeWindow();
    }

    @FXML
    void minimizeWindow(MouseEvent event) {
        ((Stage)((Button) event.getSource()).getScene().getWindow()).setIconified(true);
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

    private void closeWindow(){
        try{
            App.SaveData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    public void blankSelection(){
        centralVBox.setVisible(false);
    }

    public void selectSeries(Series s) {
        if (selectedSeries != s){
            selectedSeries = s;
            selectSeriesButton(seriesButtons.get(getSeriesIndex(s)));
            centralVBox.setVisible(true);


            if (!s.getSeasons().isEmpty()){
                seasonList.clear();
                for (int i : s.getSeasons()){
                    Season season = App.findSeason(i);
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
                centralVBox.setVisible(false);
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
        globalBackground.setImage(new Image("file:" +
                "src/main/resources/img/backgrounds/" + selectedSeries.getName() + "_" + selectedSeason.getName() + "_desktopBlur.png"));
        ImageView img = new ImageView(new Image("file:" + selectedSeason.getDesktopBackgroundEffect()));
        img.setPreserveRatio(true);
        seasonBackground.setImageView(img);
        fadeInTransition(globalBackground);
        fadeInTransition(seasonBackground.getImageView());
        if (selectedSeason.getLogoSrc().equals("")){
            seasonCoverLogoBox.getChildren().remove(1);
            Label seasonLogoText = new Label(selectedSeries.getName());
            seasonLogoText.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 42));
            seasonLogoText.setTextFill(Color.color(1, 1, 1));
            seasonLogoText.setEffect(new DropShadow());
            seasonLogoText.setPadding(new Insets(0, 0, 0, 15));
            seasonCoverLogoBox.getChildren().add(seasonLogoText);
        }else{
            seasonCoverLogoBox.getChildren().remove(1);
            seasonLogo = new ImageView();
            File file = new File(selectedSeason.getLogoSrc());
            try{
                seasonLogo.setImage(new Image(file.toURI().toURL().toExternalForm()));
                seasonLogo.setFitWidth(353);
                seasonLogo.setPreserveRatio(true);
                seasonCoverLogoBox.getChildren().add(seasonLogo);
            } catch (MalformedURLException e) {
                System.err.println("DesktopViewController: Logo not loaded");
            }
        }

        try{
            File file = new File(selectedSeries.getCoverSrc());
            seriesCover.setImage(new Image(file.toURI().toURL().toExternalForm()));
        } catch (MalformedURLException e) {
            System.err.println("Series cover not found");
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
            seasonButton.getStyleClass().add("desktopTextButton");
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

    private void selectSeasonButton(Button btn) {
        //Clear Selected Button
        for (Button b : seasonsButtons){
            b.getStyleClass().clear();
            b.getStyleClass().add("desktopTextButton");
        }
        //Select current button
        btn.getStyleClass().clear();
        btn.getStyleClass().add("desktopButtonActive");
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

    private void showDiscs(Season s) {
        discList.clear();
        discContainer.getChildren().clear();
        discButtons.clear();
        for (int i : s.getDiscs()){
            Disc d = App.findDisc(i);
            if (d != null){
                discList.add(d);
            }
        }

        if (!discList.isEmpty()){
            discList.sort(new Utils.DiscComparator());
            for (Disc d : discList){
                Button btn = new Button();
                btn.setText(App.textBundle.getString("episode") + " " + d.getEpisodeNumber() + "\n" + d.getName());
                btn.getStyleClass().clear();
                btn.getStyleClass().add("discButton");
                btn.setMaxWidth(Integer.MAX_VALUE);
                btn.setAlignment(Pos.BASELINE_LEFT);
                btn.setWrapText(true);

                HBox.setMargin(btn, new Insets(2, 2, 2, 2));
                btn.setPadding(new Insets(2, 2, 2, 5));

                discButtons.add(btn);

                btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                    Disc disc = getDiscFromButton(btn);
                    if (event.getButton() == MouseButton.SECONDARY) {
                        if (disc != null)
                            openDiscMenu(event, disc);
                    }else{
                        if (event.isControlDown())
                            controlSelectDisc(disc, btn);
                        else if (event.isShiftDown())
                            shiftSelectDisc(disc, btn);
                        else
                            selectDisc(disc, btn);
                    }
                });

                discContainer.getChildren().add(btn);
            }
        }
    }

    private Disc getDiscFromButton(Button btn){
        for (int i = 0; i < discButtons.size(); i++){
            if (discButtons.get(i).getText().equals(btn.getText()))
                return discList.get(i);
        }
        return null;
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
    void removeCategory(MouseEvent event) {
        App.removeCategory(currentCategory);
        updateCategories();
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

    @FXML
    void openMenu(MouseEvent event) {
        menuParentPane.setVisible(true);
        mainMenu.setLayoutX(event.getSceneX());
        mainMenu.setLayoutY(event.getSceneY());
        mainMenu.setVisible(true);
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
        discToEdit = disc;
        menuParentPane.setVisible(true);
        discMenu.setLayoutX(event.getSceneX());
        if (discList.indexOf(disc) > 2)
            discMenu.setLayoutY(event.getSceneY() - discMenu.getHeight());
        else
            discMenu.setLayoutY(event.getSceneY());
        discMenu.setVisible(true);
    }

    public void controlSelectDisc(Disc d, Button btn){
        int selectedIndex = selectedDiscs.indexOf(d);
        if (selectedIndex == -1){
            selectedDiscs.add(d);
            btn.getStyleClass().clear();
            btn.getStyleClass().add("discSelected");
        }else{
            selectedDiscs.remove(selectedIndex);
            btn.getStyleClass().clear();
            btn.getStyleClass().add("discButton");
        }
    }

    public void shiftSelectDisc(Disc d, Button btn){
        if (discToEdit != null){
            int index = getDiscIndex(d);
            int selectedIndex = getDiscIndex(discToEdit);

            selectedDiscs.clear();
            if (index > selectedIndex){
                for (int i = selectedIndex; i <= index; i++){
                    controlSelectDisc(discList.get(i), discButtons.get(i));
                }
            }else{
                for (int i = index; i <= selectedIndex; i++){
                    controlSelectDisc(discList.get(i), discButtons.get(i));
                }
            }
        }else{
            selectDisc(d, btn);
        }
    }

    private int getDiscIndex(Disc d){
        for (int i = 0; i < discList.size(); i++){
            if (discList.get(i).getId() == d.getId())
                return i;
        }
        return -1;
    }

    private int getSeriesIndex(Series s){
        for (int i = 0; i < seriesList.size(); i++){
            if (seriesList.get(i).getId() == s.getId())
                return i;
        }
        return 0;
    }

    @FXML
    void openSettings(MouseEvent event){
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

    private void hideMenu(){
        discMenu.setVisible(false);
        seasonMenu.setVisible(false);
        seriesMenu.setVisible(false);
        mainMenu.setVisible(false);
        menuParentPane.setVisible(false);
    }

    public void selectDisc(Disc disc, Button btn){
        clearDiscSelection();
        if (selectedDiscs.size() == 1 && discToEdit != null && discToEdit.getId() == disc.getId()){
            playEpisode(disc);
        }else{
            assert selectedDiscs != null;
            selectedDiscs.clear();
            discToEdit = disc;
            selectedDiscs.add(disc);
            clearDiscSelection();
            btn.getStyleClass().clear();
            btn.getStyleClass().add("discSelected");
        }
    }

    private void clearDiscSelection(){
        for (Node n : discContainer.getChildren()){
            n.getStyleClass().clear();
            n.getStyleClass().add("discButton");
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

    public void addSeries(Series s){
        seriesList.add(s);
        selectCategory(currentCategory);
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
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideMenu();
    }

    public Season getCurrentSeason(){
        return selectedSeason;
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
    void removeSeason(MouseEvent event){
        seasonList.remove(selectedSeason);
        App.removeSeason(selectedSeason.getId());

        selectedSeason = null;
        selectSeries(selectedSeries);
        hideMenu();
    }

    @FXML
    void editDisc(MouseEvent event){
        showBackgroundShadow();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addDisc-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddDiscController addDiscController = fxmlLoader.getController();
            addDiscController.setParentController(this);
            addDiscController.setDisc(discToEdit);
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

    @FXML
    void removeDisc(MouseEvent event) {
        if (selectedDiscs.size() > 1){
            for (Disc d : selectedDiscs){
                discList.remove(d);
                App.removeDisc(d);
                selectedSeason.removeDisc(d);
            }
        }else if (discToEdit != null){
            discList.remove(discToEdit);
            App.removeDisc(discToEdit);
            selectedSeason.removeDisc(discToEdit);
        }

        discToEdit = null;
        selectSeason(selectedSeason);
        hideMenu();
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

    public void showBackgroundShadow(){
        backgroundShadow.setVisible(true);
    }

    public void hideBackgroundShadow(){
        backgroundShadow.setVisible(false);
    }

    public void refreshSeason(Season s){
        selectedSeason = null;
        selectSeason(s);
    }
}
