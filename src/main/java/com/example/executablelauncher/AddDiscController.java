package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.entities.EpisodeMetadata;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDiscController {
    @FXML
    private ChoiceBox<String> typeField;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField executableField;

    @FXML
    private Button loadButton;

    @FXML
    private Label source;

    @FXML
    private Label title;

    @FXML
    private Label type;

    @FXML
    private Label nameText;

    @FXML
    private TextField nameField;

    @FXML
    private FlowPane imagesContainer;

    public Disc discToEdit = null;
    private DesktopViewController controllerParent;
    private List<File> selectedFiles = null;
    private File selectedFolder = null;
    private List<File> imagesFiles = new ArrayList<>();
    private File selectedImage = null;

    public void InitValues(){
        typeField.getItems().addAll(Arrays.asList(App.textBundle.getString("file"), App.textBundle.getString("folder")));
        typeField.setValue(App.textBundle.getString("file"));
        title.setText(App.textBundle.getString("episodeWindowTitle"));
        addButton.setText(App.buttonsBundle.getString("addButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        type.setText(App.textBundle.getString("type"));
        source.setText(App.textBundle.getString("source"));
        loadButton.setText(App.buttonsBundle.getString("loadButton"));
        if (nameText != null)
            nameText.setText(App.textBundle.getString("name"));
    }

    public void setDisc(Disc d){
        discToEdit = d;
        executableField.setText(d.getExecutableSrc());
        typeField.setValue(d.getType());
        InitValues();
        title.setText(App.textBundle.getString("episodeWindowTitleEdit"));
        addButton.setText(App.buttonsBundle.getString("saveButton"));
        nameField.setText(d.getName());
        selectedImage = new File(d.imgSrc);

        showImages();
    }

    private void showImages(){
        //Add images to view
        File dir = new File("src/main/resources/img/discCovers/");
        File[] files = dir.listFiles();
        assert files != null;
        for (File f : files){
            String[] name = f.getName().split("_");
            if (name[0].equals(Integer.toString(discToEdit.getId()))){
                imagesFiles.add(f);
            }
        }

        for (File f : imagesFiles){
            try{
                Image img = new Image(f.toURI().toURL().toExternalForm(), 150, 84, true, true);
                Button btn = new Button();
                ImageView image = new ImageView(img);
                HBox imageContainer = new HBox();
                imageContainer.setAlignment(Pos.CENTER);
                imageContainer.setPrefWidth(150);
                imageContainer.setPrefHeight(84);
                imageContainer.getChildren().add(image);
                imageContainer.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

                btn.setGraphic(imageContainer);
                btn.setText("");
                btn.getStyleClass().add("downloadedImageButton");
                btn.setPadding(new Insets(2));

                btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                    selectButton(btn);
                });

                imagesContainer.getChildren().add(btn);
            } catch (MalformedURLException e) {
                System.err.println("AddDiscController: Error loading image thumbnail");
            }


        }
    }

    private void selectButton(Button btn){
        int index = 0;
        int i = 0;
        for (Node n : imagesContainer.getChildren()){
            Button b = (Button)n;
            b.getStyleClass().clear();
            b.getStyleClass().add("downloadedImageButton");
            if (b == btn)
                index = i;
            i++;
        }

        btn.getStyleClass().clear();
        btn.getStyleClass().add("downloadedImageButtonSelected");

        selectedImage = imagesFiles.get(index);
    }

    public void setParentController(DesktopViewController controller){
        controllerParent = controller;
    }

    @FXML
    void loadExe(MouseEvent event) {
        if (typeField.getValue().equals(App.textBundle.getString("folder"))){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            selectedFolder = directoryChooser.showDialog((Stage)((Button) event.getSource()).getScene().getWindow());
            executableField.setText(selectedFolder.getAbsolutePath());
        }else{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(App.textBundle.getString("selectFile"));
            fileChooser.setInitialDirectory(new File("C:\\"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(App.textBundle.getString("allFiles"), "*.mkv", "*.mp4", "*.iso", "*.ISO", "*.m2ts", "*.exe", "*.bat"));
            if (discToEdit != null) {
                selectedFolder = fileChooser.showOpenDialog((Stage) ((Button) event.getSource()).getScene().getWindow());
                executableField.setText(selectedFolder.getAbsolutePath());
            }else {
                selectedFiles = fileChooser.showOpenMultipleDialog((Stage) ((Button) event.getSource()).getScene().getWindow());
                executableField.setText(App.textBundle.getString("multipleSelection"));
            }
        }
    }

    @FXML
    void cancelButton(MouseEvent event) {
        controllerParent.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(MouseEvent event) {
        boolean save = true;

        //Get file/files
        File exe = new File(executableField.getText());
        if (!executableField.getText().equals(App.textBundle.getString("multipleSelection")) && !exe.exists()){
            save = false;
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("fileNotFound"));
        }else if (!executableField.getText().equals(App.textBundle.getString("multipleSelection")) && selectedFolder != null){
            if (selectedFolder.exists()){
                String fileExtension = executableField.getText().substring(executableField.getText().length() - 4);
                fileExtension = fileExtension.toLowerCase();

                if (!fileExtension.equals(".mkv") && !fileExtension.equals(".mp4") && !fileExtension.equals("m2ts")
                        && !fileExtension.equals(".iso") && !fileExtension.equals(".exe") && !fileExtension.equals(".bat")){
                    save = false;
                    App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("extensionNotAllowed"));
                }
            }
        }

        if (save){
            Disc disc;

            disc = Objects.requireNonNullElseGet(discToEdit, Disc::new);

            if (selectedImage != null){
                disc.name = nameField.getText();
                disc.imgSrc = "src/main/resources/img/discCovers/" + selectedImage.getName();
            }else{
                if (executableField.getText().equals(App.textBundle.getString("multipleSelection")))
                    for (File file : selectedFiles)
                        setDiscInfo(disc, file, false);
                else
                    setDiscInfo(disc, selectedFolder, true);

                if (discToEdit != null)
                    discToEdit = null;
            }

            controllerParent.updateDiscView();
            controllerParent.hideBackgroundShadow();

            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    private void setDiscInfo(Disc newDisc, File file, boolean folder) {
        if (discToEdit == null)
            newDisc = new Disc();

        newDisc.setSeasonID(controllerParent.getCurrentSeason().getId());

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
                    setEpisodeName(newDisc, "NO_SEASON", newMatch.group("episode"));
                    newDisc.setEpisodeNumber(newMatch.group("episode"));
                }else{
                    newDisc.setName(fullName);
                    newDisc.setEpisodeNumber("");
                }
            }else{
                setEpisodeName(newDisc, matcher.group("season").substring(1), matcher.group("episode").substring(1));
                newDisc.setEpisodeNumber(matcher.group("episode").substring(1));
            }
        }else{
            newDisc.setName(App.textBundle.getString("disc") + " " + Objects.requireNonNull(App.findSeason(newDisc.getSeasonID())).getDiscs().size() + 1);
            newDisc.setEpisodeNumber("");
        }

        newDisc.setType(typeField.getValue());
        newDisc.setExecutableSrc(file.getAbsolutePath());

        App.addDisc(newDisc);
    }

    private void setEpisodeName(Disc disc, String season, String episode){
        Season s = App.findSeason(disc.seasonID);
        assert s != null;
        Series series = App.findSeriesByName(s.collectionName);
        assert series != null;
        String tvdbId = String.valueOf(series.thetvdbID);

        System.out.println("Series tvdbID: " + tvdbId);
        System.out.println("Episode to find: " + episode);

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
                //*************************************************************************************************     Set video thumbnail
                //setVideoThumbnail();
                return;
            }

            disc.name = episodeMeta.name;

            setEpisodeThumbnail(disc, episodeMeta.imdbID);
        }
    }

    private void setEpisodeThumbnail(Disc disc, String imdbID){
        if (imdbID.isEmpty()){
            //setVideoThumbnail();
            return;
        }

        try{
            String imdbBase = "https://www.imdb.com/title/";
            String mediaAll = "/mediaindex/?ref_=tt_mv_sm";
            String posterSrc = null;

            Document doc = Jsoup.connect(imdbBase + imdbID + mediaAll).timeout(6000).get();
            Elements body = doc.select("div.media_index_thumb_list");
            List<String> imagesUrls = new ArrayList<>();
            for (Element element : body){
                Elements elements = element.select("a");
                int i = 0;
                for (Element e : elements){
                    if (i == 8)
                        break;

                    if (i != 0)
                        imagesUrls.add("https://www.imdb.com" + e.attr("href"));
                    i++;
                }
                break;
            }

            int i = 0;
            for (String url : imagesUrls){
                doc = Jsoup.connect(url).timeout(2000).get();
                body = doc.select("div.media-viewer");
                for (Element element : body){
                    posterSrc = element.select("img").attr("src");
                }

                if (posterSrc != null){
                    Image img = new Image(posterSrc);

                    if (img.isError()){
                        return;
                    }

                    File file = new File("src/main/resources/img/discCovers/" + disc.id + "_" + i + ".png");
                    try{
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(img, null);
                        ImageIO.write(renderedImage,"png", file);
                    } catch (IOException e) {
                        System.err.println("Disc downloaded thumbnail not saved");
                        return;
                    }
                }
                i++;
            }

            File img = new File("src/main/resources/img/discCovers/" + disc.id + "_0.png");
            if (!img.exists()){
                //setVideoThumbnail()
                return;
            }

            disc.imgSrc = "src/main/resources/img/discCovers/" + disc.id + "_0.png";
        } catch (IOException e) {
            System.err.println("AddDiscController: Error connecting to IMDB");
        }

        /*try{
            String imdbBase = "https://www.imdb.com/title/";
            String imdbPoster = "/mediaviewer";
            String mediaAll = "/mediaindex/?ref_=tt_mv_sm";
            String posterSrc = null;

            Document doc = Jsoup.connect(imdbBase + imdbID + imdbPoster).timeout(6000).get();
            Elements body = doc.select("div.media-viewer");
            for (Element element : body){
                posterSrc = element.select("img").attr("src");
            }

            if (posterSrc != null){
                Image img = new Image(posterSrc);

                if (img.isError()){
                    //setVideoThumbnail();
                    return;
                }

                File file = new File("src/main/resources/img/discCovers/" + disc.id + ".png");
                try{
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(img, null);
                    ImageIO.write(renderedImage,"png", file);
                } catch (IOException e) {
                    System.err.println("Disc downloaded thumbnail not saved");
                    return;
                }

                disc.imgSrc = "src/main/resources/img/discCovers/" + disc.id + ".png";
            }
        } catch (IOException e) {
            System.err.println("AddDiscController: Error connecting to IMDB");
        }*/
    }
}
