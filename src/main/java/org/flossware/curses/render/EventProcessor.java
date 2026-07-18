package org.flossware.curses.render;

import org.flossware.curses.events.CursesEvent;
import org.flossware.curses.events.KeyEvent;
import org.flossware.curses.events.MouseEvent;
import org.flossware.curses.events.WindowEvent;
import org.flossware.curses.ffi.NcursesBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example event processor demonstrating Virtual Thread-based input handling.
 *
 * <p>This class showcases Java 21 features for event processing:
 * <ul>
 *   <li>Virtual Threads for lightweight blocking I/O</li>
 *   <li>Record Patterns for event deconstruction</li>
 *   <li>Sealed interfaces for exhaustive switching</li>
 * </ul>
 *
 * <p><b>Note:</b> This is example code. The main application (InteractiveDemo)
 * uses direct ncurses calls instead of this event processor pattern.
 *
 * @author FlossWare
 * @since 1.0
 */
public class EventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EventProcessor.class);
    private volatile boolean running = false;

    /**
     * Starts the input event loop in a Virtual Thread.
     *
     * <p>The loop reads ncurses events and dispatches them to handlers.
     * This demonstrates how to use Virtual Threads for blocking I/O operations.
     */
    public void startInputLoop() {
        running = true;
        // Use a Virtual Thread to handle blocking IO cheaply
        Thread.ofVirtual().name("CursesJava-Input-Loop").start(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    CursesEvent event = readNativeEvent();
                    if (event != null) {
                        processEvent(event);
                    }
                } catch (Exception e) {
                    logger.error("Error processing event", e);
                }
            }
        });
    }

    /**
     * Stops the input event loop.
     */
    public void stopInputLoop() {
        running = false;
    }

    /**
     * Processes an event using Record Patterns for clean deconstruction.
     *
     * <p>Demonstrates Java 21's pattern matching for switch with records.
     *
     * @param event the event to process
     */
    private void processEvent(CursesEvent event) {
        // Use Record Patterns (Java 21) for clean event deconstruction
        switch (event) {
            case KeyEvent(int code, boolean alt, boolean ctrl) ->
                handleKeyPress(code, alt, ctrl);
            case MouseEvent(int x, int y, int btn) ->
                handleMouseClick(x, y, btn);
            case WindowEvent(int w, int h) ->
                handleResize(w, h);
        }
    }

    /**
     * Reads the next event from ncurses.
     *
     * <p>This method blocks until an event is available, making it ideal
     * for Virtual Threads which have minimal blocking overhead.
     *
     * @return the next event, or null if none available
     */
    private CursesEvent readNativeEvent() {
        try {
            // Read character code from ncurses (blocking call)
            int ch = NcursesBridge.getChar();

            if (ch == -1) {
                // No character available (non-blocking mode)
                return null;
            }

            // For simplicity, create basic KeyEvent
            // Real implementation would detect modifiers and mouse events
            return new KeyEvent(ch, false, false);

        } catch (Throwable e) {
            logger.error("Error reading native event", e);
            return null;
        }
    }

    /**
     * Handles a key press event.
     *
     * @param code the key code
     * @param alt true if Alt was pressed
     * @param ctrl true if Ctrl was pressed
     */
    private void handleKeyPress(int code, boolean alt, boolean ctrl) {
        logger.debug("Key pressed: code={}, alt={}, ctrl={}", code, alt, ctrl);
        // Application-specific key handling would go here
    }

    /**
     * Handles a mouse click event.
     *
     * @param x the mouse X coordinate
     * @param y the mouse Y coordinate
     * @param btn the button code
     */
    private void handleMouseClick(int x, int y, int btn) {
        logger.debug("Mouse clicked: x={}, y={}, button={}", x, y, btn);
        // Application-specific mouse handling would go here
    }

    /**
     * Handles a window resize event.
     *
     * @param w the new width
     * @param h the new height
     */
    private void handleResize(int w, int h) {
        logger.debug("Window resized: width={}, height={}", w, h);
        // Application-specific resize handling would go here
    }
}