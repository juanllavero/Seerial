package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AddDiscController {

    @FXML
    private TextField executableField;

    @FXML
    private ChoiceBox<String> typeField;

    @FXML
    private Label errorLoad;

    public Disc discToEdit = null;
    private DesktopViewController controllerParent;
    private List<File> selectedFiles = null;
    private File selectedFolder = null;

    public void InitValues(){
        typeField.getItems().addAll(Arrays.asList("File", "Folder"));
        typeField.setValue("File");
    }

    public void setDisc(Disc d){
        discToEdit = d;
        executableField.setText(d.getExecutableSrc());
        typeField.setValue(d.getType());
        InitValues();
    }

    public void setParentController(DesktopViewController controller){
        controllerParent = controller;
    }

    @FXML
    void loadExe(MouseEvent event) {
        if (typeField.getValue().equals("Folder")){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            selectedFolder = directoryChooser.showDialog((Stage)((Button) event.getSource()).getScene().getWindow());
            executableField.setText(selectedFolder.getAbsolutePath());
        }else{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a file");
            fileChooser.setInitialDirectory(new File("C:\\"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All files", "*.mkv", "*.mp4", "*.iso", "*.ISO", "*.m2ts", "*.exe", "*.bat"));
            if (discToEdit != null) {
                selectedFolder = fileChooser.showOpenDialog((Stage) ((Button) event.getSource()).getScene().getWindow());
                executableField.setText(selectedFolder.getAbsolutePath());
            }else {
                selectedFiles = fileChooser.showOpenMultipleDialog((Stage) ((Button) event.getSource()).getScene().getWindow());
                executableField.setText("Multiple selection");
            }
        }
    }

    @FXML
    void cancelButton(MouseEvent event) {
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(MouseEvent event) {
        boolean save = true;

        //Get file/files
        File exe = new File(executableField.getText());
        if (!executableField.getText().equals("Multiple selection") && !exe.exists()){
            save = false;
            errorLoad.setText("File not found");
        }else if (!executableField.getText().equals("Multiple selection") && !selectedFolder.exists()){
            String fileExtension = executableField.getText().substring(executableField.getText().length() - 4);
            fileExtension = fileExtension.toLowerCase();

            if (!fileExtension.equals(".mkv") && !fileExtension.equals(".mp4") && !fileExtension.equals("m2ts")
                    && !fileExtension.equals(".iso") && !fileExtension.equals(".exe") && !fileExtension.equals(".bat")){
                save = false;
                errorLoad.setText("File extension not allowed");
            }else{
                errorLoad.setText("");
            }
        }else{
            errorLoad.setText("");
        }

        if (save){
            Disc disc;

            disc = Objects.requireNonNullElseGet(discToEdit, Disc::new);

            if (executableField.getText().equals("Multiple selection"))
                for (File file : selectedFiles)
                    setDiscInfo(disc, file, false);
            else
                setDiscInfo(disc, selectedFolder, true);

            if (discToEdit != null)
                discToEdit = null;

            controllerParent.updateDiscView();

            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    private void setDiscInfo(Disc newDisc, File file, boolean folder) {
        if (discToEdit == null)
            newDisc = new Disc();

        newDisc.setSeasonID(controllerParent.getCurrentSeason().getId());

        if (!folder)
            newDisc.setName(file.getName().substring(0, file.getName().length() - 4));
        else
            newDisc.setName("Disc " + Objects.requireNonNull(Main.findSeason(newDisc.getSeasonID())).getDiscs().size() + 1);

        newDisc.setType(typeField.getValue());
        newDisc.setExecutableSrc(file.getAbsolutePath());

        Main.addDisc(newDisc);
    }
}
