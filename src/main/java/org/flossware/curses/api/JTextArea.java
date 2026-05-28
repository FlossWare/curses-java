package org.flossware.curses.api;

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

    public void setText(String text) {
        renderLock.lock();
        try {
            lines.clear();
            if (text != null && !text.isEmpty()) {
                String[] split = text.split("\n");
                for (String line : split) {
                    lines.add(line);
                }
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public String getText() {
        renderLock.lock();
        try {
            return String.join("\n", lines);
        } finally {
            renderLock.unlock();
        }
    }

    public void clear() {
        renderLock.lock();
        try {
            lines.clear();
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public int getLineCount() {
        renderLock.lock();
        try {
            return lines.size();
        } finally {
            renderLock.unlock();
        }
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