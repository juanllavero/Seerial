package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.entities.Season;
import javafx.animation.FadeTransition;
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

import java.io.*;
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
    private VBox episodeSection;

    @FXML
    private ImageView episodeShadow;

    @FXML
    private ImageView episodeShadow2;

    @FXML
    private ImageView episodeShadow3;

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
    private Label yearField;

    @FXML
    private Button playButton;

    @FXML
    private Button optionsButton;

    private Controller controllerParent;
    private Label nameFiledSaved = null;

    private List<Season> seasons = new ArrayList<>();
    private List<Disc> discs = new ArrayList<>();
    private List<Pane> discsButtons = new ArrayList<>();
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
    private List<DiscController> discsControllers = new ArrayList<>();

    public void setParent(Controller c){
        controllerParent = c;
    }

    private void updateInfo(Season season){
        if (mp != null){
            if (isVideo && !playSameMusic)
                mp.stop();
        }

        if (!season.showName && infoBox.getChildren().size() == 4){
            infoBox.getChildren().remove(1);
        }else if (season.showName && infoBox.getChildren().size() == 3){
            infoBox.getChildren().add(1, nameFiledSaved);
            nameField.setText(season.getName());
        }
        yearField.setText(season.getYear());

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

        if (season.getLogoSrc().isEmpty()){
            infoBox.getChildren().remove(0);
            Label seriesTitle = new Label(App.findSeries(season.getSeriesID()).name);
            seriesTitle.setFont(new Font("Arial", 58));
            seriesTitle.setStyle("-fx-font-weight: bold");
            seriesTitle.setTextFill(Color.color(1, 1, 1));
            seriesTitle.setEffect(new DropShadow());
            infoBox.getChildren().add(0, seriesTitle);
        }else {
            Image logo = new Image("file:" + season.getLogoSrc(), screenWidth * 0.25, screenHeight * 0.25, true, true);
            logoImage.setImage(logo);
            logoImage.setFitWidth(screenWidth * 0.15);
            logoImage.setFitHeight(screenHeight * 0.15);
        }

        if (!season.getVideoSrc().isEmpty()){
            File file = new File(season.getVideoSrc());
            Media media = new Media(file.toURI().toString());
            mp = new MediaPlayer(media);
            backgroundVideo.setMediaPlayer(mp);
            backgroundVideo.setVisible(false);
            isVideo = true;

            setMediaPlayer();
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
        discsControllers.clear();
        discsButtons.clear();
        discs.clear();
        List<String> discs = season.getDiscs();
        for (String i : discs){
            Disc d = App.findDisc(i);
            addEpisodeCard(d);
        }

        //Fade In Transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        episodeSection.setTranslateY(episodeShadow.getFitHeight());
        infoBox.setTranslateY(episodeShadow.getFitHeight() / 2);

        selectPlayButton();
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

    public void setSeasons(List<String> seasonList, boolean playSameMusic){
        this.playSameMusic = playSameMusic;
        if (seasons != null){
            for (String id : seasonList){
                seasons.add(App.findSeason(id));
            }
        }

        nameFiledSaved = nameField;

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
            }else if (KeyCode.ENTER == event.getCode()) {
                System.out.println("SSDASD");
                if (playSelected)
                    selectedDisc = discs.get(0);
                if (selectedDisc != null)
                    playEpisode(selectedDisc);
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
        episodeScroll.prefHeightProperty().bind(episodeSection.heightProperty());
        episodeScroll.setPrefWidth(screenWidth);
        cardContainer.prefHeightProperty().bind(episodeScroll.heightProperty());
        cardContainer.prefWidthProperty().bind(episodeScroll.widthProperty());

        episodeShadow.fitWidthProperty().bind(episodeSection.widthProperty());
        episodeShadow.setFitHeight(500);
        episodeShadow2.fitWidthProperty().bind(episodeSection.widthProperty());
        episodeShadow2.setFitHeight(500);
        episodeShadow3.fitWidthProperty().bind(episodeSection.widthProperty());
        episodeShadow3.setFitHeight(500);

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
                cardBox.getStyleClass().add("selectedDisc");
                cardBox.setPadding(new Insets(1));

                discsControllers.add(discController);

                cardContainer.getChildren().add(cardBox);
                discsButtons.add(cardBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void playEpisode(Disc disc){
        mp.stop();

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
            mp.play();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error playing episode in DesktopViewController");
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
    void showEpisodes(){
        TranslateTransition slide = new TranslateTransition();
        TranslateTransition slideInfo = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.3));
        slideInfo.setDuration(Duration.seconds(0.3));
        slide.setNode(episodeSection);
        slideInfo.setNode(infoBox);

        if (showEpisodes) {
            slide.setToY(episodeSection.getHeight());
            slideInfo.setToY(episodeSection.getHeight() / 2);
        }else {
            slide.setToY(0);
            slideInfo.setToY(0);
        }

        slideInfo.play();
        slide.play();

        if (showEpisodes) {
            episodeSection.setTranslateY(0);
            infoBox.setTranslateY(episodeSection.getHeight() / 2);
        }else {
            episodeSection.setTranslateY(episodeSection.getHeight());
            infoBox.setTranslateY(0);
        }
        showEpisodes = !showEpisodes;
    }

    void play(){
        if (!seasons.get(currentSeason).getDiscs().isEmpty()){
            System.out.println("AAA");
            if (currentEpisoceID.isEmpty()){
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
        node.getStyleClass().add("selectedDiscActive");
        discsControllers.get(0).selectDiscFullScreen();
    }

    public void selectPrevDisc(){
        int index = getDiscIndex(selectedDisc);
        if (index > 0){
            Pane node = discsButtons.get(index);
            node.getStyleClass().clear();
            node.getStyleClass().add("selectedDisc");
            discsControllers.get(index).clearSelection();

            selectedDisc = discs.get(--index);

            node = discsButtons.get(index);
            node.getStyleClass().clear();
            node.getStyleClass().add("selectedDiscActive");
            discsControllers.get(index).selectDiscFullScreen();

            if (node.getLayoutX() + cardContainer.getLayoutX() + cardContainer.getTranslateX() < 827) {
                episodeScroll.setHvalue(episodeScroll.getHvalue() - 1);
            }
        }
    }

    public void selectNextDisc(){
        int index = getDiscIndex(selectedDisc);
        if (index != -1 && index < discsButtons.size() - 1){
            Pane node = discsButtons.get(index);
            node.getStyleClass().clear();
            node.getStyleClass().add("selectedDisc");
            discsControllers.get(index).clearSelection();

            selectedDisc = discs.get(++index);

            node = discsButtons.get(index);
            node.getStyleClass().clear();
            node.getStyleClass().add("selectedDiscActive");
            discsControllers.get(index).selectDiscFullScreen();

            if (node.getLayoutX() + cardContainer.getLayoutX() + cardContainer.getTranslateX() > episodeScroll.getViewportBounds().getWidth()) {
                episodeScroll.setHvalue(episodeScroll.getHvalue() + 1);
            }
        }
    }

    public void deselectDisc(){
        int index = getDiscIndex(selectedDisc);
        if (index != -1){
            discsControllers.get(index).clearSelection();
            Pane node = discsButtons.get(index);
            node.getStyleClass().clear();
            node.getStyleClass().add("selectedDisc");
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
