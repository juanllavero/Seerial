package com.example.executablelauncher.utils;

import com.example.executablelauncher.DesktopViewController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import xss.it.fx.AbstractDecoration;
import xss.it.fx.helpers.HitSpot;
import xss.it.fx.helpers.State;

import java.util.ArrayList;
import java.util.List;

import static com.example.executablelauncher.utils.Utils.*;
import static com.example.executablelauncher.utils.Utils.CLOSE_SHAPE;

/**
 * @author XDSSWAR
 * Created on 09/30/2023
 */
public class WindowDecoration extends AbstractDecoration {
    /**
     * An AnchorPane that serves as the main content container.
     */
    private final AnchorPane container;

    /**
     * A list of HitSpot objects, used for tracking interaction spots.
     */
    private final List<HitSpot> spots;
    private DesktopViewController parentDesktop;

    /**
     * Constructor for the WindowDecoration class.
     *
     * @param stage The JavaFX Stage object associated with the window.
     * @param showInTaskBar Indicates whether the window should be shown in the taskbar.
     */
    public WindowDecoration(Stage stage, boolean showInTaskBar) {
        // Call the constructor of the parent class (likely a custom class that extends from AnchorPane)
        // and pass the provided Stage and showInTaskBar flag to it.
        super(stage, showInTaskBar);
        container = new AnchorPane();
        spots = new ArrayList<>();
    }
    public void setDesktopParent(DesktopViewController desktopParent){
        this.parentDesktop = desktopParent;
    }
    /**
     * Initialize the UI elements and their properties.
     */
    public void initialize(HBox leftArea, HBox rightArea) {
        // Configure the container AnchorPane.
        AnchorPane.setBottomAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setRightAnchor(container, 0.0);
        AnchorPane.setTopAnchor(container, 0.0);
        getChildren().add(container);

        windowStateProperty().addListener((observable, oldValue, state) -> {
            // Update the maximize/restore button's icon based on the window state.
            switch (state) {
                case NORMAL -> parentDesktop.onRestoreWindow(); // Set to "maximize" icon.
                case MAXIMIZED -> parentDesktop.onMaximizeWindow(); // Set to "restore" icon.
            }
        });

        // Build the HitSpot elements.
        buildHitSports(leftArea, rightArea);
    }

    /**
     * Create and configure HitSpot objects for the UI buttons (minimize, maximize/restore, close).
     * These objects define regions where user interactions are detected.
     * This step is very important. css styles must be handled this way only. Do not waste time using PseudoClasses
     */
    private void buildHitSports(HBox leftArea, HBox rightArea) {
        // Create and configure a HitSpot for the minimize button.
        /*HitSpot minSpot = HitSpot.builder()
                .control(minButton)
                .maximize(false)
                .minimize(true)
                .close(false)
                .systemMenu(false)
                .build();*/

        // Create and configure a HitSpot for the maximize/restore button.
        /*HitSpot maxSpot = HitSpot.builder()
                .control(maxButton)
                .maximize(true)
                .minimize(false)
                .close(false)
                .systemMenu(false)
                .build();*/

        // Create and configure a HitSpot for the close button.
        /*HitSpot closeSpot = HitSpot.builder()
                .control(closeButton)
                .maximize(false)
                .minimize(false)
                .close(true)
                .systemMenu(false)
                .build();*/

        HitSpot leftAreaSpot = HitSpot.builder()
                .control(leftArea)
                .maximize(false)
                .minimize(false)
                .close(false)
                .systemMenu(false)
                .build();

        HitSpot rightAreaSpot = HitSpot.builder()
                .control(rightArea)
                .maximize(false)
                .minimize(false)
                .close(false)
                .systemMenu(false)
                .build();

        // Add all the configured HitSpot objects to the spots list.
        spots.addAll(List.of(leftAreaSpot, rightAreaSpot));

        /*
         * NOTE : You can still add new HitSpots to this list later and they will be accepted
         * by the native side.
         * Example: a client button in the Header area,like settings button next to action buttons
         *
         * HitSpot clientSpot = HitSpot.builder()
                .control(clientBtn)
                .maximize(false)
                .minimize(false)
                .close(false)
                .systemMenu(false)
                .build();
         *
         * Since all flags are false, the button will be handles as if it where located in the Client Area
         */
    }

    public void minimizeWindow(){
        setWindowState(State.MINIMIZED);
    }

    public void maximizeWindow(){
        setWindowState(State.MAXIMIZED);
    }

    public void restoreWindow(){
        setWindowState(State.NORMAL);
    }

    /**
     * Returns a list of HitSpot objects representing interaction areas on the window decoration.
     *
     * @return A list of HitSpot objects.
     */
    @Override
    public List<HitSpot> getHitSpots() {
        return spots;
    }

    /**
     * Returns the height of the title bar in the window decoration.
     *
     * @return The height of the title bar.
     */
    @Override
    public double getTitleBarHeight() {
        return container.getHeight();
    }
}