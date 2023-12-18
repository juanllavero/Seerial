package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewDesign {

    @FXML
    private Button addCollectionButton;

    @FXML
    private Button addDiscButton;

    @FXML
    private Button addSeasonButton;

    @FXML
    private ImageView backgroundShadow;

    @FXML
    private ChoiceBox<?> categorySelector;

    @FXML
    private VBox centralVBox;

    @FXML
    private Button closeButton;

    @FXML
    private VBox detailsBox;

    @FXML
    private VBox discContainer;

    @FXML
    private VBox discMenu;

    @FXML
    private Button editColButton;

    @FXML
    private Button editDiscButton;

    @FXML
    private Button editSeasonButton;

    @FXML
    private Button exitButton;

    @FXML
    private ImageView globalBackground;

    @FXML
    private ImageView globalBackgroundShadow;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private StackPane mainBox;

    @FXML
    private VBox mainMenu;

    @FXML
    private ImageView maximizeRestoreImage;

    @FXML
    private Pane menuParentPane;

    @FXML
    private Button removeColButton;

    @FXML
    private Button removeDiscButton;

    @FXML
    private Button removeSeasonButton;

    @FXML
    private HBox seasonContainer;

    @FXML
    private HBox seasonCoverLogoBox;

    @FXML
    private VBox seasonInfoInside;

    @FXML
    private StackPane seasonInfoPane;

    @FXML
    private ImageView seasonLogo;

    @FXML
    private VBox seasonMenu;

    @FXML
    private Label seasonName;

    @FXML
    private ScrollPane seasonScroll;

    @FXML
    private VBox seriesContainer;

    @FXML
    private ImageView seriesCover;

    @FXML
    private VBox seriesMenu;

    @FXML
    private ScrollPane seriesScrollPane;

    @FXML
    private Button settingsButton;

    @FXML
    private Button switchFSButton;

    @FXML
    private BorderPane topBar;

    @FXML
    private HBox topRightBar;

    @FXML
    void addCategory(MouseEvent event) {

    }

    @FXML
    void addCollection(MouseEvent event) {

    }

    @FXML
    void addDisc(MouseEvent event) {

    }

    @FXML
    void addSeason(MouseEvent event) {

    }

    @FXML
    void close(MouseEvent event) {
        ((Stage)mainBox.getScene().getWindow()).close();
    }

    @FXML
    void editCategory(MouseEvent event) {

    }

    @FXML
    void editDisc(MouseEvent event) {

    }

    @FXML
    void editSeason(MouseEvent event) {

    }

    @FXML
    void editSeries(MouseEvent event) {

    }

    @FXML
    void maximizeWindow(MouseEvent event) {

    }

    @FXML
    void minimizeWindow(MouseEvent event) {

    }

    @FXML
    void openMenu(MouseEvent event) {

    }

    @FXML
    void openSettings(MouseEvent event) {

    }

    @FXML
    void removeCategory(MouseEvent event) {

    }

    @FXML
    void removeCollection(MouseEvent event) {

    }

    @FXML
    void removeDisc(MouseEvent event) {

    }

    @FXML
    void removeSeason(MouseEvent event) {

    }

    @FXML
    void switchToFullScreen(MouseEvent event) {

    }
    public void initValues(){

        globalBackground.fitWidthProperty().bind(mainBorderPane.prefWidthProperty());
        globalBackground.fitHeightProperty().bind(mainBorderPane.prefHeightProperty());
        globalBackgroundShadow.fitWidthProperty().bind(mainBorderPane.prefWidthProperty());
        globalBackgroundShadow.fitHeightProperty().bind(mainBorderPane.prefHeightProperty());
    }
}
