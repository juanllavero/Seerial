package com.example.executablelauncher;

import com.example.executablelauncher.entities.Series;
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Objects;

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

    public void setMetadata(String name, String url){
        nameField.setText(name);
        System.out.println(url);
    }

    @FXML
    void cancelButton(MouseEvent event) {
        controllerParent.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void loadImage(MouseEvent event){
        if (!nameField.getText().isEmpty()){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("image-cropper-view.fxml"));
                Parent root1 = fxmlLoader.load();
                ImageCropper cropperController = fxmlLoader.getController();
                cropperController.setCollectionParent(this);
                cropperController.initValues("src/main/resources/img/seriesCovers/" + nameField.getText() + "_cover.png", false);
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

    public void setImageFile(String path){
        selectedFile = new File(path);
        try{
            Image img = new Image(selectedFile.toURI().toURL().toExternalForm());
            resolutionText.setText(img.getWidth() + "x" + img.getHeight());
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

    }

    @FXML
    void removeCover(ActionEvent event) {
        selectedFile = null;
        coverImageView.setImage(null);
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
        File image = new File(coverSrc);

        if (App.nameExist(nameField.getText()) && !nameField.getText().equals(nameString)){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("collectionExists"));
            return;
        }else if (nameField.getText().isEmpty()){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("emptyField"));
            return;
        }

        if (!image.exists()) {
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("imageNotFound"));
            return;
        }else{
            String imageExtension = image.getPath().substring(image.getPath().length() - 3);
            imageExtension = imageExtension.toLowerCase();

            if (!imageExtension.equals("jpg") && !imageExtension.equals("png"))
                return;
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("sortingError"));
            return;
        }

        if (categoryField.getValue() == null)
            categoryField.setValue("NO CATEGORY");

        if (selectedFile == null)
            selectedFile = image;

        File newCover = new File("src/main/resources/img/seriesCovers/"+ nameField.getText() + "_cover.png");

        try{
            Files.copy(selectedFile.toPath(), newCover.toPath());
        }catch (IOException e){
            System.err.println("Image not copied");
        }

        Series series;

        series = Objects.requireNonNullElseGet(seriesToEdit, Series::new);

        series.setName(nameField.getText());
        series.setCoverSrc("src/main/resources/img/seriesCovers/" + newCover.getName());

        if (!orderField.getText().isEmpty() && !orderField.getText().equals("0")){
            series.setOrder(Integer.parseInt(orderField.getText()));
        }

        series.setCategory(categoryField.getValue());

        if (seriesToEdit != null){
            seriesToEdit = null;
        }else{
            App.addCollection(series);
            controllerParent.addSeries(series);
        }

        controllerParent.hideBackgroundShadow();

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
