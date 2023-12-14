package com.example.executablelauncher;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeasonController {
    @FXML
    private ImageView menuShadow;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView backgroundShadow;

    @FXML
    private MediaView backgroundVideo;

    @FXML
    private HBox cardContainer;

    @FXML
    private HBox episodeBox;

    @FXML
    private VBox episodeSection;

    @FXML
    private ScrollPane episodeScroll;

    @FXML
    private VBox infoBox;

    @FXML
    private Button lastSeasonButton;

    @FXML
    private ImageView logoImage;

    @FXML
    private Label nameField;

    @FXML
    private Button nextSeasonButton;

    @FXML
    private StackPane mainBox;

    @FXML
    private ImageView showEpisodesButton;

    @FXML
    private Label yearField;

    @FXML
    private Button playButton;

    @FXML
    private Button optionsButton;

    private Controller controllerParent;

    private List<Season> seasons = new ArrayList<>();
    private List<Disc> discs = new ArrayList<>();
    private int currentSeason = 0;
    public int currentEpisoceID = -1;
    private Disc selectedDisc = null;
    private boolean showEpisodes = false;
    private boolean optionsSelected = false;
    private boolean playSelected = false;

    private MediaPlayer mp = null;

    Timeline alertTimer = null;

    int pos = 0;
    final int minPos = 0;
    final int maxPos = 100;
    private boolean isVideo = false;

    public void setParent(Controller c){
        controllerParent = c;
    }

    private void updateInfo(Season season){
        if (mp != null){
            mp.stop();
        }

        alertTimer = new Timeline(new KeyFrame(Duration.seconds(3), event ->{
            playVideo();
        }));

        nameField.setText(season.getName());
        yearField.setText(season.getYear());

        //Set Background Image
        Image background = new Image("file:" + season.getBackgroundSrc());
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
            Image logo = new Image("file:" + season.getLogoSrc(), 500, 500, true, true);
            logoImage.setImage(logo);
        }

        if (!season.getVideoSrc().equals("NO_VIDEO")){
            File file = new File(season.getVideoSrc());
            Media media = new Media(file.toURI().toString());
            mp = new MediaPlayer(media);
            backgroundVideo.setMediaPlayer(mp);
            backgroundVideo.setVisible(false);

            mp.setOnEndOfMedia(() -> {
                mp.seek(Duration.ZERO);
                mp.play();
            });

            isVideo = true;

            alertTimer.play();
        }else if (!season.getMusicSrc().equals("NO_MUSIC")){
            File file = new File(season.getMusicSrc());
            Media media = new Media(file.toURI().toString());
            mp = new MediaPlayer(media);

            mp.setOnEndOfMedia(() -> {
                mp.seek(Duration.ZERO);
                mp.play();
            });

            isVideo = false;
            alertTimer.play();
        }

        cardContainer.getChildren().clear();
        discs.clear();
        List<Integer> discs = season.getDiscs();
        for (int i : discs){
            Disc d = App.findDisc(i);
            this.discs.add(d);
            addEpisodeCard(d);
        }

        //Fade In Transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        showEpisodesButton.setVisible(season.getDiscs().size() > 1);

        episodeSection.setTranslateY(episodeBox.getPrefHeight());
        infoBox.setTranslateY(episodeBox.getPrefHeight() / 2);

        selectPlayButton();
    }

    private void updateButtons(){
        lastSeasonButton.setVisible(currentSeason != 0);
        nextSeasonButton.setVisible(currentSeason != seasons.size() - 1);
    }

    public void setSeasons(List<Integer> seasonList){
        if (seasons != null){
            for (int i : seasonList){
                seasons.add(App.findSeason(i));
            }
        }

        menuShadow.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        menuShadow.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        menuShadow.setVisible(false);

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode() || KeyCode.BACK_SPACE == event.getCode()){
                goBack(event);
            }else if (KeyCode.LEFT == event.getCode()){
                if (selectedDisc != null)
                    selectPrevDisc();
                else{
                    if (optionsSelected) {
                        selectPlayButton();
                    }
                    else if (playSelected && lastSeasonButton.isVisible()){
                        controllerParent.playCategoriesSound();
                        lastSeason();
                    }
                }
            }else if (KeyCode.RIGHT == event.getCode()){
                if (selectedDisc != null)
                    selectNextDisc();
                else{
                    if (playSelected)
                        selectOptionsButton();
                    else if (optionsSelected && nextSeasonButton.isVisible()){
                        controllerParent.playCategoriesSound();
                        nextSeason();
                    }
                }
            }else if (KeyCode.DOWN == event.getCode() && !showEpisodes && discs.size() > 1){
                controllerParent.playInteractionSound();
                showEpisodes();
                selectFirstDisc();
            }else if (KeyCode.UP == event.getCode() && showEpisodes && discs.size() > 1){
                controllerParent.playInteractionSound();
                showEpisodes();
                deselectDisc();
                selectPlayButton();
            }
        });

        //Set buttons for next and last season
        updateButtons();

        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        //Fit all elements to screen size
        mainBox.setPrefWidth(screenWidth);
        mainBox.setPrefHeight(screenHeight);
        backgroundImage.setFitWidth(screenWidth);
        backgroundImage.setFitHeight(screenHeight);
        backgroundVideo.setFitHeight(screenHeight);
        backgroundVideo.setFitWidth(screenWidth);
        backgroundShadow.setFitWidth(screenWidth);
        backgroundShadow.setFitHeight(screenHeight);
        episodeSection.setPrefHeight(screenHeight * 0.2);
        episodeBox.setPrefHeight(episodeSection.getPrefHeight() - 50);
        episodeScroll.prefHeightProperty().bind(episodeBox.heightProperty());
        episodeScroll.setPrefWidth(screenWidth);
        cardContainer.prefHeightProperty().bind(episodeScroll.heightProperty());
        cardContainer.prefWidthProperty().bind(episodeScroll.widthProperty());

        episodeScroll.setOnScroll(event -> {

            if (event.getDeltaY() > 0)
                episodeScroll.setHvalue(pos == minPos ? minPos : pos--);
            else
                episodeScroll.setHvalue(pos == maxPos ? maxPos : pos++);
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
                if (isVideo){
                    backgroundVideo.setVisible(true);

                    //Fade In Transition
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundVideo);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                }
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
                        System.out.println(line);
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
    void goBack(KeyEvent event){
        if (mp != null)
            mp.stop();
        alertTimer.stop();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) mainBox.getScene().getWindow();
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
    void lastSeason(){
        updateInfo(seasons.get(--currentSeason));
        updateButtons();
    }

    @FXML
    void nextSeason(){
        updateInfo(seasons.get(++currentSeason));
        updateButtons();
    }

    @FXML
    void openMenu(ActionEvent event){
        menuShadow.setVisible(true);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("seasonMenu-view.fxml"));
            Parent root = fxmlLoader.load();
            SeasonMenuController controller = fxmlLoader.getController();
            controller.setParentController(this);
            controller.setLabel(seasons.get(currentSeason).getName());
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Cannot load menu");
        }
    }

    @FXML
    void removeSeason(){
        int currentID = seasons.get(currentSeason).getId();

        seasons.remove(seasons.get(currentSeason));
        App.removeSeason(currentID);

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) mainBox.getScene().getWindow();
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
    void showEpisodes(){
        if (showEpisodes) {
            showEpisodesButton.setImage(new Image("file:src/main/resources/img/icons/arrowDown.png"));
        }else {
            showEpisodesButton.setImage(new Image("file:src/main/resources/img/icons/arrowUp.png"));
        }

        TranslateTransition slide = new TranslateTransition();
        TranslateTransition slideInfo = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.25));
        slideInfo.setDuration(Duration.seconds(0.25));
        slide.setNode(episodeSection);
        slideInfo.setNode(infoBox);

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
            episodeSection.setTranslateY(0);
            infoBox.setTranslateY(episodeBox.getPrefHeight() / 2);
        }else {
            episodeSection.setTranslateY(episodeBox.getPrefHeight());
            infoBox.setTranslateY(0);
        }
        showEpisodes = !showEpisodes;
    }

    @FXML
    void play(ActionEvent event){
        if (!seasons.get(currentSeason).getDiscs().isEmpty()){
            if (currentEpisoceID == -1){
                currentEpisoceID = Objects.requireNonNull(App.findDisc(seasons.get(currentSeason).getDiscs().get(0))).getId();
            }
            Disc d = App.findDisc(currentEpisoceID);
            if (d != null){
                playEpisode(d);
            }
        }
    }

    @FXML
    void editSeason(){
        //Edit "sorting order"
    }

    public void hideMenuShadow(){
        menuShadow.setVisible(false);
    }

    public void selectFirstDisc(){
        deselectButtons();
        selectedDisc = discs.get(0);
        Node node = cardContainer.getChildren().get(0);
        node.getStyleClass().clear();
        node.getStyleClass().add("selectedDisc");
    }

    public void selectPrevDisc(){
        int index = getDiscIndex(selectedDisc);
        if (index > 0){
            Node node = cardContainer.getChildren().get(index);
            node.getStyleClass().clear();
            node.getStyleClass().add("buttonGradient");

            selectedDisc = discs.get(index - 1);

            node = cardContainer.getChildren().get(index - 1);
            node.getStyleClass().clear();
            node.getStyleClass().add("selectedDisc");
        }
    }

    public void selectNextDisc(){
        int index = getDiscIndex(selectedDisc);
        if (index != -1 && index < cardContainer.getChildren().size() - 1){
            Node node = cardContainer.getChildren().get(index);
            node.getStyleClass().clear();
            node.getStyleClass().add("buttonGradient");

            selectedDisc = discs.get(index + 1);

            node = cardContainer.getChildren().get(index + 1);
            node.getStyleClass().clear();
            node.getStyleClass().add("selectedDisc");
        }
    }

    public void deselectDisc(){
        int index = getDiscIndex(selectedDisc);
        if (index != -1){
            Node node = cardContainer.getChildren().get(index);
            node.getStyleClass().clear();
            node.getStyleClass().add("buttonGradient");
            selectedDisc = null;
        }
    }

    private int getDiscIndex(Disc d){
        for (int i = 0; i < discs.size(); i++){
            if (discs.get(i).getId() == d.getId())
                return i;
        }

        return -1;
    }

    private void selectPlayButton(){
        if (optionsSelected){
            optionsButton.getStyleClass().clear();
            optionsButton.getStyleClass().add("optionsButton");
            setOptionsButtonImage(false);
            optionsSelected = false;
        }
        playButton.getStyleClass().clear();
        playButton.getStyleClass().add("seeButtonSelected");
        playSelected = true;
    }

    private void selectOptionsButton(){
        if (playSelected){
            playButton.getStyleClass().clear();
            playButton.getStyleClass().add("seeButton");
            playSelected = false;
        }
        optionsButton.getStyleClass().clear();
        optionsButton.getStyleClass().add("optionsButtonSelected");
        setOptionsButtonImage(true);
        optionsSelected = true;
    }

    private void deselectButtons(){
        playButton.getStyleClass().clear();
        playButton.getStyleClass().add("seeButton");
        playSelected = false;

        optionsButton.getStyleClass().clear();
        optionsButton.getStyleClass().add("optionsButton");
        setOptionsButtonImage(false);
        optionsSelected = false;
    }

    private void setOptionsButtonImage(boolean selected){
        String src;
        if (selected){
            src = "src/main/resources/img/icons/optionsSelected.png";
        }else{
            src = "src/main/resources/img/icons/options.png";
        }

        Image image = new Image("file:" + src, 20, 20, true, true);

        ImageView imageView = new ImageView(image);
        optionsButton.setGraphic(imageView);
    }
}
