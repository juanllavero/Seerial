package com.example.executablelauncher;

import com.example.executablelauncher.tmdbMetadata.groups.SeasonsGroup;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Objects;

public class SearchResultEpGroupCardController {
    @FXML
    private Label descriptionText;

    @FXML
    private Label episodesText;

    @FXML
    private Label middleComma;

    @FXML
    private Label groupsText;

    @FXML
    private Label titleText;

    public void initValues(SeasonsGroup group){
        titleText.setText(group.name);

        if (group.group_count > 0)
            groupsText.setText(group.group_count + " " + App.textBundle.getString("groups"));
        else
            groupsText.setText("");

        if (group.episode_count > 0)
            episodesText.setText(group.episode_count + " " + App.textBundle.getString("episodes").toLowerCase());
        else {
            episodesText.setText("");
            middleComma.setText("");
        }

        descriptionText.setText(Objects.requireNonNullElse(group.description, ""));
    }
}