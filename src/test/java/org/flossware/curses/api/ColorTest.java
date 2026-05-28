package org.flossware.curses.api;

import org.flossware.curses.ffi.NcursesBridge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Color Tests")
class ColorTest {

    @Test
    @DisplayName("should have correct ncurses codes for all colors")
    void testNcursesCodes() {
        assertEquals(NcursesBridge.COLOR_BLACK, Color.BLACK.getNcursesCode());
        assertEquals(NcursesBridge.COLOR_RED, Color.RED.getNcursesCode());
        assertEquals(NcursesBridge.COLOR_GREEN, Color.GREEN.getNcursesCode());
        assertEquals(NcursesBridge.COLOR_YELLOW, Color.YELLOW.getNcursesCode());
        assertEquals(NcursesBridge.COLOR_BLUE, Color.BLUE.getNcursesCode());
        assertEquals(NcursesBridge.COLOR_MAGENTA, Color.MAGENTA.getNcursesCode());
        assertEquals(NcursesBridge.COLOR_CYAN, Color.CYAN.getNcursesCode());
        assertEquals(NcursesBridge.COLOR_WHITE, Color.WHITE.getNcursesCode());
    }

    @Test
    @DisplayName("should have 8 standard colors")
    void testColorCount() {
        assertEquals(8, Color.values().length);
    }

    @Test
    @DisplayName("should be able to get color by name")
    void testValueOf() {
        assertEquals(Color.RED, Color.valueOf("RED"));
        assertEquals(Color.BLUE, Color.valueOf("BLUE"));
        assertEquals(Color.GREEN, Color.valueOf("GREEN"));
    }
}
