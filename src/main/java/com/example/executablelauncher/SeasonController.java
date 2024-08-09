package com.example.executablelauncher;

import com.example.executablelauncher.entities.Episode;
import com.example.executablelauncher.entities.Library;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import com.example.executablelauncher.fileMetadata.AudioTrack;
import com.example.executablelauncher.fileMetadata.SubtitleTrack;
import com.example.executablelauncher.fileMetadata.VideoTrack;
import com.example.executablelauncher.tmdbMetadata.movieCredits.Cast;
import com.example.executablelauncher.utils.Configuration;
import com.example.executablelauncher.utils.Utils;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.executablelauncher.App.getBaseFontSize;
import static com.example.executablelauncher.App.mediaExecutor;
import static com.example.executablelauncher.utils.Utils.*;

public class SeasonController {
    //region FXML ATTRIBUTES
    @FXML Pane fill;
    @FXML Pane shade;
    @FXML Label directedByText;
    @FXML Label writtenByText;
    @FXML Label createdByText;
    @FXML Label musicByText;
    @FXML Label studioText;
    @FXML Label directedByField;
    @FXML Label writtenByField;
    @FXML Label createdByField;
    @FXML Label musicByField;
    @FXML Label studioField;
    @FXML Label fileSizeText;
    @FXML Label fileSizeField;
    @FXML Label bitrateText;
    @FXML Label bitrateField;
    @FXML Label castText;
    @FXML VBox castLeftBox;
    @FXML VBox castRightBox;
    @FXML BorderPane videoError;
    @FXML Button goBackButton;
    @FXML Button videoTrackButton;
    @FXML Button audioTrackButton;
    @FXML Button subsTrackButton;
    @FXML Label errorTitle;
    @FXML Label errorMessage;
    @FXML Button errorButton;
    @FXML MediaView backgroundVideo;
    @FXML ImageView backgroundImage;
    @FXML ImageView backgroundShadow;
    @FXML ImageView backgroundShadow2;
    @FXML ImageView scoreProviderImg;
    @FXML HBox cardContainer;
    @FXML VBox detailsText;
    @FXML HBox timeLeftBox;
    @FXML Label timeLeftField;
    @FXML BorderPane detailsBox;
    @FXML ImageView detailsImage;
    @FXML HBox detailsInfo;
    @FXML Label detailsOverview;
    @FXML Label detailsTitle;
    @FXML Label durationField;
    @FXML Label episodeName;
    @FXML Label fileDetailsText;
    @FXML Label fileNameField;
    @FXML Label fileNameText;
    @FXML VBox infoBox;
    @FXML Button lastSeasonButton;
    @FXML ImageView logo;
    @FXML StackPane mainBox;
    @FXML BorderPane mainPane;
    @FXML ImageView menuShadow;
    @FXML Button nextSeasonButton;
    @FXML Button watchedButton;
    @FXML Button optionsButton;
    @FXML Label overviewField;
    @FXML Button playButton;
    @FXML Button detailsButton;
    @FXML Label scoreField;
    @FXML Label seasonEpisodeNumber;
    @FXML ScrollPane episodeScroll;
    @FXML Label genresText;
    @FXML Label genresField;
    @FXML Label yearField;
    //endregion

    //region ATTRIBUTES
    Controller controllerParent;
    Library library;
    List<Season> seasons = new ArrayList<>();
    List<Episode> episodes = new ArrayList<>();
    List<Button> episodeButtons = new ArrayList<>();
    Episode selectedEpisode = null;
    Series series = null;
    int currentSeason = 0;
    int buttonCount = 0;
    MediaPlayer mp = null;
    boolean isShow = false;
    boolean isVideo = false;
    boolean playingVideo = false;
    boolean playSameMusic = false;
    boolean episodesFocussed = true;
    //endregion

