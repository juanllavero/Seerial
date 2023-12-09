package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AddCollectionController {
    @FXML
    private Button cancelButton;

    @FXML
    private Label category;

    @FXML
    private ChoiceBox<String> categoryField;

    @FXML
    private TextField coverField;

    @FXML
    private Label errorCategory;

    @FXML
    private Label imageError;

    @FXML
    private Label name;

    @FXML
    private Label nameError;

    @FXML
    private TextField nameField;

    @FXML
    private Label orderError;

    @FXML
    private TextField orderField;

    @FXML
    private Button saveButton;

    @FXML
    private Label sorting;

    @FXML
    private Label title;

    public Series seriesToEdit = null;
    private DesktopViewController controllerParent;
    private File selectedFile = null;
    private String nameString;


    public void setSeries(Series s){
        seriesToEdit = s;

        nameString = s.getName();
        nameField.setText(s.getName());
        coverField.setText(s.getCoverSrc());

        categoryField.getItems().addAll(Main.getCategories());
        categoryField.setValue(s.getCategory());

        if (s.getOrder() > 0)
            orderField.setText(Integer.toString(s.getOrder()));

        title.setText(Main.textBundle.getString("collectionWindowTitleEdit"));
        initValues();
    }

    public void initializeCategories(){
        categoryField.getItems().addAll(Main.getCategories());
        title.setText(Main.textBundle.getString("collectionWindowTitle"));
        initValues();
    }

    private void initValues() {
        saveButton.setText(Main.buttonsBundle.getString("saveButton"));
        cancelButton.setText(Main.buttonsBundle.getString("cancelButton"));
        name.setText(Main.textBundle.getString("name"));
        category.setText(Main.textBundle.getString("category"));
        sorting.setText(Main.textBundle.getString("sortingOrder"));
    }

    public void setParentController(DesktopViewController controller){
        controllerParent = controller;
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
                cropperController.initValues(this, "src/main/resources/img/seriesCovers/" + nameField.getText() + "_cover.png");
                Stage stage = new Stage();
                stage.setResizable(true);
                stage.setMaximized(false);
                stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 1.5);
                stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 1.5);
                stage.setTitle(Main.textBundle.getString("imageCropper"));
                stage.setAlwaysOnTop(true);
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            imageError.setText(Main.textBundle.getString("emptyField"));
        }
    }

    public void setImageFile(String path){
        coverField.setText(path);
    }

    @FXML
    void addCategory(MouseEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addCategory-view.fxml"));
            Parent root1 = fxmlLoader.load();
            AddCategoryController addCategoryController = fxmlLoader.getController();
            addCategoryController.setParent(controllerParent);
            Stage stage = new Stage();
            stage.setTitle(Main.textBundle.getString("categoryWindowTitle"));
            stage.setAlwaysOnTop(true);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void save(MouseEvent event) {
        File image = new File(coverField.getText());
        boolean save = true;

        if (Main.nameExist(nameField.getText()) && !nameField.getText().equals(nameString)){
            save = false;
            nameError.setText(Main.textBundle.getString("collectionExists"));
        }else if (nameField.getText().isEmpty()){
            save = false;
            nameError.setText(Main.textBundle.getString("emptyField"));
        }else{
            nameError.setText("");
        }

        if (!image.exists()) {
            save = false;
            imageError.setText(Main.textBundle.getString("imageNotFound"));
        }else{
            String imageExtension = coverField.getText().substring(coverField.getText().length() - 3);
            imageExtension = imageExtension.toLowerCase();

            if (!imageExtension.equals("jpg") && !imageExtension.equals("png")){
                save = false;
                imageError.setText(Main.textBundle.getString("imageErrorFormat"));
            }else{
                imageError.setText("");
            }
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            save = false;
            orderError.setText(Main.textBundle.getString("sortingError"));
        }else{
            orderError.setText("");
        }

        if (categoryField.getValue() == null){
            categoryField.setValue("NO CATEGORY");
            errorCategory.setText("");
        }else{
            errorCategory.setText("");
        }

        if (save){
            if (selectedFile == null)
                selectedFile = new File(coverField.getText());

            String extension = "";

            int i = selectedFile.getName().lastIndexOf('.');
            if (i > 0) {
                extension = selectedFile.getName().substring(i+1);
            }
            File newCover = new File("src/main/resources/img/seriesCovers/"+ nameField.getText() + "_cover.png");

            try{
                Files.copy(selectedFile.toPath(), newCover.toPath());
            }catch (IOException e){
                System.err.println("Image not copied");
            }

            Series series;

            if (seriesToEdit != null){
                series = seriesToEdit;
            }else{
                series = new Series();
            }

            series.setName(nameField.getText());
            series.setCoverSrc(newCover.getAbsolutePath());

            if (!orderField.getText().isEmpty() && !orderField.getText().equals("0")){
                series.setOrder(Integer.parseInt(orderField.getText()));
            }

            series.setCategory(categoryField.getValue());

            if (seriesToEdit != null){
                seriesToEdit = null;
            }else{
                Main.addCollection(series);
                controllerParent.addSeries(series);
            }

            controllerParent.hideBackgroundShadow();

            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
}
