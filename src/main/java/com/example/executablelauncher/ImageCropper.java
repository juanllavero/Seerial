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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.executablelauncher.Main.lastDirectory;

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
    private VBox mainBox;

    @FXML
    private ImageView mainImageView;

    @FXML
    private ScrollPane rootPane;

    @FXML
    private Group selectionGroup;

    @FXML
    private TextField urlText;
    private Image mainImage;
    private boolean isAreaSelected = false;
    private final AreaSelection areaSelection = new AreaSelection();
    @FXML
    private AnchorPane firstAnchor = new AnchorPane();

    private double fixedWidth = 0;
    private double fixedHeight = 0;
    private final double aspectRatio = (double) 7/9;

    public String savePath;
    private AddCollectionController parentController = null;

    public void initValues(AddCollectionController parent, String path) {
        parentController = parent;
        savePath = path;

        urlText.setPromptText(Main.textBundle.getString("urlText"));
        downloadButton.setText(Main.buttonsBundle.getString("downloadButton"));
        loadButton.setText(Main.buttonsBundle.getString("loadImageButton"));
        cropButton.setText(Main.buttonsBundle.getString("cropImageButton"));

        fixedWidth = Screen.getPrimary().getBounds().getWidth();
        fixedHeight = Screen.getPrimary().getBounds().getHeight();

        firstAnchor.setPrefWidth(fixedWidth);
        firstAnchor.setPrefHeight(fixedHeight);
        mainBox.prefWidthProperty().bind(firstAnchor.prefWidthProperty());
        mainBox.prefHeightProperty().bind(firstAnchor.prefHeightProperty());
        selectionGroup.prefHeight(fixedHeight);
        selectionGroup.prefWidth(fixedWidth);
        mainImageView.setFitWidth(fixedWidth / 2);
        mainImageView.setFitHeight(fixedHeight / 2);
    }

    @FXML
    void downloadImage(ActionEvent event) {
        if (!urlText.getText().isEmpty()){
            mainImage = new Image(urlText.getText(), Screen.getPrimary().getBounds().getWidth() / 1.5, Screen.getPrimary().getBounds().getHeight() / 1.5, true, true);
            if (mainImage.isError()) {
                System.out.println("Error loading image from " + urlText.getText());
            } else {
                //clearSelection(selectionGroup);
                mainImageView.setImage(mainImage);
                areaSelection.selectArea(selectionGroup);
            }
        }
    }

    @FXML
    void loadImage(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Main.textBundle.getString("selectImage"));
        if (lastDirectory != null && Files.exists(Path.of(lastDirectory)))
            fileChooser.setInitialDirectory(new File(new File(lastDirectory).getParentFile().getAbsolutePath()));
        else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(Main.textBundle.getString("allImages"), "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog((Stage)((Button) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            lastDirectory = selectedFile.getPath();
            imageText.setText(selectedFile.getAbsolutePath());
            //clearSelection(selectionGroup);
            mainImageView.setImage(mainImage);
            mainImage = new Image(selectedFile.getAbsolutePath(), Screen.getPrimary().getBounds().getWidth() / 1.5, Screen.getPrimary().getBounds().getHeight() / 1.5, true, true);
            mainImageView.setImage(mainImage);
            areaSelection.selectArea(selectionGroup);
        }
    }

    @FXML
    private void cropImage(ActionEvent action) {
        int width = (int) areaSelection.selectArea(selectionGroup).getBoundsInParent().getWidth();
        int height = (int) areaSelection.selectArea(selectionGroup).getBoundsInParent().getHeight();

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D(areaSelection.selectArea(selectionGroup).getBoundsInParent().getMinX(), areaSelection.selectArea(selectionGroup).getBoundsInParent().getMinY(), width, height));

        WritableImage wi = new WritableImage(width, height);
        Image croppedImage = mainImageView.snapshot(parameters, wi);

        showCroppedImageNewStage(wi, croppedImage);
    }

    private void showCroppedImageNewStage(WritableImage wi, Image croppedImage) {
        final Stage croppedImageStage = new Stage();
        croppedImageStage.setAlwaysOnTop(true);
        croppedImageStage.setResizable(true);
        croppedImageStage.setTitle(Main.textBundle.getString("croppedImage"));
        changeStageSizeImageDimensions(croppedImageStage,croppedImage);
        final BorderPane borderPane = new BorderPane();
        final MenuBar menuBar = new MenuBar();
        final Menu menu1 = new Menu(Main.textBundle.getString("file"));
        final MenuItem save = new MenuItem(Main.textBundle.getString("save"));
        save.setOnAction(event -> saveCroppedImage(croppedImageStage,wi));
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
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(wi, null);
            ImageIO.write(renderedImage,"png", file);
        } catch (IOException e) {
            System.err.println("Image not saved");
        }

        parentController.setImageFile(file.getAbsolutePath());

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
