package org.flossware.curses.api;

public class SplitPane extends Container {
    public static final int HORIZONTAL_SPLIT = 0;
    public static final int VERTICAL_SPLIT = 1;

    private int orientation;
    private Component leftTop;
    private Component rightBottom;
    private double dividerLocation = 0.5;

    public SplitPane(int orientation) {
        this.orientation = orientation;
    }

    public SplitPane(int orientation, Component left, Component right) {
        this.orientation = orientation;
        setLeftComponent(left);
        setRightComponent(right);
    }

    public void setLeftComponent(Component comp) {
        renderLock.lock();
        try {
            if (leftTop != null) {
                remove(leftTop);
            }
            leftTop = comp;
            if (comp != null) {
                add(comp);
            }
        } finally {
            renderLock.unlock();
        }
        doLayout();
        repaint();
    }

    public void setRightComponent(Component comp) {
        renderLock.lock();
        try {
            if (rightBottom != null) {
                remove(rightBottom);
            }
            rightBottom = comp;
            if (comp != null) {
                add(comp);
            }
        } finally {
            renderLock.unlock();
        }
        doLayout();
        repaint();
    }

    public void setDividerLocation(double proportionalLocation) {
        renderLock.lock();
        try {
            this.dividerLocation = Math.clamp(proportionalLocation, 0.1, 0.9);
        } finally {
            renderLock.unlock();
        }
        doLayout();
        repaint();
    }

    public double getDividerLocation() {
        return dividerLocation;
    }

    public Component getLeftComponent() {
        return leftTop;
    }

    public Component getRightComponent() {
        return rightBottom;
    }

    public int getOrientation() {
        return orientation;
    }

    @Override
    public void doLayout() {
        renderLock.lock();
        try {
            if (orientation == HORIZONTAL_SPLIT) {
                int leftWidth = (int) (width * dividerLocation);
                int rightWidth = width - leftWidth - 1;

                if (leftTop != null) {
                    leftTop.setLocation(x, y);
                    leftTop.setSize(leftWidth, height);
                }

                if (rightBottom != null) {
                    rightBottom.setLocation(x + leftWidth + 1, y);
                    rightBottom.setSize(rightWidth, height);
                }
            } else {
                int topHeight = (int) (height * dividerLocation);
                int bottomHeight = height - topHeight - 1;

                if (leftTop != null) {
                    leftTop.setLocation(x, y);
                    leftTop.setSize(width, topHeight);
                }

                if (rightBottom != null) {
                    rightBottom.setLocation(x, y + topHeight + 1);
                    rightBottom.setSize(width, bottomHeight);
                }
            }
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        super.paint(buffer);

        if (orientation == HORIZONTAL_SPLIT) {
            int dividerX = x + (int) (width * dividerLocation);
            for (int i = 0; i < height; i++) {
                writeStringToBuffer(buffer, "|", dividerX, y + i);
            }
        } else {
            int dividerY = y + (int) (height * dividerLocation);
            for (int i = 0; i < width; i++) {
                writeStringToBuffer(buffer, "-", x + i, dividerY);
            }
        }
    }
}
