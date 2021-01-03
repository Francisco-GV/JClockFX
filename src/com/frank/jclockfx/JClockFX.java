package com.frank.jclockfx;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import resources.utilities.WindowDragger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JClockFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(700, 700);
        ClockGraphics clockGraphics = new ClockGraphics(canvas.getGraphicsContext2D());

        AnchorPane anchorPane = new AnchorPane(canvas);
        anchorPane.setStyle("-fx-background-color: transparent;");

        CheckMenuItem onTopItem = new CheckMenuItem("Always on top");
        CheckMenuItem muteItem = new CheckMenuItem("Mute");
        Menu sizeMenu = new Menu("Size");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem minItem = new RadioMenuItem("Little");
        RadioMenuItem midItem = new RadioMenuItem("Medium");
        RadioMenuItem bigItem = new RadioMenuItem("Big");
        RadioMenuItem maxItem = new RadioMenuItem("Bigger");

        minItem.setToggleGroup(toggleGroup);
        midItem.setToggleGroup(toggleGroup);
        bigItem.setToggleGroup(toggleGroup);
        maxItem.setToggleGroup(toggleGroup);

        minItem.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                clockGraphics.setRadius(ClockGraphics.MIN_RADIUS);
            }
        });
        midItem.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                clockGraphics.setRadius(ClockGraphics.MID_RADIUS);
            }
        });
        bigItem.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                clockGraphics.setRadius(ClockGraphics.BIG_RADIUS);
            }
        });
        maxItem.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                clockGraphics.setRadius(ClockGraphics.MAX_RADIUS);
            }
        });

        bigItem.setSelected(true);

        sizeMenu.getItems().addAll(minItem, midItem, bigItem, maxItem);

        MenuItem minimizeItem = new MenuItem("Minimize");
        MenuItem closeItem = new MenuItem("Close");
        ContextMenu contextMenu = new ContextMenu(
                onTopItem,
                muteItem,
                new SeparatorMenuItem(),
                sizeMenu,
                new SeparatorMenuItem(),
                minimizeItem,
                closeItem);

        onTopItem.setSelected(false);
        muteItem.setSelected(false);

        onTopItem.selectedProperty().addListener((obs, old, newValue) -> primaryStage.setAlwaysOnTop(newValue));
        muteItem.selectedProperty().addListener((obs, old, newValue) -> clockGraphics.setMute(newValue));
        minimizeItem.setOnAction(e -> primaryStage.setIconified(true));
        closeItem.setOnAction(e -> primaryStage.close());
        anchorPane.setOnContextMenuRequested(e -> contextMenu.show(primaryStage, e.getScreenX(), e.getScreenY()));

        Scene scene = new Scene(anchorPane);
        scene.setFill(Color.TRANSPARENT);

        WindowDragger.addToParent(primaryStage, anchorPane);
        primaryStage.setTitle("JClockFX");
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setOnShown(e -> clockGraphics.start());
        primaryStage.setOnCloseRequest(e -> clockGraphics.finish());
        primaryStage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/resources/image/icon-clock-32x32.png"), 32, 32, true, true),
                new Image(getClass().getResourceAsStream("/resources/image/icon-clock-24x24.png"), 24, 24, true, true));
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}


