package org.flossware.curses.integration;

import org.flossware.curses.api.RootPane;
import org.flossware.curses.events.MouseEvent;
import org.flossware.curses.testutil.MockNcursesBridge;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
    private final List<Thread> asyncThreads = new ArrayList<>();

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
     *
     * Spawns a virtual thread that runs event loop cycles. The thread is tracked
     * for cleanup during teardown to prevent resource leaks.
     *
     * @param maxIterations Maximum number of cycles to run
     * @return The spawned thread (tracked internally)
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

        // Track the thread for cleanup during teardown
        synchronized (asyncThreads) {
            asyncThreads.add(thread);
        }

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
     * Join all tracked async threads with a timeout.
     *
     * This method should be called during test teardown to ensure all background
     * threads are properly cleaned up before resources are released.
     *
     * @param timeoutMillis Maximum time in milliseconds to wait for all threads
     * @throws InterruptedException if interrupted while joining
     */
    public void joinAsyncThreads(long timeoutMillis) throws InterruptedException {
        synchronized (asyncThreads) {
            if (asyncThreads.isEmpty()) {
                return;
            }
        }

        long startTime = System.currentTimeMillis();

        synchronized (asyncThreads) {
            for (Thread thread : asyncThreads) {
                if (!thread.isAlive()) {
                    continue;
                }

                long elapsed = System.currentTimeMillis() - startTime;
                long remaining = timeoutMillis - elapsed;

                if (remaining <= 0) {
                    // Timeout exceeded; remaining threads will be left to finish
                    break;
                }

                try {
                    thread.join(remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }

    /**
     * Wait for screen to update (dirty flag cleared).
     */
    public void waitForRender(Duration timeout) throws TimeoutException {
        runUntilCondition(() -> !root.isDirty(), timeout);
    }

    /**
     * Handle keyboard input.
     * Dispatches key events to the component hierarchy.
     * Can be overridden in subclasses for custom key handling.
     */
    protected void handleKey(int ch) {
        // Hook point for keyboard event dispatch
        // Subclasses can override to implement actual key handling
        // This is called by runCycle() when keyboard input is available
    }

    /**
     * Handle mouse input.
     * Dispatches mouse events through the component hierarchy starting at root.
     * This ensures the actual event dispatch mechanism is exercised in tests.
     */
    protected void handleMouse(MockNcursesBridge.MouseEventData mouse) {
        // Convert bridge mouse event to MouseEvent and dispatch through component tree
        MouseEvent event = new MouseEvent(mouse.x, mouse.y, (int)(mouse.buttonState & 0xFFFFFFFFL));
        root.handleMouseEvent(event);
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
