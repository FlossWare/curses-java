package org.flossware.jcurses.api;

public class JFrame extends Container {
    private String title;
    private boolean visible = false;

    public JFrame() {
        this("");
    }

    public JFrame(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        renderLock.lock();
        try {
            this.title = title;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public String getTitle() {
        return title;
    }

    public void setVisible(boolean visible) {
        renderLock.lock();
        try {
            this.visible = visible;
            if (visible) {
                RootPane.getInstance().add(this);
            } else {
                RootPane.getInstance().remove(this);
            }
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void paint(char[][] buffer) {
        if (!visible) return;

        drawBorder(buffer);

        if (title != null && !title.isEmpty() && width > 2) {
            String titleDisplay = "[ " + title + " ]";
            int titleX = getX() + (width - titleDisplay.length()) / 2;
            writeStringToBuffer(buffer, titleDisplay, titleX, getY());
        }

        super.paint(buffer);
    }
}
