package org.flossware.jcurses.api;

import org.flossware.jcurses.events.MouseEvent;

public class JFrame extends Container implements DraggableWindow {
    private String title;
    private boolean visible = false;
    private boolean draggable = true;
    private boolean resizable = true;
    private int minWidth = 10;
    private int minHeight = 3;

    public JFrame() {
        this("");
    }

    public JFrame(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        renderLock.lock();
        try {
            this.title = title;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public String getTitle() {
        return title;
    }

    public void setVisible(boolean visible) {
        renderLock.lock();
        try {
            this.visible = visible;
            if (visible) {
                RootPane.getInstance().add(this);
            } else {
                RootPane.getInstance().remove(this);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void paint(char[][] buffer) {
        if (!visible) return;

        drawBorder(buffer);

        if (title != null && !title.isEmpty() && width > 2) {
            String titleDisplay = "[ " + title + " ]";
            int titleX = getX() + (width - titleDisplay.length()) / 2;
            writeStringToBuffer(buffer, titleDisplay, titleX, getY());
        }

        super.paint(buffer);
    }

    // DraggableWindow interface implementation

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        renderLock.lock();
        try {
            this.draggable = draggable;
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        renderLock.lock();
        try {
            this.resizable = resizable;
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        renderLock.lock();
        try {
            this.minWidth = Math.max(5, minWidth);
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        renderLock.lock();
        try {
            this.minHeight = Math.max(3, minHeight);
        } finally {
            renderLock.unlock();
        }
    }

    // Mouse event handling with drag/resize support

    @Override
    public boolean handleMouseEvent(MouseEvent event) {
        // First, try drag/resize on the frame itself (borders, title bar)
        if (WindowDragManager.getInstance().handleMouseEvent(event, this)) {
            return true;  // Event consumed by drag/resize operation
        }

        // Check if event is in content area (not on borders)
        int mx = event.x();
        int my = event.y();
        boolean isInContentArea = mx > x && mx < x + width - 1 && my > y && my < y + height - 1;

        if (isInContentArea) {
            // Delegate to Container's default behavior (dispatch to children)
            return super.handleMouseEvent(event);
        }

        // Event is on border but not handled by drag manager (disabled)—don't consume
        return false;
    }
}
