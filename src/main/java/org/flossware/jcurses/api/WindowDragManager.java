package org.flossware.jcurses.api;

import org.flossware.jcurses.events.MouseEvent;
import org.flossware.jcurses.ffi.NcursesBridge;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages drag-to-move and resize operations for {@link DraggableWindow} instances.
 *
 * <p>This singleton class tracks active drag state and provides thread-safe handling
 * of mouse events to enable window manipulation. It supports:
 * <ul>
 *   <li>Moving windows by dragging the title bar</li>
 *   <li>Resizing windows by dragging edges or corners</li>
 *   <li>Size constraints (min/max width/height)</li>
 *   <li>Parent bounds constraints</li>
 *   <li>Layout invalidation</li>
 * </ul>
 *
 * <p><strong>Usage:</strong> JFrame and other window components delegate mouse events
 * to this manager via {@link #handleMouseEvent(MouseEvent, Component)}. The manager
 * determines if the event should trigger a drag/resize operation and handles the
 * drag lifecycle.
 *
 * <p><strong>Thread Safety:</strong> All operations are protected by a ReentrantLock
 * to ensure safe concurrent access.
 *
 * @since 1.6
 */
public class WindowDragManager {

    private static final WindowDragManager instance = new WindowDragManager();
    private final ReentrantLock lock = new ReentrantLock();

    // Active drag state
    private DragOperation currentDrag = null;

    // Hit zone constants
    private static final int HIT_ZONE_NONE = 0;
    private static final int HIT_ZONE_TITLE_BAR = 1;
    private static final int HIT_ZONE_TOP_EDGE = 2;
    private static final int HIT_ZONE_BOTTOM_EDGE = 3;
    private static final int HIT_ZONE_LEFT_EDGE = 4;
    private static final int HIT_ZONE_RIGHT_EDGE = 5;
    private static final int HIT_ZONE_TOP_LEFT = 6;
    private static final int HIT_ZONE_TOP_RIGHT = 7;
    private static final int HIT_ZONE_BOTTOM_LEFT = 8;
    private static final int HIT_ZONE_BOTTOM_RIGHT = 9;

    private WindowDragManager() {
    }

    /**
     * Get the singleton instance of WindowDragManager.
     *
     * @return the singleton instance
     */
    public static WindowDragManager getInstance() {
        return instance;
    }

    /**
     * Handle a mouse event for potential drag/resize operation.
     *
     * <p>This method should be called by window components when they receive mouse events.
     * It returns true if the event was consumed by a drag operation, false otherwise.
     *
     * @param event  the mouse event
     * @param window the window component
     * @return true if the event was consumed by a drag operation
     */
    public boolean handleMouseEvent(MouseEvent event, Component window) {
        if (!(window instanceof DraggableWindow draggable)) {
            return false;
        }

        lock.lock();
        try {
            long buttonState = event.button();

            // Check for drag start (BUTTON1_PRESSED or BUTTON1_CLICKED)
            // ncurses may send different events depending on REPORT_MOUSE_POSITION setting
            if ((buttonState & (NcursesBridge.BUTTON1_PRESSED | NcursesBridge.BUTTON1_CLICKED)) != 0) {
                if (currentDrag == null) {
                    // Start new drag
                    return startDrag(event, window, draggable);
                } else if (currentDrag.window == window) {
                    // Continue existing drag
                    return continueDrag(event);
                }
            }

            // Check for drag end (BUTTON1_RELEASED)
            if ((buttonState & NcursesBridge.BUTTON1_RELEASED) != 0) {
                if (currentDrag != null && currentDrag.window == window) {
                    return endDrag(event);
                }
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Start a new drag operation.
     */
    private boolean startDrag(MouseEvent event, Component window, DraggableWindow draggable) {
        int hitZone = detectHitZone(event, window);

        if (hitZone == HIT_ZONE_NONE) {
            return false;  // Not in a draggable zone
        }

        // Check permissions
        if (hitZone == HIT_ZONE_TITLE_BAR && !draggable.isDraggable()) {
            return false;
        }

        if (hitZone != HIT_ZONE_TITLE_BAR && !draggable.isResizable()) {
            return false;
        }

        currentDrag = new DragOperation(
                window,
                hitZone,
                event.x(),
                event.y(),
                window.getX(),
                window.getY(),
                window.getWidth(),
                window.getHeight()
        );

        return true;
    }

    /**
     * Continue an existing drag operation.
     */
    private boolean continueDrag(MouseEvent event) {
        int deltaX = event.x() - currentDrag.lastMouseX;
        int deltaY = event.y() - currentDrag.lastMouseY;

        if (deltaX == 0 && deltaY == 0) {
            return true;  // No movement
        }

        Component window = currentDrag.window;
        DraggableWindow draggable = (DraggableWindow) window;

        switch (currentDrag.hitZone) {
            case HIT_ZONE_TITLE_BAR:
                // Move window
                window.setLocation(
                        window.getX() + deltaX,
                        window.getY() + deltaY
                );
                break;

            case HIT_ZONE_RIGHT_EDGE:
                // Resize width (expand right)
                resizeWindow(window, draggable, 0, 0, deltaX, 0);
                break;

            case HIT_ZONE_LEFT_EDGE:
                // Resize width (expand left) - move window and adjust width
                resizeWindow(window, draggable, deltaX, 0, -deltaX, 0);
                break;

            case HIT_ZONE_BOTTOM_EDGE:
                // Resize height (expand down)
                resizeWindow(window, draggable, 0, 0, 0, deltaY);
                break;

            case HIT_ZONE_TOP_EDGE:
                // Resize height (expand up) - move window and adjust height
                resizeWindow(window, draggable, 0, deltaY, 0, -deltaY);
                break;

            case HIT_ZONE_TOP_LEFT:
                // Resize both dimensions (top-left corner)
                resizeWindow(window, draggable, deltaX, deltaY, -deltaX, -deltaY);
                break;

            case HIT_ZONE_TOP_RIGHT:
                // Resize both dimensions (top-right corner)
                resizeWindow(window, draggable, 0, deltaY, deltaX, -deltaY);
                break;

            case HIT_ZONE_BOTTOM_LEFT:
                // Resize both dimensions (bottom-left corner)
                resizeWindow(window, draggable, deltaX, 0, -deltaX, deltaY);
                break;

            case HIT_ZONE_BOTTOM_RIGHT:
                // Resize both dimensions (bottom-right corner)
                resizeWindow(window, draggable, 0, 0, deltaX, deltaY);
                break;
        }

        currentDrag.lastMouseX = event.x();
        currentDrag.lastMouseY = event.y();

        window.repaint();
        return true;
    }

    /**
     * Resize a window with constraints.
     */
    private void resizeWindow(Component window, DraggableWindow draggable,
                              int deltaX, int deltaY, int deltaW, int deltaH) {
        int newX = window.getX() + deltaX;
        int newY = window.getY() + deltaY;
        int newWidth = window.getWidth() + deltaW;
        int newHeight = window.getHeight() + deltaH;

        // Apply minimum size constraints
        newWidth = Math.max(draggable.getMinWidth(), newWidth);
        newHeight = Math.max(draggable.getMinHeight(), newHeight);

        // Apply maximum size constraints
        if (draggable.getMaxWidth() > 0) {
            newWidth = Math.min(draggable.getMaxWidth(), newWidth);
        }
        if (draggable.getMaxHeight() > 0) {
            newHeight = Math.min(draggable.getMaxHeight(), newHeight);
        }

        // Constrain to parent bounds if parent exists
        Component parent = window.getParent();
        if (parent != null) {
            newX = Math.max(0, Math.min(newX, parent.getWidth() - newWidth));
            newY = Math.max(0, Math.min(newY, parent.getHeight() - newHeight));
        }

        window.setLocation(newX, newY);
        window.setSize(newWidth, newHeight);

        // Invalidate layout for containers
        if (window instanceof Container container) {
            container.invalidateLayout();
        }
    }

    /**
     * End the current drag operation.
     */
    private boolean endDrag(MouseEvent event) {
        currentDrag = null;
        return true;
    }

    /**
     * Detect which hit zone the mouse event is in.
     */
    private int detectHitZone(MouseEvent event, Component window) {
        int x = event.x();
        int y = event.y();
        int wx = window.getX();
        int wy = window.getY();
        int ww = window.getWidth();
        int wh = window.getHeight();

        // Check if inside window bounds
        if (x < wx || x >= wx + ww || y < wy || y >= wy + wh) {
            return HIT_ZONE_NONE;
        }

        boolean onLeft = (x == wx);
        boolean onRight = (x == wx + ww - 1);
        boolean onTop = (y == wy);
        boolean onBottom = (y == wy + wh - 1);

        // Check corners first (higher priority)
        if (onLeft && onTop) return HIT_ZONE_TOP_LEFT;
        if (onRight && onTop) return HIT_ZONE_TOP_RIGHT;
        if (onLeft && onBottom) return HIT_ZONE_BOTTOM_LEFT;
        if (onRight && onBottom) return HIT_ZONE_BOTTOM_RIGHT;

        // Check edges
        if (onTop) {
            // Title bar is top edge, excluding corners
            return HIT_ZONE_TITLE_BAR;
        }
        if (onBottom) return HIT_ZONE_BOTTOM_EDGE;
        if (onLeft) return HIT_ZONE_LEFT_EDGE;
        if (onRight) return HIT_ZONE_RIGHT_EDGE;

        return HIT_ZONE_NONE;
    }

    /**
     * Check if a drag operation is currently in progress.
     *
     * @return true if dragging, false otherwise
     */
    public boolean isDragging() {
        lock.lock();
        try {
            return currentDrag != null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Cancel any active drag operation.
     * This can be useful when ESC is pressed or the window becomes invisible.
     */
    public void cancelDrag() {
        lock.lock();
        try {
            currentDrag = null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Internal class to track drag operation state.
     */
    private static class DragOperation {
        final Component window;
        final int hitZone;
        int lastMouseX;
        int lastMouseY;
        final int startWindowX;
        final int startWindowY;
        final int startWindowWidth;
        final int startWindowHeight;

        DragOperation(Component window, int hitZone, int mouseX, int mouseY,
                      int windowX, int windowY, int windowWidth, int windowHeight) {
            this.window = window;
            this.hitZone = hitZone;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.startWindowX = windowX;
            this.startWindowY = windowY;
            this.startWindowWidth = windowWidth;
            this.startWindowHeight = windowHeight;
        }
    }
}
