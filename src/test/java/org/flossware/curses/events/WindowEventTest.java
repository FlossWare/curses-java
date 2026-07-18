package org.flossware.curses.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WindowEvent Tests")
class WindowEventTest {

    @Test
    @DisplayName("should create WindowEvent with correct values")
    void testCreation() {
        WindowEvent event = new WindowEvent(80, 24);

        assertEquals(80, event.width());
        assertEquals(24, event.height());
    }

    @Test
    @DisplayName("should be instance of CursesEvent")
    void testSealedInterface() {
        WindowEvent event = new WindowEvent(100, 50);

        assertInstanceOf(CursesEvent.class, event);
    }

    @Test
    @DisplayName("should support record pattern matching")
    void testPatternMatching() {
        CursesEvent event = new WindowEvent(120, 30);

        switch (event) {
            case KeyEvent(int code, boolean alt, boolean ctrl) -> fail("Should be WindowEvent");
            case MouseEvent(int x, int y, int btn) -> fail("Should be WindowEvent");
            case WindowEvent(int w, int h) -> {
                assertEquals(120, w);
                assertEquals(30, h);
            }
        }
    }

    @Test
    @DisplayName("should have value-based equality")
    void testEquality() {
        WindowEvent event1 = new WindowEvent(80, 24);
        WindowEvent event2 = new WindowEvent(80, 24);
        WindowEvent event3 = new WindowEvent(100, 50);

        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("should have proper toString representation")
    void testToString() {
        WindowEvent event = new WindowEvent(80, 24);
        String str = event.toString();

        assertTrue(str.contains("80"));
        assertTrue(str.contains("24"));
    }

    @ParameterizedTest
    @CsvSource({
        "80, 24",
        "100, 50",
        "120, 30",
        "0, 0",
        "1, 1",
        "1000, 1000"
    })
    @DisplayName("should handle various dimension values")
    void testVariousDimensions(int width, int height) {
        WindowEvent event = new WindowEvent(width, height);

        assertEquals(width, event.width());
        assertEquals(height, event.height());
    }

    @Test
    @DisplayName("should support exhaustive pattern matching")
    void testExhaustivePatternMatching() {
        CursesEvent[] events = {
            new WindowEvent(80, 24),
            new WindowEvent(100, 50),
            new WindowEvent(120, 30)
        };

        for (CursesEvent event : events) {
            String result = switch (event) {
                case KeyEvent e -> "Key";
                case MouseEvent e -> "Mouse";
                case WindowEvent e -> "Window " + e.width() + "x" + e.height();
            };
            assertTrue(result.startsWith("Window"));
        }
    }

    @Test
    @DisplayName("should be immutable")
    void testImmutability() {
        WindowEvent event = new WindowEvent(80, 24);

        // Records are immutable, accessors should always return the same values
        assertEquals(80, event.width());
        assertEquals(80, event.width());
        assertEquals(24, event.height());
        assertEquals(24, event.height());
    }

    @Test
    @DisplayName("should handle terminal resize scenarios")
    void testTerminalResize() {
        // Common terminal sizes
        WindowEvent standardTerminal = new WindowEvent(80, 24);
        WindowEvent wideTerminal = new WindowEvent(132, 43);
        WindowEvent smallTerminal = new WindowEvent(40, 12);

        assertEquals(80, standardTerminal.width());
        assertEquals(24, standardTerminal.height());
        assertEquals(132, wideTerminal.width());
        assertEquals(43, wideTerminal.height());
        assertEquals(40, smallTerminal.width());
        assertEquals(12, smallTerminal.height());
    }
}
