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

    @Override
    public boolean handleMouseEvent(MouseEvent event) {
        // Try drag/resize on frame borders/title bar first
        if (WindowDragManager.getInstance().handleMouseEvent(event, this)) {
            return true;  // Consumed by drag operation
        }

        // Otherwise, delegate to children (existing Container behavior)
        return super.handleMouseEvent(event);
    }

}
