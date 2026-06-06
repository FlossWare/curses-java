package org.flossware.curses.integration;

import org.flossware.curses.api.RootPane;
import org.flossware.curses.testutil.MockNcursesBridge;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Controlled event loop runner for integration tests.
 * Provides single-step, conditional, and async execution modes.
 */
public class EventLoopRunner {
    private final MockNcursesBridge bridge;
    private final RootPane root;
    private final char[][] buffer;
    private volatile boolean running;

    public EventLoopRunner(MockNcursesBridge bridge, RootPane root, char[][] buffer) {
        this.bridge = bridge;
        this.root = root;
        this.buffer = buffer;
        this.running = false;
    }

    /**
     * Execute one render + input cycle.
     */
    public void runCycle() {
        // Render if dirty
        if (root.isDirty()) {
            clearBuffer();
            root.paint(buffer);
            updateScreen();
            root.clearDirty();
        }

        // Process one input event
        int ch = bridge.getChar();
        if (ch != -1) {
            handleKey(ch);
        }

        // Process mouse events
        MockNcursesBridge.MouseEventData mouse = bridge.getMouseEvent();
        if (mouse != null) {
            handleMouse(mouse);
        }
    }

    /**
     * Run event loop until condition is met or timeout.
     */
    public void runUntilCondition(Supplier<Boolean> condition, Duration timeout) throws TimeoutException {
        long startTime = System.currentTimeMillis();
        long timeoutMillis = timeout.toMillis();

        while (!condition.get()) {
            runCycle();

            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw new TimeoutException(
                    "Condition not met within " + timeout.toSeconds() + " seconds"
                );
            }

            // Small delay to avoid tight loop
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new TimeoutException("Interrupted while waiting for condition");
            }
        }
    }

    /**
     * Run event loop for a specific number of iterations.
     */
    public void runForIterations(int iterations) {
        for (int i = 0; i < iterations; i++) {
            runCycle();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Run event loop in background thread (async).
     */
    public Thread runAsync(int maxIterations) {
        running = true;
        Thread thread = Thread.ofVirtual().start(() -> {
            for (int i = 0; i < maxIterations && running; i++) {
                runCycle();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            running = false;
        });
        return thread;
    }

    /**
     * Stop async event loop.
     */
    public void stop() {
        running = false;
    }

    /**
     * Check if async loop is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Wait for screen to update (dirty flag cleared).
     */
    public void waitForRender(Duration timeout) throws TimeoutException {
        runUntilCondition(() -> !root.isDirty(), timeout);
    }

    /**
     * Handle keyboard input (override in subclass for custom handling).
     */
    protected void handleKey(int ch) {
        // Default: no-op
        // Integration tests can override or provide key handler
    }

    /**
     * Handle mouse input (override in subclass for custom handling).
     */
    protected void handleMouse(MockNcursesBridge.MouseEventData mouse) {
        // Default: no-op
        // Integration tests can override or provide mouse handler
    }

    /**
     * Clear the buffer.
     */
    private void clearBuffer() {
        for (int i = 0; i < buffer.length; i++) {
            for (int j = 0; j < buffer[i].length; j++) {
                buffer[i][j] = ' ';
            }
        }
    }

    /**
     * Update mock screen from buffer.
     */
    private void updateScreen() {
        for (int y = 0; y < buffer.length; y++) {
            for (int x = 0; x < buffer[y].length; x++) {
                // Skip bottom-right corner (ncurses limitation)
                if (y == buffer.length - 1 && x == buffer[y].length - 1) {
                    continue;
                }
                bridge.moveCursor(y, x, buffer[y][x]);
            }
        }
        bridge.refresh();
    }
}
