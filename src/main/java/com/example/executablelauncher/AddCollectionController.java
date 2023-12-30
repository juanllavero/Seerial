package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class AddCollectionController {
    @FXML
    private ImageView coverImageView;

    @FXML
    private Label resolutionText;

    @FXML
    private Button cancelButton;

    @FXML
    private Label category;

    @FXML
    private ChoiceBox<String> categoryField;

    @FXML
    private Label coverText;

    @FXML
    private Label name;

    @FXML
    private TextField nameField;

    @FXML
    private TextField orderField;

    @FXML
    private Button saveButton;

    @FXML
    private Button searchButton;

    @FXML
    private Label sorting;

    @FXML
    private Label title;

    public Series seriesToEdit = null;
    private DesktopViewController controllerParent;
    private File selectedFile = null;
    private String nameString;
    private String coverSrc = "";
    private String oldCoverPath = "";
    private long tvdbID = -1;

    public void setSeries(Series s){
        seriesToEdit = s;

        nameString = s.getName();
        nameField.setText(s.getName());

        oldCoverPath = s.getCoverSrc();
        setImageFile(s.getCoverSrc());

        categoryField.getItems().addAll(App.getCategories());
        categoryField.setValue(s.getCategory());

        if (s.getOrder() > 0)
            orderField.setText(Integer.toString(s.getOrder()));

        title.setText(App.textBundle.getString("collectionWindowTitleEdit"));
        initValues();
    }

    public void initializeCategories(){
        categoryField.getItems().addAll(App.getCategories());
        title.setText(App.textBundle.getString("collectionWindowTitle"));
        initValues();
    }

    private void initValues() {
        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        name.setText(App.textBundle.getString("name"));
        category.setText(App.textBundle.getString("category"));
        sorting.setText(App.textBundle.getString("sortingOrder"));
    }

    public void setParentController(DesktopViewController controller){
        controllerParent = controller;
    }

    public void setMetadata(String name, String url, long tvdbID){
        nameField.setText(name);
        this.tvdbID = tvdbID;

        if (!url.isEmpty()){
            Image img = new Image(url);

            if (img.isError())
                return;

            File file = new File("src/main/resources/img/DownloadCache/coverFromUrl.png");
            try{
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(img, null);
                ImageIO.write(renderedImage,"png", file);
            } catch (IOException e) {
                System.err.println("Image not saved");
                return;
            }

            setImageFile("src/main/resources/img/DownloadCache/coverFromUrl.png");
        }
    }

    @FXML
    void cancelButton(MouseEvent event) {
        controllerParent.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setImageFile(String path){
        selectedFile = new File(path);
        try{
            Image img = new Image(selectedFile.toURI().toURL().toExternalForm());
            resolutionText.setText((int) img.getWidth() + "x" + (int) img.getHeight());
            coverImageView.setImage(img);
            coverSrc = path;
        } catch (MalformedURLException e) {
            System.err.println("AddCollectionController: Error loading new cover");
        }
    }

    @FXML
    void downloadCover(ActionEvent event) {

    }

    @FXML
    void loadImage(ActionEvent event) {
        if (!nameField.getText().isEmpty()){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("image-cropper-view.fxml"));
                Parent root1 = fxmlLoader.load();
                ImageCropper cropperController = fxmlLoader.getController();
                cropperController.setCollectionParent(this);
                cropperController.initValues("src/main/resources/img/DownloadCache/cover.png", false);
                Stage stage = new Stage();
                stage.setResizable(true);
                stage.setMaximized(false);
                stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
                stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
                stage.setTitle(App.textBundle.getString("imageCropper"));
                App.setPopUpProperties(stage);
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("emptyField"));
        }
    }

    @FXML
    void removeCover(ActionEvent event) {
        selectedFile = null;
        coverImageView.setImage(null);
        if (seriesToEdit != null)
            seriesToEdit.setCoverSrc("");
        oldCoverPath = "";
    }

    @FXML
    void addCategory(MouseEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCategory-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddCategoryController addCategoryController = fxmlLoader.getController();
            addCategoryController.setParent(controllerParent);
            Stage stage = new Stage();
            stage.setTitle(App.textBundle.getString("categoryWindowTitle"));
            stage.setAlwaysOnTop(true);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void searchSeries(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchSeries-view.fxml"));
            Parent root1 = fxmlLoader.load();
            SearchSeriesController controller = fxmlLoader.getController();
            controller.setParent(this);
            controller.initValues(nameField.getText());
            Stage stage = new Stage();
            stage.setTitle("Search Series");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setAlwaysOnTop(true);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void save(MouseEvent event) {

        if (App.nameExist(nameField.getText()) && !nameField.getText().equals(nameString)){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("collectionExists"));
            return;
        }else if (nameField.getText().isEmpty()){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("emptyField"));
            return;
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("sortingError"));
            return;
        }

        if (categoryField.getValue() == null)
            categoryField.setValue("NO CATEGORY");

        if (selectedFile == null){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("selectImage"));
            return;
        }

        Series series;

        series = Objects.requireNonNullElseGet(seriesToEdit, Series::new);

        series.setName(nameField.getText());
        series.thetvdbID = tvdbID;

        if (!series.getSeasons().isEmpty()){
            for (long s : series.getSeasons()){
                Season season = App.findSeason(s);
                if (season != null){
                    season.setCollectionName(nameField.getText());
                }
            }
        }

        //region SAVE COVER
        String newName = "";
        String oldName = "";
        if (selectedFile != null)
            newName = selectedFile.getName();
        if (!oldCoverPath.isEmpty())
            oldName = oldCoverPath.substring(oldCoverPath.lastIndexOf("/")+1);
        if (oldCoverPath.isEmpty() || !newName.equals(oldName)){
            try {
                File f = new File(series.getCoverSrc());
                if (f.exists())
                    Files.delete(FileSystems.getDefault().getPath(series.getCoverSrc()));
                File newCover = new File("src/main/resources/img/seriesCovers/"+ series.getId() + "_cover.png");
                try{
                    Files.copy(selectedFile.toPath(), newCover.toPath(), REPLACE_EXISTING);
                }catch (IOException e){
                    System.err.println("Cover not copied");
                }
                series.setCoverSrc("src/main/resources/img/seriesCovers/" + newCover.getName());
            } catch (IOException e) {
                System.err.println("Cover not deleted");
            }
        }
        //endregion

        if (!orderField.getText().isEmpty() && !orderField.getText().equals("0")){
            series.setOrder(Integer.parseInt(orderField.getText()));
        }

        series.setCategory(categoryField.getValue());

        if (seriesToEdit == null){
            App.addCollection(series);
            controllerParent.addSeries(series);
        }else{
            if (!seriesToEdit.getSeasons().isEmpty() && seriesToEdit.thetvdbID != -1){
                for (long seasonId : seriesToEdit.getSeasons()){
                    Season season = App.findSeason(seasonId);
                    if (season != null){
                        if (!season.getDiscs().isEmpty()){
                            for (long discId : season.getDiscs()){
                                Disc disc = App.findDisc(discId);
                                if (disc != null){
                                    if (disc.imgSrc.equals("src/main/resources/img/Default_video_thumbnail.jpg") && !disc.type.equals("folder")){
                                        File file = new File(disc.executableSrc);
                                        String fullName = file.getName().substring(0, file.getName().length() - 4);

                                        final String regexSeasonEpisode = "(?i)(?<season>S[0-9]{1,3}+)(?<episode>E[0-9]{1,4})";
                                        final String regexOnlyEpisode = "(?i)(?<episode>[0-9]{1,4})";

                                        final Pattern pattern = Pattern.compile(regexSeasonEpisode, Pattern.MULTILINE);
                                        final Matcher matcher = pattern.matcher(fullName);

                                        if (!matcher.find()){
                                            Pattern newPattern = Pattern.compile(regexOnlyEpisode, Pattern.MULTILINE);
                                            Matcher newMatch = newPattern.matcher(fullName);

                                            if (newMatch.find()){
                                                controllerParent.setEpisodeNameAndThumbnail(disc, "NO_SEASON", newMatch.group("episode"));
                                                disc.setEpisodeNumber(newMatch.group("episode"));
                                            }
                                        }else{
                                            controllerParent.setEpisodeNameAndThumbnail(disc, matcher.group("season").substring(1), matcher.group("episode").substring(1));
                                            disc.setEpisodeNumber(matcher.group("episode").substring(1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        controllerParent.hideBackgroundShadow();

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
