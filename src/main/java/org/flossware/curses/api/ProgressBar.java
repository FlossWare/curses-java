package org.flossware.curses.api;

import java.util.concurrent.locks.ReentrantLock;

public class ProgressBar extends Component {
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
        renderLock.lock();
        try {
            return percent;
        } finally {
            renderLock.unlock();
        }
    }

    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            int filledLength = (int) (getWidth() * percent);
            int startY = getY();
            int startX = getX();
            for (int i = 0; i < getWidth(); i++) {
                // Use safe buffer access to prevent ArrayIndexOutOfBoundsException (Issue #64)
                char c = (i < filledLength) ? fillChar : emptyChar;
                writeCharToBuffer(buffer, startX + i, startY, c);
            }
        } finally {
            renderLock.unlock();
        }
    }
}