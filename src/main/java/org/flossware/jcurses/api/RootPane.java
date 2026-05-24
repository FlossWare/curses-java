package org.flossware.jcurses.api;

import static org.flossware.jcurses.api.Constants.*;

public class RootPane extends Container {
    private static final RootPane instance = new RootPane();
    private volatile boolean dirty = false;

    // Dirty rectangle tracking for partial repaints
    private int dirtyMinX = Integer.MAX_VALUE;
    private int dirtyMinY = Integer.MAX_VALUE;
    private int dirtyMaxX = Integer.MIN_VALUE;
    private int dirtyMaxY = Integer.MIN_VALUE;

    private RootPane() {
        this.x = 0;
        this.y = 0;
        this.width = DEFAULT_TERMINAL_WIDTH;
        this.height = DEFAULT_TERMINAL_HEIGHT;
    }

    public static RootPane getInstance() {
        return instance;
    }

    public synchronized void markDirty() {
        dirty = true;
        // Mark entire screen dirty
        markDirtyRegion(0, 0, width, height);
    }

    public synchronized void markDirtyRegion(int x, int y, int w, int h) {
        dirty = true;
        dirtyMinX = Math.min(dirtyMinX, x);
        dirtyMinY = Math.min(dirtyMinY, y);
        dirtyMaxX = Math.max(dirtyMaxX, x + w);
        dirtyMaxY = Math.max(dirtyMaxY, y + h);
    }

    public synchronized boolean isDirty() {
        return dirty;
    }

    public synchronized void clearDirty() {
        dirty = false;
        dirtyMinX = Integer.MAX_VALUE;
        dirtyMinY = Integer.MAX_VALUE;
        dirtyMaxX = Integer.MIN_VALUE;
        dirtyMaxY = Integer.MIN_VALUE;
    }

    /**
     * Get the dirty rectangle bounds.
     * Returns [minX, minY, maxX, maxY] or null if not dirty.
     */
    public synchronized int[] getDirtyBounds() {
        if (!dirty) {
            return null;
        }
        return new int[] {
            Math.max(0, dirtyMinX),
            Math.max(0, dirtyMinY),
            Math.min(width, dirtyMaxX),
            Math.min(height, dirtyMaxY)
        };
    }

    @Override
    public void paint(char[][] buffer) {
        super.paint(buffer);
    }
}