    //region INITIALIZATION
    public void setParent(Controller c){
        controllerParent = c;
    }
    public void setSeasons(Library library, Series series, boolean playSameMusic, boolean isShow){
        this.library = library;
        this.isShow = isShow;
        this.series = series;
        this.playSameMusic = playSameMusic;
        seasons = series.getSeasons();

        assert seasons != null;
        seasons.sort(new Utils.SeasonComparator());

        shade.setVisible(false);
        fill.setVisible(false);

        menuShadow.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        menuShadow.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        menuShadow.setVisible(false);

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (App.pressedBack(event)){
                if (!playingVideo){
                    if (detailsBox.isVisible())
                        closeDetails();
                    else
                        goBack();
                }
            }
        });

        errorTitle.setText(App.textBundle.getString("playbackError"));
        errorMessage.setText(App.textBundle.getString("videoErrorMessage"));
        errorButton.setText(App.buttonsBundle.getString("ok"));

        fileDetailsText.setText(App.textBundle.getString("fileDetails"));
        castText.setText(App.textBundle.getString("cast"));
        directedByText.setText(App.textBundle.getString("directedBy"));
        writtenByText.setText(App.textBundle.getString("writtenBy"));
        createdByText.setText(App.textBundle.getString("createdBy"));
        musicByText.setText(App.textBundle.getString("musicBy"));
        studioText.setText(App.textBundle.getString("studios"));
        genresText.setText(App.textBundle.getString("genres"));
        fileNameText.setText(App.textBundle.getString("fileName"));
        fileSizeText.setText(App.textBundle.getString("size"));
        bitrateText.setText(App.textBundle.getString("bitrate"));

        errorButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (App.pressedSelect(event)) {
                videoError.setVisible(false);

                if (isShow)
                    episodeButtons.get(episodes.indexOf(selectedEpisode)).requestFocus();
                else
                    playButton.requestFocus();
            }
        });

        errorButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            videoError.setVisible(false);

            if (isShow)
                episodeButtons.get(episodes.indexOf(selectedEpisode)).requestFocus();
            else
                playButton.requestFocus();
        });

        videoError.setVisible(false);

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
        backgroundVideo.setFitHeight(screenHeight);
        backgroundVideo.setFitWidth(screenWidth);

        episodeScroll.setPrefWidth(screenWidth);

        episodeScroll.setOnMouseClicked(e -> {
            if (!episodesFocussed)
                episodeButtons.get(episodes.indexOf(selectedEpisode)).requestFocus();
        });

        detailsText.setPrefWidth(screenWidth / 2);
        detailsBox.setVisible(false);

        double aspectRatio = screenWidth / screenHeight;
        if (aspectRatio > (double) 16/9){
            shade.setPrefWidth(((double) 16/9) * screenHeight);
            shade.setPrefHeight(screenHeight);

            fill.setPrefWidth(screenWidth - shade.getPrefWidth());
            fill.setPrefHeight(screenHeight);
        }else{
            shade.setPrefWidth(screenWidth);
            shade.setPrefHeight(screenHeight);

            fill.setPrefWidth(0);
        }

        detailsButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                playInteractionSound();
        });

        playButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                playInteractionSound();
                playButton.setText(App.buttonsBundle.getString("playButton"));
                ImageView img = (ImageView) playButton.getGraphic();
                img.setImage(new Image(getFileAsIOStream("img/icons/playSelected.png"), 30, 30, true, true));
            }else{
                playButton.setText("");
                ImageView img = (ImageView) playButton.getGraphic();
                img.setImage(new Image(getFileAsIOStream("img/icons/play.png"), 30, 30, true, true));
            }
        });

        watchedButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            ImageView img = (ImageView) watchedButton.getGraphic();
            if (newVal){
                playInteractionSound();
                if (selectedEpisode.isWatched()){
                    watchedButton.setText(App.buttonsBundle.getString("markUnwatched"));
                    img.setImage(new Image(getFileAsIOStream("img/icons/watchedSelected.png"), 30, 30, true, true));
                }else{
                    watchedButton.setText(App.buttonsBundle.getString("markWatched"));
                    img.setImage(new Image(getFileAsIOStream("img/icons/toWatchSelected.png"), 30, 30, true, true));
                }
            }else{
                watchedButton.setText("");
                if (selectedEpisode.isWatched()){
                    img.setImage(new Image(getFileAsIOStream("img/icons/watched.png"), 30, 30, true, true));
                }else{
                    img.setImage(new Image(getFileAsIOStream("img/icons/toWatch.png"), 30, 30, true, true));
                }
            }
        });

        optionsButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                playInteractionSound();
                optionsButton.setText(App.buttonsBundle.getString("moreButton"));
                ImageView img = (ImageView) optionsButton.getGraphic();
                img.setImage(new Image(getFileAsIOStream("img/icons/optionsSelected.png"), 30, 30, true, true));
            }else{
                optionsButton.setText("");
                ImageView img = (ImageView) optionsButton.getGraphic();
                img.setImage(new Image(getFileAsIOStream("img/icons/options.png"), 30, 30, true, true));
            }
        });

        watchedButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (App.pressedSelect(event)){
                toggleWatched();
            }
        });

        lastSeasonButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                playInteractionSound();
        });

        nextSeasonButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                playInteractionSound();
        });

        detailsButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (App.pressedDown(event)){
                if (episodeButtons.size() <= 1 && !isShow)
                    playButton.requestFocus();
                else
                    episodeButtons.get(episodes.indexOf(selectedEpisode)).requestFocus();
            }else if (App.pressedLeft(event) && lastSeasonButton.isVisible())
                lastSeasonButton.requestFocus();
            else if (App.pressedRight(event) && nextSeasonButton.isVisible())
                nextSeasonButton.requestFocus();
        });

        playButton.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (App.pressedUp(event)){
                if (episodeButtons.size() <= 1 && !isShow)
                    detailsButton.requestFocus();
                else
                    episodeButtons.get(episodes.indexOf(selectedEpisode)).requestFocus();
            }else if (App.pressedRight(event))
                watchedButton.requestFocus();
            else if (App.pressedLeft(event) && lastSeasonButton.isVisible())
                lastSeasonButton.requestFocus();
        });

        playButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (App.pressedSelect(event))
                playEpisode(selectedEpisode);
        });

        watchedButton.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (App.pressedUp(event)){
                if (episodeButtons.size() <= 1 && !isShow)
                    detailsButton.requestFocus();
                else
                    episodeButtons.get(episodes.indexOf(selectedEpisode)).requestFocus();
            }else if (App.pressedRight(event) && !optionsButton.isVisible())
                nextSeasonButton.requestFocus();
            else if (App.pressedRight(event) && optionsButton.isVisible())
                optionsButton.requestFocus();
            else if (App.pressedLeft(event))
                playButton.requestFocus();
        });

        optionsButton.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (App.pressedUp(event)){
                if (episodeButtons.size() <= 1 && !isShow)
                    detailsButton.requestFocus();
                else
                    episodeButtons.get(episodes.indexOf(selectedEpisode)).requestFocus();
            }else if (App.pressedRight(event))
                watchedButton.requestFocus();
            else if (App.pressedLeft(event) && nextSeasonButton.isVisible())
                nextSeasonButton.requestFocus();
        });

        lastSeasonButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (App.pressedSelect(event)){
                playCategoriesSound();
                lastSeason();
            }else if (App.pressedRight(event)) {
                if (episodeButtons.size() > 1 && episodesFocussed)
                    episodeButtons.getFirst().requestFocus();
                else
                    playButton.requestFocus();
            }else if (App.pressedLeft(event)) {
                playCategoriesSound();
                lastSeason();
            }
        });

        nextSeasonButton.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (App.pressedSelect(event)){
                playCategoriesSound();
                nextSeason();
            }else if (App.pressedRight(event)) {
                playCategoriesSound();
                nextSeason();
            }else if (App.pressedLeft(event)) {
                if (episodeButtons.size() > 1 && episodesFocussed)
                    episodeButtons.getLast().requestFocus();
                else
                    playButton.requestFocus();
            }
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

        if (playSameMusic)
            findAndPlaySong();

        currentSeason = 0;

        //Find currently watching episode if exists
        if (series.isBeingWatched())
            currentSeason = series.getCurrentlyWatchingSeasonIndex();

        assert seasons != null;
        updateInfo(seasons.get(currentSeason));
    }
    private void updateInfo(Season season){
        if (mp != null && (isVideo || !playSameMusic))
            stopPlayer();

        if (season.getEpisodes().size() > 1 || isShow){
            cardContainer.setPrefHeight((Screen.getPrimary().getBounds().getHeight() / 5) + 20);
            cardContainer.setVisible(true);
        }else{
            cardContainer.setPrefHeight(0);
            cardContainer.setVisible(false);
        }

        updateButtons();

        //Set Background Image
        Image background = new Image("file:" + season.getBackgroundSrc());
        backgroundImage.setImage(background);
        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);
        backgroundImage.setCache(true);

        //region BACKGROUND CROP
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double targetAspectRatio = screenWidth / screenHeight;

        double originalWidth = backgroundImage.getImage().getWidth();
        double originalHeight = backgroundImage.getImage().getHeight();
        double originalAspectRatio = originalWidth / originalHeight;

        double newWidth, newHeight;
        if (originalAspectRatio > targetAspectRatio) {
            newWidth = originalHeight * targetAspectRatio;
            newHeight = originalHeight;
        } else {
            newWidth = originalWidth;
            newHeight = originalWidth / targetAspectRatio;
        }

        int xOffset = 0;
        int yOffset = (int) ((originalHeight - newHeight) / 2);

        PixelReader pixelReader = backgroundImage.getImage().getPixelReader();
        WritableImage croppedImage = new WritableImage(pixelReader, xOffset, 0, (int) newWidth, (int) newHeight);

        backgroundImage.setImage(croppedImage);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), backgroundImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        //endregion

        String logoSrc;
        if (isShow)
            logoSrc = series.getLogoSrc();
        else
            logoSrc = season.getLogoSrc();

        File logoFile = new File(logoSrc);

        if (!logoFile.exists() || !logoFile.isFile()){
            infoBox.getChildren().remove(0);
            Label seriesTitle = new Label(series.getName());
            seriesTitle.setFont(new Font("Roboto", 65));
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

        processBackgroundMedia();

        cardContainer.getChildren().clear();
        episodeButtons.clear();
        episodes = season.getEpisodes();

        episodes.sort(new Utils.EpisodeComparator());
        for (Episode episode : episodes)
            addEpisodeCard(episode);

        if (!episodes.isEmpty())
            selectedEpisode = episodes.get(0);

        if (episodes.size() == 1)
            updateSelectedTrackButtons(selectedEpisode);

        if (season.isBeingWatched())
            selectedEpisode = episodes.get(season.getCurrentlyWatchingEpisodeIndex());

        if (episodeButtons.size() <= 1 && !isShow)
            setTimeLeft(timeLeftBox, timeLeftField, episodes.get(0));

        Platform.runLater(() -> {
            cardContainer.setTranslateX(0);
            buttonCount = getVisibleButtonCountGlobal(episodeScroll, cardContainer);

            if (episodeButtons.size() > 1 || isShow) {
                if (season.isBeingWatched())
                    episodeButtons.get(season.getCurrentlyWatchingEpisodeIndex()).requestFocus();
                else
                    episodeButtons.get(season.getLastDisc()).requestFocus();
            }else
                playButton.requestFocus();
        });

        if (!season.getOverview().isEmpty())
            overviewField.setText(season.getOverview());
        else
            overviewField.setText(App.textBundle.getString("defaultOverview"));

        yearField.setText(season.getYear());
        durationField.setText(setRuntime(selectedEpisode.getRuntime()));
        episodeName.setText("");

        if (selectedEpisode.getImdbScore() != 0){
            scoreProviderImg.setImage(new Image(getFileAsIOStream("img/icons/imdb.png"), 40, 40, true, true));
            scoreField.setText(String.valueOf(selectedEpisode.getImdbScore()));
        }else{
            scoreProviderImg.setImage(new Image(getFileAsIOStream("img/icons/tmdb.png"), 40, 40, true, true));
            scoreField.setText(String.valueOf(selectedEpisode.getScore()));
        }

        if (!isShow){
            detailsInfo.getChildren().remove(seasonEpisodeNumber);
        }

        updateWatchedButton();
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
    public void stopVideo(){
        mainBox.setDisable(false);
        playingVideo = false;

        if (episodeButtons.size() > 1 || isShow)
            episodeButtons.get(episodes.indexOf(selectedEpisode)).requestFocus();
        else
            playButton.requestFocus();

        Series selectedSeries = App.getSelectedSeries();
        Season selectedSeason = seasons.get(currentSeason);
        markPreviousAndNextEpisodes(selectedSeries, selectedSeason, selectedEpisode);

        setTimeLeft(timeLeftBox, timeLeftField, selectedEpisode);
        updateWatchedButton();

        for (Episode episode : selectedSeason.getEpisodes())
            reloadEpisodeCard(episode);

        if (playSameMusic)
            findAndPlaySong();
        else
            processBackgroundMedia();
    }
    //endregion

    //region EPISODES
    public void addEpisodeCard(Episode episode){
        if (episode != null){
            Button btn = new Button();
            btn.setPadding(new Insets(0));

            btn.setFocusTraversable(false);

            btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    if (!episodesFocussed){
                        episodesFocussed = true;
                        enableAllEpisodes();
                    }

                    seasons.get(currentSeason).setLastDisc(episodeButtons.indexOf(btn));

                    //Move ScrollPane
                    handleButtonFocus(btn);

                    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.1), btn);
                    scaleTransition.setToX(1.1);
                    scaleTransition.setToY(1.1);
                    scaleTransition.play();

                    playInteractionSound();

                    updateDiscInfo(episodes.get(episodeButtons.indexOf(btn)));
                }else{
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.1), btn);
                    scaleTransition.setToX(1);
                    scaleTransition.setToY(1);
                    scaleTransition.play();
                }
            });

            btn.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
                if (App.pressedSelect(event))
                    playEpisode(episodes.get(episodeButtons.indexOf(btn)));
            });

            btn.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) ->{
                if (App.pressedDown(event))
                    playButton.requestFocus();

                if (App.pressedUp(event))
                    detailsButton.requestFocus();

                int index = episodeButtons.indexOf(btn);
                if (App.pressedLeft(event)){
                    if (index > 0)
                        episodeButtons.get(index - 1).requestFocus();
                    else if (lastSeasonButton.isVisible())
                        lastSeasonButton.requestFocus();
                }else if (App.pressedRight(event)){
                    if (index < episodeButtons.size() - 1)
                        episodeButtons.get(index + 1).requestFocus();
                    else if (nextSeasonButton.isVisible())
                        nextSeasonButton.requestFocus();
                }
            });

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
                if (event.getButton().equals(MouseButton.PRIMARY)){
                    if (btn == episodeButtons.get(episodes.indexOf(selectedEpisode)) && episodesFocussed)
                        playEpisode(selectedEpisode);
                    else
                        btn.requestFocus();
                }
            });

            setEpisodeCardValues(btn, episode);

            cardContainer.getChildren().add(btn);
            episodeButtons.add(btn);
        }
    }

    private void handleButtonFocus(Button focusedButton) {
        double screenCenter, buttonCenterX, offset, finalPos;

        if (episodeButtons.indexOf(focusedButton) <= buttonCount / 2 || episodeButtons.size() <= 3 || episodeButtons.size() == buttonCount){
            finalPos = 0;
        }else{
            //Get center of screen
            screenCenter = Screen.getPrimary().getBounds().getWidth() / 2;

            //Check aspect ratio to limit the right end of the button list
            double aspectRatio = Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight();
            int maxButtons = (episodeButtons.size() - (buttonCount / 2));
            if (aspectRatio > 1.8)
                maxButtons--;

            if (episodeButtons.indexOf(focusedButton) >= (episodeButtons.size() - (buttonCount / 2)))
                focusedButton = episodeButtons.get(Math.max(2, maxButtons));

            //Get center of button in the screen
            Bounds buttonBounds = focusedButton.localToScene(focusedButton.getBoundsInLocal());
            buttonCenterX = buttonBounds.getMinX() + buttonBounds.getWidth() / 2;

            //Calculate offset
            offset = screenCenter - buttonCenterX;

            //Calculate position
            finalPos = cardContainer.getTranslateX() + offset;
        }

        //Translation with animation
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.2), cardContainer);
        transition.setToX(finalPos);
        transition.play();
    }

    public void playEpisode(Episode episode){
        if (playingVideo)
            return;

        //Check if the video file exists before showing the video player
        File videoFile = new File(episode.getVideoSrc());

        if (!videoFile.isFile()){
            videoError.setVisible(true);
            errorButton.requestFocus();
            return;
        }

        if (mp != null)
            stopPlayer();

        selectedEpisode = episode;
        playingVideo = true;

        mainBox.setDisable(true);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("videoPlayer.fxml"));
            Parent root = fxmlLoader.load();
            root.setStyle(getBaseFontSize());

            Stage videoStage = (Stage) mainBox.getScene().getWindow();

            Stage controlsStage = new Stage();
            controlsStage.setTitle("Video Player Controls");
            controlsStage.initStyle(StageStyle.TRANSPARENT);
            controlsStage.initModality(Modality.NONE);
            controlsStage.initOwner(videoStage);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            controlsStage.setScene(scene);

            String name = series.getName();
            if (!isShow)
                name = seasons.get(currentSeason).getName();

            VideoPlayerController playerController = fxmlLoader.getController();
            playerController.setFullScreenPlayer(this, controlsStage);
            playerController.setVideo(seasons.get(currentSeason), episode, name, videoStage);

            controlsStage.setMaximized(true);
            controlsStage.show();

            fadeInEffect((Pane) root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        int index = episodes.indexOf(selectedEpisode);
        for (int i = 0; i < episodeButtons.size(); i++){
            if (i != index){
                episodeButtons.get(i).setDisable(true);
            }
        }
    }

    private void enableAllEpisodes(){
        for (Button btn : episodeButtons){
            btn.setDisable(false);
        }
    }

    @FXML
    void toggleWatched(){
        if (selectedEpisode.isWatched())
            selectedEpisode.setUnWatched();
        else
            selectedEpisode.setWatched();

        Series selectedSeries = App.getSelectedSeries();
        Season selectedSeason = seasons.get(currentSeason);
        markPreviousAndNextEpisodes(selectedSeries, selectedSeason, selectedEpisode);

        setTimeLeft(timeLeftBox, timeLeftField, selectedEpisode);
        updateWatchedButton();

        for (Episode episode : selectedSeason.getEpisodes())
            reloadEpisodeCard(episode);
    }
    private void updateWatchedButton(){
        ImageView img = (ImageView) watchedButton.getGraphic();

        if (watchedButton.isFocused()){
            if (selectedEpisode.isWatched()){
                img.setImage(new Image(getFileAsIOStream("img/icons/watchedSelected.png"), 30, 30, true, true));
            }else{
                img.setImage(new Image(getFileAsIOStream("img/icons/toWatchSelected.png"), 30, 30, true, true));
            }
        }else{
            if (selectedEpisode.isWatched()){
                img.setImage(new Image(getFileAsIOStream("img/icons/watched.png"), 30, 30, true, true));
            }else{
                img.setImage(new Image(getFileAsIOStream("img/icons/toWatch.png"), 30, 30, true, true));
            }
        }
    }
    private void reloadEpisodeCard(Episode episode){
        if (episodeButtons.isEmpty())
            return;

        Button btn = episodeButtons.get(episodes.indexOf(episode));
        btn.setGraphic(null);

        setEpisodeCardValues(btn, episode);
    }
    private void setEpisodeCardValues(Button btn, Episode selectedEpisode) {
        ImageView thumbnail = new ImageView();

        double targetHeight = Screen.getPrimary().getBounds().getHeight() / 5.5;
        double targetWidth = ((double) 16 /9) * targetHeight;

        thumbnail.setFitWidth(targetWidth);
        thumbnail.setFitHeight(targetHeight);

        File newFile = new File(selectedEpisode.getImgSrc());
        if (!newFile.exists())
            selectedEpisode.setImgSrc("resources/img/Default_video_thumbnail.jpg");

        Image originalImage = new Image("file:" + selectedEpisode.getImgSrc(), targetWidth, targetHeight, true, true);

        thumbnail.setImage(originalImage);
        thumbnail.setPreserveRatio(false);
        thumbnail.setSmooth(true);

        btn.getStyleClass().add("episodeButton");

        StackPane main = new StackPane(thumbnail);
        BorderPane details = new BorderPane();

        if (selectedEpisode.getTimeWatched() != 0){
            JFXSlider slider = new JFXSlider(0, selectedEpisode.getRuntimeInSeconds()
                    , selectedEpisode.getTimeWatched());
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
            Label info = new Label("S" + seasons.get(currentSeason).getSeasonNumber() + "E" + selectedEpisode.getEpisodeNumber());
            info.setFont(new Font(24));
            info.setTextFill(Color.WHITE);
            videoInfo.getChildren().add(info);
        }

        if (selectedEpisode.isWatched()){
            ImageView watched = new ImageView(
                    new Image(getFileAsIOStream("img/icons/episodeTick.png"), 25, 25, true, true));
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
        if (mp != null) {
            stopPlayer();
        }

        controllerParent.closeSeasonView();
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
    void play(){
        if (selectedEpisode != null)
            playEpisode(selectedEpisode);
    }
    //endregion

    //region DETAILS
    @FXML
    void openDetails(){
        playCategoriesSound();
        fadeOutEffect(mainPane, 0.6f, 0).play();
        mainPane.setVisible(false);

        if (isShow){
            detailsImage.setImage(new Image("file:" + selectedEpisode.getImgSrc()));
            genresField.setText(series.getGenres());
        }else{
            detailsImage.setFitHeight(Screen.getPrimary().getBounds().getHeight());
            detailsImage.setImage(new Image("file:" + seasons.get(currentSeason).getCoverSrc()));
            genresField.setText(seasons.get(currentSeason).getGenres());
        }
        detailsTitle.setText(selectedEpisode.getName());
        detailsOverview.setText(overviewField.getText());

        directedByField.setVisible(!selectedEpisode.getDirectedBy().isEmpty());
        directedByField.setManaged(!selectedEpisode.getDirectedBy().isEmpty());
        directedByText.setVisible(!selectedEpisode.getDirectedBy().isEmpty());
        directedByText.setManaged(!selectedEpisode.getDirectedBy().isEmpty());

        writtenByField.setVisible(!selectedEpisode.getWrittenBy().isEmpty());
        writtenByField.setManaged(!selectedEpisode.getWrittenBy().isEmpty());
        writtenByText.setVisible(!selectedEpisode.getWrittenBy().isEmpty());
        writtenByText.setManaged(!selectedEpisode.getWrittenBy().isEmpty());

        createdByField.setVisible(!seasons.get(currentSeason).getCreator().isEmpty());
        createdByField.setManaged(!seasons.get(currentSeason).getCreator().isEmpty());
        createdByText.setVisible(!seasons.get(currentSeason).getCreator().isEmpty());
        createdByText.setManaged(!seasons.get(currentSeason).getCreator().isEmpty());

        musicByField.setVisible(!seasons.get(currentSeason).getMusicComposer().isEmpty());
        musicByField.setManaged(!seasons.get(currentSeason).getMusicComposer().isEmpty());
        musicByText.setVisible(!seasons.get(currentSeason).getMusicComposer().isEmpty());
        musicByText.setManaged(!seasons.get(currentSeason).getMusicComposer().isEmpty());

        studioField.setVisible(!seasons.get(currentSeason).getProductionStudios().isEmpty());
        studioField.setManaged(!seasons.get(currentSeason).getProductionStudios().isEmpty());
        studioText.setVisible(!seasons.get(currentSeason).getProductionStudios().isEmpty());
        studioText.setManaged(!seasons.get(currentSeason).getProductionStudios().isEmpty());

        directedByField.setText(selectedEpisode.getDirectedBy());
        writtenByField.setText(selectedEpisode.getWrittenBy());
        createdByField.setText(seasons.get(currentSeason).getCreator());
        musicByField.setText(seasons.get(currentSeason).getMusicComposer());
        studioField.setText(seasons.get(currentSeason).getProductionStudios());
        fileNameField.setText(selectedEpisode.getMediaInfo().getFile());
        fileSizeField.setText(selectedEpisode.getMediaInfo().getSize());
        bitrateField.setText(selectedEpisode.getMediaInfo().getBitrate());

        castLeftBox.getChildren().clear();
        castRightBox.getChildren().clear();
        VBox box = castLeftBox;
        boolean changed = false;
        for (Cast member : seasons.get(currentSeason).getCast()){
            if (box.getChildren().size() == 11 && !changed) {
                box = castRightBox;
                changed = true;
            }else if (box.getChildren().size() == 10 && changed){
                Label label = new Label("...");
                label.setFont(new Font("Roboto", 21));
                label.setTextFill(Color.WHITE);
                label.setAlignment(Pos.CENTER_LEFT);
                box.getChildren().add(label);
                break;
            }

            box.getChildren().add(new TextFlow(
                    new Text() {{
                        setText(member.name);
                        setFill(Color.WHITE);
                        setFont(Font.font("System", FontWeight.BOLD, 21));
                    }},
                    new Text() {{
                        setText(" " + App.textBundle.getString("as") + " '" + member.character + "'");
                        setFill(Color.LIGHTGRAY);
                        setFont(Font.font("System", FontWeight.NORMAL, 21));
                    }}
            ));
        }

        File file = new File(selectedEpisode.getVideoSrc());
        fileNameField.setText(file.getName());

        menuShadow.setVisible(true);
        detailsBox.setVisible(true);
        fadeInEffect(menuShadow);
        fadeInEffect(detailsBox);

        detailsBox.requestFocus();
    }
    @FXML
    private void closeDetails(){
        fadeOutEffect(menuShadow, 0.6f, 0).play();
        fadeOutEffect(detailsBox, 0.6f, 0).play();
        fadeInEffect(mainPane);

        menuShadow.setVisible(false);
        detailsBox.setVisible(false);

        detailsButton.requestFocus();
    }

    private void updateDiscInfo(Episode episode) {
        selectedEpisode = episode;
        episodeName.setText(episode.getName());

        if (!episode.getOverview().isEmpty())
            overviewField.setText(episode.getOverview());
        else
            overviewField.setText(App.textBundle.getString("defaultOverview"));

        seasonEpisodeNumber.setText(App.textBundle.getString("seasonLetter") + seasons.get(currentSeason).getSeasonNumber() + " " + App.textBundle.getString("episodeLetter") + episode.getEpisodeNumber());
        yearField.setText(episode.getYear());
        durationField.setText(setRuntime(episode.getRuntime()));
        scoreField.setText(String.valueOf(episode.getScore()));

        setTimeLeft(timeLeftBox, timeLeftField, episode);

        updateSelectedTrackButtons(episode);

        updateWatchedButton();
    }

    private void updateSelectedTrackButtons(Episode episode){
        if (episode.getVideoTracks().isEmpty()){
            videoTrackButton.setVisible(false);
        }else{
            videoTrackButton.setVisible(true);
            VideoTrack selectedTrack = episode.getVideoTracks().getFirst();
            for (VideoTrack track : episode.getVideoTracks()){
                if (track.isSelected()){
                    selectedTrack = track;
                    break;
                }
            }

            videoTrackButton.setText(selectedTrack.getDisplayTitle());
        }

        if (episode.getAudioTracks().isEmpty()){
            audioTrackButton.setVisible(false);
        }else{
            audioTrackButton.setVisible(true);
            AudioTrack selectedTrack = episode.getAudioTracks().getFirst();
            for (AudioTrack track : episode.getAudioTracks()){
                if (track.isSelected()){
                    selectedTrack = track;
                    break;
                }
            }

            audioTrackButton.setText(selectedTrack.getDisplayTitle());
        }

        if (episode.getSubtitleTracks().isEmpty()){
            subsTrackButton.setVisible(false);
        }else{
            subsTrackButton.setVisible(true);
            SubtitleTrack selectedTrack = episode.getSubtitleTracks().getFirst();
            for (SubtitleTrack track : episode.getSubtitleTracks()){
                if (track.isSelected()){
                    selectedTrack = track;
                    break;
                }
            }

            subsTrackButton.setText(selectedTrack.getDisplayTitle());
        }
    }
    //endregion

    //region BACKGROUND VIDEO/MUSIC
    private void processBackgroundMedia(){
        if (mp != null)
            stopPlayer();

        if (!seasons.get(currentSeason).getVideoSrc().isEmpty()){
            File file = new File(seasons.get(currentSeason).getVideoSrc());
            Media media = new Media(file.toURI().toString());
            mp = new MediaPlayer(media);

            backgroundVideo.setMediaPlayer(mp);
            backgroundVideo.setVisible(false);
            isVideo = true;

            setMediaPlayer();
        }else if (!playSameMusic && !seasons.get(currentSeason).getMusicSrc().isEmpty()){
            File file = new File(seasons.get(currentSeason).getMusicSrc());
            Media media = new Media(file.toURI().toString());
            mp = new MediaPlayer(media);
            isVideo = false;

            setMediaPlayer();
        }
    }
    private void setMediaPlayer(){
        double delay;
        if (isVideo)
            delay = Double.parseDouble(Configuration.loadConfig("backgroundDelay", "3"));
        else
            delay = 0.5;

        App.initializeMediaExecutor();
        mediaExecutor.schedule(() -> {
            int volume = Integer.parseInt(Configuration.loadConfig("backgroundVolume", "0.4"));
            mp.setVolume((double) volume / 100);
            mp.setOnEndOfMedia(this::stopPlayer);

            Platform.runLater(() ->{
                if (isVideo){
                    double screenRatio = Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight();
                    double mediaRatio = (double) backgroundVideo.getMediaPlayer().getMedia().getWidth() / backgroundVideo.getMediaPlayer().getMedia().getHeight();

                    backgroundVideo.setPreserveRatio(true);

                    if (screenRatio > 1.8f && mediaRatio > 1.8f)
                        backgroundVideo.setPreserveRatio(false);

                    double videoWidth = mediaRatio * backgroundVideo.getFitHeight();
                    fill.setPrefWidth(Screen.getPrimary().getBounds().getWidth() - videoWidth);

                    RadialGradient shadePaint = new RadialGradient(
                            0, 1, 0.5, 0.5, 2.4, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.TRANSPARENT),
                            new Stop(0.5, Color.TRANSPARENT),
                            new Stop(0.6, Color.BLACK)
                    );

                    fill.setBackground(new Background(new BackgroundFill(Color.BLACK, null, new Insets(-10))));
                    shade.setBackground(new Background(new BackgroundFill(shadePaint, null, new Insets(-10))));

                    shade.setEffect(new BoxBlur(10, 10, 4));

                    if (mediaRatio <= screenRatio){
                        fadeInEffect(shade, 0.2f).play();
                        fadeInEffect(fill, 0.2f).play();
                    }

                    fadeInEffect(backgroundVideo, 1).play();

                }

                mp.seek(mp.getStartTime());
                mp.play();
            });
        }, (long) delay * 1000, TimeUnit.MILLISECONDS);

        mediaExecutor.shutdown();
    }
    private void stopPlayer() {
        fadeOutEffect(backgroundVideo);
        fadeOutEffect(shade, 0.3f);
        fadeOutEffect(fill, 0.3f);

        double increment = mp.getVolume() * 0.05;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.05), event -> mp.setVolume(mp.getVolume() - increment))
        );
        timeline.setCycleCount(20);
        timeline.play();

        timeline.setOnFinished(event -> {
            mp.stop();
            mediaExecutor.shutdownNow();
        });
    }
    private void findAndPlaySong(){
        if (mp != null)
            stopPlayer();

        for (Season season : seasons){
            if (!season.getMusicSrc().isEmpty()){
                Platform.runLater(() -> {
                    File file = new File(season.getMusicSrc());
                    Media media = new Media(file.toURI().toString());
                    mp = new MediaPlayer(media);
                    isVideo = false;

                    setMediaPlayer();
                });
                break;
            }
        }
    }
    //endregion
}
