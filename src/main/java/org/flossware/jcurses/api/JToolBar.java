package org.flossware.jcurses.api;

public class JToolBar extends Container {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation;

    public JToolBar() {
        this(HORIZONTAL);
    }

    public JToolBar(int orientation) {
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
