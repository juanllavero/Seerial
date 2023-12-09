package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AddCategoryController {
    @FXML
    private Label categoryError;

    @FXML
    private TextField categoryField;

    @FXML
    private Label title;

    @FXML
    private CheckBox showOnFullscreen;

    private DesktopViewController parentController;
    private boolean toEdit = false;
    private String catName;

    @FXML
    void cancelButton(MouseEvent event) {
        parentController.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setValues(String catName, boolean showFS){
        title.setText("EDIT CATEGORY");
        categoryField.setText(catName);
        showOnFullscreen.setSelected(showFS);
        toEdit = true;
        this.catName = catName;
    }

    @FXML
    void save(MouseEvent event) {
        if (toEdit){
            if (!catName.equals(categoryField.getText()) && Main.categoryExist(categoryField.getText())) {
                categoryError.setText("The category already exists");
            }else if (categoryField.getText().isEmpty()){
                categoryError.setText("The text field is empty");
            }else{
                Main.editCategory(categoryField.getText(), showOnFullscreen.isSelected());
                parentController.updateCategories();
                parentController.hideBackgroundShadow();
                cancelButton(event);
            }
        }else{
            if (Main.categoryExist(categoryField.getText())){
                categoryError.setText("The category already exists");
            }else if (categoryField.getText().isEmpty()){
                categoryError.setText("The text field is empty");
            }else{
                Main.addCategory(categoryField.getText(), showOnFullscreen.isSelected());
                parentController.updateCategories();
                parentController.hideBackgroundShadow();
                cancelButton(event);
            }
        }
    }

    public void setParent(DesktopViewController desktopViewController) {
        parentController = desktopViewController;
    }
}
