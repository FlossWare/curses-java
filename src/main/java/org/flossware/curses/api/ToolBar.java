package org.flossware.curses.api;

public class ToolBar extends Container {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation;

    public ToolBar() {
        this(HORIZONTAL);
    }

    public ToolBar(int orientation) {
        this.orientation = orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        doLayout();
        repaint();
    }

    @Override
    public void doLayout() {
        renderLock.lock();
        try {
            if (orientation == HORIZONTAL) {
                int currentX = x;
                for (Component child : children) {
                    child.setLocation(currentX, y);
                    currentX += child.getWidth() + 1;
                }
            } else {
                int currentY = y;
                for (Component child : children) {
                    child.setLocation(x, currentY);
                    currentY += child.getHeight() + 1;
                }
            }
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        super.paint(buffer);
    }
}
