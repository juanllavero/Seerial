package com.example.executablelauncher;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CardController {
    @FXML
    private ImageView image;
    private Series series;
    private Controller parentController;

    public void setData(Series col){
        Image image = new Image(col.getCoverSrc(), 234, 351, true, true);
        this.image.setImage(image);
        series = col;
    }

    public void setParent(Controller col){
        parentController = col;
    }

    @FXML
    private void onMouseClick(MouseEvent event){
        if (event.getButton() == MouseButton.SECONDARY) {
            parentController.showContextMenu(series, this);
        }else{
            parentController.selectSeries(series);
        }
    }
}
