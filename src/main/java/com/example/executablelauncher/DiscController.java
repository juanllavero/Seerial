package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class DiscController {
    @FXML
    private ImageView image;
    @FXML
    private Label name;

    private Disc disc;
    private SeasonController parentController = null;
    private DesktopViewController desktopController = null;

    public void setData(Disc d){
        Image image = new Image(d.getCoverSrc());

        this.image.setImage(image);
        this.image.setPreserveRatio(true);
        this.image.setSmooth(true);

        name.setText(d.getName());

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
            if (parentController != null)
                parentController.showDiscMenu(disc);
            else
                desktopController.openDiscMenu(disc);
        }else{
            if (parentController != null)
                parentController.playEpisode(disc);
            else
                desktopController.selectDisc(disc);
        }
    }
}
