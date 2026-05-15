package org.flossware.jcurses.api;

import org.flossware.jcurses.ffi.NcursesBridge;

/**
 * Enum representing standard ncurses colors.
 */
public enum Color {
    BLACK(NcursesBridge.COLOR_BLACK),
    RED(NcursesBridge.COLOR_RED),
    GREEN(NcursesBridge.COLOR_GREEN),
    YELLOW(NcursesBridge.COLOR_YELLOW),
    BLUE(NcursesBridge.COLOR_BLUE),
    MAGENTA(NcursesBridge.COLOR_MAGENTA),
    CYAN(NcursesBridge.COLOR_CYAN),
    WHITE(NcursesBridge.COLOR_WHITE);

    private final int ncursesCode;

    Color(int ncursesCode) {
        this.ncursesCode = ncursesCode;
    }

    public int getNcursesCode() {
        return ncursesCode;
    }
}
