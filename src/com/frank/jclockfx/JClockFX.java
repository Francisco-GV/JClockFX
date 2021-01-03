package com.frank.jclockfx;

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
        Canvas canvas = new Canvas(500, 500);
        ClockGraphics clockGraphics = new ClockGraphics(canvas.getGraphicsContext2D());

        AnchorPane anchorPane = new AnchorPane(canvas);
        anchorPane.setStyle("-fx-background-color: transparent;");

        MenuItem onTopItem = new MenuItem("Always on top");
        MenuItem muteItem = new MenuItem("Mute");
        MenuItem minimizeItem = new MenuItem("Minimize");
        MenuItem closeItem = new MenuItem("Close");
        ContextMenu contextMenu = new ContextMenu(onTopItem, muteItem, minimizeItem, closeItem);

        onTopItem.setOnAction(e -> {
            primaryStage.setAlwaysOnTop(!primaryStage.isAlwaysOnTop());
            onTopItem.setText("Always on top " + (primaryStage.isAlwaysOnTop() ? "\u2714" : " "));
        });
        muteItem.setOnAction(e -> {
            clockGraphics.setMute(!clockGraphics.isMute());
            muteItem.setText("Mute " + ((clockGraphics.isMute()) ? "\u2714" : " "));
        });
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


