package org.flossware.jcurses.api;

public class JPanel extends Container {
    private boolean bordered = false;

    public JPanel() {
        this(null);
    }

    public JPanel(LayoutManager layout) {
        if (layout != null) {
            setLayout(layout);
        }
    }

    public void setBordered(boolean bordered) {
        renderLock.lock();
        try {
            this.bordered = bordered;
        } finally {
            renderLock.unlock();
        }
        repaint();
    }

    public boolean isBordered() {
        return bordered;
    }

    @Override
    public void paint(char[][] buffer) {
        if (bordered) {
            drawBorder(buffer);
        }
        super.paint(buffer);
    }
}
