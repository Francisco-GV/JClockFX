package resources.utilities;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

public class WindowDragger implements EventHandler<MouseEvent> {
    private double xOffset;
    private double yOffset;
    private final Window window;

    public static void addToParent(Window target, Parent parentDraggable) {
        WindowDragger windowDragger = new WindowDragger(target);

        parentDraggable.setOnMouseDragged(windowDragger);
        parentDraggable.setOnMousePressed(windowDragger);
    }

    public WindowDragger(Window window) {
        this.window = window;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            xOffset = window.getX() - event.getScreenX();
            yOffset = window.getY() - event.getScreenY();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            window.setX(event.getScreenX() + xOffset);
            window.setY(event.getScreenY() + yOffset);
        }
    }
}
