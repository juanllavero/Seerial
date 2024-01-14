package com.example.executablelauncher;

import com.example.executablelauncher.entities.Disc;
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

    public Disc disc;
    private SeasonController parentController = null;
    private DesktopViewController desktopParent = null;
    public boolean discSelected = false;
    private String oldThumbnailPath = "";

    public void setData(Disc d) {
        name.setText(d.getName());
        if (d.getEpisodeNumber() == 0){
            number.setText("");
        }else{
            number.setText(App.textBundle.getString("episode") + " " + d.getEpisodeNumber());
        }
        disc = d;

        clearSelection();
        if (!d.imgSrc.isEmpty())
            setThumbnail();

        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        /*if (screenWidth < 1920){
            name.setFont(new Font("System", 20));
            number.setFont(new Font("System", 14));
        }else if (screenWidth >= 2048){
            name.setFont(new Font("System", 23));
            number.setFont(new Font("System", 18));
        }*/

        thumbnailStackPane.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) ->{
            if (playButton.isVisible()){
                if (event.getCode().equals(KeyCode.ENTER)){
                    if (desktopParent != null)
                        desktopParent.playEpisode(disc);
                    else
                        parentController.playEpisode(disc);
                }/*else if (event.getCode().equals(KeyCode.X) || event.getCode().equals(KeyCode.GAME_C)){
                //showSeriesMenu();
            }*/
            }
        });

        thumbnailStackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if (playButton.isVisible()){
                if (event.getButton().equals(MouseButton.PRIMARY)){
                    if (parentController != null)
                        parentController.playEpisode(disc);
                }
            }
        });

        playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if (desktopParent != null)
                desktopParent.playEpisode(disc);
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
            if (event.getButton() == MouseButton.PRIMARY && desktopParent.isDiscSelected()) {
                selectDiscDesktop();
            }
        });

        playButton.setOnMouseEntered(e -> {
            playImage.setImage(new Image("file:src/main/resources/img/icons/playSelected.png"));
        });

        playButton.setOnMouseExited(e -> {
            playImage.setImage(new Image("file:src/main/resources/img/icons/play.png"));
        });
    }

    private void setHoverButtons(ImageView img){
        img.setOnMouseEntered(e -> {
            img.setOpacity(1);
        });

        img.setOnMouseExited(e -> {
            img.setOpacity(0.7);
        });
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
        desktopParent.selectedDisc = disc;
        desktopParent.openDiscMenu(event, disc);
    }

    public void selectDiscFullScreen(){
        discSelected = true;
        thumbnailShadow.setVisible(true);
        playButton.setVisible(true);
        selectDiscButton.setVisible(false);
        discMenu.setVisible(false);
    }

    public void selectDiscDesktop(){
        desktopParent.selectDisc(disc);
        if (discSelected){
            clearSelection();
        }else{
            discSelected = true;
            thumbnailShadow.setVisible(true);
            playButton.setVisible(false);
            discMenu.setVisible(false);
            selectDiscButton.setVisible(true);
            thumbnailStackPane.getStyleClass().add("discSelected");
            selectDiscButton.setImage(new Image("file:src/main/resources/img/icons/tick.png"));
        }
    }

    public void hoverDisc(){
        thumbnailShadow.setVisible(true);
        selectDiscButton.setVisible(true);

        if (!desktopParent.isDiscSelected()){
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
        selectDiscButton.setImage(new Image("file:src/main/resources/img/icons/circle.png"));
    }

    public void setThumbnail(){
        if (disc.imgSrc.isEmpty() || oldThumbnailPath.equals(disc.imgSrc))
            return;

        double targetWidth = thumbnail.getFitWidth();
        double targetHeight = thumbnail.getFitHeight();

        File newFile = new File(disc.imgSrc);
        if (!newFile.exists())
            disc.imgSrc = "src/main/resources/img/Default_video_thumbnail.jpg";

        Image originalImage = new Image("file:" + disc.imgSrc, targetWidth, targetHeight, true, true);

        oldThumbnailPath = disc.imgSrc;

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
                    .outputQuality(1)
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