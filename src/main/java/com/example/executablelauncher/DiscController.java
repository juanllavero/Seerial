package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class DiscController {
    @FXML
    private Label name;
    @FXML
    private Label number;
    private Disc disc;
    private SeasonController parentController = null;
    private DesktopViewController desktopController = null;

    public void setData(Disc d) {
        number.setText(d.getName());
        disc = d;
    }

    public void setParent(SeasonController col){
        parentController = col;
    }

    public void setDesktopParent(DesktopViewController col){
        desktopController = col;
    }

    @FXML
    private void onMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            if (desktopController != null)
                desktopController.openDiscMenu(event, disc);
        }else {
            if (parentController != null)
                parentController.playEpisode(disc);
            else {
                VBox discBox = (VBox)event.getSource();

                if (event.isControlDown())
                    desktopController.controlSelectDisc(disc, discBox);
                else if (event.isShiftDown())
                    desktopController.shiftSelectDisc(disc, discBox);
                else
                    desktopController.selectDisc(disc, discBox);
            }
        }
    }
}