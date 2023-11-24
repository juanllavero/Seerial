package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
    private Button saveButton;

    @FXML
    private Label imageError;
    @FXML
    private Label nameError;

    public Series seriesToEdit = null;
    private CardController cardControllerParent;
    private Controller controllerParent;
    private File selectedFile = null;
    private String name;

    public void setSeries(Series s){
        seriesToEdit = s;

        name = s.getName();
        nameField.setText(s.getName());
        coverField.setText(s.getCoverSrc());
    }

    public void setParentCardController(CardController cardController){
        cardControllerParent = cardController;
    }

    public void setParentController(Controller controller){
        controllerParent = controller;
    }

    @FXML
    void cancelImage(MouseEvent event) {
        Stage stage = (Stage) ((ImageView)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancelButton(MouseEvent event) {
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

        if (save){
            if (selectedFile == null)
                selectedFile = new File(coverField.getText());

            String extension = "";

            int i = selectedFile.getName().lastIndexOf('.');
            if (i > 0) {
                extension = selectedFile.getName().substring(i+1);
            }
            File newCover = new File("src/main/resources/img/seriesCovers/"+ nameField.getText() + "_cover." + extension);

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

            if (seriesToEdit != null){
                cardControllerParent.setData(series);
                seriesToEdit = null;
            }else{
                Main.addCollection(series);
                controllerParent.addSeries(series);
            }

            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
}
