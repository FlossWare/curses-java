package org.flossware.curses.api;

/**
 * Application-wide constants for jcurses library.
 */
public final class Constants {

    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }

    // ============================================================================
    // INDEX SENTINELS
    // ============================================================================

    /** Sentinel value indicating no index selected or initialized. */
    public static final int NO_INDEX = -1;

    /** Sentinel value indicating unlimited length. */
    public static final int UNLIMITED = -1;

    // ============================================================================
    // BUFFER AND HISTORY LIMITS
    // ============================================================================

    /** Maximum size of undo/redo history stack. */
    public static final int MAX_UNDO_SIZE = 100;

    // ============================================================================
    // DEFAULT DIMENSIONS
    // ============================================================================

    /** Default terminal width in characters. */
    public static final int DEFAULT_TERMINAL_WIDTH = 80;

    /** Default terminal height in characters. */
    public static final int DEFAULT_TERMINAL_HEIGHT = 24;

    // ============================================================================
    // TIMING CONSTANTS
    // ============================================================================

    /** Default tick interval for event loop in milliseconds. */
    public static final int DEFAULT_TICK_INTERVAL_MS = 100;

    /** Mouse event timeout in milliseconds. */
    public static final int MOUSE_EVENT_TIMEOUT_MS = 50;
}
