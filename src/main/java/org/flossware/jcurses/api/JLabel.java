package org.flossware.jcurses.api;

public class JLabel extends Component {
    private String text;
    private int alignment = ALIGN_LEFT;

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    public JLabel() {
        this("");
    }

    public JLabel(String text) {
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

    public void setAlignment(int alignment) {
        renderLock.lock();
        try {
            this.alignment = alignment;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    @Override
    public void paint(char[][] buffer) {
        if (text == null || text.isEmpty()) {
            return;
        }

        int x = getX();
        if (alignment == ALIGN_CENTER && width > 0) {
            x = getX() + (width - text.length()) / 2;
        } else if (alignment == ALIGN_RIGHT && width > 0) {
            x = getX() + width - text.length();
        }

        writeStringToBuffer(buffer, text, x, getY());
    }
}
