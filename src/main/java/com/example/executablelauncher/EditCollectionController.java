package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
import com.example.executablelauncher.entities.Season;
import com.example.executablelauncher.entities.Series;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class EditCollectionController {
    //region FXML ATTRIBUTES
    @FXML
    private Button cancelButton;

    @FXML
    private Button downloadImagesButton;

    @FXML
    private Button postersViewButton;

    @FXML
    private VBox generalBox;

    @FXML
    private Button generalViewButton;

    @FXML
    private Label name;

    @FXML
    private TextField nameField;

    @FXML
    private TextField orderField;

    @FXML
    private CheckBox playSameMusic;

    @FXML
    private ScrollPane posterBox;

    @FXML
    private FlowPane posterContainer;

    @FXML
    private Button saveButton;

    @FXML
    private Button selectImageButton;

    @FXML
    private Label sorting;

    @FXML
    private Label title;
    //endregion

    private DesktopViewController controllerParent;
    public Series seriesToEdit = null;
    private List<File> imagesFiles = new ArrayList<>();
    private File selectedImage = null;

    //region INITIALIZATION
    public void setSeries(Series s){
        seriesToEdit = s;

        nameField.setText(s.getName());
        playSameMusic.setSelected(s.playSameMusic);

        setImageFile(s.getCoverSrc());

        if (s.getOrder() > 0)
            orderField.setText(Integer.toString(s.getOrder()));

        //Initial values
        posterBox.setVisible(false);
        generalBox.setVisible(true);
        name.setText(App.textBundle.getString("name"));
        sorting.setText(App.textBundle.getString("sortingOrder"));
        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        title.setText(App.textBundle.getString("collectionWindowTitleEdit"));
        selectImageButton.setText(App.buttonsBundle.getString("selectImage"));
        downloadImagesButton.setText(App.buttonsBundle.getString("downloadImages"));

        showGeneralView();
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
    //endregion

    //region POSTERS
    public void setImageFile(String path){
        selectedImage = new File(path);
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

                int number = -1;

                for (File file : imagesFiles){
                    if (Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf("."))) > number){
                        number = Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf(".")));
                    }
                }

                cropperController.initValues("src/main/resources/img/seriesCovers/" + seriesToEdit.id + "/" + (number + 1) + ".png", false);
                Stage stage = new Stage();
                stage.setResizable(true);
                stage.setMaximized(false);
                stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 2);
                stage.setWidth(Screen.getPrimary().getBounds().getWidth() / 2);
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
    public void loadImage(String src){
        File file = new File(src);
        if (file.exists()) {
            selectedImage = file;
            imagesFiles.add(file);
            addImage(file);
        }
    }
    //endregion

    //region SECTIONS
    @FXML
    void showGeneralView() {
        posterBox.setVisible(false);
        generalBox.setVisible(true);

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("buttonSelected");

        postersViewButton.getStyleClass().clear();
        postersViewButton.getStyleClass().add("editButton");
    }
    @FXML
    void showPostersView() {
        posterBox.setVisible(true);
        generalBox.setVisible(false);

        postersViewButton.getStyleClass().clear();
        postersViewButton.getStyleClass().add("buttonSelected");

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("editButton");

        showImages();
    }
    private void showImages() {
        if (imagesFiles.isEmpty())
            loadImages();
    }
    private void loadImages(){
        posterContainer.getChildren().clear();
        //Add images to view
        File dir = new File("src/main/resources/img/seriesCovers/" + seriesToEdit.id);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            assert files != null;
            imagesFiles.addAll(Arrays.asList(files));

            for (File f : imagesFiles) {
                addImage(f);

                if (selectedImage != null && selectedImage.getAbsolutePath().equals(f.getAbsolutePath())){
                    selectButton((Button) posterContainer.getChildren().get(imagesFiles.indexOf(f)));
                }
            }
        }
    }
    private void addImage(File file){
        try {
            Image img = new Image(file.toURI().toURL().toExternalForm(), 125, 200, true, true);
            ImageView image = new ImageView(img);

            Button btn = new Button();
            btn.setGraphic(image);
            btn.setText("");
            btn.getStyleClass().add("downloadedImageButton");
            btn.setPadding(new Insets(2));

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                selectButton(btn);
            });

            posterContainer.getChildren().add(btn);
        } catch (MalformedURLException e) {
            System.err.println("EditCollectionController: Error loading image thumbnail");
        }
    }
    private void selectButton(Button btn){
        int index = 0;
        int i = 0;
        for (Node n : posterContainer.getChildren()){
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
    //endregion

    @FXML
    void save(MouseEvent event) {

        if (nameField.getText().isEmpty()){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("emptyField"));
            return;
        }

        if (!orderField.getText().isEmpty() && orderField.getText().matches("\\d{3,}")){
            App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("sortingError"));
            return;
        }

        seriesToEdit.setName(nameField.getText());
        seriesToEdit.playSameMusic = playSameMusic.isSelected();

        if (!orderField.getText().isEmpty() && !orderField.getText().equals("0"))
            seriesToEdit.setOrder(Integer.parseInt(orderField.getText()));

        if (selectedImage != null)
            seriesToEdit.setCoverSrc("src/main/resources/img/seriesCovers/" + seriesToEdit.id + "/" + selectedImage.getName());

        //Refresh Series Data in DesktopView
        controllerParent.hideBackgroundShadow();

        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
