package com.example.executablelauncher;

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
    private VBox seasonContainer;

    @FXML
    private HBox seasonCoverLogoBox;

    @FXML
    private StackPane seasonInfoPane;

    @FXML
    private ImageView seasonLogo;

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

    private List<Series> seriesList = new ArrayList<>();
    private List<Season> seasonList = new ArrayList<>();
    private List<Disc> discList = new ArrayList<>();

    private Series selectedSeries = null;
    private Season selectedSeason = null;
    private Disc selectedDisc = null;

    public void initValues(){
        seriesList = Main.getCollection();

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

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.F11 == event.getCode()) {
                switchToFullScreen(event);
            }
        });

        //Elements size
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        seriesScrollPane.setPrefHeight(screenHeight);
        seriesContainer.setPrefHeight(screenHeight);
        seasonScroll.setPrefHeight(screenHeight);
        seasonContainer.setPrefHeight(screenHeight);
        discContainer.setPrefHeight(screenHeight);

        seasonInfoPane.prefWidthProperty().bind(centralBox.widthProperty());

        seasonBackground.fitWidthProperty().bind(seasonInfoPane.widthProperty());
        seasonBackground.fitHeightProperty().bind(seasonInfoPane.heightProperty());
        seasonBackground.setPreserveRatio(false);

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

    private void fillSeasonInfo() {
        seasonBackground.setImage(new Image(selectedSeason.getBackgroundSrc()));
        if (selectedSeason.getLogoSrc().equals("NO_LOGO")){
            seasonCoverLogoBox.getChildren().remove(1);
            Label seasonLogoText = new Label(selectedSeries.getName());
            seasonLogoText.setFont(new Font("Arial", 32));
            seasonLogoText.setStyle("-fx-font-weight: bold");
            seasonLogoText.setTextFill(Color.color(1, 1, 1));
            seasonLogoText.setEffect(new DropShadow());
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
                    openSasonMenu(event);
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

    private void openSasonMenu(MouseEvent event) {

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
                    Pane cardBox = fxmlLoader.load();
                    DiscController discController = fxmlLoader.getController();
                    discController.setDesktopParent(this);
                    discController.setData(d);

                    HBox.setMargin(cardBox, new Insets(0, 0, 0, 50));

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

    }

    @FXML
    void editSeason(MouseEvent event) {

    }

    @FXML
    void openMenu(MouseEvent event) {

    }

    @FXML
    void switchToFullScreen(KeyEvent event){
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

            Stage thisStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openDiscMenu(Disc disc) {

    }

    public void selectDisc(Disc disc){
        if (selectedDisc.getId() == disc.getId()){
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
}
