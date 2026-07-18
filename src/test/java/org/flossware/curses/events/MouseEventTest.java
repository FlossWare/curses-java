package org.flossware.curses.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MouseEvent Tests")
class MouseEventTest {

    @Test
    @DisplayName("should create MouseEvent with correct values")
    void testCreation() {
        MouseEvent event = new MouseEvent(10, 20, 1);

        assertEquals(10, event.x());
        assertEquals(20, event.y());
        assertEquals(1, event.button());
    }

    @Test
    @DisplayName("should be instance of CursesEvent")
    void testSealedInterface() {
        MouseEvent event = new MouseEvent(0, 0, 0);

        assertInstanceOf(CursesEvent.class, event);
    }

    @Test
    @DisplayName("should support record pattern matching")
    void testPatternMatching() {
        CursesEvent event = new MouseEvent(15, 25, 2);

        switch (event) {
            case KeyEvent(int code, boolean alt, boolean ctrl) -> fail("Should be MouseEvent");
            case MouseEvent(int x, int y, int btn) -> {
                assertEquals(15, x);
                assertEquals(25, y);
                assertEquals(2, btn);
            }
            case WindowEvent(int w, int h) -> fail("Should be MouseEvent");
        }
    }

    @Test
    @DisplayName("should have value-based equality")
    void testEquality() {
        MouseEvent event1 = new MouseEvent(10, 20, 1);
        MouseEvent event2 = new MouseEvent(10, 20, 1);
        MouseEvent event3 = new MouseEvent(10, 20, 2);

        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("should have proper toString representation")
    void testToString() {
        MouseEvent event = new MouseEvent(10, 20, 1);
        String str = event.toString();

        assertTrue(str.contains("10"));
        assertTrue(str.contains("20"));
        assertTrue(str.contains("1"));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0, 0",
        "10, 20, 1",
        "50, 100, 2",
        "-1, -1, -1",
        "1000, 2000, 3"
    })
    @DisplayName("should handle various coordinate and button values")
    void testVariousCoordinates(int x, int y, int button) {
        MouseEvent event = new MouseEvent(x, y, button);

        assertEquals(x, event.x());
        assertEquals(y, event.y());
        assertEquals(button, event.button());
    }

    @Test
    @DisplayName("should support exhaustive pattern matching")
    void testExhaustivePatternMatching() {
        CursesEvent[] events = {
            new MouseEvent(10, 20, 1),
            new MouseEvent(30, 40, 2),
            new MouseEvent(0, 0, 0)
        };

        for (CursesEvent event : events) {
            String result = switch (event) {
                case KeyEvent e -> "Key";
                case MouseEvent e -> "Mouse at (" + e.x() + "," + e.y() + ")";
                case WindowEvent e -> "Window";
            };
            assertTrue(result.startsWith("Mouse at"));
        }
    }

    @Test
    @DisplayName("should be immutable")
    void testImmutability() {
        MouseEvent event = new MouseEvent(10, 20, 1);

        // Records are immutable, accessors should always return the same values
        assertEquals(10, event.x());
        assertEquals(10, event.x());
        assertEquals(20, event.y());
        assertEquals(20, event.y());
        assertEquals(1, event.button());
        assertEquals(1, event.button());
    }
}
