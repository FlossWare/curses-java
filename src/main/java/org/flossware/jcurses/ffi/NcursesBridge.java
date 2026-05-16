package org.flossware.jcurses.ffi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class NcursesBridge {
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
    private static MemorySegment stdscr;
    private static boolean initialized = false;

    // Mouse event constants
    public static final long BUTTON1_CLICKED = 0x00000004L;
    public static final long BUTTON1_DOUBLE_CLICKED = 0x00000008L;
    public static final long BUTTON1_PRESSED = 0x00000002L;
    public static final long BUTTON1_RELEASED = 0x00000001L;
    public static final long REPORT_MOUSE_POSITION = 0x08000000L;
    public static final long ALL_MOUSE_EVENTS = 0x1FFFFFFFL;  // Includes REPORT_MOUSE_POSITION

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
            ncurses = SymbolLookup.libraryLookup("libncurses.so.6", Arena.global());
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

            initialized = true;
        } catch (Exception e) {
            System.err.println("Warning: Could not load ncurses library: " + e.getMessage());
            initialized = false;
        }
    }

    public static boolean isAvailable() {
        return initialized;
    }

    public static void init() throws Throwable {
        if (!initialized) {
            throw new UnsupportedOperationException("ncurses library not available");
        }
        stdscr = (MemorySegment) initscr.invokeExact();
        int result1 = (int) cbreak.invokeExact();
        int result2 = (int) noecho.invokeExact();
        int result3 = (int) keypad.invokeExact(stdscr, (byte) 1);
    }

    public static void stop() throws Throwable {
        if (initialized) {
            int result = (int) endwin.invokeExact();
        }
    }

    public static void refresh() throws Throwable {
        if (initialized) {
            int result = (int) refresh.invokeExact();
        }
    }

    public static int getChar() throws Throwable {
        if (initialized) {
            return (int) getch.invokeExact();
        }
        return -1;
    }

    public static void moveCursor(int y, int x, char ch) throws Throwable {
        if (initialized) {
            int result = (int) mvaddch.invokeExact(y, x, (int) ch);
        }
    }

    public static void clear() throws Throwable {
        if (initialized) {
            int result = (int) clear.invokeExact();
        }
    }

    public static void setNonBlocking(boolean nonBlocking) throws Throwable {
        if (initialized) {
            int result = (int) nodelay.invokeExact(stdscr, (byte) (nonBlocking ? 1 : 0));
        }
    }

    public static long enableMouse(long mask) throws Throwable {
        if (initialized) {
            MemorySegment oldMaskPtr = Arena.global().allocate(ValueLayout.JAVA_LONG);
            long result = (long) mousemask.invokeExact(mask, oldMaskPtr);
            return result;
        }
        return 0;
    }

    public static MouseEventData getMouseEvent() throws Throwable {
        if (initialized) {
            // MEVENT structure: short id, int x, int y, int z, long bstate
            MemorySegment mevent = Arena.global().allocate(32);  // Allocate enough space for MEVENT
            int result = (int) getmouse.invokeExact(mevent);
            if (result == 0) {  // OK
                int x = mevent.get(ValueLayout.JAVA_INT, 4);
                int y = mevent.get(ValueLayout.JAVA_INT, 8);
                long bstate = mevent.get(ValueLayout.JAVA_LONG, 16);
                return new MouseEventData(x, y, bstate);
            }
        }
        return null;
    }

    public static void startColor() throws Throwable {
        if (initialized) {
            int result = (int) start_color.invokeExact();
        }
    }

    public static void initColorPair(short pair, short fg, short bg) throws Throwable {
        if (initialized) {
            int result = (int) init_pair.invokeExact(pair, fg, bg);
        }
    }

    public static int getColorPair(int pairNumber) throws Throwable {
        if (initialized) {
            return (int) color_pair.invokeExact(pairNumber);
        }
        return 0;
    }

    public static void enableAttribute(int attr) throws Throwable {
        if (initialized) {
            int result = (int) attron.invokeExact(attr);
        }
    }

    public static void disableAttribute(int attr) throws Throwable {
        if (initialized) {
            int result = (int) attroff.invokeExact(attr);
        }
    }

    public record MouseEventData(int x, int y, long buttonState) {}
}