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
    private SeasonController parentController;

    public void setData(Disc d){
        Image image = new Image(d.getCoverSrc());

        this.image.setImage(image);
        this.image.setPreserveRatio(true);
        this.image.setSmooth(true);
        //Rectangle2D rectagle2D = new Rectangle2D(20, 0, 200, 200);
        //this.image.setViewport(rectagle2D);

        name.setText(d.getName());

        disc = d;
    }

    public void setParent(SeasonController col){
        parentController = col;
    }

    @FXML
    private void onMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            parentController.showDiscMenu(disc);
        }else{
            parentController.playEpisode(disc);
        }
    }
}
