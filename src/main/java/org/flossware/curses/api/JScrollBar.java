package org.flossware.curses.api;

public class JScrollBar extends Component {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation;
    private int value = 0;
    private int minimum = 0;
    private int maximum = 100;
    private int visibleAmount = 10;

    public JScrollBar(int orientation) {
        this.orientation = orientation;
    }

    public void setValue(int value) {
        renderLock.lock();
        try {
            this.value = Math.clamp(value, minimum, maximum - visibleAmount);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public int getValue() {
        return value;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
        repaint();
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
        repaint();
    }

    public void setVisibleAmount(int visibleAmount) {
        this.visibleAmount = visibleAmount;
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        if (orientation == HORIZONTAL) {
            paintHorizontal(buffer);
        } else {
            paintVertical(buffer);
        }
    }

    private void paintHorizontal(char[][] buffer) {
        int range = maximum - minimum;
        if (range <= 0 || width <= 2) return;

        int thumbSize = Math.max(1, (visibleAmount * width) / range);
        int thumbPos = ((value - minimum) * (width - thumbSize)) / (range - visibleAmount);

        for (int i = 0; i < width; i++) {
            char c = (i >= thumbPos && i < thumbPos + thumbSize) ? '#' : '.';
            writeStringToBuffer(buffer, String.valueOf(c), getX() + i, getY());
        }
    }

    private void paintVertical(char[][] buffer) {
        int range = maximum - minimum;
        if (range <= 0 || height <= 2) return;

        int thumbSize = Math.max(1, (visibleAmount * height) / range);
        int thumbPos = ((value - minimum) * (height - thumbSize)) / (range - visibleAmount);

        for (int i = 0; i < height; i++) {
            char c = (i >= thumbPos && i < thumbPos + thumbSize) ? '#' : '.';
            writeStringToBuffer(buffer, String.valueOf(c), getX(), getY() + i);
        }
    }
}
