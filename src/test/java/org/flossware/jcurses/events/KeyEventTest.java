package org.flossware.jcurses.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KeyEvent Tests")
class KeyEventTest {

    @Test
    @DisplayName("should create KeyEvent with correct values")
    void testCreation() {
        KeyEvent event = new KeyEvent(65, true, false);

        assertEquals(65, event.keyCode());
        assertTrue(event.altPressed());
        assertFalse(event.ctrlPressed());
    }

    @Test
    @DisplayName("should be instance of JcursesEvent")
    void testSealedInterface() {
        KeyEvent event = new KeyEvent(32, false, false);

        assertInstanceOf(JcursesEvent.class, event);
    }

    @Test
    @DisplayName("should support record pattern matching")
    void testPatternMatching() {
        JcursesEvent event = new KeyEvent(13, false, true);

        switch (event) {
            case KeyEvent(int code, boolean alt, boolean ctrl) -> {
                assertEquals(13, code);
                assertFalse(alt);
                assertTrue(ctrl);
            }
            case MouseEvent(int x, int y, int btn) -> fail("Should be KeyEvent");
            case WindowEvent(int w, int h) -> fail("Should be KeyEvent");
        }
    }

    @Test
    @DisplayName("should have value-based equality")
    void testEquality() {
        KeyEvent event1 = new KeyEvent(65, true, false);
        KeyEvent event2 = new KeyEvent(65, true, false);
        KeyEvent event3 = new KeyEvent(66, true, false);

        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("should have proper toString representation")
    void testToString() {
        KeyEvent event = new KeyEvent(65, true, false);
        String str = event.toString();

        assertTrue(str.contains("65"));
        assertTrue(str.contains("true"));
        assertTrue(str.contains("false"));
    }

    @ParameterizedTest
    @CsvSource({
        "65, true, true",
        "13, false, false",
        "27, true, false",
        "32, false, true",
        "0, false, false",
        "-1, true, true"
    })
    @DisplayName("should handle various key codes and modifier combinations")
    void testVariousKeyCodes(int keyCode, boolean alt, boolean ctrl) {
        KeyEvent event = new KeyEvent(keyCode, alt, ctrl);

        assertEquals(keyCode, event.keyCode());
        assertEquals(alt, event.altPressed());
        assertEquals(ctrl, event.ctrlPressed());
    }

    @Test
    @DisplayName("should support exhaustive pattern matching")
    void testExhaustivePatternMatching() {
        JcursesEvent[] events = {
            new KeyEvent(65, false, false),
            new KeyEvent(13, true, false),
            new KeyEvent(27, false, true)
        };

        for (JcursesEvent event : events) {
            String result = switch (event) {
                case KeyEvent e -> "Key: " + e.keyCode();
                case MouseEvent e -> "Mouse";
                case WindowEvent e -> "Window";
            };
            assertTrue(result.startsWith("Key:"));
        }
    }

    @Test
    @DisplayName("should be immutable")
    void testImmutability() {
        KeyEvent event = new KeyEvent(65, true, false);

        // Records are immutable, accessors should always return the same values
        assertEquals(65, event.keyCode());
        assertEquals(65, event.keyCode());
        assertTrue(event.altPressed());
        assertTrue(event.altPressed());
        assertFalse(event.ctrlPressed());
        assertFalse(event.ctrlPressed());
    }
}
