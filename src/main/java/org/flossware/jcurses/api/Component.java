package org.flossware.jcurses.api;

import org.flossware.jcurses.events.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Component {
    protected int x, y, width, height;
    protected final ReentrantLock renderLock = new ReentrantLock();
    protected Component parent;
    protected final List<MouseListener> mouseListeners = new ArrayList<>();
    protected ColorPair colorPair = ColorPair.DEFAULT;

    public abstract void paint(char[][] buffer);

    public void setLocation(int x, int y) {
        renderLock.lock();
        try {
            this.x = x;
            this.y = y;
        } finally {
            renderLock.unlock();
        }
    }

    public void setSize(int width, int height) {
        renderLock.lock();
        try {
            this.width = width;
            this.height = height;
        } finally {
            renderLock.unlock();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public ColorPair getColorPair() {
        return colorPair;
    }

    public void setColorPair(ColorPair colorPair) {
        renderLock.lock();
        try {
            this.colorPair = colorPair;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public void repaint() {
        Component root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        if (root instanceof RootPane) {
            ((RootPane) root).markDirty();
        }
    }

    protected void writeStringToBuffer(char[][] buffer, String text, int x, int y) {
        if (y < 0 || y >= buffer.length || text == null) {
            return;
        }
        for (int i = 0; i < text.length(); i++) {
            int targetX = x + i;
            if (targetX >= 0 && targetX < buffer[y].length) {
                buffer[y][targetX] = text.charAt(i);
            }
        }
    }

    public void addMouseListener(MouseListener listener) {
        renderLock.lock();
        try {
            mouseListeners.add(listener);
        } finally {
            renderLock.unlock();
        }
    }

    public void removeMouseListener(MouseListener listener) {
        renderLock.lock();
        try {
            mouseListeners.remove(listener);
        } finally {
            renderLock.unlock();
        }
    }

    public boolean handleMouseEvent(MouseEvent event) {
        // Check if the click is within this component's bounds
        if (event.x() >= x && event.x() < x + width &&
            event.y() >= y && event.y() < y + height) {

            renderLock.lock();
            try {
                // Notify all mouse listeners
                for (MouseListener listener : mouseListeners) {
                    listener.onMouseEvent(event);
                }
            } finally {
                renderLock.unlock();
            }
            return true;
        }
        return false;
    }
}