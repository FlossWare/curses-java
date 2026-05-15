package org.flossware.jcurses.api;

import java.util.concurrent.locks.ReentrantLock;

public class JProgressBar extends Component {
    private double percent = 0.0;
    private final char fillChar = '#';
    private final char emptyChar = '.';

    public void setPercent(double percent) {
        renderLock.lock();
        try {
            this.percent = Math.clamp(percent, 0.0, 1.0);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public double getPercent() {
        return percent;
    }

    @Override
    public void paint(char[][] buffer) {
        int filledLength = (int) (getWidth() * percent);
        for (int i = 0; i < getWidth(); i++) {
            // Logic to write to the 2D back buffer [cite: 15]
            buffer[getY()][getX() + i] = (i < filledLength) ? fillChar : emptyChar;
        }
    }
}