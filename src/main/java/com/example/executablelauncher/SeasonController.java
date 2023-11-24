package com.example.executablelauncher;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeasonController {
    @FXML
    private HBox mainHBox;

    @FXML
    private StackPane secondBox;

    @FXML
    private VBox bottomBox;

    @FXML
    private HBox episodeBox;

    @FXML
    private ScrollPane episodeScroll;

    @FXML
    private HBox cardContainer;

    @FXML
    private Button lastSeasonButton;

    @FXML
    private Button nextSeasonButton;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView backgroundShadow;

    @FXML
    private MediaView backgroundVideo;

    @FXML
    private ImageView showEpisodesButton;

    @FXML
    private FlowPane contextMenu;

    @FXML
    private ImageView logoImage;

    @FXML
    private Label nameField;

    @FXML
    private VBox infoBox;

    @FXML
    private Label contextMenuLabel;

    @FXML
    private BorderPane infoBorderPane;

    @FXML
    private Label yearField;

    @FXML
    private FlowPane discContextMenu;

    @FXML
    private Label discContextMLabel;

    @FXML
    private Button removeEpisodeButton;

    private Controller controllerParent;

    private List<Season> seasons = new ArrayList<>();
    private List<Disc> discs = new ArrayList<>();
    private Disc discToEdit = null;
    private int currentSeason = 0;
    public int currentEpisoceID = -1;
    private boolean showEpisodes = false;

    private MediaPlayer mp;

    Timeline alertTimer = new Timeline(new KeyFrame(Duration.seconds(3), event ->{
        playVideo();
    }));

    int pos = 0;
    final int minPos = 0;
    final int maxPos = 100;

    public void setParent(Controller c){
        controllerParent = c;
    }

    private void updateInfo(Season season){
        if (mp != null){
            mp.stop();
        }

        nameField.setText(season.getName());
        yearField.setText(season.getYear());
        contextMenuLabel.setText(season.getName());

        //Set Background Image
        Image background = new Image(season.getBackgroundSrc());
        backgroundImage.setImage(background);
        backgroundImage.setPreserveRatio(false);
        backgroundImage.setSmooth(true);
        backgroundImage.setCache(true);

        if (season.getLogoSrc().equals("NO_LOGO")){
            infoBox.getChildren().remove(0);
            Label seriesTitle = new Label(season.getCollectionName());
            seriesTitle.setFont(new Font("Arial", 58));
            seriesTitle.setStyle("-fx-font-weight: bold");
            seriesTitle.setTextFill(Color.color(1, 1, 1));
            seriesTitle.setEffect(new DropShadow());
            infoBox.getChildren().add(0, seriesTitle);
        }else{
            Image logo = new Image(season.getLogoSrc(), 500, 500, true, true);
            logoImage.setImage(logo);
        }

        if (!season.getVideoSrc().equals("NO_VIDEO")){
            controllerParent.stopBackground();
            File file = new File(season.getVideoSrc());
            Media media = new Media(file.toURI().toString());
            mp = new MediaPlayer(media);
            backgroundVideo.setMediaPlayer(mp);
            backgroundVideo.setVisible(false);

            mp.setOnEndOfMedia(() -> {
                mp.seek(Duration.ZERO);
                mp.play();
            });

            alertTimer.play();
        }

        cardContainer.getChildren().clear();
        discs.clear();
        List<Integer> discs = season.getDiscs();
        for (int i : discs){
            Disc d = Main.findDisc(i);
            this.discs.add(d);
            addEpisodeCard(d);
        }

        //Fade In Transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        if (season.getDiscs().size() <= 1){
            showEpisodesButton.setVisible(false);
            if (!season.getDiscs().isEmpty()){
                removeEpisodeButton.setDisable(false);
                discToEdit = Main.findDisc(season.getDiscs().get(0));
            }
        }else{
            removeEpisodeButton.setDisable(true);
        }

        contextMenu.setVisible(false);
        discContextMenu.setVisible(false);
        bottomBox.setTranslateY(episodeBox.getPrefHeight());
        infoBorderPane.setTranslateY(episodeBox.getPrefHeight() / 2);
    }

    private void updateButtons(){
        lastSeasonButton.setVisible(currentSeason != 0);
        nextSeasonButton.setVisible(currentSeason != seasons.size() - 1);
    }

    public void setSeasons(List<Integer> seasonList){
        if (seasons != null){
            for (int i : seasonList){
                seasons.add(Main.findSeason(i));
            }
        }

        //Set buttons for next and last season
        updateButtons();

        //Fit all elements to screen size
        mainHBox.setPrefWidth(Screen.getPrimary().getBounds().getWidth());
        mainHBox.setPrefHeight(Screen.getPrimary().getBounds().getHeight());
        secondBox.prefWidthProperty().bind(mainHBox.widthProperty());
        secondBox.prefHeightProperty().bind(mainHBox.heightProperty());
        backgroundImage.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        backgroundImage.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        backgroundVideo.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        backgroundVideo.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        backgroundShadow.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        backgroundShadow.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        //bottomBox.setPrefHeight(Screen.getPrimary().getBounds().getHeight() * 0.4);
        bottomBox.setPrefWidth(Screen.getPrimary().getBounds().getWidth());
        episodeScroll.prefHeightProperty().bind(bottomBox.heightProperty());
        episodeScroll.prefWidthProperty().bind(bottomBox.widthProperty());
        cardContainer.prefHeightProperty().bind(episodeScroll.heightProperty());
        cardContainer.prefWidthProperty().bind(episodeScroll.widthProperty());


        episodeScroll.setOnScroll(new EventHandler<>() {
            @Override
            public void handle(ScrollEvent event) {

                if (event.getDeltaY() > 0)
                    episodeScroll.setHvalue(pos == minPos ? minPos : pos--);
                else
                    episodeScroll.setHvalue(pos == maxPos ? maxPos : pos++);
            }
        });

        //Remove horizontal and vertical scroll
        episodeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        episodeScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        assert seasons != null;
        updateInfo(seasons.get(currentSeason));
    }

    private void playVideo(){
        Platform.runLater(() ->{
            if (mp != null) {
                backgroundVideo.setVisible(true);

                //Fade In Transition
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundVideo);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                //Play video
                mp.play();
            }
        });
    }

    public void addEpisodeCard(Disc d){
        if (d != null){
            discs.add(d);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("discCard.fxml"));
                Pane cardBox = fxmlLoader.load();
                DiscController discController = fxmlLoader.getController();
                discController.setParent(this);
                discController.setData(d);

                HBox.setMargin(cardBox, new Insets(0, 0, 0, 50));

                cardContainer.getChildren().add(cardBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            showEpisodesButton.setVisible(seasons.get(currentSeason).getDiscs().size() > 1);
        }
    }

    public Season getCurrentSeason(){
        return seasons.get(currentSeason);
    }

    public void playEpisode(Disc disc){
        mp.stop();

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

            List<Integer> discList = seasons.get(currentSeason).getDiscs();
            for (int i = 0; i < discList.size(); i++){
                if (discList.get(i) == disc.getId()){
                    if (i + 1 < discList.size()){
                        currentEpisoceID = discList.get(i + 1);
                        break;
                    }
                }
            }

            mp.play();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goBack(MouseEvent event){
        if (mp != null)
            mp.stop();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((ImageView)event.getSource()).getScene().getWindow();
            stage.setTitle("ExecutableLauncher");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setWidth(Screen.getPrimary().getBounds().getWidth());
            stage.setHeight(Screen.getPrimary().getBounds().getHeight());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void lastSeason(MouseEvent event){
        updateInfo(seasons.get(--currentSeason));
        updateButtons();
    }

    @FXML
    void nextSeason(MouseEvent event){
        updateInfo(seasons.get(++currentSeason));
        updateButtons();
    }

    @FXML
    void addDisc(MouseEvent event){
        try{
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

        contextMenu.setVisible(false);
    }

    @FXML
    void openMenu(MouseEvent event){
        contextMenu.setVisible(true);
    }

    @FXML
    void editSeason(MouseEvent event){
        try{
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

        contextMenu.setVisible(false);
    }

    @FXML
    void cancelButton(MouseEvent event){
        contextMenu.setVisible(false);
        discContextMenu.setVisible(false);
    }

    @FXML
    void removeSeason(MouseEvent event){
        int currentID = seasons.get(currentSeason).getId();

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
        }
    }

    @FXML
    void showEpisodes(MouseEvent event){
        if (showEpisodes) {
            showEpisodesButton.setImage(new Image("file:src/main/resources/img/icons/arrowDown.png"));
            //bottomBox.setVisible(false);
            //cardContainer.setEffect(null);
        }else {
            showEpisodesButton.setImage(new Image("file:src/main/resources/img/icons/arrowUp.png"));
            //bottomBox.setVisible(true);
            //Apply blur to background
            //cardContainer.setEffect(new GaussianBlur());
        }

        TranslateTransition slide = new TranslateTransition();
        TranslateTransition slideInfo = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.25));
        slideInfo.setDuration(Duration.seconds(0.25));
        slide.setNode(bottomBox);
        slideInfo.setNode(infoBorderPane);

        if (showEpisodes) {
            slide.setToY(episodeBox.getPrefHeight());
            slideInfo.setToY(episodeBox.getPrefHeight() / 2);
        }else {
            slide.setToY(0);
            slideInfo.setToY(0);
        }

        slideInfo.play();
        slide.play();

        if (showEpisodes) {
            bottomBox.setTranslateY(0);
            infoBorderPane.setTranslateY(episodeBox.getPrefHeight() / 2);
        }else {
            bottomBox.setTranslateY(episodeBox.getPrefHeight());
            infoBorderPane.setTranslateY(0);
        }
        showEpisodes = !showEpisodes;
    }

    @FXML
    void play(MouseEvent event){
        if (!seasons.get(currentSeason).getDiscs().isEmpty()){
            if (currentEpisoceID == -1){
                currentEpisoceID = Objects.requireNonNull(Main.findDisc(seasons.get(currentSeason).getDiscs().get(0))).getId();
            }
            Disc d = Main.findDisc(currentEpisoceID);
            if (d != null){
                playEpisode(d);
            }
        }
    }

    @FXML
    void editDisc(MouseEvent event){
        try{
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
        }

        discContextMenu.setVisible(false);
    }

    @FXML
    void removeDisc(MouseEvent event) {
        if (discToEdit != null){
            discs.remove(discToEdit);
            Main.removeDisc(discToEdit);
            seasons.get(currentSeason).removeDisc(discToEdit);
            discToEdit = null;

            if (discContextMenu.isVisible())
                showEpisodes(event);
            updateInfo(seasons.get(currentSeason));
        }

        if (seasons.get(currentSeason).getDiscs().isEmpty())
            removeEpisodeButton.setDisable(true);

        discContextMenu.setVisible(false);
    }

    public void showDiscMenu(Disc disc) {
        discContextMLabel.setText(disc.getName());
        discContextMenu.setVisible(true);
        discToEdit = disc;
    }
}
