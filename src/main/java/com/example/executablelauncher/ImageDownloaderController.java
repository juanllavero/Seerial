package com.example.executablelauncher;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ImageDownloaderController {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField heightField;

    @FXML
    private FlowPane imagesContainer;

    @FXML
    private Button loadMoreButton;

    @FXML
    private Label resolutionText;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button selectImageButton;

    @FXML
    private Label title;

    @FXML
    private CheckBox transparentCheck;

    @FXML
    private TextField widthField;

    private final int numberOfImages = 60;
    private boolean isCover = false;
    private boolean isLogo = false;
    private boolean moreLoaded = false;
    private List<Button> downloadedImages = new ArrayList<>();
    private List<File> imagesFiles = new ArrayList<>();
    private File selectedFile = null;
    private AddSeasonController seasonParent = null;

    public void setSeasonParent(AddSeasonController parent){
        seasonParent = parent;
    }

    public void initValues(String searchText, String width, String height, boolean isCover, boolean isLogo, boolean transparent){
        searchTextField.setText(searchText);
        this.isCover = isCover;
        this.isLogo = isLogo;
        transparentCheck.setSelected(transparent);

        title.setText(App.textBundle.getString("imageDownloaderTitle"));
        transparentCheck.setText(App.textBundle.getString("transparentCheck"));
        widthField.setText(width);
        heightField.setText(height);
        resolutionText.setText(App.textBundle.getString("resolutionText"));
        selectImageButton.setText(App.buttonsBundle.getString("selectButton"));
        loadMoreButton.setText(App.buttonsBundle.getString("loadMoreButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        searchButton.setText(App.buttonsBundle.getString("searchButton"));

        searchImages();
    }

    public void searchImages(){
        imagesContainer.getChildren().clear();
        imagesFiles.clear();
        downloadedImages.clear();

        try{
            FileUtils.cleanDirectory(new File("src/main/resources/img/DownloadCache"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String coverFilters = "+filterui:aspect-tall&form=IRFLTR&first=1";
        String wideScreen = "+filterui:aspect-wide+filterui:imagesize-custom_"+widthField.getText()+"_"+heightField.getText()+"&form=IRFLTR&first=1";
        String transparent = "+filterui:photo-transparent";
        String filters;

        if (isCover)
            filters = coverFilters;
        else
            filters = wideScreen;

        if (transparentCheck.isSelected())
            filters += transparent;

        try {
            ProcessBuilder pb =
                    new ProcessBuilder("python","src/main/resources/python/BingDownloader.py"
                            ,"-o", "src/main/resources/img/DownloadCache"
                            , "--filters", filters
                            , "--limit", Integer.toString(numberOfImages)
                            , searchTextField.getText());

            pb.redirectErrorStream(true);
            Process process = pb.start();

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error downloading images");
        }

        //Add images to view
        File dir = new File("src/main/resources/img/DownloadCache/");
        File[] files = dir.listFiles();
        assert files != null;
        for (File f : files){
            try{
                BufferedImage bimg = ImageIO.read(f);
                if (bimg != null){
                    int width = bimg.getWidth();
                    int height = bimg.getHeight();
                    addImageButton(bimg, width, height);
                    imagesFiles.add(f);
                }
            } catch (IOException e) {
                System.err.println("ImageDownloader: Error loading images");
            }
        }
    }

    private void addImageButton(BufferedImage bimg, int width, int height){
        Image image = SwingFXUtils.toFXImage(bimg, null);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        if (isCover){
            imageView.setFitHeight(300);
        }else{
            imageView.setFitWidth(300);
        }
        Button btn = new Button();
        btn.setGraphic(imageView);
        btn.setText(width + "x" + height + "px");
        btn.setContentDisplay(ContentDisplay.TOP);
        btn.setPadding(new Insets(5, 5, 5, 5));
        btn.getStyleClass().add("downloadedImageButton");

        HBox.setMargin(btn, new Insets(2, 2, 2, 2));

        btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            selectButton(btn);
        });

        downloadedImages.add(btn);
        imagesContainer.getChildren().add(btn);
    }

    private void selectButton(Button btn){
        int index = 0;
        int i = 0;
        for (Button b : downloadedImages){
            b.getStyleClass().clear();
            b.getStyleClass().add("downloadedImageButton");
            if (b == btn)
                index = i;
            i++;
        }

        btn.getStyleClass().clear();
        btn.getStyleClass().add("downloadedImageButtonSelected");

        selectedFile = imagesFiles.get(index);
        selectImageButton.setDisable(false);
    }

    @FXML
    void loadImages(ActionEvent event) {
        moreLoaded = !moreLoaded;
        loadMoreButton.setDisable(!loadMoreButton.isDisabled());
        searchImages();
    }

    @FXML
    void loadMore(ActionEvent event) {
        loadImages(event);
    }

    @FXML
    void cancel(ActionEvent event) {
        ((Stage)title.getScene().getWindow()).close();
    }

    @FXML
    void selectImage(ActionEvent event) {
        if (seasonParent != null){
            if (isLogo)
                seasonParent.loadLogo(selectedFile.getAbsolutePath());
            else
                seasonParent.loadBackground(selectedFile.getAbsolutePath());
        }
        cancel(event);
    }
}
