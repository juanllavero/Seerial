package com.example.executablelauncher;

import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
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
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SeasonController {
    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView backgroundShadow;

    @FXML
    private ImageView backgroundShadow2;

    @FXML
    private MediaView backgroundVideo;

    @FXML
    private HBox cardContainer;

    @FXML
    private VBox detailsText;

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
    private Button optionsButton;

    @FXML
    private Label overviewField;

    @FXML
    private Button playButton;

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

    private Controller controllerParent;
    private Label nameFiledSaved = null;

    private List<Season> seasons = new ArrayList<>();
    private List<Disc> discs = new ArrayList<>();
    private List<Button> discsButtons = new ArrayList<>();
    private int currentSeason = 0;
    public String currentEpisoceID = "";
    private Disc selectedDisc = null;
    private boolean showEpisodes = false;
    private boolean optionsSelected = false;
    private boolean playSelected = false;

    private MediaPlayer mp = null;

    int pos = 0;
    final int minPos = 0;
    final int maxPos = 100;
    private boolean isVideo = false;
    private boolean playSameMusic = false;
    private boolean isShow = false;
    private Series series = null;
    private boolean episodesFocussed = true;

    public void setParent(Controller c){
        controllerParent = c;
    }

    private void updateInfo(Season season){
        if (mp != null){
            if (isVideo && !playSameMusic)
                mp.stop();
        }

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

        if (isShow){
            if (series.logoSrc.isEmpty()){
                infoBox.getChildren().remove(0);
                Label seriesTitle = new Label(series.name);
                seriesTitle.setFont(new Font("Arial", 58));
                seriesTitle.setStyle("-fx-font-weight: bold");
                seriesTitle.setTextFill(Color.color(1, 1, 1));
                seriesTitle.setEffect(new DropShadow());
                infoBox.getChildren().add(0, seriesTitle);
            }else {
                Image img;
                img = new Image("file:" + series.logoSrc, screenWidth * 0.25, screenHeight * 0.25, true, true);

                logo.setImage(img);
                logo.setFitWidth(screenWidth * 0.15);
                logo.setFitHeight(screenHeight * 0.15);
            }
        }else{
            if (season.getLogoSrc().isEmpty()){
                infoBox.getChildren().remove(0);
                Label seriesTitle = new Label(series.name);
                seriesTitle.setFont(new Font("Arial", 58));
                seriesTitle.setStyle("-fx-font-weight: bold");
                seriesTitle.setTextFill(Color.color(1, 1, 1));
                seriesTitle.setEffect(new DropShadow());
                infoBox.getChildren().add(0, seriesTitle);
            }else {
                Image img;
                img = new Image("file:" + season.logoSrc, screenWidth * 0.25, screenHeight * 0.25, true, true);

                logo.setImage(img);
                logo.setFitWidth(screenWidth * 0.15);
                logo.setFitHeight(screenHeight * 0.15);
            }
        }

        if (!season.getVideoSrc().isEmpty()){
            File file = new File(season.getVideoSrc());
            Media media = new Media(file.toURI().toString());
            mp = new MediaPlayer(media);
            backgroundVideo.setMediaPlayer(mp);
            backgroundVideo.setVisible(false);
            isVideo = true;

            normalizeVolume(file);

            setMediaPlayer();
        }else if (!season.getMusicSrc().isEmpty()){
            if (!playSameMusic || (currentSeason == 0 && mp == null)){
                File file = new File(season.getMusicSrc());
                Media media = new Media(file.toURI().toString());
                mp = new MediaPlayer(media);
                isVideo = false;

                normalizeVolume(file);

                setMediaPlayer();
            }
        }

        cardContainer.getChildren().clear();
        discsButtons.clear();
        discs.clear();
        List<String> discs = season.getDiscs();
        for (String i : discs){
            Disc d = App.findDisc(i);
            addEpisodeCard(d);
        }

        mainPane.requestFocus();

        if (discsButtons.size() > 1)
            discsButtons.get(0).requestFocus();
        else
            playButton.requestFocus();

        selectedDisc = App.findDisc(discs.get(0));

        overviewField.setText(season.overview);
        yearField.setText(season.getYear());
        scoreField.setText(String.valueOf(selectedDisc.score));
        durationField.setText(setRuntime(selectedDisc.runtime));
        episodeName.setText(season.name);

        if (!isShow){
            detailsInfo.getChildren().remove(seasonEpisodeNumber);
        }

        fadeInEffect(backgroundImage);
    }

    private String setRuntime(int runtime){
        int h = runtime / 60;
        int m = runtime % 60;

        if (h == 0)
            return (m + "m");

        return (h + "h " + m + "m");
    }

    private void setMediaPlayer(){
        mp.setOnEndOfMedia(() -> {
            mp.seek(Duration.ZERO);
            mp.play();
        });

        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(4), event -> {
                    playVideo();
                })
        );
        timeline.play();
    }

    private void updateButtons(){
        lastSeasonButton.setVisible(currentSeason != 0);
        nextSeasonButton.setVisible(currentSeason != seasons.size() - 1);
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

        menuShadow.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        menuShadow.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        menuShadow.setVisible(false);

        videoPlayerPane.setVisible(false);

        mainBox.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode() || KeyCode.BACK_SPACE == event.getCode()){
                if (detailsBox.isVisible())
                    closeDetails();
                else
                    goBack(event);
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
        backgroundShadow2.setFitWidth(screenWidth);
        backgroundShadow2.setFitHeight(screenHeight);
        episodeScroll.setPrefWidth(screenWidth);

        detailsText.setPrefWidth(screenWidth / 2);
        detailsBox.setVisible(false);

        /*episodeScroll.setOnScroll(event -> {

            if (event.getDeltaY() > 0)
                episodeScroll.setHvalue(pos == minPos ? minPos : pos--);
            else
                episodeScroll.setHvalue(pos == maxPos ? maxPos : pos++);
        });*/

        //Remove horizontal and vertical scroll
        episodeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        episodeScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        playButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                playButton.setText("Reproducir");
                ImageView img = (ImageView) playButton.getGraphic();
                img.setImage(new Image("file:src/main/resources/img/icons/playHover.png", 30, 30, true, true));
            }else{
                playButton.setText("");
                ImageView img = (ImageView) playButton.getGraphic();
                img.setImage(new Image("file:src/main/resources/img/icons/play.png", 30, 30, true, true));
            }
        });

        optionsButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                optionsButton.setText("Más");
                ImageView img = (ImageView) optionsButton.getGraphic();
                img.setImage(new Image("file:src/main/resources/img/icons/optionsSelected.png", 30, 30, true, true));
            }else{
                optionsButton.setText("");
                ImageView img = (ImageView) optionsButton.getGraphic();
                img.setImage(new Image("file:src/main/resources/img/icons/options.png", 30, 30, true, true));
            }
        });

        setEpisodesOutOfFocusButton(playButton);
        setEpisodesOutOfFocusButton(optionsButton);

        assert seasons != null;
        updateInfo(seasons.get(currentSeason));
    }

    private void playVideo(){
        Platform.runLater(() ->{
            if (mp != null) {
                if (isVideo){
                    double screenRatio = Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight();
                    double mediaRatio = (double) backgroundVideo.getMediaPlayer().getMedia().getWidth() / backgroundVideo.getMediaPlayer().getMedia().getHeight();

                    backgroundVideo.setPreserveRatio(true);

                    if (screenRatio > 1.8f && mediaRatio > 1.8f){
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
                    mp.play();
                }else{
                    mp.stop();
                    mp.seek(mp.getStartTime());
                    mp.play();
                }
            }
        });
    }

    private void normalizeVolume(File audioFile) {
        try {
            AudioInputStream audioInputStream = javax.sound.sampled.AudioSystem.getAudioInputStream(audioFile);
            TarsosDSPAudioInputStream tarsosDSPAudioInputStream = new JVMAudioInputStream(audioInputStream);
            AudioDispatcher dispatcher = new AudioDispatcher(tarsosDSPAudioInputStream, 1024, 0);

            dispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    double rms = audioEvent.getRMS();
                    double normalizedVolume = 0.5 / rms;

                    mp.setVolume(normalizedVolume);
                    return true;
                }

                @Override
                public void processingFinished() {}
            });

            // Iniciar el procesamiento del archivo de audio
            dispatcher.run();
        } catch (IOException | javax.sound.sampled.UnsupportedAudioFileException e) {
            System.err.println("normalizeVolume: Error normalizing media volume");
        }
    }

    public void addEpisodeCard(Disc disc){
        if (disc != null){
            discs.add(disc);
            Button btn = new Button();
            btn.setPadding(new Insets(0));

            btn.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    if (!episodesFocussed){
                        episodesFocussed = true;
                        enableAllEpisodes();
                    }

                    btn.setScaleX(1.1);
                    btn.setScaleY(1.1);
                    //selectSeries(collectionList.get(seriesButtons.indexOf(btn)));
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


            ImageView thumbnail = new ImageView();

            double targetHeight = Screen.getPrimary().getBounds().getHeight() / 5.5;
            double targetWidth = ((double) 16 /9) * targetHeight;

            thumbnail.setFitWidth(targetWidth);
            thumbnail.setFitHeight(targetHeight);

            File newFile = new File(disc.imgSrc);
            if (!newFile.exists())
                disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";

            Image originalImage = new Image("file:" + disc.imgSrc, targetWidth, targetHeight, true, true);

            thumbnail.setImage(originalImage);
            thumbnail.setPreserveRatio(false);
            thumbnail.setSmooth(true);

            btn.getStyleClass().add("seriesCoverButton");
            btn.setGraphic(thumbnail);

            cardContainer.getChildren().add(btn);
            discsButtons.add(btn);
        }
    }

    private void updateDiscInfo(Disc disc) {
        selectedDisc = disc;
        episodeName.setText(disc.name);
        overviewField.setText(disc.overview);
        seasonEpisodeNumber.setText(App.textBundle.getString("seasonLetter") + seasons.get(currentSeason).seasonNumber + App.textBundle.getString("episodeLetter") + disc.episodeNumber);
        yearField.setText(disc.year);
        durationField.setText(setRuntime(disc.runtime));
        scoreField.setText(String.valueOf(disc.score));
    }

    public void playEpisode(Disc disc){
        if (mp != null)
            mp.stop();

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("videoPlayer.fxml"));
            videoPlayerPane = fxmlLoader.load();

            VideoPlayerController playerController = fxmlLoader.getController();
            playerController.setVideo(disc.executableSrc);

            videoPlayerPane.setVisible(true);
            fadeInEffect(videoPlayerPane);
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

    @FXML
    void goBack(KeyEvent event){
        if (mp != null)
            mp.stop();
        //alertTimer.stop();
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

    @FXML
    void openDetails(){
        fadeOutEffect(mainPane);

        if (isShow){
            detailsTitle.setText(episodeName.getText());
            detailsImage.setImage(new Image("file:" + selectedDisc.imgSrc));
            genresField.setText(series.getGenres());
        }else{
            detailsTitle.setText(seasons.get(currentSeason).name);
            detailsImage.setImage(new Image("file:" + series.coverSrc));
            genresField.setText(seasons.get(currentSeason).getGenres());
        }
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
    }

    private void fadeOutEffect(Pane pane){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), pane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        pane.setVisible(false);
    }

    private void fadeOutEffect(ImageView img){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), img);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0);
        fadeOut.play();
        img.setVisible(false);
    }

    private void fadeInEffect(Pane pane){
        pane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void fadeInEffect(ImageView img){
        img.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), img);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void hideMenuShadow(){
        menuShadow.setVisible(false);
    }
}
