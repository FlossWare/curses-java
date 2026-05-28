package org.flossware.curses.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test coverage for Constants class.
 */
@DisplayName("Constants Tests")
class ConstantsTest {

    @Test
    @DisplayName("Constants class should not be instantiable")
    void testCannotInstantiate() throws Exception {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(
            InvocationTargetException.class,
            constructor::newInstance,
            "Constructor should throw"
        );

        assertTrue(exception.getCause() instanceof AssertionError,
            "Should throw AssertionError");
        assertEquals("Cannot instantiate Constants class",
            exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Index sentinel constants should have correct values")
    void testIndexSentinelValues() {
        assertEquals(-1, Constants.NO_INDEX);
        assertEquals(-1, Constants.UNLIMITED);
    }

    @Test
    @DisplayName("Buffer and history limit constants should have correct values")
    void testBufferHistoryLimits() {
        assertEquals(100, Constants.MAX_UNDO_SIZE);
    }

    @Test
    @DisplayName("Default dimension constants should have correct values")
    void testDefaultDimensions() {
        assertEquals(80, Constants.DEFAULT_TERMINAL_WIDTH);
        assertEquals(24, Constants.DEFAULT_TERMINAL_HEIGHT);
    }

    @Test
    @DisplayName("Timing constants should have correct values")
    void testTimingConstants() {
        assertEquals(100, Constants.DEFAULT_TICK_INTERVAL_MS);
        assertEquals(50, Constants.MOUSE_EVENT_TIMEOUT_MS);
    }
}
