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
    private TextField coverField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField orderField;

    @FXML
    private ChoiceBox<String> categoryField;

    @FXML
    private Label errorCategory;

    @FXML
    private Label imageError;

    @FXML
    private Label nameError;

    @FXML
    private Label orderError;

    public Series seriesToEdit = null;
    private DesktopViewController controllerParent;
    private File selectedFile = null;
    private String name;

    public void setSeries(Series s){
        seriesToEdit = s;

        name = s.getName();
        nameField.setText(s.getName());
        coverField.setText(s.getCoverSrc());

        categoryField.getItems().addAll(Main.getCategories());
        categoryField.setValue(s.getCategory());

        if (s.getOrder() > 0)
            orderField.setText(Integer.toString(s.getOrder()));
    }

    public void initializeCategories(){
        categoryField.getItems().addAll(Main.getCategories());
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
                stage.setTitle("Edit Series");
                stage.setAlwaysOnTop(true);
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            imageError.setText("Name field is empty");
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
            stage.setTitle("Add Category");
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

        if (Main.nameExist(nameField.getText()) && !nameField.getText().equals(name)){
            save = false;
            nameError.setText("Name exists in another collection");
        }else if (nameField.getText().isEmpty()){
            save = false;
            nameError.setText("This field cannot be empty");
        }else{
            nameError.setText("");
        }

        if (!image.exists()) {
            save = false;
            imageError.setText("Image not found");
        }else{
            String imageExtension = coverField.getText().substring(coverField.getText().length() - 3);
            imageExtension = imageExtension.toLowerCase();

            if (!imageExtension.equals("jpg") && !imageExtension.equals("png")){
                save = false;
                imageError.setText("Image has to be '.jpg' or '.png'");
            }else{
                imageError.setText("");
            }
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            save = false;
            orderError.setText("Sorting order has to be a number");
        }else{
            orderError.setText("");
        }

        if (categoryField.getValue() == null){
            save = false;
            errorCategory.setText("You must select a category");
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
