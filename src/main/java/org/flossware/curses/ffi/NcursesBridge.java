package org.flossware.curses.ffi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NcursesBridge {
    private static final Logger logger = LoggerFactory.getLogger(NcursesBridge.class);
    private static SymbolLookup ncurses;
    private static MethodHandle initscr;
    private static MethodHandle endwin;
    private static MethodHandle refresh;
    private static MethodHandle getch;
    private static MethodHandle mvaddch;
    private static MethodHandle clear;
    private static MethodHandle cbreak;
    private static MethodHandle noecho;
    private static MethodHandle keypad;
    private static MethodHandle nodelay;
    private static MethodHandle mousemask;
    private static MethodHandle getmouse;
    private static MethodHandle start_color;
    private static MethodHandle init_pair;
    private static MethodHandle color_pair;
    private static MethodHandle attron;
    private static MethodHandle attroff;
    private static MethodHandle getmaxy;
    private static MethodHandle getmaxx;
    private static MemorySegment stdscr;
    private static boolean initialized = false;

    // ncurses error code
    private static final int ERR = -1;

    // Mouse event constants (ncurses 6.x MOUSE_VERSION 2)
    public static final long BUTTON1_CLICKED = 0x00000004L;
    public static final long BUTTON1_DOUBLE_CLICKED = 0x00000008L;
    public static final long BUTTON1_PRESSED = 0x00000002L;
    public static final long BUTTON1_RELEASED = 0x00000001L;
    public static final long REPORT_MOUSE_POSITION = 0x10000000L;  // Required for drag events
    public static final long ALL_MOUSE_EVENTS = 0x0FFFFFFFL;  // All events except REPORT_MOUSE_POSITION

    // Color constants (ncurses standard colors)
    public static final int COLOR_BLACK = 0;
    public static final int COLOR_RED = 1;
    public static final int COLOR_GREEN = 2;
    public static final int COLOR_YELLOW = 3;
    public static final int COLOR_BLUE = 4;
    public static final int COLOR_MAGENTA = 5;
    public static final int COLOR_CYAN = 6;
    public static final int COLOR_WHITE = 7;

    // Attribute constants
    public static final int A_NORMAL = 0;
    public static final int A_STANDOUT = 1 << 16;
    public static final int A_UNDERLINE = 1 << 17;
    public static final int A_REVERSE = 1 << 18;
    public static final int A_BLINK = 1 << 19;
    public static final int A_DIM = 1 << 20;
    public static final int A_BOLD = 1 << 21;
    public static final int A_PROTECT = 1 << 22;
    public static final int A_INVIS = 1 << 23;

    static {
        try {
            // Platform-specific library detection with fallback chain
            String osName = System.getProperty("os.name").toLowerCase();
            String[] libraries = switch (osName) {
                case String os when os.contains("mac") ->
                    new String[]{"libncurses.dylib", "libncurses.6.dylib", "libncurses.5.dylib"};
                case String os when os.contains("linux") ->
                    new String[]{"libncurses.so.6", "libncurses.so.5", "libncurses.so"};
                case String os when os.contains("windows") ->
                    throw new UnsupportedOperationException(
                        "Windows is not currently supported. Consider using WSL or PDCurses.");
                default ->
                    throw new UnsupportedOperationException(
                        "Unsupported operating system: " + osName);
            };

            // Try each library in order until one succeeds
            SymbolLookup foundLibrary = null;
            IllegalArgumentException lastException = null;
            for (String lib : libraries) {
                try {
                    foundLibrary = SymbolLookup.libraryLookup(lib, Arena.global());
                    break;
                } catch (IllegalArgumentException e) {
                    lastException = e;
                    // Try next library
                }
            }

            if (foundLibrary == null) {
                throw new UnsatisfiedLinkError(
                    "Could not find ncurses library. Tried: " + String.join(", ", libraries) +
                    ". Last error: " + (lastException != null ? lastException.getMessage() : "unknown"));
            }

            ncurses = foundLibrary;
            Linker linker = Linker.nativeLinker();

            initscr = linker.downcallHandle(
                ncurses.find("initscr").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS)
            );

            endwin = linker.downcallHandle(
                ncurses.find("endwin").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
            );

            refresh = linker.downcallHandle(
                ncurses.find("refresh").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
            );

            getch = linker.downcallHandle(
                ncurses.find("getch").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
            );

            mvaddch = linker.downcallHandle(
                ncurses.find("mvaddch").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT)
            );

            clear = linker.downcallHandle(
                ncurses.find("clear").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
            );

            cbreak = linker.downcallHandle(
                ncurses.find("cbreak").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
            );

            noecho = linker.downcallHandle(
                ncurses.find("noecho").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
            );

            keypad = linker.downcallHandle(
                ncurses.find("keypad").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS, ValueLayout.JAVA_BYTE)
            );

            nodelay = linker.downcallHandle(
                ncurses.find("nodelay").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS, ValueLayout.JAVA_BYTE)
            );

            mousemask = linker.downcallHandle(
                ncurses.find("mousemask").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG,
                    ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
            );

            getmouse = linker.downcallHandle(
                ncurses.find("getmouse").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS)
            );

            start_color = linker.downcallHandle(
                ncurses.find("start_color").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
            );

            init_pair = linker.downcallHandle(
                ncurses.find("init_pair").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_SHORT, ValueLayout.JAVA_SHORT, ValueLayout.JAVA_SHORT)
            );

            color_pair = linker.downcallHandle(
                ncurses.find("COLOR_PAIR").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT)
            );

            attron = linker.downcallHandle(
                ncurses.find("attron").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT)
            );

            attroff = linker.downcallHandle(
                ncurses.find("attroff").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT)
            );

            getmaxy = linker.downcallHandle(
                ncurses.find("getmaxy").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS)
            );

            getmaxx = linker.downcallHandle(
                ncurses.find("getmaxx").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS)
            );

            initialized = true;
        } catch (UnsatisfiedLinkError e) {
            logger.error("ncurses library not found: {}", e.getMessage());
            logger.error("Please install ncurses: 'sudo dnf install ncurses-devel' (Fedora) or 'sudo apt install libncurses-dev' (Debian/Ubuntu)");
            initialized = false;
        } catch (IllegalArgumentException e) {
            logger.error("Could not load ncurses library: {}", e.getMessage());
            initialized = false;
        } catch (NoSuchElementException e) {
            logger.error("Incompatible ncurses version (missing required symbols): {}", e.getMessage());
            initialized = false;
        } catch (Throwable e) {
            // Catch Throwable for critical errors during FFI setup, but rethrow Errors
            if (e instanceof Error && !(e instanceof LinkageError)) {
                throw (Error) e;
            }
            logger.error("Failed to initialize ncurses: {}: {}", e.getClass().getName(), e.getMessage(), e);
            initialized = false;
        }
    }

    public static boolean isAvailable() {
        return initialized;
    }

    /**
     * Check ncurses operation result and throw exception on error.
     * @param result the return code from ncurses function
     * @param operation description of the operation for error messages
     * @throws RuntimeException if result indicates error (ERR)
     */
    private static void checkResult(int result, String operation) {
        if (result == ERR) {
            throw new RuntimeException("ncurses operation failed: " + operation);
        }
    }

    public static void init() {
        if (!initialized) {
            throw new UnsupportedOperationException("ncurses library not available");
        }
        try {
            stdscr = (MemorySegment) initscr.invokeExact();
            checkResult((int) cbreak.invokeExact(), "cbreak");
            checkResult((int) noecho.invokeExact(), "noecho");
            checkResult((int) keypad.invokeExact(stdscr, (byte) 1), "keypad");
        } catch (Throwable e) {
            throw new NcursesException("Failed to initialize ncurses", e);
        }
    }

    public static void stop() {
        if (initialized) {
            try {
                checkResult((int) endwin.invokeExact(), "endwin");
            } catch (Throwable e) {
                throw new NcursesException("Failed to stop ncurses", e);
            }
        }
    }

    public static void refresh() {
        if (initialized) {
            try {
                checkResult((int) refresh.invokeExact(), "refresh");
            } catch (Throwable e) {
                throw new NcursesException("Failed to refresh screen", e);
            }
        }
    }

    public static int getChar() {
        if (initialized) {
            try {
                return (int) getch.invokeExact();
            } catch (Throwable e) {
                throw new NcursesException("Failed to read character from ncurses", e);
            }
        }
        return -1;
    }

    public static void moveCursor(int y, int x, char ch) {
        if (initialized) {
            try {
                checkResult((int) mvaddch.invokeExact(y, x, (int) ch), "mvaddch");
            } catch (Throwable e) {
                throw new NcursesException("Failed to move cursor and add character", e);
            }
        }
    }

    public static void clear() {
        if (initialized) {
            try {
                checkResult((int) clear.invokeExact(), "clear");
            } catch (Throwable e) {
                throw new NcursesException("Failed to clear screen", e);
            }
        }
    }

    public static void setNonBlocking(boolean nonBlocking) {
        if (initialized) {
            try {
                checkResult((int) nodelay.invokeExact(stdscr, (byte) (nonBlocking ? 1 : 0)), "nodelay");
            } catch (Throwable e) {
                throw new NcursesException("Failed to set non-blocking mode", e);
            }
        }
    }

    public static long enableMouse(long mask) {
        if (initialized) {
            try {
                // Use ofAuto() instead of global() to allow GC to reclaim memory
                MemorySegment oldMaskPtr = Arena.ofAuto().allocate(ValueLayout.JAVA_LONG);
                long result = (long) mousemask.invokeExact(mask, oldMaskPtr);
                return result;
            } catch (Throwable e) {
                throw new NcursesException("Failed to enable mouse support", e);
            }
        }
        return 0;
    }

    public static MouseEventData getMouseEvent() {
        if (initialized) {
            try {
                // MEVENT structure: short id, int x, int y, int z, long bstate
                // Use ofAuto() instead of global() to allow GC to reclaim memory
                // Critical: this method is called on every mouse event, global arena would leak memory
                MemorySegment mevent = Arena.ofAuto().allocate(32);  // Allocate enough space for MEVENT
                int result = (int) getmouse.invokeExact(mevent);
                if (result == 0) {  // OK
                    int x = mevent.get(ValueLayout.JAVA_INT, 4);
                    int y = mevent.get(ValueLayout.JAVA_INT, 8);
                    long bstate = mevent.get(ValueLayout.JAVA_LONG, 16);
                    return new MouseEventData(x, y, bstate);
                }
            } catch (Throwable e) {
                throw new NcursesException("Failed to get mouse event", e);
            }
        }
        return null;
    }

    public static void startColor() {
        if (initialized) {
            try {
                checkResult((int) start_color.invokeExact(), "start_color");
            } catch (Throwable e) {
                throw new NcursesException("Failed to start color support", e);
            }
        }
    }

    public static void initColorPair(short pair, short fg, short bg) {
        if (initialized) {
            try {
                checkResult((int) init_pair.invokeExact(pair, fg, bg), "init_pair");
            } catch (Throwable e) {
                throw new NcursesException("Failed to initialize color pair", e);
            }
        }
    }

    public static int getColorPair(int pairNumber) {
        if (initialized) {
            try {
                return (int) color_pair.invokeExact(pairNumber);
            } catch (Throwable e) {
                throw new NcursesException("Failed to get color pair", e);
            }
        }
        return 0;
    }

    public static void enableAttribute(int attr) {
        if (initialized) {
            try {
                checkResult((int) attron.invokeExact(attr), "attron");
            } catch (Throwable e) {
                throw new NcursesException("Failed to enable text attribute", e);
            }
        }
    }

    public static void disableAttribute(int attr) {
        if (initialized) {
            try {
                checkResult((int) attroff.invokeExact(attr), "attroff");
            } catch (Throwable e) {
                throw new NcursesException("Failed to disable text attribute", e);
            }
        }
    }

    public static int getTerminalHeight() {
        if (initialized && stdscr != null) {
            try {
                return (int) getmaxy.invokeExact(stdscr);
            } catch (Throwable e) {
                throw new NcursesException("Failed to get terminal height", e);
            }
        }
        return 24;  // Default fallback
    }

    public static int getTerminalWidth() {
        if (initialized && stdscr != null) {
            try {
                return (int) getmaxx.invokeExact(stdscr);
            } catch (Throwable e) {
                throw new NcursesException("Failed to get terminal width", e);
            }
        }
        return 80;  // Default fallback
    }

    public record MouseEventData(int x, int y, long buttonState) {}
}