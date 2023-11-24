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
    private TextField coverField;

    @FXML
    private TextField executableField;

    @FXML
    private ChoiceBox<String> typeField;

    @FXML
    private Label errorCover;

    @FXML
    private Label errorLoad;

    public Disc discToEdit = null;
    private SeasonController controllerParent;
    private File selectedCover = null;
    private List<File> selectedFiles = null;
    private File selectedFolder = null;

    public void InitValues(){
        typeField.getItems().addAll(Arrays.asList("File", "Folder"));
        typeField.setValue("File");
    }

    public void setDisc(Disc d){
        discToEdit = d;
        coverField.setText(d.getCoverSrc());
        executableField.setText(d.getExecutableSrc());
        typeField.setValue(d.getType());
        InitValues();
    }

    public void setParentController(SeasonController controller){
        controllerParent = controller;
    }

    @FXML
    void loadCover(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All images", "*.jpg", "*.png", "*.jpeg"));
        selectedCover = fileChooser.showOpenDialog((Stage)((Button) event.getSource()).getScene().getWindow());
        coverField.setText(selectedCover.getPath());
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
    void cancelImage(MouseEvent event) {
        Stage stage = (Stage) ((ImageView)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancelButton(MouseEvent event) {
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(MouseEvent event) {
        boolean save = true;

        //Get image cover
        File cover = new File(coverField.getText());
        if (!coverField.getText().isEmpty() && !cover.exists()) {
            save = false;
            errorCover.setText("Image not found");
        }else if (!coverField.getText().isEmpty()){
            String imageExtension = coverField.getText().substring(coverField.getText().length() - 4);
            imageExtension = imageExtension.toLowerCase();

            if (!imageExtension.equals(".jpg") && !imageExtension.equals(".png") && !imageExtension.equals("jpeg")){
                save = false;
                errorCover.setText("Image has to be '.jpg', '.png' or '.jpeg'");
            }else{
                errorCover.setText("");
            }
        }else{
            errorCover.setText("");
        }

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
            if (selectedCover == null)
                selectedCover = new File(coverField.getText());

            if (!coverField.getText().isEmpty()){
                String extension = "";
                int i = selectedCover.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = selectedCover.getName().substring(i+1);
                }

                File newCover = new File("src/main/resources/img/discCovers/"+ controllerParent.getCurrentSeason().getCollectionName() + "_" + controllerParent.getCurrentSeason().getDiscs().size() + "_discCover." + extension);

                try{
                    Files.copy(selectedCover.toPath(), newCover.toPath());
                }catch (IOException e){
                    System.err.println("Image not copied");
                }
            }else{
                File newCover = new File("src/main/resources/img/discCovers/"+ controllerParent.getCurrentSeason().getCollectionName() + "_" + controllerParent.getCurrentSeason().getDiscs().size() + "_discCover.png");

                try{
                    Files.copy(Path.of("src/main/resources/img/icons/disc.png"), newCover.toPath());
                }catch (IOException e){
                    System.err.println("Image not copied");
                }

                coverField.setText(newCover.getAbsolutePath());
            }

            Disc disc;

            disc = Objects.requireNonNullElseGet(discToEdit, Disc::new);

            if (executableField.getText().equals("Multiple selection"))
                for (File file : selectedFiles)
                    setDiscInfo(disc, file, false);
            else
                setDiscInfo(disc, selectedFolder, true);

            if (discToEdit != null)
                discToEdit = null;

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
        newDisc.setCoverSrc(coverField.getText());
        newDisc.setExecutableSrc(file.getAbsolutePath());

        Main.addDisc(newDisc);
        controllerParent.addEpisodeCard(newDisc);
    }
}
