package org.flossware.curses.api;

public class ScrollBar extends Component {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation;
    private int value = 0;
    private int minimum = 0;
    private int maximum = 100;
    private int visibleAmount = 10;

    public ScrollBar(int orientation) {
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
        renderLock.lock();
        try {
            return value;
        } finally {
            renderLock.unlock();
        }
    }

    public int getMinimum() {
        renderLock.lock();
        try {
            return minimum;
        } finally {
            renderLock.unlock();
        }
    }

    public void setMinimum(int minimum) {
        renderLock.lock();
        try {
            this.minimum = minimum;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public int getMaximum() {
        renderLock.lock();
        try {
            return maximum;
        } finally {
            renderLock.unlock();
        }
    }

    public void setMaximum(int maximum) {
        renderLock.lock();
        try {
            this.maximum = Math.max(0, maximum);
            this.value = Math.min(this.value, this.maximum);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public int getVisibleAmount() {
        renderLock.lock();
        try {
            return visibleAmount;
        } finally {
            renderLock.unlock();
        }
    }

    public void setVisibleAmount(int visibleAmount) {
        renderLock.lock();
        try {
            this.visibleAmount = visibleAmount;
        } finally {
            renderLock.unlock();
        }
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
        int value, minimum, maximum, visibleAmount;
        renderLock.lock();
        try {
            value = this.value;
            minimum = this.minimum;
            maximum = this.maximum;
            visibleAmount = this.visibleAmount;
        } finally {
            renderLock.unlock();
        }

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
        int value, minimum, maximum, visibleAmount;
        renderLock.lock();
        try {
            value = this.value;
            minimum = this.minimum;
            maximum = this.maximum;
            visibleAmount = this.visibleAmount;
        } finally {
            renderLock.unlock();
        }

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
