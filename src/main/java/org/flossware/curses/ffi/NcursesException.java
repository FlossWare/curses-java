package org.flossware.curses.ffi;

/**
 * RuntimeException subclass for ncurses FFI (Foreign Function Interface) errors.
 *
 * <p>This exception wraps checked exceptions thrown by {@link java.lang.invoke.MethodHandle#invokeExact()},
 * converting them to unchecked exceptions for cleaner error handling in the ncurses bridge.
 *
 * <p>Common causes include:
 * <ul>
 *   <li>ncurses library not available or initialization failure</li>
 *   <li>Invalid arguments passed to ncurses functions</li>
 *   <li>ncurses operation failures (e.g., color initialization, drawing)</li>
 * </ul>
 *
 * @author FlossWare
 * @since 1.0
 */
public class NcursesException extends RuntimeException {
    /**
     * Constructs a new NcursesException with the specified detail message.
     *
     * @param message the detail message
     */
    public NcursesException(String message) {
        super(message);
    }

    /**
     * Constructs a new NcursesException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public NcursesException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new NcursesException with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public NcursesException(Throwable cause) {
        super("ncurses FFI error", cause);
    }
}
