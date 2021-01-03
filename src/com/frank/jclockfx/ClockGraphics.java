package com.frank.jclockfx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ClockGraphics {
    private final GraphicsContext g;
    private final ClockTimer clockTimer;
    private final Timer timer;
    private TimerTask executeTask;

    private final double radius = 250.0;
    private double canvasWidth;
    private double canvasHeight;
    private double zeroX;
    private double zeroY;

    private MediaPlayer ticSound;
    private MediaPlayer tacSound;

    private boolean exit = false;

    public ClockGraphics(GraphicsContext graphicsContext) {
        this.g = graphicsContext;
        timer = new Timer();
        clockTimer = new ClockTimer();
        loadSound();
    }

    private void loadSound() {
        File ticFile = new File(getClass().getResource("/resources/sound/tic.wav").getPath());
        File tacFile = new File(getClass().getResource("/resources/sound/tac.wav").getPath());

        ticSound = new MediaPlayer(new Media(ticFile.toURI().toString()));
        tacSound = new MediaPlayer(new Media(tacFile.toURI().toString()));

        ticSound.setVolume(0.2);
        tacSound.setVolume(0.2);
    }


    public void start() {
        init();
        executeTask = new TimerTask() {
            private boolean tic;
            @Override
            public void run() {
                if (!exit) {
                    makeSound();
                    draw();
                }
            }

            int lastSecond = clockTimer.getSecond();
            private void makeSound() {
                if (lastSecond != clockTimer.getSecond()) {
                    if (tic) {
                        ticSound.play();
                        tacSound.stop();
                    } else {
                        tacSound.play();
                        ticSound.stop();
                    }
                    tic = !tic;
                    lastSecond = clockTimer.getSecond();
                }
            }
        };
        timer.scheduleAtFixedRate(executeTask, 0L, 100L);
    }

    public void finish() {
        ticSound.dispose();
        tacSound.dispose();
        executeTask.cancel();
        timer.cancel();
        exit = true;
    }

    public void init() {
        canvasWidth = g.getCanvas().getWidth();
        canvasHeight = g.getCanvas().getHeight();

        zeroX = canvasWidth / 2;
        zeroY = canvasHeight / 2;
        g.translate(zeroX, zeroY);
    }

    public void draw() {
        clear();
        drawBackground();
        drawLines(radius - 45, 15.0, 60, 1); // seconds
        drawLines(radius - 50, 20.0, 12, 2.5); // hours
        drawHand(360.0 / 60 * clockTimer.getSecond(), Color.WHITE, radius - 30, 25);
        drawHand(360.0 / 60 * clockTimer.getMinute(), Color.WHITESMOKE, radius - 100, 20);
        drawHand(360.0 / 12 * clockTimer.getHour()  , Color.LIGHTSLATEGRAY, radius - 175, 15);

        g.setFill(getColor(200, 200, 200, 1));
        g.fillOval(-7, -7, 14, 14);

        g.setStroke(Color.DARKRED);
        g.strokeOval(-zeroX + 20, -zeroY + 20, (radius - 20) * 2, (radius - 20) * 2);
    }

    private void drawLines(double radiusLocation, double size, int number, double width) {
        g.setLineWidth(width);
        for (int i = 0; i < number; i++) {
            double deg = 360.0 / number * i;
            double pointX1 = radiusLocation * Math.cos(Math.toRadians(deg));
            double pointY1 = radiusLocation * Math.sin(Math.toRadians(deg));
            double pointX2 = (radiusLocation + size) * Math.cos(Math.toRadians(deg));
            double pointY2 = (radiusLocation + size) * Math.sin(Math.toRadians(deg));

            g.setStroke(Color.WHITE);
            g.strokeLine(pointX1, pointY1, pointX2, pointY2);
        }
    }

    private void drawBackground() {
        g.setFill(getColor(20, 20, 20, 0.9));
        g.fillOval(-zeroX, -zeroY, radius * 2, radius * 2);
    }

    private void drawHand(double deg, Color handColor, double handSize, double minHandSize) {
        double pointX = handSize * Math.cos(Math.toRadians(deg - 90));
        double pointY = handSize * Math.sin(Math.toRadians(deg - 90));

        double minPointX = minHandSize * Math.cos(Math.toRadians(deg - 270));
        double minPointY = minHandSize * Math.sin(Math.toRadians(deg - 270));

        g.setLineCap(StrokeLineCap.ROUND);
        g.setLineJoin(StrokeLineJoin.ROUND);
        g.setLineWidth(2);
        g.setStroke(handColor);
        g.strokeLine(0, 0, (int) pointX, (int) pointY);

        g.setLineWidth(4);
        g.setStroke(handColor);
        g.strokeLine(0, 0, (int) minPointX, (int) minPointY);
    }

    private void clear() {
        g.clearRect(-zeroX, -zeroY, canvasWidth, canvasHeight);
    }

    private Color getColor(int r, int g, int b, double alpha) {
        double unit = 1.0 / 255;

        return Color.color(r * unit, g * unit, b * unit, alpha);
    }

    public void setMute(boolean mute) {
        ticSound.setMute(mute);
        tacSound.setMute(mute);
    }

    public boolean isMute() {
        return ticSound.isMute() && tacSound.isMute();
    }
}
