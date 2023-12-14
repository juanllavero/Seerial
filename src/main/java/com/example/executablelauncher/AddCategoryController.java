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
    private Button cancelButton;

    @FXML
    private Label categoryError;

    @FXML
    private TextField categoryField;

    @FXML
    private Button saveButton;

    @FXML
    private CheckBox showOnFullscreen;

    @FXML
    private Label textFieldTitle;

    @FXML
    private Label title;

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
        title.setText(App.textBundle.getString("categoryWindowTitleEdit"));
        categoryField.setText(catName);
        showOnFullscreen.setSelected(showFS);
        toEdit = true;
        this.catName = catName;

        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
    }

    public void initValues(){
        title.setText(App.textBundle.getString("categoryWindowTitle"));
        showOnFullscreen.setSelected(false);
        toEdit = false;
        saveButton.setText(App.buttonsBundle.getString("saveButton"));
        cancelButton.setText(App.buttonsBundle.getString("cancelButton"));
        showOnFullscreen.setText(App.textBundle.getString("showOnFullscreen"));
    }

    @FXML
    void save(MouseEvent event) {
        if (toEdit){
            if (!catName.equals(categoryField.getText()) && App.categoryExist(categoryField.getText())) {
                categoryError.setText(App.textBundle.getString("categoryExists"));
            }else if (categoryField.getText().isEmpty()){
                categoryError.setText(App.textBundle.getString("emptyField"));
            }else{
                App.editCategory(categoryField.getText(), showOnFullscreen.isSelected());
                parentController.updateCategories();
                parentController.hideBackgroundShadow();
                cancelButton(event);
            }
        }else{
            if (App.categoryExist(categoryField.getText())){
                categoryError.setText(App.textBundle.getString("categoryExists"));
            }else if (categoryField.getText().isEmpty()){
                categoryError.setText(App.textBundle.getString("emptyField"));
            }else{
                App.addCategory(categoryField.getText(), showOnFullscreen.isSelected());
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
