package com.example.executablelauncher;

import com.example.executablelauncher.entities.Library;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.executablelauncher.utils.Utils.getFileAsIOStream;

public class AddLibraryController {
    //region FXML ATTRIBUTES
    @FXML
    private Button addFolderButton;

    @FXML
    private Button closeButton;

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
    private Library libraryToEdit = null;

    //region INITIALIZATION
    @FXML
    void cancelButton(ActionEvent event) {
        parentController.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void initValues(){
        title.setText(App.textBundle.getString("libraryWindowTitle"));
        generalBox.setVisible(true);
        folderBox.setVisible(false);
        showOnFullscreen.setSelected(true);
        setButtonsValues();

        List<Locale> languages = App.tmdbLanguages;
        for (Locale locale : languages){
            languageChoice.getItems().add(locale.getDisplayName());
        }

        languageChoice.setValue(languageChoice.getItems().get(0));

        setMoviesType();
        showGeneralView();
    }

    public void setValues(Library toEdit){
        libraryToEdit = toEdit;
        title.setText(App.textBundle.getString("libraryWindowTitleEdit"));
        nameField.setText(libraryToEdit.getName());
        showOnFullscreen.setSelected(libraryToEdit.isShowOnFullscreen());
        generalBox.setVisible(true);
        folderBox.setVisible(false);
        this.type = libraryToEdit.getType();
        this.catName = libraryToEdit.getName();
        this.folders = libraryToEdit.getFolders();

        List<Locale> languages = App.tmdbLanguages;
        for (Locale locale : languages){
            languageChoice.getItems().add(locale.getDisplayName());
        }

        Locale locale = Locale.forLanguageTag(libraryToEdit.getLanguage());
        languageChoice.setValue(locale.getDisplayName());

        setButtonsValues();

        if (type.equals("Movies")){
            setMoviesType();
            showsTypeButton.setDisable(true);
            showsTypeButton.setOpacity(0.5);

            moviesTypeButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/moviesSelected.png"))));
        }else if (type.equals("Shows")){
            setShowsType();
            moviesTypeButton.setDisable(true);
            moviesTypeButton.setOpacity(0.5);
            showsTypeButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/showsSelected.png"))));
        }

        showGeneralView();
    }

    private void setButtonsValues() {
        typeText.setText(App.textBundle.getString("type") + ":");
        saveButton.setText(App.buttonsBundle.getString("next"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        showOnFullscreen.setText(App.textBundle.getString("showOnFullscreen"));
        addFolderButton.setText(App.buttonsBundle.getString("addFolder"));
        generalViewButton.setText(App.buttonsBundle.getString("generalButton"));
        foldersViewButton.setText(App.buttonsBundle.getString("folders"));
        moviesTypeButton.setText(App.textBundle.getString("movies"));
        showsTypeButton.setText(App.textBundle.getString("shows"));
        nameText.setText(App.textBundle.getString("name"));
        languageText.setText(App.textBundle.getString("languageText"));
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

            if ((!catName.equals(nameField.getText()) && DataManager.INSTANCE.libraryExists(nameField.getText()))){
                App.showErrorMessage(App.textBundle.getString("error"), "", App.textBundle.getString("libraryExists"));
                return;
            }

            String language = "es-ES";
            List<Locale> languages = App.tmdbLanguages;
            for (Locale locale : languages){
                if (locale.getDisplayName().equals(languageChoice.getValue())){
                    language = locale.getLanguage();
                }
            }

            if (libraryToEdit != null){
                String oldName = libraryToEdit.getName();

                libraryToEdit.setName(nameField.getText());
                libraryToEdit.setLanguage(language);
                libraryToEdit.setType(type);
                libraryToEdit.setFolders(folders);
                libraryToEdit.setShowOnFullscreen(showOnFullscreen.isSelected());

                parentController.updateLibraries(oldName, libraryToEdit.getName());

                parentController.searchFiles();
            }else{
                Library library = new Library(nameField.getText(), language, type, DataManager.INSTANCE.libraries.size(), folders, showOnFullscreen.isSelected());
                DataManager.INSTANCE.createLibrary(library);
                parentController.loadLibrary(library);
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
        if (libraryToEdit == null)
            nameField.setText(App.textBundle.getString("movies"));
        moviesTypeButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/moviesSelected.png"))));
        showsTypeButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/shows.png"))));
    }

    @FXML
    void setShowsType() {
        type = "Shows";
        clearTypeSelection();
        showsTypeButton.getStyleClass().clear();
        showsTypeButton.getStyleClass().add("buttonSelected");
        if (libraryToEdit == null)
            nameField.setText(App.textBundle.getString("shows"));
        moviesTypeButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/movies.png"))));
        showsTypeButton.setGraphic(new ImageView(new Image(getFileAsIOStream("img/icons/showsSelected.png"))));
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
        Image img = new Image(getFileAsIOStream("img/icons/close.png"));
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
