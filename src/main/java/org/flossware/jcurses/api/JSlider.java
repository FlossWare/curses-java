package org.flossware.jcurses.api;

public class JSlider extends Component {
    private int value = 50;
    private int minimum = 0;
    private int maximum = 100;
    private boolean showValue = true;

    public JSlider() {
        this(0, 100, 50);
    }

    public JSlider(int min, int max, int value) {
        this.minimum = min;
        this.maximum = max;
        this.value = Math.clamp(value, min, max);
    }

    public void setValue(int value) {
        renderLock.lock();
        try {
            this.value = Math.clamp(value, minimum, maximum);
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
        this.value = Math.clamp(value, minimum, maximum);
        repaint();
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
        this.value = Math.clamp(value, minimum, maximum);
        repaint();
    }

    public void setShowValue(boolean showValue) {
        this.showValue = showValue;
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        int range = maximum - minimum;
        if (range <= 0 || width <= 0) return;

        int sliderWidth = showValue ? width - 6 : width;
        int thumbPos = ((value - minimum) * sliderWidth) / range;

        StringBuilder slider = new StringBuilder();
        slider.append('[');
        for (int i = 0; i < sliderWidth; i++) {
            slider.append(i == thumbPos ? 'O' : '-');
        }
        slider.append(']');

        if (showValue) {
            slider.append(String.format(" %3d", value));
        }

        writeStringToBuffer(buffer, slider.toString(), getX(), getY());
    }
}
