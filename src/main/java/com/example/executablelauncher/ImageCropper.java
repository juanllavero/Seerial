package com.example.executablelauncher;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageCropper {
    @FXML
    private Button cropButton;

    @FXML
    private Button downloadButton;

    @FXML
    private TextField imageText;

    @FXML
    private Button loadButton;

    @FXML
    private BorderPane mainBox;

    @FXML
    private ImageView mainImageView;

    @FXML
    private Group selectionGroup;

    @FXML
    private TextField urlText;
    private Image originalImage;
    private Image mainImage;
    private boolean isAreaSelected = false;
    private final AreaSelection areaSelection = new AreaSelection();

    private double fixedWidth = 0;
    private double fixedHeight = 0;
    private double aspectRatio = (double) 7/9;
    private double screenRatio = 0;
    private double originalWidth = 0;
    private double originalHeight = 0;
    private double newWidth = 0;
    private double newHeight = 0;
    public String savePath;
    private WritableImage imageToSave = null;
    private WritableImage croppedImage = null;
    private EditCollectionController parentController = null;
    private EditSeasonController seasonController = null;

    public void setSeasonParent(EditSeasonController s){ seasonController = s; }

    public void setCollectionParent(EditCollectionController s){ parentController = s; }

    public void initValues(String path, boolean isBackground) {
        savePath = path;

        screenRatio = Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight();

        if (isBackground)
            aspectRatio = screenRatio;

        urlText.setPromptText(App.textBundle.getString("urlText"));
        downloadButton.setText(App.buttonsBundle.getString("downloadButton"));
        loadButton.setText(App.buttonsBundle.getString("loadImageButton"));
        cropButton.setText(App.buttonsBundle.getString("cropImageButton"));

        fixedWidth = Screen.getPrimary().getBounds().getWidth();
        fixedHeight = Screen.getPrimary().getBounds().getHeight();
    }

    private void setImage(){
        originalHeight = originalImage.getHeight();
        originalWidth = originalImage.getWidth();

        double originalRatio = originalWidth / originalHeight;

        newHeight = Screen.getPrimary().getBounds().getHeight() / 2;
        newWidth = newHeight * originalRatio;

        mainImage = new Image(originalImage.getUrl(), newWidth, newHeight, false, true);

        //Show Image
        mainImageView.setFitHeight(newHeight);
        mainImageView.setFitWidth(newWidth);
        mainImageView.setImage(mainImage);
        areaSelection.selectArea(selectionGroup);
    }

    @FXML
    void downloadImage(ActionEvent event) {
        if (!urlText.getText().isEmpty()){
            originalImage = new Image(urlText.getText());
            if (originalImage.isError()) {
                System.out.println("Error loading image from " + urlText.getText());
            } else {
                clearSelection(selectionGroup);
                setImage();
            }
        }
    }

    @FXML
    void loadImage(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(App.textBundle.getString("selectImage"));
        if (App.lastDirectory != null && Files.exists(Path.of(App.lastDirectory)))
            fileChooser.setInitialDirectory(new File(new File(App.lastDirectory).getParentFile().getAbsolutePath()));
        else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(App.textBundle.getString("allImages"), "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            App.lastDirectory = selectedFile.getPath().substring(0, (selectedFile.getPath().length() - selectedFile.getName().length()));
            imageText.setText(selectedFile.getAbsolutePath());
            try{
                originalImage = new Image(selectedFile.toURI().toURL().toExternalForm());
                clearSelection(selectionGroup);
                setImage();
            } catch (MalformedURLException e) {
                System.err.println("ImageCropper: Error loading image");
            }
        }
    }

    public void loadImageToCrop(File imgFile){
        if (imgFile != null) {
            try{
                App.lastDirectory = imgFile.getPath();
                imageText.setText(imgFile.getAbsolutePath());
                originalImage = new Image(imgFile.toURI().toURL().toExternalForm());
                setImage();
            } catch (MalformedURLException e) {
                System.err.println("ImageCropper: Error trying to load an image to crop");
            }
        }
    }

    @FXML
    private void cropImage(ActionEvent action) {
        //Scale to transform the points
        double scaleX = (originalWidth / newWidth);
        double scaleY = (originalHeight / newHeight);

        //Size of the rectangle
        int width = (int) areaSelection.selectArea(selectionGroup).getBoundsInParent().getWidth();
        int height = (int) areaSelection.selectArea(selectionGroup).getBoundsInParent().getHeight();

        //Size of the resized rectangle
        int realWidth = (int) (scaleX * width);
        int realHeight = (int) (scaleY * height);

        //Starting position of the resized rectangle
        int x = (int) (areaSelection.selectArea(selectionGroup).getBoundsInParent().getMinX() * scaleX);
        int y = (int) (areaSelection.selectArea(selectionGroup).getBoundsInParent().getMinY() * scaleY);

        //Crop image
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D(areaSelection.selectArea(selectionGroup).getBoundsInParent().getMinX(), areaSelection.selectArea(selectionGroup).getBoundsInParent().getMinY(), width, height));
        WritableImage wi = new WritableImage(width, height);
        Image croppedImage = mainImageView.snapshot(parameters, wi);

        //Crop original image
        WritableImage realImage = new WritableImage(realWidth, realHeight);
        SnapshotParameters param2 = new SnapshotParameters();
        param2.setFill(Color.TRANSPARENT);
        param2.setViewport(new Rectangle2D(x, y, realWidth, realHeight));
        Image realCropped = new ImageView(originalImage).snapshot(param2, realImage);

        //Show cropped image and pass original to save
        showCroppedImageNewStage(wi, croppedImage, realImage);
    }

    private void showCroppedImageNewStage(WritableImage wi, Image croppedImage, WritableImage realImage) {
        final Stage croppedImageStage = new Stage();
        croppedImageStage.setAlwaysOnTop(true);
        croppedImageStage.setResizable(true);
        croppedImageStage.setTitle(App.textBundle.getString("croppedImage"));
        changeStageSizeImageDimensions(croppedImageStage,croppedImage);
        final BorderPane borderPane = new BorderPane();
        final MenuBar menuBar = new MenuBar();
        final Menu menu1 = new Menu(App.textBundle.getString("file"));
        final MenuItem save = new MenuItem(App.textBundle.getString("save"));
        save.setOnAction(event -> saveCroppedImage(croppedImageStage, realImage));
        menu1.getItems().add(save);
        menuBar.getMenus().add(menu1);
        borderPane.setTop(menuBar);
        borderPane.setCenter(new ImageView(croppedImage));
        final Scene scene = new Scene(borderPane);
        croppedImageStage.setScene(scene);
    }

    private void saveCroppedImage(Stage stage, WritableImage wi) {
        File file = new File(savePath);

        try{
            RenderedImage renderedImage;
            if (croppedImage != null)
                renderedImage = SwingFXUtils.fromFXImage(croppedImage, null);
            else
                renderedImage = SwingFXUtils.fromFXImage(wi, null);
            ImageIO.write(renderedImage,"png", file);
        } catch (IOException e) {
            System.err.println("Image not saved");
        }

        if (parentController != null) {
            parentController.loadImage(file.getAbsolutePath());
        }else {
            seasonController.loadBackground(file.getAbsolutePath());
            seasonController.setCroppedImage(true);
        }

        stage.close();
    }

    private void clearSelection(Group group) {
        //deletes everything except for base container layer
        isAreaSelected = false;
        group.getChildren().remove(1,group.getChildren().size());
    }

    private void changeStageSizeImageDimensions(Stage stage, Image image) {
        if (image != null) {
            stage.setMinHeight(250);
            stage.setMinWidth(250);
            stage.setWidth(image.getWidth()+4);
            stage.setHeight(image.getHeight()+56);
        }
        stage.show();
    }

    private class AreaSelection {

        private Group group;

        private ResizableRectangle selectionRectangle = null;
        private double rectangleStartX;
        private double rectangleStartY;
        private final Paint darkAreaColor = Color.color(0,0,0,0.5);

        private ResizableRectangle selectArea(Group group) {
            this.group = group;

            if (mainImageView != null && mainImage != null) {
                mainImageView.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
                mainImageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
                mainImageView.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
            }

            return selectionRectangle;
        }

        EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
            if (event.isSecondaryButtonDown())
                return;

            rectangleStartX = event.getX();
            rectangleStartY = event.getY();

            clearSelection(group);

            selectionRectangle = new ResizableRectangle(rectangleStartX, rectangleStartY, 0, 0, group);

            darkenOutsideRectangle(selectionRectangle);
        };

        EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
            if (event.isSecondaryButtonDown())
                return;

            double offsetX = event.getX() - rectangleStartX;
            //double offsetY = event.getY() - rectangleStartY;

            //Free drawing
            /*if (offsetX > 0) {
                if (event.getX() > mainImageView.getFitWidth())
                    selectionRectangle.setWidth(mainImageView.getFitWidth() - rectangleStartX);
                else
                    selectionRectangle.setWidth(offsetX);
            } else {
                if (event.getX() < 0)
                    selectionRectangle.setX(0);
                else
                    selectionRectangle.setX(event.getX());
                selectionRectangle.setWidth(rectangleStartX - selectionRectangle.getX());
            }

            if (offsetY > 0) {
                if (event.getY() > mainImageView.getFitHeight())
                    selectionRectangle.setHeight(mainImageView.getFitHeight() - rectangleStartY);
                else
                    selectionRectangle.setHeight(offsetY);
            } else {
                if (event.getY() < 0)
                    selectionRectangle.setY(0);
                else
                    selectionRectangle.setY(event.getY());
                selectionRectangle.setHeight(rectangleStartY - selectionRectangle.getY());
            }*/

            if (offsetX > 0) {
                if (event.getX() > mainImageView.getFitWidth()) {
                    selectionRectangle.setWidth(mainImageView.getFitWidth() - rectangleStartX);
                }else {
                    selectionRectangle.setWidth(offsetX);
                }
            } else {
                if (event.getX() < 0)
                    selectionRectangle.setX(0);
                else
                    selectionRectangle.setX(event.getX());
                selectionRectangle.setWidth(rectangleStartX - selectionRectangle.getX());
            }

            selectionRectangle.setHeight(selectionRectangle.getWidth() / aspectRatio);

        };

        EventHandler<MouseEvent> onMouseReleasedEventHandler = event -> {
            if (selectionRectangle != null)
                isAreaSelected = true;
        };


        private void darkenOutsideRectangle(Rectangle rectangle) {
            Rectangle darkAreaTop = new Rectangle(0,0,darkAreaColor);
            Rectangle darkAreaLeft = new Rectangle(0,0,darkAreaColor);
            Rectangle darkAreaRight = new Rectangle(0,0,darkAreaColor);
            Rectangle darkAreaBottom = new Rectangle(0,0,darkAreaColor);

            darkAreaTop.setWidth(mainImageView.getFitWidth());
            darkAreaTop.heightProperty().bind(rectangle.yProperty());

            darkAreaLeft.yProperty().bind(rectangle.yProperty());
            darkAreaLeft.widthProperty().bind(rectangle.xProperty());
            darkAreaLeft.heightProperty().bind(rectangle.heightProperty());

            darkAreaRight.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty()));
            darkAreaRight.yProperty().bind(rectangle.yProperty());
            darkAreaRight.widthProperty().bind(mainImageView.fitWidthProperty().subtract(
                    rectangle.xProperty().add(rectangle.widthProperty())));
            darkAreaRight.heightProperty().bind(rectangle.heightProperty());

            darkAreaBottom.yProperty().bind(rectangle.yProperty().add(rectangle.heightProperty()));
            darkAreaBottom.setWidth(mainImageView.getFitWidth());
            darkAreaBottom.setHeight(mainImageView.getFitHeight());

            // adding dark area rectangles before the selectionRectangle. So it can't overlap rectangle
            group.getChildren().add(1,darkAreaTop);
            group.getChildren().add(1,darkAreaLeft);
            group.getChildren().add(1,darkAreaBottom);
            group.getChildren().add(1,darkAreaRight);

            // make dark area container layer as well
            darkAreaTop.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            darkAreaTop.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            darkAreaTop.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

            darkAreaLeft.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            darkAreaLeft.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            darkAreaLeft.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

            darkAreaRight.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            darkAreaRight.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            darkAreaRight.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

            darkAreaBottom.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            darkAreaBottom.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            darkAreaBottom.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
        }
    }
}
