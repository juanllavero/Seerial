package com.example.executablelauncher;

import com.example.executablelauncher.entities.Category;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddCategoryController {
    //region FXML ATTRIBUTES
    @FXML
    private Button addFolderButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button generalViewButton;

    @FXML
    private Button foldersViewButton;

    @FXML
    private ScrollPane folderBox;

    @FXML
    private VBox folderContainer;

    @FXML
    private Label folderText;

    @FXML
    private VBox generalBox;

    @FXML
    private ChoiceBox<String> languageChoice;

    @FXML
    private Label languageText;

    @FXML
    private Button moviesTypeButton;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameText;

    @FXML
    private Button saveButton;

    @FXML
    private CheckBox showOnFullscreen;

    @FXML
    private Button showsTypeButton;

    @FXML
    private Label title;

    @FXML
    private Label typeText;
    //endregion

    private DesktopViewController parentController;
    private String catName = "";
    private String type = "";
    private List<String> folders = new ArrayList<>();
    private boolean inGeneralView = true;
    private Category categoryToEdit = null;

    //region INITIALIZATION
    @FXML
    void cancelButton(ActionEvent event) {
        parentController.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void initValues(){
        title.setText(App.textBundle.getString("categoryWindowTitle"));
        generalBox.setVisible(true);
        folderBox.setVisible(false);
        showOnFullscreen.setSelected(true);
        saveButton.setText(App.buttonsBundle.getString("next"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        showOnFullscreen.setText(App.textBundle.getString("showOnFullscreen"));

        List<Locale> languages = App.tmdbLanguages;
        for (Locale locale : languages){
            languageChoice.getItems().add(locale.getDisplayName());
        }

        languageChoice.setValue(languageChoice.getItems().get(0));

        setMoviesType();
        showGeneralView();
    }

    public void setValues(Category toEdit){
        categoryToEdit = toEdit;
        title.setText(App.textBundle.getString("categoryWindowTitleEdit"));
        nameField.setText(categoryToEdit.name);
        showOnFullscreen.setSelected(categoryToEdit.showOnFullscreen);
        generalBox.setVisible(true);
        folderBox.setVisible(false);
        this.type = categoryToEdit.type;
        this.catName = categoryToEdit.name;
        this.folders = categoryToEdit.folders;

        List<Locale> languages = App.tmdbLanguages;
        for (Locale locale : languages){
            languageChoice.getItems().add(locale.getDisplayName());
        }

        Locale locale = Locale.forLanguageTag(categoryToEdit.language);
        languageChoice.setValue(locale.getDisplayName());

        saveButton.setText(App.buttonsBundle.getString("next"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        showOnFullscreen.setText(App.textBundle.getString("showOnFullscreen"));

        if (type.equals("Movies")){
            setMoviesType();
            showsTypeButton.setDisable(true);
            showsTypeButton.setOpacity(0.5);
            moviesTypeButton.setGraphic(new ImageView(new Image(("file:src/main/resources/img/icons/moviesSelected.png"))));
        }else if (type.equals("Shows")){
            setShowsType();
            moviesTypeButton.setDisable(true);
            moviesTypeButton.setOpacity(0.5);
            showsTypeButton.setGraphic(new ImageView(new Image(("file:src/main/resources/img/icons/showsSelected.png"))));
        }

        showGeneralView();
    }
    //endregion

    @FXML
    void nextOrSave(ActionEvent event) {
        if (inGeneralView){
            showFoldersView();
        }else{
            if (folders.isEmpty()){
                App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("foldersEmpty"));
                return;
            }

            if (nameField.getText().isEmpty()) {
                App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("emptyName"));
                return;
            }

            String language = "es";
            List<Locale> languages = App.tmdbLanguages;
            for (Locale locale : languages){
                if (locale.getDisplayName().equals(languageChoice.getValue())){
                    language = locale.getLanguage();
                }
            }

            if (categoryToEdit != null){
                categoryToEdit.name = nameField.getText();
                categoryToEdit.language = language;
                categoryToEdit.type = type;
                categoryToEdit.folders = folders;
                categoryToEdit.showOnFullscreen = showOnFullscreen.isSelected();

                parentController.searchFiles();
            }else{
                Category category = DBManager.INSTANCE.createCategory(new Category(nameField.getText(), language, type, folders, showOnFullscreen.isSelected()));
                parentController.loadCategory(category);
            }

            parentController.hideBackgroundShadow();
            cancelButton(event);
        }
    }

    @FXML
    void setMoviesType() {
        type = "Movies";
        clearTypeSelection();
        moviesTypeButton.getStyleClass().clear();
        moviesTypeButton.getStyleClass().add("buttonSelected");
        nameField.setText(App.textBundle.getString("movies"));
        moviesTypeButton.setGraphic(new ImageView(new Image(("file:src/main/resources/img/icons/moviesSelected.png"))));
        showsTypeButton.setGraphic(new ImageView(new Image(("file:src/main/resources/img/icons/shows.png"))));
    }

    @FXML
    void setShowsType() {
        type = "Shows";
        clearTypeSelection();
        showsTypeButton.getStyleClass().clear();
        showsTypeButton.getStyleClass().add("buttonSelected");
        nameField.setText(App.textBundle.getString("shows"));
        moviesTypeButton.setGraphic(new ImageView(new Image(("file:src/main/resources/img/icons/movies.png"))));
        showsTypeButton.setGraphic(new ImageView(new Image(("file:src/main/resources/img/icons/showsSelected.png"))));
    }

    private void clearTypeSelection(){
        moviesTypeButton.getStyleClass().clear();
        moviesTypeButton.getStyleClass().add("editButton");
        showsTypeButton.getStyleClass().clear();
        showsTypeButton.getStyleClass().add("editButton");
    }

    @FXML
    void showFoldersView() {
        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        generalBox.setVisible(false);
        folderBox.setVisible(true);
        inGeneralView = false;

        foldersViewButton.getStyleClass().clear();
        foldersViewButton.getStyleClass().add("buttonSelected");

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("editButton");

        folderContainer.getChildren().clear();
        for (String folder : folders){
            showFolder(folder);
        }
    }

    @FXML
    void showGeneralView() {
        saveButton.setText(App.buttonsBundle.getString("next"));
        folderBox.setVisible(false);
        generalBox.setVisible(true);
        inGeneralView = true;

        generalViewButton.getStyleClass().clear();
        generalViewButton.getStyleClass().add("buttonSelected");

        foldersViewButton.getStyleClass().clear();
        foldersViewButton.getStyleClass().add("editButton");
    }

    @FXML
    void addFolder(ActionEvent event){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File folder = directoryChooser.showDialog(((Button) event.getSource()).getScene().getWindow());
        if (folder != null) {
            folders.add(folder.getAbsolutePath());
            showFolder(folder.getAbsolutePath());
        }
    }

    private void showFolder(String folder){
        BorderPane folderPane = new BorderPane();
        folderPane.getStyleClass().add("folderContainer");
        folderPane.setPadding(new Insets(2));
        Label folderSrc = new Label(folder);
        folderSrc.setTextAlignment(TextAlignment.CENTER);
        folderSrc.setAlignment(Pos.CENTER);
        folderSrc.setTextFill(Color.WHITE);
        HBox box = new HBox(folderSrc);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(0, 0, 0, 10));
        Image img = new Image("file:src/main/resources/img/icons/close.png");
        ImageView image = new ImageView(img);
        image.setFitWidth(20);
        image.setFitHeight(20);

        Button btn = new Button();
        btn.setGraphic(image);
        btn.getStyleClass().add("folderRemove");
        btn.setOnMouseClicked(event -> {
            folders.remove(folderSrc.getText());
            folderContainer.getChildren().remove(folderPane);
        });

        folderPane.setLeft(box);
        folderPane.setRight(btn);

        folderContainer.getChildren().add(folderPane);
    }

    public void setParent(DesktopViewController desktopViewController) {
        parentController = desktopViewController;
    }
}
