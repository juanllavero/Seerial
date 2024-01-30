package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.utils.Configuration;
import com.example.executablelauncher.utils.Utils;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeasonController {
    //region FXML ATTRIBUTES
    @FXML
    private ImageView videoImage;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView backgroundShadow;

    @FXML
    private ImageView backgroundShadow2;

    @FXML
    private ImageView scoreProviderImg;

    @FXML
    private HBox cardContainer;

    @FXML
    private VBox detailsText;

    @FXML
    private HBox timeLeftBox;

    @FXML
    private Label timeLeftField;

    @FXML
    private BorderPane detailsBox;

    @FXML
    private ImageView detailsImage;

    @FXML
    private Pane videoPlayerPane;

    @FXML
    private HBox detailsInfo;

    @FXML
    private Label detailsOverview;

    @FXML
    private Label detailsTitle;

    @FXML
    private Label durationField;

    @FXML
    private Label episodeName;

    @FXML
    private ScrollPane episodeScroll;

    @FXML
    private Label fileDetailsText;

    @FXML
    private Label fileNameField;

    @FXML
    private Label fileNameText;

    @FXML
    private VBox infoBox;

    @FXML
    private Button lastSeasonButton;

    @FXML
    private ImageView logo;

    @FXML
    private StackPane mainBox;

    @FXML
    private BorderPane mainPane;

    @FXML
    private ImageView menuShadow;

    @FXML
    private Button nextSeasonButton;

    @FXML
    private Button watchedButton;

    @FXML
    private Button optionsButton;

    @FXML
    private Label overviewField;

    @FXML
    private Button playButton;

    @FXML
    private Button detailsButton;

    @FXML
    private Label scoreField;

    @FXML
    private Label seasonEpisodeNumber;

    @FXML
    private Label writtenByField;

    @FXML
    private Label writtenByText;

    @FXML
    private Label genresText;

    @FXML
    private Label genresField;

    @FXML
    private Label yearField;
    //endregion

    //region ATTRIBUTES
    private Controller controllerParent;

    private List<Season> seasons = new ArrayList<>();
    private List<Disc> discs = new ArrayList<>();
    private List<Button> discsButtons = new ArrayList<>();
    private int currentSeason = 0;
    private Disc selectedDisc = null;
    private Timeline timeline = null;
    private MediaPlayer mp = null;
    public MediaPlayerFactory mediaPlayerFactory;

    public EmbeddedMediaPlayer embeddedMediaPlayer;
    private boolean isVideo = false;
    private boolean playSameMusic = false;
    private boolean isShow = false;
    private Series series = null;
    private boolean episodesFocussed = true;
    private boolean playingVideo = false;
    private boolean firstSelection = true;
    //endregion

    //region INITIALIZATION
    public void setParent(Controller c){
        controllerParent = c;
    }
    public void setSeasons(Series series, List<String> seasonList, boolean playSameMusic, boolean isShow){
        this.isShow = isShow;
        this.series = series;
        this.playSameMusic = playSameMusic;
        if (seasons != null){
            for (String id : seasonList){
                seasons.add(App.findSeason(id));
            }
        }

        assert seasons != null;
        seasons.sort(new Utils.SeasonComparator());

        videoImage.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        videoImage.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        videoImage.setVisible(false);
        initVideoPlayer();

        menuShadow.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        menuShadow.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        menuShadow.setVisible(false);

        videoPlayerPane.setVisible(false);

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode() || KeyCode.BACK_SPACE == event.getCode()){
                if (!playingVideo){
                    if (detailsBox.isVisible())
                        closeDetails();
                    else
                        goBack();
                }
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
        backgroundShadow.setFitWidth(screenWidth);
        backgroundShadow.setFitHeight(screenHeight);
        backgroundShadow2.setFitWidth(screenWidth);
        backgroundShadow2.setFitHeight(screenHeight);
        episodeScroll.setPrefWidth(screenWidth);

        detailsText.setPrefWidth(screenWidth / 2);
        detailsBox.setVisible(false);

        //Remove horizontal and vertical scroll
        episodeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        episodeScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        playButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                controllerParent.playInteractionSound();
                playButton.setText(App.buttonsBundle.getString("playButton"));
                ImageView img = (ImageView) playButton.getGraphic();
                img.setImage(new Image("file:src/main/resources/img/icons/playSelected.png", 30, 30, true, true));
            }else{
                playButton.setText("");
                ImageView img = (ImageView) playButton.getGraphic();
                img.setImage(new Image("file:src/main/resources/img/icons/play.png", 30, 30, true, true));
            }
        });

        watchedButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            ImageView img = (ImageView) watchedButton.getGraphic();
            if (newVal){
                controllerParent.playInteractionSound();
                if (selectedDisc.isWatched()){
                    watchedButton.setText(App.buttonsBundle.getString("markUnwatched"));
                    img.setImage(new Image("file:src/main/resources/img/icons/watchedSelected.png", 30, 30, true, true));
                }else{
                    watchedButton.setText(App.buttonsBundle.getString("markWatched"));
                    img.setImage(new Image("file:src/main/resources/img/icons/toWatchSelected.png", 30, 30, true, true));
                }
            }else{
                watchedButton.setText("");
                if (selectedDisc.isWatched()){
                    img.setImage(new Image("file:src/main/resources/img/icons/watched.png", 30, 30, true, true));
                }else{
                    img.setImage(new Image("file:src/main/resources/img/icons/toWatch.png", 30, 30, true, true));
                }
            }
        });

        optionsButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                controllerParent.playInteractionSound();
                optionsButton.setText(App.buttonsBundle.getString("moreButton"));
                ImageView img = (ImageView) optionsButton.getGraphic();
                img.setImage(new Image("file:src/main/resources/img/icons/optionsSelected.png", 30, 30, true, true));
            }else{
                optionsButton.setText("");
                ImageView img = (ImageView) optionsButton.getGraphic();
                img.setImage(new Image("file:src/main/resources/img/icons/options.png", 30, 30, true, true));
            }
        });

        watchedButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)){
                toggleWatched();
            }
        });

        lastSeasonButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                controllerParent.playInteractionSound();
                lastSeason();
            }
        });

        nextSeasonButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                controllerParent.playInteractionSound();
                nextSeason();
            }
        });

        detailsButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.DOWN)){
                if (discsButtons.size() <= 1)
                    playButton.requestFocus();
                else
                    discsButtons.get(discs.indexOf(selectedDisc)).requestFocus();
            }else if (event.getCode().equals(KeyCode.LEFT) && lastSeasonButton.isVisible())
                lastSeasonButton.requestFocus();
            else if (event.getCode().equals(KeyCode.RIGHT) && nextSeasonButton.isVisible())
                nextSeasonButton.requestFocus();
        });

        playButton.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.UP)){
                if (discsButtons.size() <= 1)
                    detailsButton.requestFocus();
                else
                    discsButtons.get(discs.indexOf(selectedDisc)).requestFocus();
            }else if (event.getCode().equals(KeyCode.RIGHT))
                watchedButton.requestFocus();
            else if (event.getCode().equals(KeyCode.LEFT) && lastSeasonButton.isVisible())
                lastSeasonButton.requestFocus();
        });

        watchedButton.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.UP)){
                if (discsButtons.size() <= 1)
                    detailsButton.requestFocus();
                else
                    discsButtons.get(discs.indexOf(selectedDisc)).requestFocus();
            }else if (event.getCode().equals(KeyCode.RIGHT))
                optionsButton.requestFocus();
            else if (event.getCode().equals(KeyCode.LEFT))
                playButton.requestFocus();
        });

        optionsButton.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.UP)){
                if (discsButtons.size() <= 1)
                    detailsButton.requestFocus();
                else
                    discsButtons.get(discs.indexOf(selectedDisc)).requestFocus();
            }else if (event.getCode().equals(KeyCode.LEFT))
                watchedButton.requestFocus();
            else if (event.getCode().equals(KeyCode.RIGHT) && nextSeasonButton.isVisible())
                nextSeasonButton.requestFocus();
        });

        detailsButton.setFocusTraversable(false);
        playButton.setFocusTraversable(false);
        watchedButton.setFocusTraversable(false);
        optionsButton.setFocusTraversable(false);
        lastSeasonButton.setFocusTraversable(false);
        nextSeasonButton.setFocusTraversable(false);

        setEpisodesOutOfFocusButton(detailsButton);
        setEpisodesOutOfFocusButton(playButton);
        setEpisodesOutOfFocusButton(watchedButton);
        setEpisodesOutOfFocusButton(optionsButton);

        currentSeason = 0;
        assert seasons != null;
        updateInfo(seasons.get(0));
    }
    private void updateInfo(Season season){
        if (mp != null){
            if (isVideo && !playSameMusic) {
                mp.stop();
            }
        }

        if (timeline != null)
            timeline.stop();

        stopBackgroundVideo();

        firstSelection = true;

        if (season.getDiscs().size() > 1){
            cardContainer.setPrefHeight((Screen.getPrimary().getBounds().getHeight() / 5) + 20);
            cardContainer.setVisible(true);
        }else{
            cardContainer.setPrefHeight(0);
            cardContainer.setVisible(false);
        }

        //Set Background Image
        Image background = new Image("file:" + season.getBackgroundSrc());
        backgroundImage.setImage(background);
        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);
        backgroundImage.setCache(true);

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double targetAspectRatio = screenWidth / screenHeight;

        // Calcular las dimensiones del recorte
        double originalWidth = backgroundImage.getImage().getWidth();
        double originalHeight = backgroundImage.getImage().getHeight();
        double originalAspectRatio = originalWidth / originalHeight;

        double newWidth, newHeight;
        if (originalAspectRatio > targetAspectRatio) {
            // Recortar en la altura
            newWidth = originalHeight * targetAspectRatio;
            newHeight = originalHeight;
        } else {
            // Recortar en el ancho
            newWidth = originalWidth;
            newHeight = originalWidth / targetAspectRatio;
        }

        // Calcular la posición de inicio del recorte
        int xOffset = 0;
        int yOffset = (int) ((originalHeight - newHeight) / 2);

        // Obtener el lector de píxeles de la imagen original
        PixelReader pixelReader = backgroundImage.getImage().getPixelReader();

        // Crear una nueva imagen recortada utilizando WritableImage
        WritableImage croppedImage = new WritableImage(pixelReader, xOffset, 0, (int) newWidth, (int) newHeight);

        // Crear el nuevo ImageView con la imagen recortada
        backgroundImage.setImage(croppedImage);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), backgroundImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        String logoSrc;
        if (isShow)
            logoSrc = series.logoSrc;
        else
            logoSrc = season.logoSrc;

        if (logoSrc.isEmpty()){
            infoBox.getChildren().remove(0);
            Label seriesTitle = new Label(series.name);
            seriesTitle.setFont(new Font("Arial", 58));
            seriesTitle.setStyle("-fx-font-weight: bold");
            seriesTitle.setTextFill(Color.color(1, 1, 1));
            seriesTitle.setEffect(new DropShadow());
            infoBox.getChildren().add(0, seriesTitle);
        }else {
            Image img;
            img = new Image("file:" + logoSrc, screenWidth * 0.25, screenHeight * 0.25, true, true);

            logo.setImage(img);
            logo.setFitWidth(screenWidth * 0.25);
            logo.setFitHeight(screenHeight * 0.25);
        }

        /*if (!isShow)
            infoBox.getChildren().remove(episodeName);*/

        if (!season.getVideoSrc().isEmpty()){
            File file = new File(season.getVideoSrc());
            //Media media = new Media(file.toURI().toString());
            //mp = new MediaPlayer(media);
            //backgroundVideo.setMediaPlayer(mp);
            //backgroundVideo.setVisible(false);
            isVideo = true;

            //normalizeVolume(file);

            setVideoPlayer();
        }else if (!season.getMusicSrc().isEmpty()){
            if (!playSameMusic || (currentSeason == 0 && mp == null)){
                File file = new File(season.getMusicSrc());
                Media media = new Media(file.toURI().toString());
                mp = new MediaPlayer(media);
                isVideo = false;

                setMediaPlayer();
            }
        }

        cardContainer.getChildren().clear();
        discsButtons.clear();
        discs.clear();
        List<String> discs = season.getDiscs();
        List<Disc> discList = new ArrayList<>();
        for (String i : discs){
            Disc d = App.findDisc(i);
            discList.add(d);
        }

        discList.sort(new Utils.DiscComparator().reversed());
        for (Disc disc : discList){
            addEpisodeCard(disc);
        }

        if (discsButtons.size() <= 1){
            setTimeLeft(discList.get(0));
        }

        Platform.runLater(() -> {
            //Set ScrollPane values
            episodeScroll.setHmax(cardContainer.getWidth());
            episodeScroll.setHvalue(0);

            if (discsButtons.size() > 1)
                discsButtons.get(season.lastDisc).requestFocus();
            else
                playButton.requestFocus();
        });

        selectedDisc = App.findDisc(discs.get(0));

        overviewField.setText(season.overview);
        yearField.setText(season.getYear());
        durationField.setText(setRuntime(selectedDisc.runtime));
        episodeName.setText(season.name);

        if (selectedDisc.imdbScore != 0){
            scoreProviderImg.setImage(new Image("file:src/main/resources/img/icons/imdb.png", 30, 30, true, true));
            scoreField.setText(String.valueOf(selectedDisc.imdbScore));
        }else{
            scoreProviderImg.setImage(new Image("file:src/main/resources/img/icons/tmdb.png", 30, 30, true, true));
            scoreField.setText(String.valueOf(selectedDisc.score));
        }

        if (!isShow){
            detailsInfo.getChildren().remove(seasonEpisodeNumber);
        }

        fadeInEffect(backgroundImage);
    }
    public Controller getParent(){
        return controllerParent;
    }
    //endregion

    //region UTILS
    private void updateButtons(){
        lastSeasonButton.setVisible(currentSeason != 0);
        nextSeasonButton.setVisible(currentSeason != seasons.size() - 1);
    }
    public void hideMenuShadow(){
        menuShadow.setVisible(false);
    }
    public void stopVideo(){
        playingVideo = false;

        if (discsButtons.size() > 1)
            discsButtons.get(discs.indexOf(selectedDisc)).requestFocus();
        else
            playButton.requestFocus();

        setTimeLeft(selectedDisc);
        reloadEpisodeCard();
        updateWatchedButton();
    }
    //endregion

    //region EPISODES
    public void addEpisodeCard(Disc disc){
        if (disc != null){
            discs.add(disc);
            Button btn = new Button();
            btn.setPadding(new Insets(0));

            btn.setFocusTraversable(false);

            btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    if (!episodesFocussed){
                        episodesFocussed = true;
                        enableAllEpisodes();
                    }

                    seasons.get(currentSeason).lastDisc = discsButtons.indexOf(btn);

                    //Move ScrollPane
                    handleButtonFocus(btn);

                    btn.setScaleX(1.1);
                    btn.setScaleY(1.1);
                    controllerParent.playInteractionSound();

                    updateDiscInfo(discs.get(discsButtons.indexOf(btn)));
                }else{
                    btn.setScaleX(1);
                    btn.setScaleY(1);
                }
            });

            btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (event.getCode().equals(KeyCode.ENTER)){
                    playEpisode(discs.get(discsButtons.indexOf(btn)));
                }
            });

            btn.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) ->{
                if (event.getCode().equals(KeyCode.DOWN))
                    playButton.requestFocus();

                if (event.getCode().equals(KeyCode.UP))
                    detailsButton.requestFocus();

                if (discsButtons.indexOf(btn) == 0
                        && event.getCode().equals(KeyCode.LEFT) && lastSeasonButton.isVisible())
                    lastSeasonButton.requestFocus();

                if (discsButtons.indexOf(btn) == (discsButtons.size() - 1)
                        && event.getCode().equals(KeyCode.RIGHT) && nextSeasonButton.isVisible())
                    nextSeasonButton.requestFocus();

                int index = discsButtons.indexOf(btn);
                if (event.getCode().equals(KeyCode.LEFT)){
                    if (index > 0)
                        discsButtons.get(index - 1).requestFocus();
                    else if (lastSeasonButton.isVisible())
                        lastSeasonButton.requestFocus();
                }else if (event.getCode().equals(KeyCode.RIGHT)){
                    if (index < discsButtons.size() - 1)
                        discsButtons.get(index + 1).requestFocus();
                    else if (nextSeasonButton.isVisible())
                        nextSeasonButton.requestFocus();
                }
            });

            setEpisodeCardValues(btn, disc);

            cardContainer.getChildren().add(btn);
            discsButtons.add(btn);
        }
    }

    private void handleButtonFocus(Button focusedButton) {
        double screenCenter = Screen.getPrimary().getBounds().getWidth() / 2;

        Bounds buttonBounds = focusedButton.localToScreen(focusedButton.getBoundsInLocal());
        double globalButtonCenterX = (buttonBounds.getMinX() + buttonBounds.getMaxX()) / 2;

        if (firstSelection) {
            if (discsButtons.indexOf(focusedButton) <= 2) {
                firstSelection = false;
                return;
            }

            episodeScroll.setHvalue(globalButtonCenterX + (focusedButton.getWidth() / 2));
            firstSelection = false;
            return;
        }

        double newHvalue = getNewHvalue(globalButtonCenterX, screenCenter);

        animateScrollTransition(newHvalue);
    }

    private double getNewHvalue(double globalButtonCenterX, double screenCenter) {
        double currentHvalue = episodeScroll.getHvalue();

        double newHvalue;
        if (globalButtonCenterX > screenCenter) {
            //Movement to right
            double offset = globalButtonCenterX - screenCenter;
            newHvalue = Math.min(episodeScroll.getHmax(), currentHvalue + offset);
        } else {
            //Movement to left
            double offset = screenCenter - globalButtonCenterX;
            newHvalue = Math.max(episodeScroll.getHmin(), currentHvalue - offset);
        }
        return newHvalue;
    }

    private void animateScrollTransition(double endValue) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.25), new KeyValue(episodeScroll.hvalueProperty(), endValue))
        );
        timeline.play();
    }

    public void playEpisode(Disc disc){
        if (mp != null)
            mp.stop();

        if (timeline != null)
            timeline.stop();

        stopBackgroundVideo();

        playingVideo = true;

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("videoPlayer.fxml"));
            Parent root1 = fxmlLoader.load();

            Stage thisStage = (Stage) mainBox.getScene().getWindow();

            Stage stage = new Stage();
            stage.setTitle("VideoPlayer");
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(thisStage);
            Scene scene = new Scene(root1);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            String name = series.name;
            if (!isShow)
                name = seasons.get(currentSeason).name;

            VideoPlayerController playerController = fxmlLoader.getController();
            playerController.setVideo(this, discs, disc, name, scene);

            stage.setMaximized(true);
            stage.show();

            fadeInEffect((Pane) root1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        /*
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
            if (mp != null)
                mp.play();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error playing episode in DesktopViewController");
        }*/
    }

    private void setEpisodesOutOfFocusButton(Button btn){
        btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                disableAllButCurrentEpisode();
            }
        });
    }

    private void disableAllButCurrentEpisode(){
        episodesFocussed = false;
        int index = discs.indexOf(selectedDisc);
        for (int i = 0; i < discsButtons.size(); i++){
            if (i != index){
                discsButtons.get(i).setDisable(true);
            }
        }
    }

    private void enableAllEpisodes(){
        for (Button btn : discsButtons){
            btn.setDisable(false);
        }
    }

    private void toggleWatched(){
        if (selectedDisc.isWatched())
            selectedDisc.setUnWatched();
        else
            selectedDisc.setWatched();

        reloadEpisodeCard();

        updateWatchedButton();
    }
    private void updateWatchedButton(){
        ImageView img = (ImageView) watchedButton.getGraphic();

        if (watchedButton.isFocused()){
            if (selectedDisc.isWatched()){
                img.setImage(new Image("file:src/main/resources/img/icons/watchedSelected.png", 30, 30, true, true));
            }else{
                img.setImage(new Image("file:src/main/resources/img/icons/toWatchSelected.png", 30, 30, true, true));
            }
        }else{
            if (selectedDisc.isWatched()){
                img.setImage(new Image("file:src/main/resources/img/icons/watched.png", 30, 30, true, true));
            }else{
                img.setImage(new Image("file:src/main/resources/img/icons/toWatch.png", 30, 30, true, true));
            }
        }
    }
    private void reloadEpisodeCard(){
        Button btn = discsButtons.get(discs.indexOf(selectedDisc));
        btn.setGraphic(null);

        setEpisodeCardValues(btn, selectedDisc);
    }
    private void setEpisodeCardValues(Button btn, Disc selectedDisc) {
        ImageView thumbnail = new ImageView();

        double targetHeight = Screen.getPrimary().getBounds().getHeight() / 5.5;
        double targetWidth = ((double) 16 /9) * targetHeight;

        thumbnail.setFitWidth(targetWidth);
        thumbnail.setFitHeight(targetHeight);

        File newFile = new File(selectedDisc.imgSrc);
        if (!newFile.exists())
            selectedDisc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";

        Image originalImage = new Image("file:" + selectedDisc.imgSrc, targetWidth, targetHeight, true, true);

        thumbnail.setImage(originalImage);
        thumbnail.setPreserveRatio(false);
        thumbnail.setSmooth(true);

        btn.getStyleClass().add("seriesCoverButton");

        StackPane main = new StackPane(thumbnail);
        BorderPane details = new BorderPane();

        if (selectedDisc.getTimeWatched() > 5000){
            JFXSlider slider = new JFXSlider(0, selectedDisc.runtime * 60 * 1000
                    , selectedDisc.getTimeWatched());
            slider.setFocusTraversable(false);
            details.setBottom(slider);
        }

        HBox videoInfoParent = new HBox();
        videoInfoParent.setPadding(new Insets(6, 0, 0, 6));

        HBox videoInfo = new HBox();
        videoInfo.setAlignment(Pos.TOP_RIGHT);
        videoInfo.setPrefWidth(Region.USE_COMPUTED_SIZE);
        videoInfo.setMaxWidth(Region.USE_PREF_SIZE);
        videoInfo.setPadding(new Insets(8));
        videoInfo.getStyleClass().add("episodeCardInfo");
        videoInfo.setSpacing(5);

        videoInfoParent.getChildren().add(videoInfo);

        if (isShow){
            Label info = new Label("S" + seasons.get(currentSeason).seasonNumber + "E" + selectedDisc.episodeNumber);
            info.setFont(new Font(24));
            info.setTextFill(Color.WHITE);
            videoInfo.getChildren().add(info);
        }

        if (selectedDisc.isWatched()){
            ImageView watched = new ImageView(
                    new Image("file:src/main/resources/img/icons/tick.png", 25, 25, true, true));
            videoInfo.getChildren().add(watched);
            watched.setTranslateY(5);
        }

        if (!videoInfo.getChildren().isEmpty())
            details.setTop(videoInfoParent);

        main.getChildren().add(details);
        btn.setGraphic(main);
    }
    //endregion

    //region MOVEMENT
    @FXML
    void goBack(){
        if (mp != null)
            mp.stop();

        if (timeline != null)
            timeline.stop();

        stopBackgroundVideo();
        embeddedMediaPlayer.release();
        mediaPlayerFactory.release();

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

    }
    @FXML
    void play(){
        if (selectedDisc != null)
            playEpisode(selectedDisc);
    }
    @FXML
    void editSeason(){
        //Edit "sorting order"
    }
    //endregion

    //region EFFECTS
    public void fadeOutEffect(Pane pane){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), pane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        pane.setVisible(false);
    }

    public void fadeOutEffect(ImageView img){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), img);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        img.setVisible(false);
    }

    public void fadeInEffect(Pane pane){
        pane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void fadeInEffect(ImageView img){
        img.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), img);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    //endregion

    //region DETAILS
    @FXML
    void openDetails(){
        fadeOutEffect(mainPane);

        if (isShow){
            detailsImage.setImage(new Image("file:" + selectedDisc.imgSrc));
            genresField.setText(series.getGenres());
        }else{
            detailsImage.setFitHeight(Screen.getPrimary().getBounds().getHeight());
            detailsImage.setImage(new Image("file:" + seasons.get(currentSeason).coverSrc));
            genresField.setText(seasons.get(currentSeason).getGenres());
        }
        detailsTitle.setText(selectedDisc.name);
        detailsOverview.setText(overviewField.getText());

        File file = new File(selectedDisc.executableSrc);
        fileNameField.setText(file.getName());

        fadeInEffect(menuShadow);
        fadeInEffect(detailsBox);

        detailsBox.requestFocus();
    }

    private void closeDetails(){
        fadeOutEffect(menuShadow);
        fadeOutEffect(detailsBox);
        fadeInEffect(mainPane);

        detailsButton.requestFocus();
    }
    public String setRuntime(int runtime){
        int h = runtime / 60;
        int m = runtime % 60;

        if (h == 0)
            return (m + "m");

        return (h + "h " + m + "m");
    }
    private void updateDiscInfo(Disc disc) {
        selectedDisc = disc;
        episodeName.setText(disc.name);
        overviewField.setText(disc.overview);
        seasonEpisodeNumber.setText(App.textBundle.getString("seasonLetter") + seasons.get(currentSeason).seasonNumber + " " + App.textBundle.getString("episodeLetter") + disc.episodeNumber);
        yearField.setText(disc.year);
        durationField.setText(setRuntime(disc.runtime));
        scoreField.setText(String.valueOf(disc.score));

        setTimeLeft(disc);

        updateWatchedButton();
    }
    private void setTimeLeft(Disc disc){
        if (disc.getTimeWatched() > 5000){
            timeLeftBox.setVisible(true);
            long leftTime = ((long) disc.runtime * 60 * 1000) - disc.getTimeWatched();

            long hours = leftTime / 3600000;
            long minutes = (leftTime % 3600000) / 60000;

            String timeLeft;
            if (App.globalLanguage != Locale.forLanguageTag("es-ES")) {
                if (hours > 0) {
                    timeLeft = hours + " " + App.textBundle.getString("hours") + " " + minutes + " " + App.textBundle.getString("minutes") + " " + App.textBundle.getString("timeLeft");
                } else {
                    timeLeft = minutes + " min left";
                }
            } else {
                if (hours > 0) {
                    timeLeft = App.textBundle.getString("timeLeft") + " " + hours + " " + App.textBundle.getString("hours") + " " + minutes + " " + App.textBundle.getString("minutes");
                } else {
                    timeLeft = "Quedan " + minutes + " minutos";
                }
            }

            timeLeftField.setText(timeLeft);
        }else{
            timeLeftBox.setVisible(false);
        }
    }
    //endregion

    //region BACKGROUND MUSIC
    private void setMediaPlayer(){
        mp.setVolume(Double.parseDouble(Configuration.loadConfig("volume", "0.2")));
        mp.setOnEndOfMedia(() -> {
            mp.seek(Duration.ZERO);
            mp.play();
        });

        timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(4), event -> {
                    playVideo();
                })
        );
        timeline.play();
    }
    private void playVideo(){
        Platform.runLater(() ->{
            if (isVideo){
                playBackgroundVideo();
            }else{
                if (mp != null) {
                    mp.stop();
                    mp.seek(mp.getStartTime());
                    mp.play();
                }
            }
        });
    }
    //endregion

    //region BACKGROUND VIDEO
    private void initVideoPlayer(){
        mediaPlayerFactory = new MediaPlayerFactory();
        embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImage));
    }
    private void setVideoPlayer(){
        timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(4), event -> {
                    playVideo();
                })
        );
        timeline.play();
    }
    public void playBackgroundVideo(){
        embeddedMediaPlayer.media().play(seasons.get(currentSeason).videoSrc);
        embeddedMediaPlayer.controls().setPosition(0);
        embeddedMediaPlayer.controls().pause();

        //Get video ratio
        //double screenRatio = Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight();
        //double mediaRatio = Double.parseDouble(embeddedMediaPlayer.video().aspectRatio());

        /*if (screenRatio > 1.8f && mediaRatio > 1.8f){
            backgroundVideo.setPreserveRatio(false);
        }

        backgroundVideo.setVisible(true);

        //Fade In Transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundVideo);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        mp.stop();
        mp.seek(mp.getStartTime());
        mp.play();*/

        //embeddedMediaPlayer.audio().setVolume(20);
        embeddedMediaPlayer.controls().play();
        fadeInEffect(videoImage);
    }
    public void stopBackgroundVideo(){
        embeddedMediaPlayer.controls().stop();
        fadeOutEffect(videoImage);
    }
    //endregion
}
