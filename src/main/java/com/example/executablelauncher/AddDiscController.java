package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AddDiscController {
    @FXML
    private ChoiceBox<String> typeField;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorLoad;

    @FXML
    private TextField executableField;

    @FXML
    private Button loadButton;

    @FXML
    private Label source;

    @FXML
    private Label title;

    @FXML
    private Label type;

    public Disc discToEdit = null;
    private DesktopViewController controllerParent;
    private List<File> selectedFiles = null;
    private File selectedFolder = null;

    public void InitValues(){
        typeField.getItems().addAll(Arrays.asList(App.textBundle.getString("file"), App.textBundle.getString("folder")));
        typeField.setValue(App.textBundle.getString("file"));
        title.setText(App.textBundle.getString("episodeWindowTitle"));
        addButton.setText(App.buttonsBundle.getString("addButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        type.setText(App.textBundle.getString("type"));
        source.setText(App.textBundle.getString("source"));
        loadButton.setText(App.buttonsBundle.getString("loadButton"));
    }

    public void setDisc(Disc d){
        discToEdit = d;
        executableField.setText(d.getExecutableSrc());
        typeField.setValue(d.getType());
        InitValues();
        title.setText(App.textBundle.getString("episodeWindowTitleEdit"));
        addButton.setText(App.buttonsBundle.getString("saveButton"));
    }

    public void setParentController(DesktopViewController controller){
        controllerParent = controller;
    }

    @FXML
    void loadExe(MouseEvent event) {
        if (typeField.getValue().equals(App.textBundle.getString("folder"))){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            selectedFolder = directoryChooser.showDialog((Stage)((Button) event.getSource()).getScene().getWindow());
            executableField.setText(selectedFolder.getAbsolutePath());
        }else{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(App.textBundle.getString("selectFile"));
            fileChooser.setInitialDirectory(new File("C:\\"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(App.textBundle.getString("allFiles"), "*.mkv", "*.mp4", "*.iso", "*.ISO", "*.m2ts", "*.exe", "*.bat"));
            if (discToEdit != null) {
                selectedFolder = fileChooser.showOpenDialog((Stage) ((Button) event.getSource()).getScene().getWindow());
                executableField.setText(selectedFolder.getAbsolutePath());
            }else {
                selectedFiles = fileChooser.showOpenMultipleDialog((Stage) ((Button) event.getSource()).getScene().getWindow());
                executableField.setText(App.textBundle.getString("multipleSelection"));
            }
        }
    }

    @FXML
    void cancelButton(MouseEvent event) {
        controllerParent.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(MouseEvent event) {
        boolean save = true;

        //Get file/files
        File exe = new File(executableField.getText());
        if (!executableField.getText().equals(App.textBundle.getString("multipleSelection")) && !exe.exists()){
            save = false;
            errorLoad.setText(App.textBundle.getString("fileNotFound"));
        }else if (!executableField.getText().equals(App.textBundle.getString("multipleSelection")) && !selectedFolder.exists()){
            String fileExtension = executableField.getText().substring(executableField.getText().length() - 4);
            fileExtension = fileExtension.toLowerCase();

            if (!fileExtension.equals(".mkv") && !fileExtension.equals(".mp4") && !fileExtension.equals("m2ts")
                    && !fileExtension.equals(".iso") && !fileExtension.equals(".exe") && !fileExtension.equals(".bat")){
                save = false;
                errorLoad.setText(App.textBundle.getString("extensionNotAllowed"));
            }else{
                errorLoad.setText("");
            }
        }else{
            errorLoad.setText("");
        }

        if (save){
            Disc disc;

            disc = Objects.requireNonNullElseGet(discToEdit, Disc::new);

            if (executableField.getText().equals(App.textBundle.getString("multipleSelection")))
                for (File file : selectedFiles)
                    setDiscInfo(disc, file, false);
            else
                setDiscInfo(disc, selectedFolder, true);

            if (discToEdit != null)
                discToEdit = null;

            controllerParent.updateDiscView();
            controllerParent.hideBackgroundShadow();

            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    private void setDiscInfo(Disc newDisc, File file, boolean folder) {
        if (discToEdit == null)
            newDisc = new Disc();

        newDisc.setSeasonID(controllerParent.getCurrentSeason().getId());

        if (!folder){
            String[] result = file.getName().substring(0, file.getName().length() - 4).split(" - ");

            if (result.length == 3){
                String episodeName = result[2];
                String episodeNumberSeason = result[1];

                result = episodeNumberSeason.split("E");
                String episodeNumber = result[1];

                newDisc.setName(episodeName);
                newDisc.setEpisodeNumber(App.textBundle.getString("episode") + " " + episodeNumber);
            }else{
                //FIX THIS
                newDisc.setName(file.getName().substring(0, file.getName().length() - 4));
            }

        }else{
            newDisc.setName(App.textBundle.getString("disc") + " " + Objects.requireNonNull(App.findSeason(newDisc.getSeasonID())).getDiscs().size() + 1);
        }

        newDisc.setType(typeField.getValue());
        newDisc.setExecutableSrc(file.getAbsolutePath());

        App.addDisc(newDisc);
    }
}
