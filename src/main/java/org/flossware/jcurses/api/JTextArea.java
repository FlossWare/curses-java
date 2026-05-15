package org.flossware.jcurses.api;

import java.util.List;
import java.util.ArrayList;

public class JTextArea extends Component {
    private final List<String> lines = new ArrayList<>();
    private CursorPosition cursor = new CursorPosition(0, 0);

    public void append(String text) {
        renderLock.lock();
        try {
            lines.add(text);
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        renderLock.lock();
        try {
            int currentY = getY();
            for (String line : lines) {
                if (currentY >= getY() + getHeight()) {
                    break;
                }
                writeStringToBuffer(buffer, line, getX(), currentY);
                currentY++;
            }
        } finally {
            renderLock.unlock();
        }
    }
}