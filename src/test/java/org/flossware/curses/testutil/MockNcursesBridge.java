package org.flossware.curses.testutil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Mock implementation of NcursesBridge for integration testing.
 * Provides an in-memory terminal simulator without requiring actual ncurses library.
 */
public class MockNcursesBridge {
    private static final ThreadLocal<MockNcursesBridge> INSTANCE = ThreadLocal.withInitial(MockNcursesBridge::new);

    private char[][] screen;
    private int width;
    private int height;
    private final BlockingQueue<Integer> keyQueue;
    private final BlockingQueue<MouseEventData> mouseQueue;
    private boolean initialized;
    private boolean blocking;

    /**
     * Mouse event data for injection.
     */
    public static class MouseEventData {
        public final int x;
        public final int y;
        public final long buttonState;

        public MouseEventData(int x, int y, long buttonState) {
            this.x = x;
            this.y = y;
            this.buttonState = buttonState;
        }
    }

    private MockNcursesBridge() {
        this(80, 24);
    }

    public MockNcursesBridge(int width, int height) {
        this.width = width;
        this.height = height;
        this.screen = new char[height][width];
        this.keyQueue = new LinkedBlockingQueue<>();
        this.mouseQueue = new LinkedBlockingQueue<>();
        this.initialized = false;
        this.blocking = false;
        clearScreen();
    }

    /**
     * Get the mock instance for current thread.
     */
    public static MockNcursesBridge getInstance() {
        return INSTANCE.get();
    }

    /**
     * Reset the mock for current thread.
     */
    public static void reset() {
        INSTANCE.remove();
    }

    /**
     * Initialize the mock terminal.
     */
    public void init() {
        initialized = true;
        clearScreen();
    }

    /**
     * Stop the mock terminal.
     */
    public void stop() {
        initialized = false;
        keyQueue.clear();
        mouseQueue.clear();
    }

    /**
     * Check if mock terminal is initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Check if ncurses is available (always true for mock).
     */
    public boolean isAvailable() {
        return true;
    }

    /**
     * Set blocking mode for getChar().
     */
    public void setNonBlocking(boolean nonBlocking) {
        this.blocking = !nonBlocking;
    }

    /**
     * Get terminal height.
     */
    public int getTerminalHeight() {
        return height;
    }

    /**
     * Get terminal width.
     */
    public int getTerminalWidth() {
        return width;
    }

    /**
     * Set terminal size and resize internal buffer.
     * This method allows simulating terminal resize events in tests.
     * The internal screen buffer is resized to match the new dimensions.
     * Content from the old buffer is preserved where possible (top-left corner).
     * New areas are filled with spaces.
     *
     * @param newWidth the new terminal width (must be > 0)
     * @param newHeight the new terminal height (must be > 0)
     * @throws IllegalArgumentException if width or height is <= 0
     */
    public void setTerminalSize(int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Terminal dimensions must be positive: width=" + newWidth + ", height=" + newHeight);
        }

        if (this.width == newWidth && this.height == newHeight) {
            return; // No change needed
        }

        // Create new buffer with new dimensions
        char[][] newScreen = new char[newHeight][newWidth];

        // Initialize new buffer with spaces
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newScreen[i][j] = ' ';
            }
        }

        // Copy existing content (preserve top-left corner)
        int copyHeight = Math.min(this.height, newHeight);
        int copyWidth = Math.min(this.width, newWidth);
        for (int i = 0; i < copyHeight; i++) {
            System.arraycopy(screen[i], 0, newScreen[i], 0, copyWidth);
        }

        // Update dimensions and screen buffer
        this.width = newWidth;
        this.height = newHeight;
        this.screen = newScreen;
    }

    /**
     * Get next character from input queue.
     * Returns -1 if no input available (non-blocking mode).
     */
    public int getChar() {
        try {
            if (blocking) {
                Integer ch = keyQueue.poll(100, TimeUnit.MILLISECONDS);
                return ch != null ? ch : -1;
            } else {
                Integer ch = keyQueue.poll();
                return ch != null ? ch : -1;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    /**
     * Move cursor and write character.
     */
    public void moveCursor(int y, int x, char ch) {
        if (y >= 0 && y < height && x >= 0 && x < width) {
            screen[y][x] = ch;
        }
    }

    /**
     * Clear the screen.
     */
    public void clear() {
        clearScreen();
    }

    /**
     * Refresh the screen (no-op for mock).
     */
    public void refresh() {
        // No-op in mock - screen is always up to date
    }

    /**
     * Start color support (no-op for mock).
     */
    public void startColor() {
        // No-op in mock
    }

    /**
     * Initialize color pair (no-op for mock).
     */
    public void initColorPair(int pairNum, int fg, int bg) {
        // No-op in mock
    }

    /**
     * Enable mouse events (no-op for mock).
     */
    public void enableMouse(long mask) {
        // No-op in mock
    }

    /**
     * Get mouse event (returns null if no events).
     */
    public MouseEventData getMouseEvent() {
        return mouseQueue.poll();
    }

    // ===== Test Helper Methods =====

    /**
     * Inject a keyboard event into the input queue.
     */
    public void injectKey(int keyCode) {
        keyQueue.offer(keyCode);
    }

    /**
     * Inject multiple keyboard events.
     */
    public void injectKeySequence(int... keyCodes) {
        for (int code : keyCodes) {
            keyQueue.offer(code);
        }
    }

    /**
     * Inject a mouse event into the input queue.
     */
    public void injectMouse(int x, int y, long buttonState) {
        mouseQueue.offer(new MouseEventData(x, y, buttonState));
    }

    /**
     * Capture current screen state as a copy.
     */
    public char[][] captureScreen() {
        char[][] copy = new char[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(screen[i], 0, copy[i], 0, width);
        }
        return copy;
    }

    /**
     * Get character at specific screen position.
     */
    public char getCharAt(int y, int x) {
        if (y >= 0 && y < height && x >= 0 && x < width) {
            return screen[y][x];
        }
        return ' ';
    }

    /**
     * Get text from screen row.
     */
    public String getRow(int y) {
        if (y >= 0 && y < height) {
            return new String(screen[y]);
        }
        return "";
    }

    /**
     * Clear input queues.
     */
    public void clearInput() {
        keyQueue.clear();
        mouseQueue.clear();
    }

    /**
     * Check if there's pending keyboard input.
     */
    public boolean hasKeyInput() {
        return !keyQueue.isEmpty();
    }

    /**
     * Check if there's pending mouse input.
     */
    public boolean hasMouseInput() {
        return !mouseQueue.isEmpty();
    }

    private void clearScreen() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                screen[i][j] = ' ';
            }
        }
    }
}
