package org.flossware.curses.api;

public class Panel extends Container {
    private boolean bordered = false;

    public Panel() {
        this(null);
    }

    public Panel(LayoutManager layout) {
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
    protected void drawBorder(char[][] buffer, int[][] colorBuffer) {
        if (bordered) {
            super.drawBorder(buffer, colorBuffer);
        }
    }
}
