package org.flossware.curses.api;

public class JStatusBar extends Component {
    private String text = "";

    public JStatusBar() {
        this("");
    }

    public JStatusBar(String text) {
        this.text = text;
    }

    public void setText(String text) {
        renderLock.lock();
        try {
            this.text = text;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public String getText() {
        return text;
    }

    @Override
    public void paint(char[][] buffer) {
        if (width <= 0) return;

        for (int i = 0; i < width; i++) {
            writeStringToBuffer(buffer, " ", getX() + i, getY());
        }

        if (text != null && !text.isEmpty()) {
            String displayText = text.substring(0, Math.min(text.length(), width));
            writeStringToBuffer(buffer, displayText, getX(), getY());
        }
    }
}
