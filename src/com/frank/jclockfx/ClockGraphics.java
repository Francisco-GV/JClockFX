package com.frank.jclockfx;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;

public class ClockGraphics {
    private final GraphicsContext g;
    private final ClockTimer clockTimer;
    private final AnimationTimer timer;

    private double radius;
    private double unitPorcent;
    private double canvasWidth;
    private double canvasHeight;
    private double zeroX;
    private double zeroY;

    private Font numbersFont;
    // The font has custom numbers, so, yes, it makes sense
    private final String[] numberValues = new String[] { /*
             1    2    3     4    5     6     7     8     9,  10    11    12 */
            "1", "2", "3", "14", "4", "41", "42", "43", "15", "5", "51", "52"
    };

    private MediaPlayer ticSound;
    private MediaPlayer tacSound;

    public ClockGraphics(GraphicsContext graphicsContext) {
        setRadius(BIG_RADIUS);
        this.g = graphicsContext;
        clockTimer = new ClockTimer();

        timer = new AnimationTimer() {
            private boolean tic;
            @Override
            public void handle(long now) {
                makeSound();
                draw();
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

        timer.start();

        loadSound();
        loadFont();
    }

    private void loadSound() {
        File ticFile = new File(getClass().getResource("/resources/sound/tic.wav").getPath());
        File tacFile = new File(getClass().getResource("/resources/sound/tac.wav").getPath());

        ticSound = new MediaPlayer(new Media(ticFile.toURI().toString()));
        tacSound = new MediaPlayer(new Media(tacFile.toURI().toString()));

        ticSound.setVolume(0.4);
        tacSound.setVolume(0.4);
    }

    private void loadFont() {
        double fontPorcent = unitPorcent * 10;

        numbersFont = Font.loadFont(getClass()
                .getResourceAsStream("/resources/font/ancient_geek/geek.ttf"), fontPorcent);
    }


    public void start() {
        init();
    }

    public void finish() {
        ticSound.dispose();
        tacSound.dispose();
        timer.stop();
    }

    private void init() {
        canvasWidth = g.getCanvas().getWidth();
        canvasHeight = g.getCanvas().getHeight();

        zeroX = canvasWidth / 2;
        zeroY = canvasHeight / 2;
        g.translate(zeroX, zeroY);
    }

    private void draw() {
        clear();
        drawBackground();
        drawNumbers();

        double lineSecondPosition    = unitPorcent * 85;
        double lineHourPosition      = unitPorcent * 80;

        double lineSecondSize        = unitPorcent * 4;
        double lineHourSize          = unitPorcent * 8;

        double lineSecondWidth       = unitPorcent * 0.4;
        double lineHourWidth         = unitPorcent * 1;

        drawLines(lineSecondPosition, lineSecondSize, 60, lineSecondWidth); // seconds
        drawLines(lineHourPosition, lineHourSize, 12, lineHourWidth); // hours

        double secondHandSize    = unitPorcent * 65;
        double minuteHandSize    = unitPorcent * 50;
        double hourHandSize      = unitPorcent * 40;

        double secondHandWidth   = unitPorcent * 0.6;
        double minuteHandWidth   = unitPorcent * 1;
        double hourHandWidth     = unitPorcent * 1.4;

        drawHand(360.0 / 60 * clockTimer.getSecond(), Color.WHITE, secondHandSize, secondHandWidth);
        drawHand(360.0 / 60 * clockTimer.getMinute(), Color.WHITESMOKE, minuteHandSize, minuteHandWidth);
        drawHand(360.0 / 12 * clockTimer.getHour(), Color.FLORALWHITE, hourHandSize, hourHandWidth);

        double centerCircleRadius = unitPorcent * 3;

        g.setFill(getColor(200, 200, 200, 1));
        g.fillOval(-centerCircleRadius, -centerCircleRadius, centerCircleRadius * 2, centerCircleRadius * 2);

        double borderCircleRadius = unitPorcent * 92;

        g.setStroke(Color.DARKRED);
        g.strokeOval(-borderCircleRadius, -borderCircleRadius, borderCircleRadius * 2, borderCircleRadius * 2);
    }

    private void drawNumbers() {
        g.setFont(numbersFont);
        for (int i = 0; i < 12; i++) {
            double deg = (360.0 / 12 * i) - 60;
            double x = unitPorcent * 70 * Math.cos(Math.toRadians(deg));
            double y = unitPorcent * 70 * Math.sin(Math.toRadians(deg));

            Text number = new Text(numberValues[i]);
            number.setFont(numbersFont);

            double textWidth = number.getLayoutBounds().getWidth();
            double textHeight = number.getLayoutBounds().getHeight();

            g.setFill(Color.WHITE);
            g.fillText(number.getText(), x - textWidth / 2, y + textHeight / 2);
        }
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
        g.fillOval(-radius, -radius, radius * 2, radius * 2);
    }

    private void drawHand(double deg, Color handColor, double handSize, double handWidth) {
        double pointX = handSize * Math.cos(Math.toRadians(deg - 90));
        double pointY = handSize * Math.sin(Math.toRadians(deg - 90));

        double minSizePorcent = unitPorcent * 8;

        double minPointX = minSizePorcent * Math.cos(Math.toRadians(deg - 270));
        double minPointY = minSizePorcent * Math.sin(Math.toRadians(deg - 270));

        g.setLineCap(StrokeLineCap.ROUND);
        g.setLineJoin(StrokeLineJoin.ROUND);
        g.setLineWidth(handWidth);
        g.setStroke(handColor);
        g.strokeLine(0, 0, (int) pointX, (int) pointY);

        g.setLineWidth(handWidth * 1.5);
        g.setStroke(handColor);
        g.strokeLine(0, 0, (int) minPointX, (int) minPointY);
    }

    private void clear() {
        g.clearRect(-zeroX, -zeroY, canvasWidth, canvasHeight);
    }

    private Color getColor(int r, int g, int b, double alpha) {
        final double COLOR_UNIT = 1.0 / 255;

        return Color.color( r * COLOR_UNIT,
                            g * COLOR_UNIT,
                            b * COLOR_UNIT, alpha);
    }

    public void setMute(boolean mute) {
        ticSound.setMute(mute);
        tacSound.setMute(mute);
    }

    public void setRadius(double radius) {
        this.radius = radius;
        unitPorcent = radius / 100;
        loadFont();
    }

    public static final double MIN_RADIUS = 90.0;
    public static final double MID_RADIUS = 150.0;
    public static final double BIG_RADIUS = 250.0;
    public static final double MAX_RADIUS = 350.0;
}
