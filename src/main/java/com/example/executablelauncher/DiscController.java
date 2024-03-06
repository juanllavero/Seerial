package com.example.executablelauncher;

import com.example.executablelauncher.entities.Episode;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DiscController {
    @FXML
    private ImageView discMenu;

    @FXML
    private VBox mainBox;

    @FXML
    private Label name;

    @FXML
    private Label number;

    @FXML
    private ImageView playImage;

    @FXML
    private ImageView selectDiscButton;

    @FXML
    private ImageView thumbnail;

    @FXML
    private ImageView thumbnailShadow;

    @FXML
    private StackPane thumbnailStackPane;

    @FXML
    private Button playButton;

    public Episode episode;
    private SeasonController parentController = null;
    private DesktopViewController desktopParent = null;
    public boolean discSelected = false;
    private String oldThumbnailPath = "";

    public void setData(Episode d) {
        name.setText(d.getName());

        if (d.getEpisodeNumber() == 0 || !DataManager.INSTANCE.currentLibrary.type.equals("Shows")){
            mainBox.getChildren().remove(number);
        }else{
            number.setText(App.textBundle.getString("episode") + " " + d.getEpisodeNumber());
        }

        episode = d;

        clearSelection();
        if (!d.imgSrc.isEmpty())
            setThumbnail();

        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        if (screenWidth < 2000){
            name.setFont(new Font("System", 18));
            number.setFont(new Font("System", 16));
        }

        thumbnailStackPane.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (playButton.isVisible()){
                if (event.getCode().equals(KeyCode.ENTER)){
                    if (desktopParent != null)
                        desktopParent.playEpisode(episode);
                    else
                        parentController.playEpisode(episode);
                }
            }
        });

        thumbnailStackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if (playButton.isVisible()){
                if (event.getButton().equals(MouseButton.PRIMARY)){
                    if (parentController != null)
                        parentController.playEpisode(episode);
                }
            }
        });

        playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if (desktopParent != null)
                desktopParent.playEpisode(episode);
        });

        thumbnailStackPane.setOnMouseEntered(e -> {
            hoverDisc();
        });

        thumbnailStackPane.setOnMouseExited(e -> {
            if (discSelected){
                discMenu.setVisible(false);
                playButton.setVisible(false);
                selectDiscButton.setVisible(true);
            }else{
                clearSelection();
            }
        });

        discMenu.setOpacity(0.7);
        selectDiscButton.setOpacity(0.7);
        setHoverButtons(discMenu);
        setHoverButtons(selectDiscButton);

        thumbnailStackPane.getStyleClass().add("discButton");

        thumbnailShadow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY && desktopParent.isEpisodeSelected()) {
                selectDiscDesktop();
            }
        });

        playButton.setOnMouseEntered(e -> {
            playImage.setImage(new Image("file:resources/img/icons/playSelected.png"));
        });

        playButton.setOnMouseExited(e -> {
            playImage.setImage(new Image("file:resources/img/icons/play.png"));
        });
    }

    private void setHoverButtons(ImageView img){
        img.setOnMouseEntered(e -> img.setOpacity(1));

        img.setOnMouseExited(e -> img.setOpacity(0.7));
    }

    public void setParent(SeasonController c){
        parentController = c;
    }

    public void setDesktopParentParent(DesktopViewController c){
        desktopParent = c;
    }

    @FXML
    void selectDisc(MouseEvent event){
        selectDiscDesktop();
    }

    @FXML
    void openMenu(MouseEvent event){
        desktopParent.selectedEpisode = episode;
        desktopParent.openDiscMenu(event);
    }

    public void selectDiscDesktop(){
        desktopParent.selectEpisode(episode);
        if (discSelected){
            clearSelection();
        }else{
            discSelected = true;
            thumbnailShadow.setVisible(true);
            playButton.setVisible(false);
            discMenu.setVisible(false);
            selectDiscButton.setVisible(true);
            thumbnailStackPane.getStyleClass().add("discSelected");
            selectDiscButton.setImage(new Image("file:resources/img/icons/tick.png"));
        }
    }

    public void hoverDisc(){
        thumbnailShadow.setVisible(true);
        selectDiscButton.setVisible(true);

        if (!desktopParent.isEpisodeSelected()){
            playButton.setVisible(true);
            discMenu.setVisible(true);
        }else{
            playButton.setVisible(false);
            discMenu.setVisible(false);
        }
    }

    public void clearSelection(){
        discSelected = false;
        thumbnailShadow.setVisible(false);
        playButton.setVisible(false);
        selectDiscButton.setVisible(false);
        discMenu.setVisible(false);
        thumbnailStackPane.getStyleClass().add("discButton");
        selectDiscButton.setImage(new Image("file:resources/img/icons/circle.png"));
    }

    public void setThumbnail(){
        if (episode.imgSrc.isEmpty() || oldThumbnailPath.equals(episode.imgSrc))
            return;

        double targetWidth = thumbnail.getFitWidth();
        double targetHeight = thumbnail.getFitHeight();

        File newFile = new File(episode.imgSrc);
        if (!newFile.exists())
            episode.imgSrc = "resources/img/Default_video_thumbnail.jpg";

        Image originalImage = new Image("file:" + episode.imgSrc, targetWidth, targetHeight, true, true);

        oldThumbnailPath = episode.imgSrc;

        /*double aspectRatio = targetWidth / targetHeight;
        double originalWidth = originalImage.getWidth();
        double originalHeight = originalImage.getHeight();

        double cropWidth;
        double cropHeight;

        if (originalWidth / originalHeight > aspectRatio) {
            cropHeight = originalHeight;
            cropWidth = originalHeight * aspectRatio;
        } else {
            cropWidth = originalWidth;
            cropHeight = originalWidth / aspectRatio;
        }

        double startX = (originalWidth - cropWidth) / 2;
        double startY = (originalHeight - cropHeight) / 2;

        WritableImage croppedImage = new WritableImage((int) targetWidth, (int) targetHeight);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = croppedImage.getPixelWriter();

        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int sourceX = (int) (startX + x * (cropWidth / targetWidth));
                int sourceY = (int) (startY + y * (cropHeight / targetHeight));
                pixelWriter.setArgb(x, y, pixelReader.getArgb(sourceX, sourceY));
            }
        }*/

        try{
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);

            //Compress image
            BufferedImage resizedImage = Thumbnails.of(bufferedImage)
                    .size((int) targetWidth, (int) targetHeight)
                    .outputFormat("jpg")
                    .outputQuality(0.7)
                    .asBufferedImage();

            originalImage = SwingFXUtils.toFXImage(resizedImage, null);
            bufferedImage.flush();
            resizedImage.flush();
        } catch (IOException e) {
            System.err.println("DiscController: Error compressing image");
        }

        thumbnail.setImage(originalImage);
        thumbnail.setPreserveRatio(false);
        thumbnail.setSmooth(true);
    }
}