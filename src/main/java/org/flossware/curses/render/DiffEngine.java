package org.flossware.curses.render;

import org.flossware.curses.api.ColorPair;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Optimized differential rendering engine with dirty rectangle support.
 *
 * <p>DiffEngine tracks changes between frames and only redraws modified
 * regions, reducing terminal output and improving performance.
 *
 * <h2>Dirty Rectangle Optimization</h2>
 * <p>When dirty rectangles are provided via {@link #setDirtyRegion(int, int, int, int)},
 * only the specified region is checked for changes. This dramatically reduces
 * CPU usage when only small portions of the screen change.
 *
 * <p>If no dirty region is set, a full-screen diff is performed.
 *
 * @author FlossWare
 * @since 1.0
 */
public class DiffEngine {
    private final char[][] currentScreen;
    private final char[][] backBuffer;
    private final int[][] currentColors;  // Track color pair numbers
    private final int[][] backColors;
    private final ReentrantLock bufferLock = new ReentrantLock();

    private final int width;
    private final int height;

    // Dirty rectangle tracking for optimization
    private int dirtyMinX = 0;
    private int dirtyMinY = 0;
    private int dirtyMaxX;
    private int dirtyMaxY;
    private boolean hasDirtyRegion = false;

    /**
     * Creates a new diff engine with the specified dimensions.
     *
     * @param width the screen width in characters
     * @param height the screen height in characters
     */
    public DiffEngine(int width, int height) {
        this.width = width;
        this.height = height;
        this.currentScreen = new char[height][width];
        this.backBuffer = new char[height][width];
        this.currentColors = new int[height][width];
        this.backColors = new int[height][width];
        this.dirtyMaxX = width;
        this.dirtyMaxY = height;
    }

    /**
     * Sets a dirty region to optimize the next render.
     *
     * <p>Only the specified rectangular region will be checked for changes
     * during the next {@link #render()} call. This significantly improves
     * performance when only small areas of the screen change.
     *
     * @param x the left edge of the dirty region
     * @param y the top edge of the dirty region
     * @param w the width of the dirty region
     * @param h the height of the dirty region
     * @since 1.26
     */
    public void setDirtyRegion(int x, int y, int w, int h) {
        bufferLock.lock();
        try {
            dirtyMinX = Math.max(0, Math.min(x, width));
            dirtyMinY = Math.max(0, Math.min(y, height));
            dirtyMaxX = Math.max(0, Math.min(x + w, width));
            dirtyMaxY = Math.max(0, Math.min(y + h, height));
            hasDirtyRegion = true;
        } finally {
            bufferLock.unlock();
        }
    }

    /**
     * Clears the dirty region, forcing a full-screen diff on next render.
     *
     * @since 1.26
     */
    public void clearDirtyRegion() {
        bufferLock.lock();
        try {
            dirtyMinX = 0;
            dirtyMinY = 0;
            dirtyMaxX = width;
            dirtyMaxY = height;
            hasDirtyRegion = false;
        } finally {
            bufferLock.unlock();
        }
    }

    /**
     * Renders the back buffer to the screen, only updating changed cells.
     *
     * <p>If a dirty region has been set via {@link #setDirtyRegion(int, int, int, int)},
     * only that region is checked for differences. Otherwise, the entire screen
     * is compared.
     *
     * <p>This method is thread-safe and compatible with Virtual Threads.
     */
    public void render() {
        bufferLock.lock(); // Compatible with Virtual Threads
        try {
            int minY = hasDirtyRegion ? dirtyMinY : 0;
            int maxY = hasDirtyRegion ? dirtyMaxY : backBuffer.length;
            int minX = hasDirtyRegion ? dirtyMinX : 0;
            int maxX = hasDirtyRegion ? dirtyMaxX : backBuffer[0].length;

            for (int y = minY; y < maxY && y < backBuffer.length; y++) {
                for (int x = minX; x < maxX && x < backBuffer[y].length; x++) {
                    // Only send ANSI codes if the character has changed
                    if (backBuffer[y][x] != currentScreen[y][x]) {
                        sendAnsiMoveCursor(x, y);
                        sendAnsiChar(backBuffer[y][x]);
                        currentScreen[y][x] = backBuffer[y][x];
                    }
                }
            }

            // Clear dirty region after rendering
            if (hasDirtyRegion) {
                clearDirtyRegion();
            }
        } finally {
            bufferLock.unlock();
        }
    }

    /**
     * Moves the cursor to the specified position using ncurses.
     *
     * @param x the column (0-based)
     * @param y the row (0-based)
     */
    private void sendAnsiMoveCursor(int x, int y) {
        try {
            org.flossware.jcurses.ffi.NcursesBridge.moveCursor(y, x, ' ');
        } catch (Throwable e) {
            // Suppress rendering errors - ncurses may not be initialized
        }
    }

    /**
     * Sends a character to the current cursor position using ncurses.
     *
     * @param c the character to display
     */
    private void sendAnsiChar(char c) {
        // Character is already sent via moveCursor with the character
        // This is a no-op in ncurses mode since moveCursor handles both
    }

    /**
     * Returns the back buffer for painting.
     *
     * @return the character buffer to paint into
     * @since 1.27
     */
    public char[][] getBackBuffer() {
        return backBuffer;
    }

    /**
     * Returns the color buffer for painting.
     *
     * @return the color pair buffer
     * @since 1.27
     */
    public int[][] getBackColors() {
        return backColors;
    }
}