package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AddCategoryController {
    @FXML
    private Label categoryError;

    @FXML
    private TextField categoryField;

    private DesktopViewController parentController;

    @FXML
    void cancelButton(MouseEvent event) {
        parentController.hideBackgroundShadow();
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(MouseEvent event) {
        if (Main.categoryExist(categoryField.getText())){
            categoryError.setText("The category already exists");
        }else{
            Main.addCategory(categoryField.getText());
            parentController.updateCategories();
            parentController.hideBackgroundShadow();
            cancelButton(event);
        }
    }

    public void setParent(DesktopViewController desktopViewController) {
        parentController = desktopViewController;
    }
}
